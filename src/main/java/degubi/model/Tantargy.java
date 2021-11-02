package degubi.model;

import java.sql.*;
import java.util.*;

public final class Tantargy {
    public static final Map<String, String> fieldMappings = Map.of("Azonosító", "azonosito", "Név", "nev");

    public final int azonosito;
    public final String nev;

    public Tantargy(ResultSet result) throws SQLException {
        this("", result);
    }

    public Tantargy(String prefix, ResultSet result) throws SQLException {
        this.azonosito = result.getInt(prefix + "azonosito");
        this.nev = result.getString(prefix + "nev");
    }

    //FX-nek getterek
    public int getAzonosito() { return azonosito; }
    public String getNev() { return nev; }

    //FX-nek label
    @Override
    public String toString() { return nev; }
}