package degubi.model;

import degubi.mapping.*;
import java.util.*;

@MappingTable(TableNames.TANTARGY)
public final class Tantargy {
    public static final Map<String, String> fieldMappings = Map.of("Azonosító", "azonosito", "Név", "nev");

    @MappingPrimaryKey(value = "azonosito", autoIncrement = true)
    public final int azonosito;
    public final String nev;

    @MappingConstructor
    public Tantargy(@MappingParameter("azonosito") int azonosito,
                    @MappingParameter("nev") String nev) {

        this.azonosito = azonosito;
        this.nev = nev;
    }

    @MappingValuesCreator
    public Map<String, Object> createValueMappings() {
        return Map.of("azonosito", azonosito, "nev", nev);
    }

    //FX-nek getterek
    public int getAzonosito() { return azonosito; }
    public String getNev() { return nev; }

    //FX-nek label
    @Override
    public String toString() { return nev; }
}