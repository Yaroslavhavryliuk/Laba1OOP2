package connection.dao;

import connection.dao.interfaces.InsertFindDAO;
import entity.book.request.GetBookRequest;
import entity.book.request.RequestState;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class BookRequestDAO implements InsertFindDAO<GetBookRequest> {
    private final Connection connect;
    private final ResourceBundle resBundle = ResourceBundle.getBundle("database");
    private final Logger logger = Logger.getLogger(BookRequestDAO.class.getName());

    public BookRequestDAO(Connection connect) {
        this.connect = connect;
    }

    private GetBookRequest mapper(ResultSet rs) throws SQLException {
        var id = rs.getInt("id");
        var datetime = rs.getTimestamp("datetime").toLocalDateTime();
        var userID = rs.getInt("user_id");
        var bookID = rs.getInt("book_id");
        var deliveryTypeID = rs.getInt("delivery_type_id");
        var contact = rs.getString("contact");
        var state = RequestState.valueOf(rs.getString("state").toUpperCase());

        return new GetBookRequest(id, datetime, userID, bookID, deliveryTypeID, contact, state);
    }

    @Override
    public List<GetBookRequest> getAll() {
        try (var statement = connect.createStatement()) {
            try (var resultSet = statement.executeQuery(resBundle.getString("query.request.getAll"))) {
                var list = new ArrayList<GetBookRequest>();
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
    public GetBookRequest get(int id) {
        try (var statement = connect.prepareStatement(resBundle.getString("query.request.get"))) {
            statement.setInt(1, id);

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapper(resultSet);
                }
                return null;
            }
        } catch (SQLException e) {
            logger.warning("SQLException in get()");
        }
        return null;
    }

    public List<GetBookRequest> getByUserId(int userID) {
        try (var statement = connect.prepareStatement(resBundle.getString("query.request.getByUserId"))) {
            statement.setInt(1, userID);

            try (var resultSet = statement.executeQuery()) {
                var list = new ArrayList<GetBookRequest>();
                while (resultSet.next()) {
                    list.add(mapper(resultSet));
                }
                return list;
            }
        } catch (SQLException e) {
            logger.warning("SQLException in getByUserId()");
        }
        return null;
    }

    public List<GetBookRequest> getByBookId(int bookID) {
        try (var statement = connect.prepareStatement(resBundle.getString("query.request.getByBookId"))) {
            statement.setInt(1, bookID);

            try (var resultSet = statement.executeQuery()) {
                var list = new ArrayList<GetBookRequest>();
                while (resultSet.next()) {
                    list.add(mapper(resultSet));
                }
                return list;
            }
        } catch (SQLException e) {
            logger.warning("SQLException in getByBookId()");
        }
        return null;
    }

    @Override
    public boolean add(GetBookRequest entity) {
        var res = false;
        try (var statement = connect.prepareStatement(resBundle.getString("query.request.add"))) {
            statement.setTimestamp(1, Timestamp.valueOf(entity.getDatetime()));
            statement.setInt(2, entity.getUserID());
            statement.setInt(3, entity.getBookID());
            statement.setInt(4, entity.getDeliveryTypeID());
            statement.setString(5, entity.getContact());
            statement.setString(6, entity.getState().toString());

            statement.executeUpdate();
            res = true;
        } catch (SQLException e) {
            logger.warning("SQLException in add()");
        }
        return res;
    }

    public boolean editState(GetBookRequest entity) {
        var res = false;
        try (var statement = connect.prepareStatement(resBundle.getString("query.request.editState"))) {
            statement.setString(1, entity.getState().toString());
            statement.setInt(2, entity.getId());

            statement.executeUpdate();
            res = true;
        } catch (SQLException e) {
            logger.warning("SQLException in editState()");
        }
        return res;
    }
}
