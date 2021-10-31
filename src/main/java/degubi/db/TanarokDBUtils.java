package degubi.db;

import degubi.gui.*;
import degubi.model.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import javafx.collections.*;

public final class TanarokDBUtils {
    private static final String TABLE = "Teachers";

    public static CompletableFuture<ObservableList<Tanar>> listAll() {
        return list("SELECT * FROM " + TABLE);
    }

    public static CompletableFuture<ObservableList<Tanar>> listFiltered(String field, String value) {
        return list(String.format("SELECT * FROM " + TABLE + " WHERE %s LIKE '%%%s%%'", field, value));
    }

    public static void add(String szemelyi, String nev, String kepzettseg) {
        var sql = String.format("INSERT INTO " + TABLE + " VALUES('%s', '%s', '%s')", szemelyi, nev, kepzettseg);

        DBUtils.useConnection(connection -> {
            try(var statement = connection.createStatement()) {
                statement.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
                Components.showErrorDialog("SQL Hiba történt!");
            }
        });
    }

    public static void delete(String szemelyi) {
        var sql = "DELETE FROM " + TABLE + " WHERE szemelyiSzam = '" + szemelyi + "'";

        DBUtils.useConnection(connection -> {
            try(var statement = connection.createStatement()) {
                statement.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
                Components.showErrorDialog("SQL Hiba történt!");
            }
        });
    }


    private static CompletableFuture<ObservableList<Tanar>> list(String sql) {
        return CompletableFuture.supplyAsync(() -> {
            var result = new ArrayList<Tanar>();

            DBUtils.useConnection(connection -> {
                try(var resultSet = connection.createStatement().executeQuery(sql)) {
                    while(resultSet.next()) {
                        result.add(new Tanar(resultSet.getString("szemelyiSzam"), resultSet.getString("nev"), resultSet.getString("kepzettseg")));
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    Components.showErrorDialog("SQL Hiba történt!");
                }
            });

            return FXCollections.observableArrayList(result);
        });
    }
}