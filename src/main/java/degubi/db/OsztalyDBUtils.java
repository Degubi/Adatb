package degubi.db;

import degubi.model.*;
import java.util.concurrent.*;
import javafx.collections.*;

public final class OsztalyDBUtils {
    public static final String TABLE = "osztaly";

    private static final String SELECT_ALL_QUERY = "SELECT * FROM " + TABLE;

    public static CompletableFuture<ObservableList<Osztaly>> listAll() {
        return DBUtils.list(SELECT_ALL_QUERY, Osztaly::new);
    }

    public static CompletableFuture<ObservableList<Osztaly>> listAllSortedByName() {
        return DBUtils.list(SELECT_ALL_QUERY + " ORDER BY megnevezes ASC", Osztaly::new);
    }

    public static CompletableFuture<ObservableList<Osztaly>> listFiltered(String field, String value) {
        return DBUtils.list(String.format(SELECT_ALL_QUERY + " WHERE %s LIKE '%%%s%%'", field, value), Osztaly::new);
    }

    public static void add(String megnevezes) {
        DBUtils.update(String.format("INSERT INTO " + TABLE + " VALUES(NULL, '%s')", megnevezes));
    }

    public static void update(Osztaly osztaly, String megnevezes) {
        DBUtils.update("UPDATE " + TABLE + " SET megnevezes = '" + megnevezes + "' WHERE azonosito = " + osztaly.azonosito);
    }

    public static void delete(Osztaly osztaly) {
        DBUtils.update("DELETE FROM " + TABLE + " WHERE azonosito = " + osztaly.azonosito);
    }

    private OsztalyDBUtils() {}
}