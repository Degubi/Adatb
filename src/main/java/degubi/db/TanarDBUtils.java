package degubi.db;

import degubi.model.*;
import java.util.concurrent.*;
import javafx.collections.*;

public final class TanarDBUtils {
    public static final String TABLE = "tanar";

    private static final String SELECT_ALL_QUERY = "SELECT " + TABLE + ".*, " + KepzettsegDBUtils.TABLE + ".* FROM " + TABLE +
                                                   " INNER JOIN " + KepzettsegDBUtils.TABLE +
                                                   " ON " + TABLE + ".kepzettsegAzonosito = " + KepzettsegDBUtils.TABLE + ".azonosito";

    public static CompletableFuture<ObservableList<Tanar>> listAll() {
        return DBUtils.list(SELECT_ALL_QUERY, Tanar::new);
    }

    public static CompletableFuture<ObservableList<Tanar>> listFiltered(String field, String value) {
        var tableToFilterIn = field.equals("kepzettseg") ? KepzettsegDBUtils.TABLE : TABLE;
        var fieldToCheck = field.equals("kepzettseg") ? "megnevezes" : field;

        return DBUtils.list(String.format(SELECT_ALL_QUERY + " WHERE " + tableToFilterIn + ".%s LIKE '%%%s%%'", fieldToCheck, value), Tanar::new);
    }

    @SuppressWarnings("boxing")
    public static void add(String szemelyi, String nev, Kepzettseg kepzettseg) {
        DBUtils.update(String.format("INSERT INTO " + TABLE + " VALUES('%s', '%s', %d)", szemelyi, nev, kepzettseg.azonosito));
    }

    @SuppressWarnings("boxing")
    public static void update(Tanar tanar, String nev, Kepzettseg kepzettseg) {
        var toUpdate = String.format("nev = %s, kepzettsegAzonosito = %d", nev, kepzettseg.azonosito);

        DBUtils.update("UPDATE " + TABLE + " SET " + toUpdate + " WHERE szemelyiSzam = " + tanar.szemelyiSzam);
    }

    public static void delete(Tanar tanar) {
        DBUtils.update("DELETE FROM " + TABLE + " WHERE szemelyiSzam = '" + tanar.szemelyiSzam + "'");
    }

    private TanarDBUtils() {}
}