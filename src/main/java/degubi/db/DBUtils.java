package degubi.db;

import degubi.gui.*;
import java.sql.*;
import java.util.function.*;

public final class DBUtils {

    public static void useConnection(Consumer<Connection> connectionConsumer) {
        try(var conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "test", "test123")) {
            connectionConsumer.accept(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            Components.showErrorDialog("Nem sikerült csatlakozni a szerverhez!");
        }
    }
}