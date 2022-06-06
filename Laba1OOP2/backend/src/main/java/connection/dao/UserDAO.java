package connection.dao;

import entity.misc.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class UserDAO {
    private final Connection connect;
    private final ResourceBundle resBundle = ResourceBundle.getBundle("database");
    private final Logger logger = Logger.getLogger(UserDAO.class.getName());

    public UserDAO(Connection connect) {
        this.connect = connect;
    }

    private User mapper(ResultSet rs) throws SQLException {
        var id = rs.getInt("id");
        var login = rs.getString("login");
        var password = rs.getString("password");
        var role = User.Role.valueOf(rs.getString("role").toUpperCase());
        var contact = rs.getString("contact");

        return new User(id, login, password, role, contact);
    }

    public List<User> getAll() {
        try (var statement = connect.createStatement()) {
            try (var resultSet = statement.executeQuery(resBundle.getString("query.user.getAll"))) {
                var list = new ArrayList<User>();
                while (resultSet.next()) {
                    list.add(mapper(resultSet));
                }
                return list;
            }
        } catch (SQLException e) {
            logger.warning("SQLException in getAll()");
        }
        return null;
    }

    public User get(String login, String password) {
        try (var statement = connect.prepareStatement(resBundle.getString("query.user.getByLogPass"))) {
            statement.setString(1, login);
            statement.setString(2, password);

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapper(resultSet);
                }
            }
        } catch (SQLException e) {
            logger.warning("SQLException in get()");
        }
        return null;
    }
}
