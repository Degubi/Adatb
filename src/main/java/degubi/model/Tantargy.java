package degubi.model;

import degubi.db.*;
import degubi.mapping.*;
import java.util.*;

@MappingTable(TantargyDBUtils.TABLE)
public final class Tantargy {
    public static final Map<String, String> fieldMappings = Map.of("Azonosító", "azonosito", "Név", "nev");

    @MappingPrimaryKey("azonosito")
    public final int azonosito;
    public final String nev;

    @MappingConstructor
    public Tantargy(@MappingParameter("azonosito") int azonosito,
                    @MappingParameter("nev") String nev) {

        this.azonosito = azonosito;
        this.nev = nev;
    }

    //FX-nek getterek
    public int getAzonosito() { return azonosito; }
    public String getNev() { return nev; }

    //FX-nek label
    @Override
    public String toString() { return nev; }
}