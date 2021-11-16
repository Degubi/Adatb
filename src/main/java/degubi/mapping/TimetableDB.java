package degubi.mapping;

import degubi.gui.*;
import degubi.model.*;
import degubi.model.stat.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;
import javafx.collections.*;

public final class TimetableDB {
    private static final boolean LOG_SQL_QUERIES = true;
    private static final boolean SIDEEFFECTS_ENABLED = false;

    private static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/timetable";
    private static final String DB_USER = "timetable";
    private static final String DB_PW = "gimmecookies";


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
                                    .mapToObj(i -> mapper.isPrimaryKeyAutoincremented && fieldNames[i].equals(mapper.primaryKeyFieldName) ? "NULL" : formatObjectForSQL(valuesMap.get(fieldNames[i])))
                                    .collect(Collectors.joining(", "));

        updateGenerated("INSERT INTO " + mapper.tableName +
                        " (" + String.join(", ", mapper.dbFieldNames) +
                        ") VALUES (" + valuesString + ")");
    }

    @SuppressWarnings("unchecked")
    public static<T> void update(T oldObj, T newObj) {
        var oldMapper = ObjectMapper.createMapper((Class<T>) oldObj.getClass());
        var newMapper = ObjectMapper.createMapper((Class<T>) newObj.getClass());
        var primaryKeyFieldName = newMapper.primaryKeyFieldName;
        var parameterFieldNames = newMapper.parameterFieldNames;
        var dbFieldNames = newMapper.dbFieldNames;

        var oldValues = ObjectMapper.createFieldValuesMap(oldMapper, oldObj);
        var newValues = ObjectMapper.createFieldValuesMap(newMapper, newObj);
        var setPartString = IntStream.range(0, parameterFieldNames.length)
                                  .filter(i -> !parameterFieldNames[i].equals(primaryKeyFieldName))
                                  .mapToObj(i -> dbFieldNames[i] + " = " + formatObjectForSQL(newValues.get(parameterFieldNames[i])))
                                  .collect(Collectors.joining(", "));

        updateGenerated("UPDATE " + newMapper.tableName +
                        " SET " + setPartString +
                        " WHERE " + primaryKeyFieldName + " = " + formatObjectForSQL(oldValues.get(primaryKeyFieldName)));
    }

    public static void delete(Object toDelete) {
        var mapper = ObjectMapper.createMapper(toDelete.getClass());
        var keyField = mapper.primaryKeyField;

        try {
            var keyValue = keyField.get(toDelete);
            var sqlKeyValue = keyField.getType() == String.class ? "'" + keyValue + "'" : keyValue;

            updateGenerated("DELETE FROM " + mapper.tableName + " WHERE " + mapper.primaryKeyFieldName + " = " + sqlKeyValue);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    public static CompletableFuture<ObservableList<TantargyFrequencyStat>> getTantargyFrequencyMap() {
        var query = "SELECT COUNT(" + TableNames.ORA + ".tantargyAzonosito) as Frequency, " + TableNames.TANTARGY + ".*" +
                    " FROM " + TableNames.TANTARGY +
                    " LEFT JOIN " + TableNames.ORA + " ON " + TableNames.ORA + ".tantargyAzonosito = " + TableNames.TANTARGY + ".azonosito" +
                    " GROUP BY " + TableNames.TANTARGY + ".azonosito" +
                    " ORDER BY Frequency DESC";

        return listCustom(query, TantargyFrequencyStat.class);
    }

    public static CompletableFuture<ObservableList<Diak>> listFilteredDiak(String field, String value) {
        var tableToFilterIn = field.equals("osztalyMegnevezes") ? TableNames.OSZTALY : TableNames.DIAK;
        var fieldToCheck = field.equals("osztalyMegnevezes") ? "megnevezes" : field;

        return listCustom(ObjectMapper.createMapper(Diak.class).listAllQuery +
                          " WHERE " + tableToFilterIn + "." + fieldToCheck + " LIKE '%" + value + "%'", Diak.class);
    }

    public static CompletableFuture<ObservableList<Tanar>> listFilteredTanar(String field, String value) {
        var tableToFilterIn = field.equals("kepzettseg") ? TableNames.KEPZETTSEG : TableNames.TANAR;
        var fieldToCheck = field.equals("kepzettseg") ? "megnevezes" : field;

        return listCustom(ObjectMapper.createMapper(Tanar.class).listAllQuery +
                          " WHERE " + tableToFilterIn + "." + fieldToCheck + " LIKE '%" + value + "%'", Tanar.class);
    }

    public static CompletableFuture<ObservableList<Ora>> listFilteredOra(String field, String value) {
        var tableToFilterIn = field.equals("osztaly") ? TableNames.OSZTALY :
                              field.equals("terem") ? TableNames.TEREM :
                              field.equals("targy") ? TableNames.TANTARGY :
                              field.equals("tanar") ? TableNames.TANAR : TableNames.ORA;
        var fieldToCheck = field.equals("osztaly") ? "megnevezes" :
                           field.equals("terem") ? "teremSzam" :
                           field.equals("targy") ? "nev" :
                           field.equals("tanar") ? "nev" : field;

        return listCustom(ObjectMapper.createMapper(Ora.class).listAllQuery +
                          " WHERE " + tableToFilterIn + "." + fieldToCheck + " LIKE '%" + value + "%'", Ora.class);
    }

    public static CompletableFuture<ObservableList<Ora>> listOraFor(Tanar tanar) {
        return listCustom(ObjectMapper.createMapper(Ora.class).listAllQuery +
                " WHERE tanarSzemelyiSzam = " + formatObjectForSQL(tanar.szemelyiSzam) +
                " ORDER BY idopont ASC", Ora.class);
    }

    @SuppressWarnings("boxing")
    public static CompletableFuture<ObservableList<Ora>> listOraFor(Osztaly osztaly) {
        return listCustom(ObjectMapper.createMapper(Ora.class).listAllQuery +
                          " WHERE osztalyAzonosito = " + formatObjectForSQL(osztaly.azonosito) +
                          " ORDER BY idopont ASC", Ora.class);
    }


    private static String formatObjectForSQL(Object obj) {
        var type = obj.getClass();

        return type == String.class ? "'" + obj + "'" :
               type == Boolean.class ? (((Boolean) obj).booleanValue() ? "1" : "0") :
               type == Integer.class || type == Long.class ? String.valueOf(obj) : getPrimaryKeyFromNestedObject(type, obj);
    }

    private static String getPrimaryKeyFromNestedObject(Class<?> type, Object obj) {
        var mapper = ObjectMapper.createMapper(type);

        try {
            return formatObjectForSQL(mapper.primaryKeyField.get(obj));
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
            throw new IllegalStateException("Wut");
        }
    }

    private static<T> T useStatement(Function<Statement, T> connectionConsumer) {
        try(var conn = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PW);
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
            useStatement(statement -> {
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
        if(SIDEEFFECTS_ENABLED) {
            useStatement(statement -> {
                try {
                    statement.executeUpdate(sql);
                }catch (Exception e) {
                    e.printStackTrace();
                    Components.showErrorDialog("SQL Hiba történt! Hiba: \n" + e.getMessage());
                }

                return null;
            });
        }
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

    private static<T> CompletableFuture<ObservableList<T>> listCustom(String sql, Class<T> resultType) {
        if(LOG_SQL_QUERIES) {
            System.out.println("Listing with custom query: \"" + sql + "\"");
        }

        return listInternal(sql, ObjectMapper.createMapper(resultType));
    }

    private TimetableDB() {}
}