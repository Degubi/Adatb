package degubi.model.stat;

import degubi.mapping.*;

public final class NaponkentiTermenkentiOrakSzamaStat {

    public final long count;
    public final int teremSzam;
    public final int napIndex;

    @MappingConstructor
    public NaponkentiTermenkentiOrakSzamaStat(@MappingParameter("Count") long count,
                                              @MappingParameter("teremSzam") int teremSzam,
                                              @MappingParameter("napIndex") int napIndex) {
        this.count = count;
        this.teremSzam = teremSzam;
        this.napIndex = napIndex;
    }
}