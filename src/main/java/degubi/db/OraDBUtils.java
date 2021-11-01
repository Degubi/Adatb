package degubi.db;

import degubi.model.*;
import java.util.concurrent.*;
import javafx.collections.*;

public final class OraDBUtils {
    public static final String TABLE = "ora";

    private static final String SELECT_ALL_QUERY = "SELECT " + TABLE + ".*, " +
                                                       OsztalyDBUtils.TABLE + ".*, " +
                                                       TeremDBUtils.TABLE + ".*, " +
                                                       TanarDBUtils.TABLE + ".szemelyiSzam, " + TanarDBUtils.TABLE + ".nev" +
                                                   " FROM " + TABLE +
                                                   " INNER JOIN " + OsztalyDBUtils.TABLE +
                                                       " ON " + TABLE + ".osztalyAzonosito = " + OsztalyDBUtils.TABLE + ".azonosito" +
                                                   " INNER JOIN " + TeremDBUtils.TABLE +
                                                       " ON " + TABLE + ".teremAzonosito = " + TeremDBUtils.TABLE + ".azonosito" +
                                                   " INNER JOIN " + TanarDBUtils.TABLE +
                                                       " ON " + TABLE + ".tanarSzemelyiSzam = " + TanarDBUtils.TABLE + ".szemelyiSzam";

    public static CompletableFuture<ObservableList<Ora>> listAll() {
        return DBUtils.list(SELECT_ALL_QUERY, Ora::new);
    }

    public static CompletableFuture<ObservableList<Ora>> listFiltered(String field, String value) {
        var tableToFilterIn = field.equals("osztaly") ? OsztalyDBUtils.TABLE :
                              field.equals("terem") ? TeremDBUtils.TABLE :
                              field.equals("tanar") ? TanarDBUtils.TABLE : TABLE;
        var fieldToCheck = field.equals("osztaly") ? "megnevezes" :
                           field.equals("terem") ? "azonosito" :
                           field.equals("tanar") ? "nev" : field;

        return DBUtils.list(String.format(SELECT_ALL_QUERY + " WHERE " + tableToFilterIn + ".%s LIKE '%%%s%%'", fieldToCheck, value), Ora::new);
    }

    @SuppressWarnings("boxing")
    public static void add(int napIndex, String idopont, String nev, String tanarSzemelyiSzam, int osztalyAzonosito, int teremAzonosito) {
        DBUtils.update(String.format("INSERT INTO " + TABLE + " VALUES(%d, '%s', '%s', '%s', %d, %d)", napIndex, idopont, nev, tanarSzemelyiSzam, osztalyAzonosito, teremAzonosito));
    }
}