package degubi.db;

import degubi.gui.*;
import degubi.model.*;
import java.sql.*;
import java.util.concurrent.*;
import javafx.collections.*;

public final class TeremDBUtils {
    public static final String TABLE = "terem";

    public static CompletableFuture<ObservableList<Terem>> listAll() {
        return DBUtils.list("SELECT * FROM " + TABLE, Terem::new);
    }

    public static CompletableFuture<ObservableList<Terem>> listFiltered(String field, String value) {
        return DBUtils.list(String.format("SELECT * FROM " + TABLE + " WHERE %s LIKE '%%%s%%'", field, value), Terem::new);
    }

    @SuppressWarnings("boxing")
    public static void add(String azonosito, String ferohelyekSzama, String epulet, boolean vanEProjektor) {
        var sql = String.format("INSERT INTO " + TABLE + " VALUES(%s, %s, %s, %d)", azonosito, ferohelyekSzama, epulet, vanEProjektor ? 1 : 0);

        DBUtils.useConnection(connection -> {
            try(var statement = connection.createStatement()) {
                statement.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
                Components.showErrorDialog("SQL Hiba történt!");
            }
        });
    }

    private TeremDBUtils() {}
}