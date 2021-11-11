package degubi.db;

import degubi.model.*;

public final class TeremDBUtils {
    public static final String TABLE = "terem";

    @SuppressWarnings("boxing")
    public static void add(String teremSzam, String epuletSzam, String ferohelyekSzama, boolean vanEProjektor) {
        DBUtils.update(String.format("INSERT INTO " + TABLE + " VALUES(NULL, %s, %s, %s, %d)", teremSzam, epuletSzam, ferohelyekSzama, vanEProjektor ? 1 : 0));
    }

    @SuppressWarnings("boxing")
    public static void update(Terem terem, String teremSzam, String epuletSzam, String ferohelyekSzama, boolean vanEProjektor) {
        var toUpdate = String.format("teremSzam = %s, epuletSzam = %s, ferohelyekSzama = %s, vanEProjektor = %d", teremSzam, epuletSzam, ferohelyekSzama, vanEProjektor ? 1 : 0);

        DBUtils.update("UPDATE " + TABLE + " SET " + toUpdate + " WHERE azonosito = " + terem.azonosito);
    }

    private TeremDBUtils() {}
}