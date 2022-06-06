package connection.dao;

import connection.dao.interfaces.InsertFindDAO;
import entity.book.history.BookBalanceLogRecord;
import entity.book.history.BookRateLogRecord;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class BookRateLogDAO implements InsertFindDAO<BookRateLogRecord> {
    private final Connection connect;
    private final ResourceBundle resBundle = ResourceBundle.getBundle("database");
    private final Logger logger = Logger.getLogger(BookRateLogDAO.class.getName());

    public BookRateLogDAO(Connection connect) {
        this.connect = connect;
    }

    private BookRateLogRecord mapper(ResultSet rs) throws SQLException {
        var id = rs.getInt("id");
        var datetime = rs.getTimestamp("datetime").toLocalDateTime();
        var bookID = rs.getInt("book_id");
        var userID = rs.getInt("user_id");
        var contribution = rs.getDouble("contribution");

        return new BookRateLogRecord(id, datetime, bookID, userID, contribution);
    }

    @Override
    public List<BookRateLogRecord> getAll() {
        try (var statement = connect.createStatement()) {
            try (var resultSet = statement.executeQuery(resBundle.getString("query.rate_changelog.getAll"))) {
                var list = new ArrayList<BookRateLogRecord>();
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
    public BookRateLogRecord get(int id) {
        try (var statement = connect.prepareStatement(resBundle.getString("query.rate_changelog.get"))) {
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

    public List<BookRateLogRecord> getByBookId(int bookID) {
        try (var statement = connect.prepareStatement(resBundle.getString("query.rate_changelog.getByBookId"))) {
            statement.setInt(1, bookID);

            try (var resultSet = statement.executeQuery()) {
                var list = new ArrayList<BookRateLogRecord>();
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

    public List<BookRateLogRecord> getInPeriod(LocalDateTime periodStart, LocalDateTime periodEnd) {
        try (var statement = connect.prepareStatement(resBundle.getString("query.rate_changelog.getInPeriod"))) {
            statement.setTimestamp(1, Timestamp.valueOf(periodStart));
            statement.setTimestamp(2, Timestamp.valueOf(periodEnd));

            try (var resultSet = statement.executeQuery()) {
                var list = new ArrayList<BookRateLogRecord>();
                while (resultSet.next()) {
                    list.add(mapper(resultSet));
                }
                return list;
            }
        } catch (SQLException e) {
            logger.warning("SQLException in getInPeriod()");
        }
        return null;
    }

    @Override
    public boolean add(BookRateLogRecord entity) {
        var res = false;
        try (var statement = connect.prepareStatement(resBundle.getString("query.rate_changelog.add"))) {
            statement.setTimestamp(1, Timestamp.valueOf(entity.getDatetime()));
            statement.setInt(2, entity.getBookID());
            statement.setInt(3, entity.getUserID());
            statement.setDouble(4, entity.getContribution());

            statement.executeUpdate();
            res = true;
        } catch (SQLException e) {
            logger.warning("SQLException in add()");
        }
        return res;
    }
}
