package degubi.db;

import degubi.model.*;
import java.util.concurrent.*;
import javafx.collections.*;

public final class DiakDBUtils {
    public static final String TABLE = "diak";

    private static final String SELECT_ALL_QUERY = "SELECT " + TABLE + ".*, " + OsztalyDBUtils.TABLE + ".* FROM " + TABLE +
                                                   " INNER JOIN " + OsztalyDBUtils.TABLE +
                                                   " ON " + TABLE + ".osztalyAzonosito = " + OsztalyDBUtils.TABLE + ".azonosito";

    public static CompletableFuture<ObservableList<Diak>> listAll() {
        return DBUtils.list(SELECT_ALL_QUERY, Diak::new);
    }

    public static CompletableFuture<ObservableList<Diak>> listFiltered(String field, String value) {
        var tableToFilterIn = field.equals("osztalyMegnevezes") ? OsztalyDBUtils.TABLE : TABLE;
        var fieldToCheck = field.equals("osztalyMegnevezes") ? "megnevezes" : field;

        return DBUtils.list(String.format(SELECT_ALL_QUERY + " WHERE " + tableToFilterIn + ".%s LIKE '%%%s%%'", fieldToCheck, value), Diak::new);
    }

    @SuppressWarnings("boxing")
    public static void add(String neptunKod, int osztalyAzonosito, String nev) {
        DBUtils.update(String.format("INSERT INTO " + TABLE + " VALUES('%s', %d, '%s')", neptunKod, osztalyAzonosito, nev));
    }

    public static void delete(Diak diak) {
        DBUtils.update("DELETE FROM " + TABLE + " WHERE neptunKod = '" + diak.neptunKod + "'");
    }

    private DiakDBUtils() {}
}