package degubi.model;

import degubi.db.*;
import java.sql.*;
import java.util.*;

public final class Tanar {
    public static final Map<String, String> fieldMappings = Map.of("Személyi Szám", "szemelyiSzam", "Név", "nev", "Képzettség", "kepzettseg");

    public final String szemelyiSzam;
    public final String nev;
    public final Kepzettseg kepzettseg;

    public Tanar(ResultSet result) throws SQLException {
        this("", result);
    }

    public Tanar(String prefix, ResultSet result) throws SQLException {
        this.szemelyiSzam = result.getString(prefix + "szemelyiSzam");
        this.nev = result.getString(prefix + "nev");

        if(prefix.equals("")) {
            this.kepzettseg = new Kepzettseg(KepzettsegDBUtils.TABLE + '.', result);
        }else {
            this.kepzettseg = null;
        }
    }

    //FX-nek getterek
    public String getSzemelyiSzam() { return szemelyiSzam; }
    public String getNev() { return nev; }
    public String getKepzettseg() { return kepzettseg.megnevezes; }

    //FX-nek label
    @Override
    public String toString() { return nev + " (" + szemelyiSzam + ")"; }
}