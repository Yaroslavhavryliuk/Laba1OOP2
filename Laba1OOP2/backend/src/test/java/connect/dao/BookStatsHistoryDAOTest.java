package connection.dao;

import connection.ConnectionPool;
import entity.book.Book;
import entity.book.BookStats;
import entity.book.history.BookStatsHistoryRecord;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookStatsHistoryDAOTest {
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
        var record1 = new BookStatsHistoryRecord(1, LocalDate.parse("2020-08-09"), new BookStats(1, 5, 10, 4.5));
        var record2 = new BookStatsHistoryRecord(2, LocalDate.parse("2021-05-06"), new BookStats(2, 3, 2, 4.2));
        var record3 = new BookStatsHistoryRecord(3, LocalDate.parse("2021-05-06"), new BookStats(1, 4, 17, 4.7));
        var record4 = new BookStatsHistoryRecord(4, LocalDate.parse("2021-01-01"), new BookStats(5, 4, 17, 4.7));

        var dao = new BookStatsHistoryDAO(connect);
        DAOTestHelper.insertAndGetTest(dao, record1, record2, record3);
    }

    @Test
    @Order(2)
    public void getByBookId() {
        var dao = new BookStatsHistoryDAO(connect);

        var list1 = dao.getByBookId(1);
        assertEquals(2, list1.size());
        assertTrue(list1.contains(dao.get(1)));
        assertTrue(list1.contains(dao.get(3)));

        var list2 = dao.getByBookId(2);
        assertEquals(1, list2.size());
        assertTrue(list2.contains(dao.get(2)));
    }

    @Test
    @Order(3)
    public void getInPeriod() {
        var dao = new BookStatsHistoryDAO(connect);

        var list1 = dao.getInPeriod(LocalDate.parse("2021-12-12"), LocalDate.parse("2021-12-13"));
        assertTrue(list1.isEmpty());

        var list2 = dao.getInPeriod(LocalDate.parse("2021-04-17"), LocalDate.parse("2021-06-07"));
        assertEquals(2, list2.size());
        assertTrue(list2.contains(dao.get(2)));
        assertTrue(list2.contains(dao.get(3)));

        var list3 = dao.getInPeriod(LocalDate.parse("2020-01-01"), LocalDate.parse("2020-12-31"));
        assertEquals(1, list3.size());
        assertTrue(list3.contains(dao.get(1)));
    }
}