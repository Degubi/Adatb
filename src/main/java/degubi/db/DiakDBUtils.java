package degubi.db;

import degubi.mapping.*;
import degubi.model.*;
import java.util.concurrent.*;
import javafx.collections.*;

public final class DiakDBUtils {

    public static CompletableFuture<ObservableList<Diak>> listFiltered(String field, String value) {
        var tableToFilterIn = field.equals("osztalyMegnevezes") ? TableNames.OSZTALY : TableNames.DIAK;
        var fieldToCheck = field.equals("osztalyMegnevezes") ? "megnevezes" : field;

        return DBUtils.listCustom(String.format(ObjectMapper.createMapper(Diak.class).listAllQuery + " WHERE " + tableToFilterIn + ".%s LIKE '%%%s%%'", fieldToCheck, value), Diak.class);
    }

    @SuppressWarnings("boxing")
    public static void add(String neptunKod, Osztaly osztaly, String nev) {
        DBUtils.updateCustom(String.format("INSERT INTO " + TableNames.DIAK + " VALUES('%s', %d, '%s')", neptunKod, osztaly.azonosito, nev));
    }

    @SuppressWarnings("boxing")
    public static void update(Diak diak, Osztaly osztaly, String nev) {
        var toUpdate = String.format("osztalyAzonosito = %d, nev = %s", osztaly.azonosito, nev);

        DBUtils.updateCustom("UPDATE " + TableNames.DIAK + " SET " + toUpdate + " WHERE neptunKod = '" + diak.neptunKod + "'");
    }

    private DiakDBUtils() {}
}