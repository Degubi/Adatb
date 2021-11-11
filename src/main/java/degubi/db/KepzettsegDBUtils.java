package degubi.db;

import degubi.model.*;

public final class KepzettsegDBUtils {
    public static final String TABLE = "kepzettseg";

    public static void add(String megnevezes) {
        DBUtils.update(String.format("INSERT INTO " + TABLE + " VALUES(NULL, '%s')", megnevezes));
    }

    public static void update(Kepzettseg kepzettseg, String megnevezes) {
        DBUtils.update("UPDATE " + TABLE + " SET megnevezes = '" + megnevezes + "' WHERE azonosito = " + kepzettseg.azonosito);
    }

    private KepzettsegDBUtils() {}
}