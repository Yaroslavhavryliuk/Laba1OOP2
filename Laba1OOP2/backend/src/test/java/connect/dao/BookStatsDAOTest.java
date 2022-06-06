package connection.dao;

import connection.ConnectionPool;
import entity.book.Book;
import entity.book.BookStats;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookStatsDAOTest {
    private static ConnectionPool pool = new ConnectionPool("database-test");
    private Connection connect;

    @BeforeEach
    public void setConnection() {
        connect = pool.getConnection();
    }

    @AfterEach
    public void returnConnection() {
        pool.putConnection(connect);
    }

    @BeforeAll
    public static void fillDatabase() {
        var connect = pool.getConnection();
        var bookDao = new BookDAO(connect);
        bookDao.create(new Book("book1", "author1", "ua", new String[]{}));
        bookDao.create(new Book("book2", "author2", "en", new String[]{}));
        bookDao.create(new Book("book3", "author3", "pl", new String[]{}));
        pool.putConnection(connect);
    }

    @AfterAll
    public static void clearAfter() throws SQLException {
        DAOTestHelper.clearAfter(pool);
        pool = null;
    }

    @Test
    @Order(1)
    public void insertAndGet() {
        var bookStats1 = new BookStats(1, 1, 1, 1, 1);
        var bookStats2 = new BookStats(2, 2, 2, 2, 2);
        var bookStats3 = new BookStats(3, 3, 3, 3, 3);

        DAOTestHelper.insertAndGetTest(new BookStatsDAO(conn), bookStats1, bookStats2, bookStats3);
    }

    @Test
    @Order(2)
    public void getByBookId() {
        var dao = new BookStatsDAO(connect);

        assertEquals(dao.find(1), dao.getByBookId(1));
        assertEquals(dao.find(2), dao.getByBookId(2));
        assertEquals(dao.find(3), dao.getByBookId(3));
        assertNull(dao.getByBookId(4));
    }

    @Test
    @Order(3)
    public void edit() {
        var dao = new BookStatsDAO(connect);

        var bookStats1 = dao.get(1);
        bookStats1.setBookID(2);
        dao.edit(bookStats1);
        assertEquals(2, dao.get(1).getBookID());

        var bookStats2 = dao.get(2);
        bookStats2.setAmount(10);
        bookStats2.setRate(5);
        dao.edit(bookStats2);
        assertEquals(10, dao.get(2).getAmount());
        assertEquals(5, dao.get(2).getRate());

        var bookStats3 = dao.get(3);
        bookStats3.setId(1);
        dao.edit(bookStats3);
        assertEquals(bookStats3, dao.get(1));
    }

    @Test
    @Order(4)
    public void delete() {
        DAOTestHelper.deleteTest(new BookStatsDAO(connect));
    }
}