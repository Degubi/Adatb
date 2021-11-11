package degubi.db;

import degubi.gui.*;
import degubi.mapping.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
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
        var mapper = ObjectMapper.createMapper(resultType);

        return listAllInternal(mapper.listAllQuery, mapper);
    }

    public static<T> CompletableFuture<ObservableList<T>> listAllOrderedBy(String orderByField, Class<T> resultType) {
        var mapper = ObjectMapper.createMapper(resultType);

        return listAllInternal(mapper.listAllQuery + " ORDER BY " + orderByField + " ASC", mapper);
    }

    public static<T> CompletableFuture<ObservableList<T>> listFiltered(String field, String value, Class<T> resultType) {
        var mapper = ObjectMapper.createMapper(resultType);

        return listAllInternal(mapper.listAllQuery + " WHERE " + field + " LIKE '%" + value + "%'", mapper);
    }


    public static void delete(Object toDelete) {
        var mapper = ObjectMapper.createMapper(toDelete.getClass());
        var keyField = mapper.primaryKeyField;

        try {
            var keyValue = keyField.get(toDelete);
            var sqlKeyValue = keyField.getType() == String.class ? "'" + keyValue + "'" : keyValue;

            DBUtils.update("DELETE FROM " + mapper.tableName + " WHERE " + mapper.primaryKeyFieldName + " = " + sqlKeyValue);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }



    private static<T> CompletableFuture<ObservableList<T>> listAllInternal(String sql, MappingReflectionResult<T> mapper) {
        if(LOG_SQL_QUERIES) {
            System.out.println("Listing with generated query: \"" + sql + "\"");
        }

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
                        result.add(ObjectMapper.createInstance(resultSet, ObjectMapper.createMapper(resultType)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Components.showErrorDialog("SQL Hiba történt!");
                }

                return FXCollections.observableArrayList(result);
            }));
    }

    public static void update(String sql) {
        if(LOG_SQL_QUERIES) {
            System.out.println("Updating with query: \"" + sql + "\"");
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

    private DBUtils() {}
}