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
    public static void add(String teremSzam, String epuletSzam, String ferohelyekSzama, boolean vanEProjektor) {
        DBUtils.update(String.format("INSERT INTO " + TABLE + " VALUES(NULL, %s, %s, %s, %d)", teremSzam, epuletSzam, ferohelyekSzama, vanEProjektor ? 1 : 0));
    }

    public static void delete(Terem terem) {
        DBUtils.update("DELETE FROM " + TABLE + " WHERE azonosito = " + terem.azonosito);
    }

    private TeremDBUtils() {}
}