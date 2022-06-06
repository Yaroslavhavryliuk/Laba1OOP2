package connection.dao;

import connection.dao.interfaces.InsertFindDAO;
import entity.book.history.BookBalanceLogRecord;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class BookBalanceLogDAO implements InsertFindDAO<BookBalanceLogRecord> {
    private final Connection connect;
    private final ResourceBundle resBundle = ResourceBundle.getBundle("database");
    private final Logger logger = Logger.getLogger(BookBalanceLogDAO.class.getName());

    public BookBalanceLogDAO(Connection connect) {
        this.connect = connect;
    }

    private BookBalanceLogRecord mapper(ResultSet rs) throws SQLException{
        var id = rs.getInt("id");
        var datetime = rs.getTimestamp("datetime").toLocalDateTime();
        var bookID = rs.getInt("book_id");
        var amount = rs.getInt("amount");
        var comment = rs.getString("comment");

        return new BookBalanceLogRecord(id, datetime, bookID, amount, comment);
    }

    @Override
    public List<BookBalanceLogRecord> getAll() {
        try (var statement = connect.createStatement()) {
            try (var resultSet = statement.executeQuery(resBundle.getString("query.balance_changelog.getAll"))) {
                var list = new ArrayList<BookBalanceLogRecord>();
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
    public BookBalanceLogRecord get(int id) {
        try (var statement = connect.prepareStatement(resBundle.getString("query.balance_changelog.get"))) {
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

    public List<BookBalanceLogRecord> getByBookId(int bookId) {
        try (var statement = connect.prepareStatement(resBundle.getString("query.balance_changelog.getByBookId"))) {
            statement.setInt(1, bookId);

            try (var resultSet = statement.executeQuery()) {
                var list = new ArrayList<BookBalanceLogRecord>();
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

    public List<BookBalanceLogRecord> getInPeriod(LocalDateTime periodStart, LocalDateTime periodEnd) {
        try (var statement = connect.prepareStatement(resBundle.getString("query.balance_changelog.getInPeriod"))) {
            statement.setTimestamp(1, Timestamp.valueOf(periodStart));
            statement.setTimestamp(2, Timestamp.valueOf(periodEnd));

            try (var resultSet = statement.executeQuery()) {
                var list = new ArrayList<BookBalanceLogRecord>();
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
    public boolean add(BookBalanceLogRecord entity) {
        var res = false;
        try (var statement = connect.prepareStatement(resBundle.getString("query.balance_changelog.add"))) {
            statement.setTimestamp(1, Timestamp.valueOf(entity.getDatetime()));
            statement.setInt(2, entity.getBookId());
            statement.setInt(3, entity.getAmount());
            statement.setString(4, entity.getComment());

            statement.executeUpdate();
            res = true;
        } catch (SQLException e) {
            logger.warning("SQLException in add()");
        }
        return res;
    }
}
