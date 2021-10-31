package degubi.model;

import java.sql.*;
import java.util.*;
import javafx.beans.property.*;

public final class Terem {
    public static final Map<String, String> fieldMappings = Map.of("Azonosító", "azonosito", "Épület", "epulet", "Férőhelyek Száma", "ferohelyekSzama", "Van-E Projektor", "vanEProjektor");

    public final int azonosito;
    public final int epulet;
    public final int ferohelyekSzama;
    public final boolean vanEProjektor;

    private final SimpleBooleanProperty vanEProjektorProp;

    public Terem(int azonosito, int epulet, int ferohelyekSzama, boolean vanEProjektor) {
        this.azonosito = azonosito;
        this.epulet = epulet;
        this.ferohelyekSzama = ferohelyekSzama;
        this.vanEProjektor = vanEProjektor;

        this.vanEProjektorProp = new SimpleBooleanProperty(vanEProjektor);
    }

    public Terem(ResultSet result) throws SQLException {
        this.azonosito = result.getInt("azonosito");
        this.epulet = result.getInt("epulet");
        this.ferohelyekSzama = result.getInt("ferohelyekSzama");
        this.vanEProjektor = result.getBoolean("vanEProjektor");

        this.vanEProjektorProp = new SimpleBooleanProperty(vanEProjektor);
    }

    //FX-nek getterek
    public int getAzonosito() { return azonosito; }
    public int getFerohelyekSzama() { return ferohelyekSzama; }
    public int getEpulet() { return epulet; }
    public SimpleBooleanProperty getVanEProjektor() { return vanEProjektorProp; }
}