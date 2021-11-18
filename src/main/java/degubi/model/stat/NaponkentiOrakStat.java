package degubi.model.stat;

import degubi.mapping.*;

public final class NaponkentiOrakStat {

    public final long count;
    public final int napIndex;

    @MappingConstructor
    public NaponkentiOrakStat(@MappingParameter("Count") long count,
                              @MappingParameter("napIndex") int napIndex) {

        this.count = count;
        this.napIndex = napIndex;
    }
}