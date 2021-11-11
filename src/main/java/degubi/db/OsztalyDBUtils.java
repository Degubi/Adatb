package degubi.db;

import degubi.model.*;

public final class OsztalyDBUtils {
    public static final String TABLE = "osztaly";

    public static void add(String megnevezes) {
        DBUtils.update(String.format("INSERT INTO " + TABLE + " VALUES(NULL, '%s')", megnevezes));
    }

    public static void update(Osztaly osztaly, String megnevezes) {
        DBUtils.update("UPDATE " + TABLE + " SET megnevezes = '" + megnevezes + "' WHERE azonosito = " + osztaly.azonosito);
    }

    private OsztalyDBUtils() {}
}