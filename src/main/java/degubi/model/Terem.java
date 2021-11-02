package degubi.model;

import java.sql.*;
import java.util.*;
import javafx.beans.property.*;

public final class Terem {
    public static final Map<String, String> fieldMappings = Map.of("Azonosító", "azonosito", "Épület", "epuletSzam", "Terem", "teremSzam", "Férőhelyek Száma", "ferohelyekSzama", "Van-E Projektor", "vanEProjektor");

    public final int azonosito;
    public final int teremSzam;
    public final int epuletSzam;
    public final int ferohelyekSzama;
    public final boolean vanEProjektor;

    private final SimpleBooleanProperty vanEProjektorProp;

    public Terem(ResultSet result) throws SQLException {
        this("", result);
    }

    public Terem(String prefix, ResultSet result) throws SQLException {
        this.azonosito = result.getInt(prefix + "azonosito");
        this.teremSzam = result.getInt(prefix + "teremSzam");
        this.epuletSzam = result.getInt(prefix + "epuletSzam");
        this.ferohelyekSzama = result.getInt(prefix + "ferohelyekSzama");
        this.vanEProjektor = result.getBoolean(prefix + "vanEProjektor");

        this.vanEProjektorProp = new SimpleBooleanProperty(vanEProjektor);
    }

    //FX-nek getterek
    public int getAzonosito() { return azonosito; }
    public int getTeremSzam() { return teremSzam; }
    public int getEpuletSzam() { return epuletSzam; }
    public int getFerohelyekSzama() { return ferohelyekSzama; }
    public SimpleBooleanProperty getVanEProjektor() { return vanEProjektorProp; }

    //FX-nek label
    @Override
    public String toString() { return epuletSzam + ". épület " + teremSzam + ". terem"; }
}