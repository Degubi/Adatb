package degubi.model;

import degubi.mapping.*;
import java.util.*;

@MappingTable(TableNames.KEPZETTSEG)
public final class Kepzettseg {
    public static final Map<String, String> fieldMappings = Map.of("Azonosító", "azonosito", "Megnevezés", "megnevezes");

    @MappingPrimaryKey(value = "azonosito", autoIncrement = true)
    public final int azonosito;
    public final String megnevezes;

    @MappingConstructor
    public Kepzettseg(@MappingParameter("azonosito") int azonosito,
                      @MappingParameter("megnevezes") String megnevezes) {

        this.azonosito = azonosito;
        this.megnevezes = megnevezes;
    }

    @MappingValuesCreator
    public Map<String, Object> createValueMappings() {
        return Map.of("azonosito", azonosito, "megnevezes", megnevezes);
    }

    //FX-nek getterek
    public int getAzonosito() { return azonosito; }
    public String getMegnevezes() { return megnevezes; }

    //FX-nek label
    @Override
    public String toString() { return megnevezes; }
}