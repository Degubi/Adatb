package degubi.db;

import degubi.gui.*;
import degubi.mapping.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import javafx.collections.*;

final class DBUtils {
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

    public static<T> CompletableFuture<ObservableList<T>> list(String sql, Class<T> resultType) {
        if(LOG_SQL_QUERIES) {
            System.out.println("Listing with query: \"" + sql + "\"");
        }

        return CompletableFuture.supplyAsync(() ->
            DBUtils.useStatement(statement -> {
                var result = new ArrayList<T>();

                try(var resultSet = statement.executeQuery(sql)) {
                    while(resultSet.next()) {
                        result.add(ObjectMapper.createInstance(resultSet, resultType));
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