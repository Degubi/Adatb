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

    public static CompletableFuture<ObservableList<Osztaly>> listFiltered(String field, String value) {
        return DBUtils.list(String.format(SELECT_ALL_QUERY + " WHERE %s LIKE '%%%s%%'", field, value), Osztaly::new);
    }

    public static void add(String megnevezes) {
        DBUtils.update(String.format("INSERT INTO " + TABLE + " VALUES(NULL, '%s')", megnevezes));
    }

    private OsztalyDBUtils() {}
}