package degubi.db;

import degubi.model.*;
import java.util.concurrent.*;
import javafx.collections.*;

public final class KepzettsegDBUtils {
    public static final String TABLE = "kepzettseg";

    private static final String SELECT_ALL_QUERY = "SELECT * FROM " + TABLE;

    public static CompletableFuture<ObservableList<Kepzettseg>> listAll() {
        return DBUtils.list(SELECT_ALL_QUERY, Kepzettseg.class);
    }

    public static CompletableFuture<ObservableList<Kepzettseg>> listFiltered(String field, String value) {
        return DBUtils.list(String.format(SELECT_ALL_QUERY + " WHERE %s LIKE '%%%s%%'", field, value), Kepzettseg.class);
    }

    public static void add(String megnevezes) {
        DBUtils.update(String.format("INSERT INTO " + TABLE + " VALUES(NULL, '%s')", megnevezes));
    }

    public static void update(Kepzettseg kepzettseg, String megnevezes) {
        DBUtils.update("UPDATE " + TABLE + " SET megnevezes = '" + megnevezes + "' WHERE azonosito = " + kepzettseg.azonosito);
    }

    public static void delete(Kepzettseg kepzettseg) {
        DBUtils.update("DELETE FROM " + TABLE + " WHERE azonosito = " + kepzettseg.azonosito);
    }

    private KepzettsegDBUtils() {}
}