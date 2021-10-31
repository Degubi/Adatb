package degubi.db;

import degubi.gui.*;
import degubi.model.*;
import java.sql.*;
import java.util.concurrent.*;
import javafx.collections.*;

public final class KepzettsegDBUtils {
    public static final String TABLE = "kepzettseg";

    public static CompletableFuture<ObservableList<Kepzettseg>> listAll() {
        return DBUtils.list("SELECT * FROM " + TABLE, Kepzettseg::new);
    }

    public static CompletableFuture<ObservableList<Kepzettseg>> listFiltered(String field, String value) {
        return DBUtils.list(String.format("SELECT * FROM " + TABLE + " WHERE %s LIKE '%%%s%%'", field, value), Kepzettseg::new);
    }

    public static void add(String megnevezes) {
        var sql = String.format("INSERT INTO " + TABLE + " VALUES(NULL, '%s')", megnevezes);

        DBUtils.useConnection(connection -> {
            try(var statement = connection.createStatement()) {
                statement.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
                Components.showErrorDialog("SQL Hiba történt!");
            }
        });
    }

    private KepzettsegDBUtils() {}
}