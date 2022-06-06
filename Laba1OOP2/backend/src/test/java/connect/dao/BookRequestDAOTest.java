package connection.dao;

import connection.ConnectionPool;
import entity.book.Book;
import entity.book.request.GetBookRequest;
import entity.book.request.RequestState;
import entity.misc.DeliveryType;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Considering that test db has test data in users table<br/>
 * See {@link UserDAOTest} description for more info
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookRequestDAOTest {
    private static ConnectionPool pool= new ConnectionPool("database-test");
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

        var deliveryTypeDao = new DeliveryTypeDAO(connect);
        deliveryTypeDao.create(new DeliveryType("home"));
        deliveryTypeDao.create(new DeliveryType("univ"));

        pool.putConnection(connect);
    }

    @AfterAll
    public static void clearAfter() throws SQLException {
        DAOTestHelper.clearAfter(pool);
        pool = null;
    }

    @Test
    @Order(1)
    public void insertAndGetAll() {
        var dao = new BookRequestDAO(connect);

        var req1 = new GetBookRequest(1, LocalDateTime.parse("2020-04-05T06:40:30"), 1, 1, 1, "@test1", RequestState.SENT);
        var req2 = new GetBookRequest(2, LocalDateTime.parse("2010-12-13T15:02:00"), 1, 2, 1, "@test2", RequestState.SENT);
        var req3 = new GetBookRequest(3, LocalDateTime.parse("2021-07-08T14:00:00"), 2, 2, 2, "@test3", RequestState.SENT);

        DAOTestHelper.insertAndGetTest(dao, req1, req2, req3);
    }

    @Test
    @Order(2)
    public void getByUserId() {
        var dao = new BookRequestDAO(connect);

        var list1 = dao.getByUserId(1);
        assertTrue(list1.contains(dao.get(1)));
        assertTrue(list1.contains(dao.get(2)));
        assertFalse(list1.contains(dao.get(3)));

        var list2 = dao.getByUserId(2);
        assertFalse(list2.contains(dao.get(1)));
        assertFalse(list2.contains(dao.get(2)));
        assertTrue(list2.contains(dao.get(3)));
    }

    @Test
    @Order(3)
    public void getByBookId() {
        var dao = new BookRequestDAO(connect);

        var list1 = dao.getByBookId(1);
        assertTrue(list1.contains(dao.get(1)));
        assertFalse(list1.contains(dao.get(2)));
        assertFalse(list1.contains(dao.get(3)));

        var list2 = dao.getByBookId(2);
        assertFalse(list2.contains(dao.get(1)));
        assertTrue(list2.contains(dao.get(2)));
        assertTrue(list2.contains(dao.get(3)));
    }

    @Test
    @Order(4)
    public void editState() {
        var dao = new BookRequestDAO(connect);

        var req1 = dao.get(1);
        req1.setState(RequestState.IN_PROGRESS);
        dao.editState(req1);
        assertEquals(RequestState.IN_PROGRESS, dao.get(1).getState());

        var req2 = dao.get(2);
        req2.setState(RequestState.REJECTED);
        dao.editState(req2);
        assertEquals(RequestState.REJECTED, dao.get(2).getState());
    }

}