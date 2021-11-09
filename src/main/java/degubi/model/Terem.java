package degubi.model;

import degubi.db.*;
import degubi.mapping.*;
import java.util.*;
import javafx.beans.property.*;

@MappingTable(TeremDBUtils.TABLE)
public final class Terem {
    public static final Map<String, String> fieldMappings = Map.of("Azonosító", "azonosito", "Épület", "epuletSzam", "Terem", "teremSzam", "Férőhelyek Száma", "ferohelyekSzama", "Van-E Projektor", "vanEProjektor");

    public final int azonosito;
    public final int teremSzam;
    public final int epuletSzam;
    public final int ferohelyekSzama;
    public final boolean vanEProjektor;

    private final SimpleBooleanProperty vanEProjektorProp;

    @MappingConstructor
    public Terem(@MappingParameter("azonosito") int azonosito,
                 @MappingParameter("teremSzam") int teremSzam,
                 @MappingParameter("epuletSzam") int epuletSzam,
                 @MappingParameter("ferohelyekSzama") int ferohelyekSzama,
                 @MappingParameter("vanEProjektor") boolean vanEProjektor) {

        this.azonosito = azonosito;
        this.teremSzam = teremSzam;
        this.epuletSzam = epuletSzam;
        this.ferohelyekSzama = ferohelyekSzama;
        this.vanEProjektor = vanEProjektor;

        this.vanEProjektorProp = new SimpleBooleanProperty(vanEProjektor);
    }

    //FX-nek getterek
    public int getAzonosito() { return azonosito; }
    public int getTeremSzam() { return teremSzam; }
    public int getEpuletSzam() { return epuletSzam; }
    public int getFerohelyekSzama() { return ferohelyekSzama; }
    public SimpleBooleanProperty getVanEProjektor() { return vanEProjektorProp; }

    //FX-nek label
    @Override
    public String toString() { return epuletSzam + ". épület " + teremSzam + ". terem"; }
}