package degubi.model;

import degubi.db.*;
import java.sql.*;
import java.util.*;

public final class Tanar {
    public static final Map<String, String> fieldMappings = Map.of("Személyi Szám", "szemelyiSzam", "Név", "nev", "Képzettség", "kepzettseg");

    public final String szemelyiSzam;
    public final String nev;
    public final String kepzettseg;

    public Tanar(ResultSet result) throws SQLException {
        this.szemelyiSzam = result.getString("szemelyiSzam");
        this.nev = result.getString("nev");
        this.kepzettseg = result.getString(KepzettsegDBUtils.TABLE + ".megnevezes");
    }

    //FX-nek getterek
    public String getSzemelyiSzam() { return szemelyiSzam; }
    public String getNev() { return nev; }
    public String getKepzettseg() { return kepzettseg; }
}