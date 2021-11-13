package degubi.db;

import degubi.model.stat.*;
import java.util.concurrent.*;
import javafx.collections.*;

public final class StatDBUtils {

    public static CompletableFuture<ObservableList<TantargyFrequencyStat>> getTantargyFrequencyMap() {
        var query = "SELECT COUNT(" + OraDBUtils.TABLE + ".tantargyAzonosito) as Frequency, " + TableNames.TANTAGY + ".*" +
                    " FROM " + TableNames.TANTAGY +
                    " LEFT JOIN " + OraDBUtils.TABLE + " ON " + OraDBUtils.TABLE + ".tantargyAzonosito = " + TableNames.TANTAGY + ".azonosito" +
                    " GROUP BY " + TableNames.TANTAGY + ".azonosito" +
                    " ORDER BY Frequency DESC";

        return DBUtils.list(query, TantargyFrequencyStat.class);
    }

    private StatDBUtils() {}
}