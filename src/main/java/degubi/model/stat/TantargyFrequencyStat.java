package degubi.model.stat;

import java.sql.*;

public final class TantargyFrequencyStat {

    public final int count;
    public final int azonosito;
    public final String nev;

    public TantargyFrequencyStat(ResultSet result) throws SQLException {
        this.count = result.getInt("Frequency");
        this.azonosito = result.getInt("tantargy.azonosito");
        this.nev = result.getString("tantargy.nev");
    }
}