package degubi.model;

import degubi.db.*;
import java.sql.*;
import java.util.*;

public final class Diak {
    public static final Map<String, String> fieldMappings = Map.of("Neptun Kód", "neptunKod", "Osztály", "osztalyMegnevezes", "Név", "nev");

    public final String neptunKod;
    public final Osztaly osztaly;
    public final String nev;

    public Diak(ResultSet result) throws SQLException {
        this.neptunKod = result.getString("neptunKod");
        this.osztaly = new Osztaly(OsztalyDBUtils.TABLE + '.', result);
        this.nev = result.getString("nev");
    }

    //FX-nek getterek
    public String getNeptunKod() { return neptunKod; }
    public String getOsztalyMegnevezes() { return osztaly.megnevezes; }
    public String getNev() { return nev; }
}