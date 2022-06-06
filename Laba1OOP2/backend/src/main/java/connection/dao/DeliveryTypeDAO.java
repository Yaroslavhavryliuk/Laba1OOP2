package connection.dao;

import connection.dao.interfaces.BaseDAO;
import entity.misc.DeliveryType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class DeliveryTypeDAO implements BaseDAO<DeliveryType> {
    private final Connection connect;
    private final ResourceBundle resBundle = ResourceBundle.getBundle("database");
    private final Logger logger = Logger.getLogger(DeliveryTypeDAO.class.getName());

    public DeliveryTypeDAO(Connection connect) {
        this.connect = connect;
    }

    private DeliveryType mapper(ResultSet rs) throws SQLException {
        var id = rs.getInt("id");
        var description = rs.getString("description");
        return new DeliveryType(id, description);
    }

    @Override
    public List<DeliveryType> getAll() {
        try (var statement = connect.createStatement()) {
            try (var resultSet = statement.executeQuery(resBundle.getString("query.delivery_type.getAll"))) {
                var list = new ArrayList<DeliveryType>();
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

    @Override
    public DeliveryType get(int id) {
        try (var statement = connect.prepareStatement(resBundle.getString("query.delivery_type.get"))) {
            statement.setInt(1, id);

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

    @Override
    public boolean add(DeliveryType entity) {
        var res = false;
        try (var statement = connect.prepareStatement(resBundle.getString("query.delivery_type.add"))) {
            statement.setString(1, entity.getDescription());

            statement.executeUpdate();
            res = true;
        } catch (SQLException e) {
            logger.warning("SQLException in add()");
        }
        return res;
    }

    @Override
    public boolean edit(DeliveryType entity) {
        var res = false;
        try (var statement = connect.prepareStatement(resBundle.getString("query.delivery_type.edit"))) {
            statement.setString(1, entity.getDescription());
            statement.setInt(2, entity.getId());

            statement.executeUpdate();
            res = true;
        } catch (SQLException e) {
            logger.warning("SQLException in edit()");
        }
        return res;
    }

    @Override
    public boolean delete(int id) {
        var res = false;
        try (var statement = connect.prepareStatement(resBundle.getString("query.delivery_type.delete"))) {
            statement.setInt(1, id);

            statement.executeUpdate();
            res = true;
        } catch (SQLException e) {
            logger.warning("SQLException in delete()");
        }
        return res;
    }
}
