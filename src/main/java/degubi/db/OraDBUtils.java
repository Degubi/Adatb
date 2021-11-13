package degubi.db;

import degubi.mapping.*;
import degubi.model.*;
import java.util.concurrent.*;
import javafx.collections.*;

public final class OraDBUtils {

    public static CompletableFuture<ObservableList<Ora>> listFiltered(String field, String value) {
        var tableToFilterIn = field.equals("osztaly") ? TableNames.OSZTALY :
                              field.equals("terem") ? TableNames.TEREM :
                              field.equals("targy") ? TableNames.TANTAGY :
                              field.equals("tanar") ? TableNames.TANAR : TableNames.ORA;
        var fieldToCheck = field.equals("osztaly") ? "megnevezes" :
                           field.equals("terem") ? "teremSzam" :
                           field.equals("targy") ? "nev" :
                           field.equals("tanar") ? "nev" : field;

        return DBUtils.listCustom(String.format(ObjectMapper.createMapper(Ora.class).listAllQuery + " WHERE " + tableToFilterIn + ".%s LIKE '%%%s%%'", fieldToCheck, value), Ora.class);
    }

    public static CompletableFuture<ObservableList<Ora>> listFor(Tanar tanar) {
        var query = String.format(ObjectMapper.createMapper(Ora.class).listAllQuery +
                                  " WHERE tanarSzemelyiSzam = '%s'" +
                                  " ORDER BY idopont ASC", tanar.szemelyiSzam);

        return DBUtils.listCustom(query, Ora.class);
    }

    @SuppressWarnings("boxing")
    public static CompletableFuture<ObservableList<Ora>> listFor(Osztaly osztaly) {
        var query = String.format(ObjectMapper.createMapper(Ora.class).listAllQuery +
                                  " WHERE osztalyAzonosito = '%s'" +
                                  " ORDER BY idopont ASC", osztaly.azonosito);

        return DBUtils.listCustom(query, Ora.class);
    }

    @SuppressWarnings("boxing")
    public static void add(int napIndex, String idopont, Tantargy targy, Tanar tanar, Osztaly osztaly, Terem terem) {
        DBUtils.updateCustom(String.format("INSERT INTO " + TableNames.ORA + " VALUES(NULL, %d, '%s', %d, '%s', %d, %d)", napIndex, idopont, targy.azonosito, tanar.szemelyiSzam, osztaly.azonosito, terem.azonosito));
    }

    @SuppressWarnings("boxing")
    public static void update(Ora ora, int napIndex, String idopont, Tantargy targy, Tanar tanar, Osztaly osztaly, Terem terem) {
        var toUpdate = String.format("napIndex = %d, idopont = '%s', tantargyAzonosito = %d, tanarSzemelyiSzam = %d, osztalyAzonosito = %d, teremAzonosito = %d", napIndex, idopont, targy.azonosito, tanar.szemelyiSzam, osztaly.azonosito, terem.azonosito);

        DBUtils.updateCustom("UPDATE " + TableNames.ORA + " SET " + toUpdate + " WHERE azonosito = " + ora.azonosito);
    }
}