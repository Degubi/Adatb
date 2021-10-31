package degubi.db;

import degubi.gui.*;
import degubi.model.*;
import java.sql.*;
import java.util.concurrent.*;
import javafx.collections.*;

public final class TanarDBUtils {
    public static final String TABLE = "tanar";

    public static CompletableFuture<ObservableList<Tanar>> listAll() {
        return DBUtils.list("SELECT " + TABLE + ".*, " + KepzettsegDBUtils.TABLE + ".* FROM `" + TABLE +
                            "` INNER JOIN " + KepzettsegDBUtils.TABLE + " ON " + TABLE + ".kepzettseg_azonosito = " + KepzettsegDBUtils.TABLE + ".azonosito", Tanar::new);
    }

    public static CompletableFuture<ObservableList<Tanar>> listFiltered(String field, String value) {
        return DBUtils.list(String.format("SELECT * FROM " + TABLE + " WHERE %s LIKE '%%%s%%'", field, value), Tanar::new);
    }

    @SuppressWarnings("boxing")
    public static void add(String szemelyi, String nev, int kepzettsegAzonosito) {
        var sql = String.format("INSERT INTO " + TABLE + " VALUES('%s', '%s', %d)", szemelyi, nev, kepzettsegAzonosito);

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

    private TanarDBUtils() {}
}