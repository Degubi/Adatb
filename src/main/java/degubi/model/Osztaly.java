package degubi.model;

import degubi.db.*;
import degubi.mapping.*;
import java.util.*;

@MappingTable(OsztalyDBUtils.TABLE)
public final class Osztaly {
    public static final Map<String, String> fieldMappings = Map.of("Azonosító", "azonosito", "Megnevezés", "megnevezes");

    @MappingPrimaryKey("azonosito")
    public final int azonosito;
    public final String megnevezes;

    @MappingConstructor
    public Osztaly(@MappingParameter("azonosito") int azonosito,
                   @MappingParameter("megnevezes") String megnevezes) {

        this.azonosito = azonosito;
        this.megnevezes = megnevezes;
    }

    //FX-nek getterek
    public int getAzonosito() { return azonosito; }
    public String getMegnevezes() { return megnevezes; }

    //FX-nek label
    @Override
    public String toString() { return megnevezes; }
}