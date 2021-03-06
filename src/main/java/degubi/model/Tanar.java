package degubi.model;

import degubi.mapping.*;
import java.util.*;

@MappingTable(TableNames.TANAR)
public final class Tanar {
    public static final Map<String, String> fieldMappings = Map.of("Személyi Szám", "szemelyiSzam", "Név", "nev", "Képzettség", "kepzettseg");

    @MappingPrimaryKey(value = "szemelyiSzam", autoIncrement = false)
    public final String szemelyiSzam;
    public final String nev;
    public final Kepzettseg kepzettseg;

    @MappingConstructor
    public Tanar(@MappingParameter("szemelyiSzam") String szemelyiSzam,
                 @MappingParameter("nev") String nev,
                 @MappingParameter(value = "kepzettseg", localKey = "kepzettsegAzonosito", foreignKey = "azonosito") Kepzettseg kepzettseg) {

        this.szemelyiSzam = szemelyiSzam;
        this.nev = nev;
        this.kepzettseg = kepzettseg;
    }

    @MappingValuesCreator
    public Map<String, Object> createValueMappings() {
        return Map.of("szemelyiSzam", szemelyiSzam, "nev", nev, "kepzettseg", kepzettseg);
    }

    //FX-nek getterek
    public String getSzemelyiSzam() { return szemelyiSzam; }
    public String getNev() { return nev; }
    public String getKepzettseg() { return kepzettseg.megnevezes; }

    //FX-nek label
    @Override
    public String toString() { return nev + " (" + szemelyiSzam + ")"; }
}