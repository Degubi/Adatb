package degubi.db;

import degubi.model.*;
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
        DBUtils.update(String.format("INSERT INTO " + TABLE + " VALUES(NULL, '%s')", megnevezes));
    }

    private KepzettsegDBUtils() {}
}