package degubi.db;

import degubi.gui.*;
import degubi.mapping.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;
import javafx.collections.*;

public final class DBUtils {
    private static final boolean LOG_SQL_QUERIES = true;

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

    public static<T> CompletableFuture<ObservableList<T>> listAll(Class<T> resultType) {
        var mapper = ObjectMapperUtils.createMapper(resultType);

        return listAllInternal(mapper.listAllQuery, mapper);
    }

    public static<T> CompletableFuture<ObservableList<T>> listAllOrderedBy(String orderByField, Class<T> resultType) {
        var mapper = ObjectMapperUtils.createMapper(resultType);

        return listAllInternal(mapper.listAllQuery + " ORDER BY " + orderByField + " ASC", mapper);
    }

    public static<T> CompletableFuture<ObservableList<T>> listFiltered(String field, String value, Class<T> resultType) {
        var mapper = ObjectMapperUtils.createMapper(resultType);

        return listAllInternal(mapper.listAllQuery + " WHERE " + field + " LIKE '%" + value + "%'", mapper);
    }


    @SuppressWarnings("unchecked")
    public static<T> void add(T toAdd) {
        var mapper = ObjectMapperUtils.createMapper((Class<T>) toAdd.getClass());
        var valuesMap = ObjectMapperUtils.createFieldValuesMap(mapper, toAdd);
        var fieldNames = mapper.parameterFieldNames;
        var valuesString = IntStream.range(0, fieldNames.length)
                                    .mapToObj(i -> fieldNames[i].equals(mapper.primaryKeyFieldName) ? "NULL" : formatObjectForSQL(valuesMap.get(fieldNames[i])))
                                    .collect(Collectors.joining(", "));

        updateGenerated("INSERT INTO " + mapper.tableName +
                        " (" + String.join(", ", mapper.parameterFieldNames) +
                        ") VALUES (" + valuesString + ")");
    }

    @SuppressWarnings("unchecked")
    public static<T> void updateExisting(T oldObj, T newObj) {
        var oldMapper = ObjectMapperUtils.createMapper((Class<T>) oldObj.getClass());
        var newMapper = ObjectMapperUtils.createMapper((Class<T>) newObj.getClass());
        var primaryKeyFieldName = newMapper.primaryKeyFieldName;

        var oldValues = ObjectMapperUtils.createFieldValuesMap(oldMapper, oldObj);
        var newValues = ObjectMapperUtils.createFieldValuesMap(newMapper, newObj);
        var setPartString = Arrays.stream(newMapper.parameterFieldNames)
                                  .filter(k -> !k.equals(primaryKeyFieldName))
                                  .map(k -> k + " = " + formatObjectForSQL(newValues.get(k)))
                                  .collect(Collectors.joining(", "));

        updateGenerated("UPDATE " + newMapper.tableName +
                        " SET " + setPartString +
                        " WHERE " + primaryKeyFieldName + " = " + formatObjectForSQL(oldValues.get(primaryKeyFieldName)));
    }

    public static void delete(Object toDelete) {
        var mapper = ObjectMapperUtils.createMapper(Objects.requireNonNull(toDelete).getClass());
        var keyField = mapper.primaryKeyField;

        try {
            var keyValue = keyField.get(toDelete);
            var sqlKeyValue = keyField.getType() == String.class ? "'" + keyValue + "'" : keyValue;

            updateGenerated("DELETE FROM " + mapper.tableName + " WHERE " + mapper.primaryKeyFieldName + " = " + sqlKeyValue);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }



    private static<T> CompletableFuture<ObservableList<T>> listAllInternal(String sql, ObjectMapper<T> mapper) {
        if(LOG_SQL_QUERIES) {
            System.out.println("Listing with generated query: \"" + sql + "\"");
        }

        return CompletableFuture.supplyAsync(() ->
            DBUtils.useStatement(statement -> {
                var result = new ArrayList<T>();

                try(var resultSet = statement.executeQuery(sql)) {
                    while(resultSet.next()) {
                        result.add(ObjectMapperUtils.createInstance(resultSet, mapper));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Components.showErrorDialog("SQL Hiba történt!");
                }

                return FXCollections.observableArrayList(result);
            }));
    }

    public static<T> CompletableFuture<ObservableList<T>> list(String sql, Class<T> resultType) {
        if(LOG_SQL_QUERIES) {
            System.out.println("Listing with builtin query: \"" + sql + "\"");
        }

        return CompletableFuture.supplyAsync(() ->
            DBUtils.useStatement(statement -> {
                var result = new ArrayList<T>();

                try(var resultSet = statement.executeQuery(sql)) {
                    while(resultSet.next()) {
                        result.add(ObjectMapperUtils.createInstance(resultSet, ObjectMapperUtils.createMapper(resultType)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Components.showErrorDialog("SQL Hiba történt!");
                }

                return FXCollections.observableArrayList(result);
            }));
    }

    private static void updateGenerated(String sql) {
        if(LOG_SQL_QUERIES) {
            System.out.println("Updating with generated query: \"" + sql + "\"");
        }

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

    public static void update(String sql) {
        if(LOG_SQL_QUERIES) {
            System.out.println("Updating with builtin query: \"" + sql + "\"");
        }

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

    // TODO: Handle not builtin object primary key extraction
    private static String formatObjectForSQL(Object obj) {
        var type = obj.getClass();

        return type == String.class ? "'" + obj + "'" :
               type == boolean.class ? ((boolean) obj ? "1" : "0") : String.valueOf(obj);
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

    private DBUtils() {}
}