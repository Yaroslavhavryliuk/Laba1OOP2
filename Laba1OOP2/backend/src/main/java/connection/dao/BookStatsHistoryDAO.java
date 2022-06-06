package connection.dao;

import connection.dao.interfaces.InsertFindDAO;
import entity.book.BookStats;
import entity.book.history.BookStatsHistoryRecord;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class BookStatsHistoryDAO implements InsertFindDAO<BookStatsHistoryRecord> {
    private final Connection connect;
    private final ResourceBundle resBundle = ResourceBundle.getBundle("database");
    private final Logger logger = Logger.getLogger(BookStatsHistoryDAO.class.getName());

    public BookStatsHistoryDAO(Connection connect) {
        this.connect = connect;
    }

    private BookStatsHistoryRecord mapper(ResultSet rs) throws SQLException {
        var id = rs.getInt("id");
        var date = rs.getDate("date").toLocalDate();
        var bookID = rs.getInt("book_id");
        var amount = rs.getInt("amount");
        var totalRequests = rs.getInt("total_requests");
        var rate = rs.getDouble("rate");

        return new BookStatsHistoryRecord(id, date, new BookStats(bookID, amount, totalRequests, rate));
    }

    @Override
    public List<BookStatsHistoryRecord> getAll() {
        try (var statement = connect.createStatement()) {
            try (var resultSet = statement.executeQuery(resBundle.getString("query.stats_history.getAll"))) {
                var list = new ArrayList<BookStatsHistoryRecord>();
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
    public BookStatsHistoryRecord get(int id) {
        try (var statement = connect.prepareStatement(resBundle.getString("query.stats_history.get"))) {
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

    public List<BookStatsHistoryRecord> getByBookId(int bookID) {
        try (var statement = connect.prepareStatement(resBundle.getString("query.stats_history.getByBookId"))) {
            statement.setInt(1, bookID);

            try (var resultSet = statement.executeQuery()) {
                var list = new ArrayList<BookStatsHistoryRecord>();
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

    public List<BookStatsHistoryRecord> getInPeriod(LocalDate periodStart, LocalDate periodEnd) {
        try (var statement = connect.prepareStatement(resBundle.getString("query.stats_history.getInPeriod"))) {
            statement.setDate(1, Date.valueOf(periodStart));
            statement.setDate(2, Date.valueOf(periodEnd));

            try (var resultSet = statement.executeQuery()) {
                var list = new ArrayList<BookStatsHistoryRecord>();
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
    public boolean add(BookStatsHistoryRecord entity) {
        var res = false;
        try (var statement = connect.prepareStatement(resBundle.getString("query.stats_history.add"))) {
            statement.setDate(1, Date.valueOf(entity.getDate()));
            statement.setInt(2, entity.getStats().getBookID());
            statement.setInt(3, entity.getStats().getAmount());
            statement.setInt(4, entity.getStats().getTotalRequests());
            statement.setDouble(5, entity.getStats().getRate());

            statement.executeUpdate();
            res = true;
        } catch (SQLException e) {
            logger.warning("SQLException in add()");
        }
        return res;
    }
}
