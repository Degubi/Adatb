package degubi.db;

import degubi.gui.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import javafx.collections.*;

final class DBUtils {

    public static void useConnection(Consumer<Connection> connectionConsumer) {
        try(var conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "test", "test123")) {
            connectionConsumer.accept(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            Components.showErrorDialog("Nem sikerült csatlakozni a szerverhez!");
        }
    }

    @SuppressWarnings("resource")
    public static<T> CompletableFuture<ObservableList<T>> list(String sql, ItemCreator<T> resultElementCreator) {
        return CompletableFuture.supplyAsync(() -> {
            var result = new ArrayList<T>();

            DBUtils.useConnection(connection -> {
                try(var resultSet = connection.createStatement().executeQuery(sql)) {
                    while(resultSet.next()) {
                        result.add(resultElementCreator.createFrom(resultSet));
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    Components.showErrorDialog("SQL Hiba történt!");
                }
            });

            return FXCollections.observableArrayList(result);
        });
    }

    private DBUtils() {}

    @FunctionalInterface
    public static interface ItemCreator<T> {
        T createFrom(ResultSet res) throws SQLException;
    }
}