package connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class TransactionManager {
    private static final Logger logger = Logger.getLogger(TransactionManager.class.getName());

    public static void start(Connection connect) {
        try {
            connect.prepareStatement("START TRANSACTION").executeUpdate();
        } catch (SQLException e) {
            logger.warning("Transaction not started");
        }
    }

    public static void commit(Connection connect) {
        try {
            conn.prepareStatement("COMMIT TRANSACTION").executeUpdate();
        } catch (SQLException e) {
            logger.warning("Transaction not commited");
        }
    }

    public static void rollback(Connection connect) {
        try {
            conn.prepareStatement("ROLLBACK TRANSACTION").executeUpdate();
        } catch (SQLException e) {
            logger.warning("Rollback failure");
        }
    }
}
