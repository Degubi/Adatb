package degubi.db;

import degubi.model.*;
import java.util.concurrent.*;
import javafx.collections.*;

public final class OraDBUtils {
    public static final String TABLE = "ora";

    static final String SELECT_ALL_QUERY = "SELECT " + TABLE + ".*, " +
                                               OsztalyDBUtils.TABLE + ".*, " +
                                               TeremDBUtils.TABLE + ".*, " +
                                               TableNames.TANTAGY + ".*, " +
                                               TanarDBUtils.TABLE + ".szemelyiSzam, " + TanarDBUtils.TABLE + ".nev" +
                                           " FROM " + TABLE +
                                           " INNER JOIN " + OsztalyDBUtils.TABLE +
                                               " ON " + TABLE + ".osztalyAzonosito = " + OsztalyDBUtils.TABLE + ".azonosito" +
                                           " INNER JOIN " + TeremDBUtils.TABLE +
                                               " ON " + TABLE + ".teremAzonosito = " + TeremDBUtils.TABLE + ".azonosito" +
                                           " INNER JOIN " + TanarDBUtils.TABLE +
                                               " ON " + TABLE + ".tanarSzemelyiSzam = " + TanarDBUtils.TABLE + ".szemelyiSzam" +
                                           " INNER JOIN " + TableNames.TANTAGY +
                                               " ON " + TABLE + ".tantargyAzonosito  = " + TableNames.TANTAGY + ".azonosito";

    public static CompletableFuture<ObservableList<Ora>> listFiltered(String field, String value) {
        var tableToFilterIn = field.equals("osztaly") ? OsztalyDBUtils.TABLE :
                              field.equals("terem") ? TeremDBUtils.TABLE :
                              field.equals("targy") ? TableNames.TANTAGY :
                              field.equals("tanar") ? TanarDBUtils.TABLE : TABLE;
        var fieldToCheck = field.equals("osztaly") ? "megnevezes" :
                           field.equals("terem") ? "teremSzam" :
                           field.equals("targy") ? "nev" :
                           field.equals("tanar") ? "nev" : field;

        return DBUtils.list(String.format(SELECT_ALL_QUERY + " WHERE " + tableToFilterIn + ".%s LIKE '%%%s%%'", fieldToCheck, value), Ora.class);
    }

    public static CompletableFuture<ObservableList<Ora>> listFor(Tanar tanar) {
        var query = String.format(OraDBUtils.SELECT_ALL_QUERY +
                                  " WHERE tanarSzemelyiSzam = '%s'" +
                                  " ORDER BY idopont ASC", tanar.szemelyiSzam);

        return DBUtils.list(query, Ora.class);
    }

    @SuppressWarnings("boxing")
    public static CompletableFuture<ObservableList<Ora>> listFor(Osztaly osztaly) {
        var query = String.format(OraDBUtils.SELECT_ALL_QUERY +
                                  " WHERE osztalyAzonosito  = '%s'" +
                                  " ORDER BY idopont ASC", osztaly.azonosito);

        return DBUtils.list(query, Ora.class);
    }

    @SuppressWarnings("boxing")
    public static void add(int napIndex, String idopont, Tantargy targy, Tanar tanar, Osztaly osztaly, Terem terem) {
        DBUtils.update(String.format("INSERT INTO " + TABLE + " VALUES(NULL, %d, '%s', %d, '%s', %d, %d)", napIndex, idopont, targy.azonosito, tanar.szemelyiSzam, osztaly.azonosito, terem.azonosito));
    }

    @SuppressWarnings("boxing")
    public static void update(Ora ora, int napIndex, String idopont, Tantargy targy, Tanar tanar, Osztaly osztaly, Terem terem) {
        var toUpdate = String.format("napIndex = %d, idopont = '%s', tantargyAzonosito = %d, tanarSzemelyiSzam = %d, osztalyAzonosito = %d, teremAzonosito = %d", napIndex, idopont, targy.azonosito, tanar.szemelyiSzam, osztaly.azonosito, terem.azonosito);

        DBUtils.update("UPDATE " + TABLE + " SET " + toUpdate + " WHERE azonosito = " + ora.azonosito);
    }
}