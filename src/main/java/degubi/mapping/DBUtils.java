package degubi.mapping;

import degubi.gui.*;
import degubi.model.stat.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;
import javafx.collections.*;

public final class DBUtils {
    private static final boolean LOG_SQL_QUERIES = true;


    public static<T> CompletableFuture<ObservableList<T>> listAll(Class<T> resultType) {
        var mapper = ObjectMapper.createMapper(resultType);

        return listGenerated(mapper.listAllQuery, mapper);
    }

    public static<T> CompletableFuture<ObservableList<T>> listAllOrderedBy(String orderByField, Class<T> resultType) {
        var mapper = ObjectMapper.createMapper(resultType);

        return listGenerated(mapper.listAllQuery + " ORDER BY " + orderByField + " ASC", mapper);
    }

    public static<T> CompletableFuture<ObservableList<T>> listFiltered(String field, String value, Class<T> resultType) {
        var mapper = ObjectMapper.createMapper(resultType);

        return listGenerated(mapper.listAllQuery + " WHERE " + field + " LIKE '%" + value + "%'", mapper);
    }


    @SuppressWarnings("unchecked")
    public static<T> void add(T toAdd) {
        var mapper = ObjectMapper.createMapper((Class<T>) toAdd.getClass());
        var valuesMap = ObjectMapper.createFieldValuesMap(mapper, toAdd);
        var fieldNames = mapper.parameterFieldNames;
        var valuesString = IntStream.range(0, fieldNames.length)
                                    .mapToObj(i -> fieldNames[i].equals(mapper.primaryKeyFieldName) ? "NULL" : formatObjectForSQL(valuesMap.get(fieldNames[i])))
                                    .collect(Collectors.joining(", "));

        updateGenerated("INSERT INTO " + mapper.tableName +
                        " (" + String.join(", ", mapper.parameterFieldNames) +
                        ") VALUES (" + valuesString + ")");
    }

    @SuppressWarnings("unchecked")
    public static<T> void update(T oldObj, T newObj) {
        var oldMapper = ObjectMapper.createMapper((Class<T>) oldObj.getClass());
        var newMapper = ObjectMapper.createMapper((Class<T>) newObj.getClass());
        var primaryKeyFieldName = newMapper.primaryKeyFieldName;

        var oldValues = ObjectMapper.createFieldValuesMap(oldMapper, oldObj);
        var newValues = ObjectMapper.createFieldValuesMap(newMapper, newObj);
        var setPartString = Arrays.stream(newMapper.parameterFieldNames)
                                  .filter(k -> !k.equals(primaryKeyFieldName))
                                  .map(k -> k + " = " + formatObjectForSQL(newValues.get(k)))
                                  .collect(Collectors.joining(", "));

        updateGenerated("UPDATE " + newMapper.tableName +
                        " SET " + setPartString +
                        " WHERE " + primaryKeyFieldName + " = " + formatObjectForSQL(oldValues.get(primaryKeyFieldName)));
    }

    public static void delete(Object toDelete) {
        var mapper = ObjectMapper.createMapper(Objects.requireNonNull(toDelete).getClass());
        var keyField = mapper.primaryKeyField;

        try {
            var keyValue = keyField.get(toDelete);
            var sqlKeyValue = keyField.getType() == String.class ? "'" + keyValue + "'" : keyValue;

            updateGenerated("DELETE FROM " + mapper.tableName + " WHERE " + mapper.primaryKeyFieldName + " = " + sqlKeyValue);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    public static<T> CompletableFuture<ObservableList<T>> listCustom(String sql, Class<T> resultType) {
        if(LOG_SQL_QUERIES) {
            System.out.println("Listing with custom query: \"" + sql + "\"");
        }

        return listInternal(sql, ObjectMapper.createMapper(resultType));
    }

    public static void updateCustom(String sql) {
        if(LOG_SQL_QUERIES) {
            System.out.println("Updating with custom query: \"" + sql + "\"");
        }

        updateInternal(sql);
    }

    public static String getNapFromIndex(int index) {
        switch(index) {
            case 0: return "Hétfő";
            case 1: return "Kedd";
            case 2: return "Szerda";
            case 3: return "Csütörtök";
            case 4: return "Péntek";
            default: throw new IllegalArgumentException("Unknown day index: " + index);
        }
    }


    public static CompletableFuture<ObservableList<TantargyFrequencyStat>> getTantargyFrequencyMap() {
        var query = "SELECT COUNT(" + TableNames.ORA + ".tantargyAzonosito) as Frequency, " + TableNames.TANTAGY + ".*" +
                    " FROM " + TableNames.TANTAGY +
                    " LEFT JOIN " + TableNames.ORA + " ON " + TableNames.ORA + ".tantargyAzonosito = " + TableNames.TANTAGY + ".azonosito" +
                    " GROUP BY " + TableNames.TANTAGY + ".azonosito" +
                    " ORDER BY Frequency DESC";

        return listCustom(query, TantargyFrequencyStat.class);
    }



    // TODO: Handle not builtin object primary key extraction
    private static String formatObjectForSQL(Object obj) {
        var type = obj.getClass();

        return type == String.class ? "'" + obj + "'" :
               type == Boolean.class ? (((Boolean) obj).booleanValue() ? "1" : "0") : String.valueOf(obj);
    }

    private static<T> T useStatement(Function<Statement, T> connectionConsumer) {
        try(var conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/timetable", "timetable", "gimmecookies");
            var statement = conn.createStatement()) {

            return connectionConsumer.apply(statement);
        } catch (SQLException e) {
            e.printStackTrace();
            Components.showErrorDialog("Nem sikerült csatlakozni a szerverhez!");
            return null;
        }
    }

    private static<T> CompletableFuture<ObservableList<T>> listInternal(String sql, ObjectMapper<T> mapper) {
        return CompletableFuture.supplyAsync(() ->
            DBUtils.useStatement(statement -> {
                var result = new ArrayList<T>();

                try(var resultSet = statement.executeQuery(sql)) {
                    while(resultSet.next()) {
                        result.add(ObjectMapper.createInstance(resultSet, mapper));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Components.showErrorDialog("SQL Hiba történt!");
                }

                return FXCollections.observableArrayList(result);
            }
        ));
    }

    private static void updateInternal(String sql) {
        DBUtils.useStatement(statement -> {
            try {
                statement.executeUpdate(sql);
            }catch (Exception e) {
                e.printStackTrace();
                Components.showErrorDialog("SQL Hiba történt! Hiba: \n" + e.getMessage());
            }

            return null;
        });
    }

    private static<T> CompletableFuture<ObservableList<T>> listGenerated(String sql, ObjectMapper<T> mapper) {
        if(LOG_SQL_QUERIES) {
            System.out.println("Listing with generated query: \"" + sql + "\"");
        }

        return listInternal(sql, mapper);
    }

    private static void updateGenerated(String sql) {
        if(LOG_SQL_QUERIES) {
            System.out.println("Updating with generated query: \"" + sql + "\"");
        }

        updateInternal(sql);
    }

    private DBUtils() {}
}