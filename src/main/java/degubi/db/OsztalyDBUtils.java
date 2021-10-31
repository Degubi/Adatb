package degubi.db;

import degubi.model.*;
import java.util.concurrent.*;
import javafx.collections.*;

public final class OsztalyDBUtils {
    public static final String TABLE = "osztaly";

    public static CompletableFuture<ObservableList<Osztaly>> listAll() {
        return DBUtils.list("SELECT * FROM " + TABLE, Osztaly::new);
    }

    public static CompletableFuture<ObservableList<Osztaly>> listFiltered(String field, String value) {
        return DBUtils.list(String.format("SELECT * FROM " + TABLE + " WHERE %s LIKE '%%%s%%'", field, value), Osztaly::new);
    }

    public static void add(String megnevezes) {
        DBUtils.update(String.format("INSERT INTO " + TABLE + " VALUES(NULL, '%s')", megnevezes));
    }

    private OsztalyDBUtils() {}
}