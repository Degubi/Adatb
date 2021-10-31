package degubi.db;

import degubi.model.*;
import java.util.concurrent.*;
import javafx.collections.*;

public final class TanarDBUtils {
    public static final String TABLE = "tanar";

    public static CompletableFuture<ObservableList<Tanar>> listAll() {
        return DBUtils.list("SELECT " + TABLE + ".*, " + KepzettsegDBUtils.TABLE + ".* FROM `" + TABLE +
                            "` INNER JOIN " + KepzettsegDBUtils.TABLE + " ON " + TABLE + ".kepzettsegAzonosito = " + KepzettsegDBUtils.TABLE + ".azonosito", Tanar::new);
    }

    public static CompletableFuture<ObservableList<Tanar>> listFiltered(String field, String value) {
        return DBUtils.list(String.format("SELECT * FROM " + TABLE + " WHERE %s LIKE '%%%s%%'", field, value), Tanar::new);
    }

    @SuppressWarnings("boxing")
    public static void add(String szemelyi, String nev, int kepzettsegAzonosito) {
        DBUtils.update(String.format("INSERT INTO " + TABLE + " VALUES('%s', '%s', %d)", szemelyi, nev, kepzettsegAzonosito));
    }

    public static void delete(String szemelyi) {
        DBUtils.update("DELETE FROM " + TABLE + " WHERE szemelyiSzam = '" + szemelyi + "'");
    }

    private TanarDBUtils() {}
}