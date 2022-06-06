package connection.dao;

import connection.ConnectionPool;
import entity.book.Book;

import entity.book.request.GetBookRequest;
import entity.book.request.RequestState;
import entity.book.request.ReturnBookRequest;
import entity.misc.DeliveryType;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReturnRequestDAOTest {
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
        bookDao.add(new Book("book1", "author1", "ua", new String[]{}));
        bookDao.add(new Book("book2", "author2", "en", new String[]{}));

        var deliveryTypeDao = new DeliveryTypeDAO(connect);
        deliveryTypeDao.add(new DeliveryType("home"));
        deliveryTypeDao.add(new DeliveryType("univ"));

        var dao = new BookRequestDAO(connect);
        dao.add(new GetBookRequest(LocalDateTime.parse("2020-04-05T06:40:30"), 1, 1, 1, "", RequestState.SENT));
        dao.add(new GetBookRequest(LocalDateTime.parse("2010-12-13T15:02:00"), 1, 2, 1, "@test", RequestState.SENT));
        dao.add(new GetBookRequest(LocalDateTime.parse("2021-07-08T14:00:00"), 2, 2, 2, "", RequestState.SENT));

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
        var dao = new ReturnRequestDAO(connect);

        var rReq1 = new ReturnBookRequest(1, LocalDateTime.parse("2020-04-16T06:40:30"), 1, RequestState.SENT);
        var rReq2 = new ReturnBookRequest(2, LocalDateTime.parse("2011-01-13T15:02:00"), 2, RequestState.SENT);
        var rReq3 = new ReturnBookRequest(3, LocalDateTime.parse("2021-09-09T14:00:00"), 3, RequestState.SENT);

        DAOTestHelper.insertAndGetTest(dao, rReq1, rReq2, rReq3);
    }

    @Test
    @Order(2)
    public void getByUserId() {
        var dao = new ReturnRequestDAO(connect);

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
        var dao = new ReturnRequestDAO(connect);

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
        var dao = new ReturnRequestDAO(connect);

        var rReq1 = dao.get(1);
        rReq1.setState(RequestState.PROCESSED);
        dao.editState(rReq1);
        assertEquals(RequestState.PROCESSED, dao.get(1).getState());

        var rReq2 = dao.get(2);
        rReq2.setState(RequestState.PROCESSED);
        dao.editState(rReq2);
        assertEquals(RequestState.PROCESSED, dao.get(2).getState());
    }

}