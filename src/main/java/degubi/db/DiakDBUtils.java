package degubi.db;

import degubi.model.*;
import java.util.concurrent.*;
import javafx.collections.*;

public final class DiakDBUtils {
    public static final String TABLE = "diak";

    public static CompletableFuture<ObservableList<Diak>> listAll() {
        return DBUtils.list("SELECT " + TABLE + ".*, " + OsztalyDBUtils.TABLE + ".* FROM " + TABLE +
                            " INNER JOIN " + OsztalyDBUtils.TABLE + " ON " + TABLE + ".osztalyAzonosito = " + OsztalyDBUtils.TABLE + ".azonosito", Diak::new);
    }

    public static CompletableFuture<ObservableList<Diak>> listFiltered(String field, String value) {
        return DBUtils.list(String.format("SELECT * FROM " + TABLE + " WHERE %s LIKE '%%%s%%'", field, value), Diak::new);
    }

    @SuppressWarnings("boxing")
    public static void add(String neptunKod, int osztalyAzonosito, String nev) {
        DBUtils.update(String.format("INSERT INTO " + TABLE + " VALUES('%s', %d, '%s')", neptunKod, osztalyAzonosito, nev));
    }

    public static void delete(String neptunKod) {
        DBUtils.update("DELETE FROM " + TABLE + " WHERE neptunKod = '" + neptunKod + "'");
    }

    private DiakDBUtils() {}
}