package degubi.db;

import degubi.mapping.*;
import degubi.model.*;
import java.util.concurrent.*;
import javafx.collections.*;

public final class TanarDBUtils {

    public static CompletableFuture<ObservableList<Tanar>> listFiltered(String field, String value) {
        var tableToFilterIn = field.equals("kepzettseg") ? TableNames.KEPZETTSEG : TableNames.TANAR;
        var fieldToCheck = field.equals("kepzettseg") ? "megnevezes" : field;

        return DBUtils.listCustom(String.format(ObjectMapper.createMapper(Tanar.class).listAllQuery + " WHERE " + tableToFilterIn + ".%s LIKE '%%%s%%'", fieldToCheck, value), Tanar.class);
    }

    @SuppressWarnings("boxing")
    public static void add(String szemelyi, String nev, Kepzettseg kepzettseg) {
        DBUtils.updateCustom(String.format("INSERT INTO " + TableNames.TANAR + " VALUES('%s', '%s', %d)", szemelyi, nev, kepzettseg.azonosito));
    }

    @SuppressWarnings("boxing")
    public static void update(Tanar tanar, String nev, Kepzettseg kepzettseg) {
        var toUpdate = String.format("nev = '%s', kepzettsegAzonosito = %d", nev, kepzettseg.azonosito);

        DBUtils.updateCustom("UPDATE " + TableNames.TANAR + " SET " + toUpdate + " WHERE szemelyiSzam = '" + tanar.szemelyiSzam + "'");
    }

    private TanarDBUtils() {}
}