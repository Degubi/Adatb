package degubi.db;

import degubi.model.*;
import java.util.concurrent.*;
import javafx.collections.*;

public final class TantargyDBUtils {
    public static final String TABLE = "tantargy";

    private static final String SELECT_ALL_QUERY = "SELECT * FROM " + TABLE;

    public static CompletableFuture<ObservableList<Tantargy>> listAll() {
        return DBUtils.list(SELECT_ALL_QUERY, Tantargy.class);
    }

    public static CompletableFuture<ObservableList<Tantargy>> listFiltered(String field, String value) {
        return DBUtils.list(String.format(SELECT_ALL_QUERY + " WHERE %s LIKE '%%%s%%'", field, value), Tantargy.class);
    }

    public static void add(String nev) {
        DBUtils.update(String.format("INSERT INTO " + TABLE + " VALUES(NULL, '%s')", nev));
    }

    public static void update(Tantargy targy, String nev) {
        DBUtils.update("UPDATE " + TABLE + " SET nev = '" + nev + "' WHERE azonosito = " + targy.azonosito);
    }

    public static void delete(Tantargy targy) {
        DBUtils.update("DELETE FROM " + TABLE + " WHERE azonosito = " + targy.azonosito);
    }

    private TantargyDBUtils() {}
}