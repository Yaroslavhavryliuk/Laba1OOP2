package connection.dao;

import connection.dao.interfaces.InsertFindDAO;
import entity.book.request.RequestState;
import entity.book.request.ReturnBookRequest;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class ReturnRequestDAO implements InsertFindDAO<ReturnBookRequest> {
    private final Connection connect;
    private final ResourceBundle resBundle = ResourceBundle.getBundle("database");
    private final Logger logger = Logger.getLogger(ReturnRequestDAO.class.getName());

    public ReturnRequestDAO(Connection connect) {
        this.connect = connect;
    }

    private ReturnBookRequest mapper(ResultSet rs) throws SQLException {
        var id = rs.getInt("id");
        var datetime = rs.getTimestamp("datetime").toLocalDateTime();
        var request_id = rs.getInt("request_id");
        var state = RequestState.valueOf(rs.getString("state").toUpperCase());

        return new ReturnBookRequest(id, datetime, request_id, state);
    }

    @Override
    public List<ReturnBookRequest> getAll() {
        try (var statement = connect.createStatement()) {
            try (var resultSet = statement.executeQuery(resBundle.getString("query.return_request.getAll"))) {
                var list = new ArrayList<ReturnBookRequest>();
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
    public ReturnBookRequest get(int id) {
        try (var statement = connect.prepareStatement(resBundle.getString("query.return_request.get"))) {
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

    public List<ReturnBookRequest> getByUserId(int userID) {
        try (var statement = connect.prepareStatement(resBundle.getString("query.return_request.getByUserId"))) {
            statement.setInt(1, userID);

            try (var resultSet = statement.executeQuery()) {
                var list = new ArrayList<ReturnBookRequest>();
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

    public List<ReturnBookRequest> getByBookId(int bookID) {
        try (var statement = connect.prepareStatement(resBundle.getString("query.return_request.getByBookId"))) {
            statement.setInt(1, bookID);

            try (var resultSet = statement.executeQuery()) {
                var list = new ArrayList<ReturnBookRequest>();
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
    public boolean add(ReturnBookRequest entity) {
        var res = false;
        try (var statement = connect.prepareStatement(resBundle.getString("query.return_request.add"))) {
            statement.setTimestamp(1, Timestamp.valueOf(entity.getDatetime()));
            statement.setInt(2, entity.getGetBookRequestID());
            statement.setString(3, entity.getState().toString());

            statement.executeUpdate();
            res = true;
        } catch (SQLException e) {
            logger.warning("SQLException in add()");
        }
        return res;
    }

    public boolean editState(ReturnBookRequest entity) {
        var res = false;
        try (var statement = connect.prepareStatement(resBundle.getString("query.return_request.editState"))) {
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
