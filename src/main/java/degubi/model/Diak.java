package degubi.model;

import degubi.mapping.*;
import java.util.*;

@MappingTable(TableNames.DIAK)
public final class Diak {
    public static final Map<String, String> fieldMappings = Map.of("Neptun Kód", "neptunKod", "Osztály", "osztalyMegnevezes", "Név", "nev");

    @MappingPrimaryKey(value = "neptunKod", autoIncrement = false)
    public final String neptunKod;
    public final Osztaly osztaly;
    public final String nev;

    @MappingConstructor
    public Diak(@MappingParameter("neptunKod") String neptunKod,
                @MappingParameter(value = "osztaly", localKey = "osztalyAzonosito", foreignKey = "azonosito") Osztaly osztaly,
                @MappingParameter("nev") String nev) {

        this.neptunKod = neptunKod;
        this.osztaly = osztaly;
        this.nev = nev;
    }

    @MappingValuesCreator
    public Map<String, Object> createValueMappings() {
        return Map.of("neptunKod", neptunKod, "osztaly", osztaly, "nev", nev);
    }

    //FX-nek getterek
    public String getNeptunKod() { return neptunKod; }
    public String getOsztalyMegnevezes() { return osztaly.megnevezes; }
    public String getNev() { return nev; }
}