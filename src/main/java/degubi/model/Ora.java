package degubi.model;

import degubi.db.*;
import java.sql.*;
import java.util.*;

public final class Ora {
    public static final Map<String, String> fieldMappings = Map.of("Nap", "nap", "Időpont", "idopont", "Név", "nev", "Osztály", "osztaly", "Terem", "terem", "Tanár", "tanar");

    public final String nap;
    public final String idopont;
    public final String nev;
    public final Tanar tanar;
    public final Osztaly osztaly;
    public final Terem terem;

    public Ora(ResultSet result) throws SQLException {
        this.nap = getNapFromIndex(result.getInt("napIndex"));
        this.idopont = result.getString("idopont");
        this.nev = result.getString("nev");
        this.osztaly = new Osztaly(OsztalyDBUtils.TABLE + '.', result);
        this.terem = new Terem(TeremDBUtils.TABLE + '.', result);
        this.tanar = new Tanar(TanarDBUtils.TABLE + '.', result);
    }


    private static String getNapFromIndex(int index) {
        switch(index) {
            case 0: return "Hétfő";
            case 1: return "Kedd";
            case 2: return "Szerda";
            case 3: return "Csütörtök";
            case 4: return "Péntek";
            default: throw new IllegalArgumentException("Unknown day index: " + index);
        }
    }


    //FX-nek getterek
    public String getNap() { return nap; }
    public String getIdopont() { return idopont; }
    public String getNev() { return nev; }
    public String getOsztaly() { return osztaly.toString(); }
    public String getTerem() { return terem.toString(); }
    public String getTanar() { return tanar.toString(); }
}