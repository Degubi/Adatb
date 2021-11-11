package degubi.db;

import degubi.model.*;

public final class TantargyDBUtils {
    public static final String TABLE = "tantargy";

    public static void add(String nev) {
        DBUtils.update(String.format("INSERT INTO " + TABLE + " VALUES(NULL, '%s')", nev));
    }

    public static void update(Tantargy targy, String nev) {
        DBUtils.update("UPDATE " + TABLE + " SET nev = '" + nev + "' WHERE azonosito = " + targy.azonosito);
    }

    private TantargyDBUtils() {}
}