package degubi.model;

import java.sql.*;
import java.util.*;

public final class Kepzettseg {
    public static final Map<String, String> fieldMappings = Map.of("Azonosító", "azonosito", "Megnevezés", "megnevezes");

    public final int azonosito;
    public final String megnevezes;

    public Kepzettseg(int azonosito, String megnevezes) {
        this.azonosito = azonosito;
        this.megnevezes = megnevezes;
    }

    public Kepzettseg(ResultSet result) throws SQLException {
        this.azonosito = result.getInt("azonosito");
        this.megnevezes = result.getString("megnevezes");
    }

    //FX-nek getterek
    public int getAzonosito() { return azonosito; }
    public String getMegnevezes() { return megnevezes; }

    //FX-nek label
    @Override
    public String toString() {
        return megnevezes;
    }
}