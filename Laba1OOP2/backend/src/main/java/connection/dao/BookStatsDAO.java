package connection.dao;

import connection.dao.interfaces.BaseDAO;
import entity.book.BookStats;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class BookStatsDAO implements BaseDAO<BookStats> {
    private final Connection connect;
    private final ResourceBundle resBundle = ResourceBundle.getBundle("database");
    private final Logger logger = Logger.getLogger(BookStatsDAO.class.getName());

    public BookStatsDAO(Connection connect) {
        this.connect = connect;
    }

    private BookStats mapper(ResultSet rs) throws SQLException {
        var id = rs.getInt("id");
        var bookID = rs.getInt("book_id");
        var amount = rs.getInt("amount");
        var totalRequests = rs.getInt("total_requests");
        var rate = rs.getDouble("rate");
        return new BookStats(id, bookID, amount, totalRequests, rate);
    }

    @Override
    public List<BookStats> getAll() {
        try (var statement = connect.createStatement()) {
            try (var resultSet = statement.executeQuery(resBundle.getString("query.book_stats.getAll"))) {
                var list = new ArrayList<BookStats>();
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
    public BookStats get(int id) {
        try (var statement = connect.prepareStatement(resBundle.getString("query.book_stats.get"))) {
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

    public BookStats getByBookId(int book_id) {
        try (var statement = connect.prepareStatement(resBundle.getString("query.book_stats.getByBookId"))) {
            statement.setInt(1, book_id);

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapper(resultSet);
                }
            }
        } catch (SQLException e) {
            logger.warning("SQLException in getByBookId()");
        }
        return null;
    }

    @Override
    public boolean add(BookStats entity) {
        var res = false;
        try (var statement = connect.prepareStatement(resBundle.getString("query.book_stats.add"))) {
            statement.setInt(1, entity.getBookID());
            statement.setInt(2, entity.getAmount());
            statement.setInt(3, entity.getTotalRequests());
            statement.setDouble(4, entity.getRate());

            statement.executeUpdate();
            res = true;
        } catch (SQLException e) {
            logger.warning("SQLException in add()");
        }
        return res;
    }

    @Override
    public boolean edit(BookStats entity) {
        var res = false;
        try (var statement = connect.prepareStatement(resBundle.getString("query.book_stats.edit"))) {
            statement.setInt(1, entity.getBookID());
            statement.setInt(2, entity.getAmount());
            statement.setInt(3, entity.getTotalRequests());
            statement.setDouble(4, entity.getRate());
            statement.setInt(5, entity.getId());

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
        try (var statement = connect.prepareStatement(resBundle.getString("query.book_stats.delete"))) {
            statement.setInt(1, id);

            statement.executeUpdate();
            res = true;
        } catch (SQLException e) {
            logger.warning("SQLException in delete()");
        }
        return res;
    }
}
