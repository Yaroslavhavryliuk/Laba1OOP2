package connection.dao;

import connection.dao.interfaces.BaseDAO;
import entity.book.Book;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class BookDAO implements BaseDAO<Book> {
    private final Connection connect;
    private final ResourceBundle resBundle = ResourceBundle.getBundle("database");
    private final Logger logger = Logger.getLogger(BookDAO.class.getName());

    public BookDAO(Connection connect) {
        this.connect = connect;
    }

    private Book mapper(ResultSet rs) throws SQLException {
        var id = rs.getInt("id");
        var name = rs.getString("name");
        var author = rs.getString("author");
        var lang = rs.getString("lang");
        var tags = (String[]) rs.getArray("tags").getArray();
        return new Book(id, name, author, lang, tags);
    }

    @Override
    public List<Book> getAll() {
        try (var statement = connect.createStatement()) {
            try (var resultSet = statement.executeQuery(resBundle.getString("query.book.getAll"))) {
                var list = new ArrayList<Book>();
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
    public Book get(int id) {
        try (var statement = connect.prepareStatement(resBundle.getString("query.book.get"))) {
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

    public int getId(Book entity) {
        int id = -1;
        try (var statement = connect.prepareStatement(resBundle.getString("query.book.getId"))) {
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getAuthor());
            statement.setString(3, entity.getLang());

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    id = resultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
            logger.warning("SQLException in getId()");
        }
        return id;
    }

    @Override
    public boolean add(Book entity) {
        var res = false;
        try (var statement = connect.prepareStatement(resBundle.getString("query.book.add"))) {
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getAuthor());
            statement.setString(3, entity.getLang());
            statement.setArray(4, connect.createArrayOf("text", entity.getTags()));

            statement.executeUpdate();
            res = true;
        } catch (SQLException e) {
            logger.warning("SQLException in add()");
        }
        return res;
    }

    @Override
    public boolean edit(Book entity) {
        var res = false;
        try (var statement = connect.prepareStatement(resBundle.getString("query.book.edit"))) {
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getAuthor());
            statement.setString(3, entity.getLang());
            statement.setArray(4, connect.createArrayOf("text", entity.getTags()));
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
        try (var statement = connect.prepareStatement(resBundle.getString("query.book.delete"))) {
            statement.setInt(1, id);

            statement.executeUpdate();
            res = true;
        } catch (SQLException e) {
            logger.warning("SQLException in delete()");
        }
        return res;
    }
}
