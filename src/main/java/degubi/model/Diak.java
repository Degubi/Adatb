package degubi.model;

import degubi.db.*;
import java.sql.*;
import java.util.*;

public final class Diak {
    public static final Map<String, String> fieldMappings = Map.of("Neptun Kód", "neptunKod", "Osztály", "osztalyMegnevezes", "Név", "nev");

    public final String neptunKod;
    public final String osztalyMegnevezes;
    public final String nev;

    public Diak(ResultSet result) throws SQLException {
        this.neptunKod = result.getString("neptunKod");
        this.osztalyMegnevezes = result.getString(OsztalyDBUtils.TABLE + ".megnevezes");
        this.nev = result.getString("nev");
    }

    //FX-nek getterek
    public String getNeptunKod() { return neptunKod; }
    public String getOsztalyMegnevezes() { return osztalyMegnevezes; }
    public String getNev() { return nev; }
}