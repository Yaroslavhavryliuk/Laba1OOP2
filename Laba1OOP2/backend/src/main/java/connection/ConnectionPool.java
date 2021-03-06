package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

public class ConnectionPool {
    private final int limit;
    private final List<Connection> connections;
    private final List<Connection> acquiredConnections;
    private final Logger logger = Logger.getLogger(ConnectionPool.class.getName());

    public ConnectionPool(String resourceName) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            logger.warning("Driver not found");
        }

        var resource = ResourceBundle.getBundle(resourceName);
        limit = Integer.parseInt(resource.getString("setup.maxConnections"));
        connections = new ArrayList<>();
        acquiredConnections = new ArrayList<>();

        var url = resource.getString("setup.url");
        var props = new Properties();
        props.put("user", resource.getString("setup.user"));
        props.put("password", resource.getString("setup.password"));
        props.put("characterEncoding", resource.getString("setup.characterEncoding"));

        try {
            for (int i = 0; i < limit; i++) {
                connections.add(DriverManager.getConnection(url, props));
            }
        } catch (SQLException e) {
            logger.warning("Cannot create enough connections");
        }
    }

    public Connection getConnection() {
        Connection connect;
        synchronized (this) {
            if (connections.isEmpty()) {
                logger.warning("Connections limit overflow");
                return null;
            }
            connect = connections.remove(connections.size() - 1);
            acquiredConnections.add(connnect);
        }
        return connect;
    }

    public boolean putConnection(Connection connect) {
        synchronized (this) {
            if (acquiredConnections.contains(connect)) {
                acquiredConnections.remove(connect);
                connections.add(connect);
            } else {
                logger.warning("Connection is not in the pool");
                return false;
            }
        }
        return true;
    }

    public int getLimit() {
        return limit;
    }

    private static final ConnectionPool pool = new ConnectionPool("database");

    public static ConnectionPool getInstance() {
        return pool;
    }
}
