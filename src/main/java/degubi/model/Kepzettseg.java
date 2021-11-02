package degubi.model;

import java.sql.*;
import java.util.*;

public final class Kepzettseg {
    public static final Map<String, String> fieldMappings = Map.of("Azonosító", "azonosito", "Megnevezés", "megnevezes");

    public final int azonosito;
    public final String megnevezes;

    public Kepzettseg(ResultSet result) throws SQLException {
        this("", result);
    }

    public Kepzettseg(String prefix, ResultSet result) throws SQLException {
        this.azonosito = result.getInt(prefix + "azonosito");
        this.megnevezes = result.getString(prefix + "megnevezes");
    }

    //FX-nek getterek
    public int getAzonosito() { return azonosito; }
    public String getMegnevezes() { return megnevezes; }

    //FX-nek label
    @Override
    public String toString() { return megnevezes; }
}