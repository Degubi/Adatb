package degubi.db;

import degubi.model.stat.*;
import java.util.concurrent.*;
import javafx.collections.*;

public final class StatDBUtils {

    public static CompletableFuture<ObservableList<TantargyFrequencyStat>> getTantargyFrequencyMap() {
        var query = "SELECT COUNT(" + OraDBUtils.TABLE + ".tantargyAzonosito) as Frequency, " + TantargyDBUtils.TABLE + ".*" +
                    " FROM " + TantargyDBUtils.TABLE +
                    " LEFT JOIN " + OraDBUtils.TABLE + " ON " + OraDBUtils.TABLE + ".tantargyAzonosito = " + TantargyDBUtils.TABLE + ".azonosito" +
                    " GROUP BY " + TantargyDBUtils.TABLE + ".azonosito" +
                    " ORDER BY Frequency DESC";

        return DBUtils.list(query, TantargyFrequencyStat.class);
    }

    private StatDBUtils() {}
}