package degubi.db;

import degubi.model.*;
import java.util.concurrent.*;
import javafx.collections.*;

public final class TeremDBUtils {
    public static final String TABLE = "terem";

    private static final String SELECT_ALL_QUERY = "SELECT * FROM " + TABLE;

    public static CompletableFuture<ObservableList<Terem>> listAll() {
        return DBUtils.list(SELECT_ALL_QUERY, Terem::new);
    }

    public static CompletableFuture<ObservableList<Terem>> listFiltered(String field, String value) {
        return DBUtils.list(String.format(SELECT_ALL_QUERY + " WHERE %s LIKE '%%%s%%'", field, value), Terem::new);
    }

    @SuppressWarnings("boxing")
    public static void add(String azonosito, String ferohelyekSzama, String epulet, boolean vanEProjektor) {
        DBUtils.update(String.format("INSERT INTO " + TABLE + " VALUES(%s, %s, %s, %d)", azonosito, ferohelyekSzama, epulet, vanEProjektor ? 1 : 0));
    }

    private TeremDBUtils() {}
}