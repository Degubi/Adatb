package degubi.db;

import degubi.model.*;
import java.util.concurrent.*;
import javafx.collections.*;

public final class DiakDBUtils {
    public static final String TABLE = "diak";

    private static final String SELECT_ALL_QUERY = "SELECT " + TABLE + ".*, " + OsztalyDBUtils.TABLE + ".* FROM " + TABLE +
                                                   " INNER JOIN " + OsztalyDBUtils.TABLE +
                                                   " ON " + TABLE + ".osztalyAzonosito = " + OsztalyDBUtils.TABLE + ".azonosito";

    public static CompletableFuture<ObservableList<Diak>> listFiltered(String field, String value) {
        var tableToFilterIn = field.equals("osztalyMegnevezes") ? OsztalyDBUtils.TABLE : TABLE;
        var fieldToCheck = field.equals("osztalyMegnevezes") ? "megnevezes" : field;

        return DBUtils.list(String.format(SELECT_ALL_QUERY + " WHERE " + tableToFilterIn + ".%s LIKE '%%%s%%'", fieldToCheck, value), Diak.class);
    }

    @SuppressWarnings("boxing")
    public static void add(String neptunKod, Osztaly osztaly, String nev) {
        DBUtils.update(String.format("INSERT INTO " + TABLE + " VALUES('%s', %d, '%s')", neptunKod, osztaly.azonosito, nev));
    }

    @SuppressWarnings("boxing")
    public static void update(Diak diak, Osztaly osztaly, String nev) {
        var toUpdate = String.format("osztalyAzonosito = %d, nev = %s", osztaly.azonosito, nev);

        DBUtils.update("UPDATE " + TABLE + " SET " + toUpdate + " WHERE neptunKod = '" + diak.neptunKod + "'");
    }

    private DiakDBUtils() {}
}