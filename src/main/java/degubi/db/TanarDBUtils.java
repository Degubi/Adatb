package degubi.db;

import degubi.model.*;
import java.util.concurrent.*;
import javafx.collections.*;

public final class TanarDBUtils {
    public static final String TABLE = "tanar";

    private static final String SELECT_ALL_QUERY = "SELECT " + TABLE + ".*, " + TableNames.KEPZETTSEG + ".* FROM " + TABLE +
                                                   " INNER JOIN " + TableNames.KEPZETTSEG +
                                                   " ON " + TABLE + ".kepzettsegAzonosito = " + TableNames.KEPZETTSEG + ".azonosito";

    public static CompletableFuture<ObservableList<Tanar>> listFiltered(String field, String value) {
        var tableToFilterIn = field.equals("kepzettseg") ? TableNames.KEPZETTSEG : TABLE;
        var fieldToCheck = field.equals("kepzettseg") ? "megnevezes" : field;

        return DBUtils.list(String.format(SELECT_ALL_QUERY + " WHERE " + tableToFilterIn + ".%s LIKE '%%%s%%'", fieldToCheck, value), Tanar.class);
    }

    @SuppressWarnings("boxing")
    public static void add(String szemelyi, String nev, Kepzettseg kepzettseg) {
        DBUtils.update(String.format("INSERT INTO " + TABLE + " VALUES('%s', '%s', %d)", szemelyi, nev, kepzettseg.azonosito));
    }

    @SuppressWarnings("boxing")
    public static void update(Tanar tanar, String nev, Kepzettseg kepzettseg) {
        var toUpdate = String.format("nev = '%s', kepzettsegAzonosito = %d", nev, kepzettseg.azonosito);

        DBUtils.update("UPDATE " + TABLE + " SET " + toUpdate + " WHERE szemelyiSzam = '" + tanar.szemelyiSzam + "'");
    }

    private TanarDBUtils() {}
}