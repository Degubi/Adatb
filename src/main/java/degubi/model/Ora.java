package degubi.model;

import degubi.db.*;
import degubi.mapping.*;
import java.util.*;

@MappingTable(OraDBUtils.TABLE)
public final class Ora {
    public static final Map<String, String> fieldMappings = Map.of("Azonosító", "azonosito", "Nap", "nap", "Időpont", "idopont", "Tantárgy", "targy", "Osztály", "osztaly", "Terem", "terem", "Tanár", "tanar");

    @MappingPrimaryKey("azonosito")
    public final int azonosito;
    public final int napIndex;
    public final String nap;
    public final String idopont;
    public final Tantargy tantargy;
    public final Tanar tanar;
    public final Osztaly osztaly;
    public final Terem terem;

    @MappingConstructor
    public Ora(@MappingParameter("azonosito") int azonosito,
               @MappingParameter("napIndex") int napIndex,
               @MappingParameter("idopont") String idopont,
               @MappingParameter(value = "tantargy", localKey = "tantargyAzonosito", foreignKey = "azonosito") Tantargy tantargy,
               @MappingParameter(value = "tanar", localKey = "tanarSzemelyiSzam", foreignKey = "szemelyiSzam") Tanar tanar,
               @MappingParameter(value = "osztaly", localKey = "osztalyAzonosito", foreignKey = "azonosito") Osztaly osztaly,
               @MappingParameter(value = "terem", localKey = "teremAzonosito", foreignKey = "azonosito") Terem terem) {

        this.azonosito = azonosito;
        this.napIndex = napIndex;
        this.nap = getNapFromIndex(napIndex);
        this.idopont = idopont;
        this.tantargy = tantargy;
        this.tanar = tanar;
        this.osztaly = osztaly;
        this.terem = terem;
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
    public int getAzonosito() { return azonosito; }
    public String getNap() { return nap; }
    public String getIdopont() { return idopont; }
    public String getTargy() { return tantargy.nev; }
    public String getOsztaly() { return osztaly.toString(); }
    public String getTerem() { return terem.toString(); }
    public String getTanar() { return tanar.toString(); }
}