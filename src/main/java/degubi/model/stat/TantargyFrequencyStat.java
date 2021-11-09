package degubi.model.stat;

import degubi.mapping.*;

public final class TantargyFrequencyStat {

    public final long count;
    public final int azonosito;
    public final String nev;

    @MappingConstructor
    public TantargyFrequencyStat(@MappingParameter("Frequency") long count,
                                 @MappingParameter("tantargy.azonosito") int azonosito,
                                 @MappingParameter("tantargy.nev") String nev) {
        this.count = count;
        this.azonosito = azonosito;
        this.nev = nev;
    }
}