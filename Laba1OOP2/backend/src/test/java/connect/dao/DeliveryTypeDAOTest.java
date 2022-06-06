package connection.dao;

import connection.ConnectionPool;
import entity.book.Book;
import entity.book.BookStats;
import entity.misc.DeliveryType;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DeliveryTypeDAOTest {
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

    @AfterAll
    public static void clearAfter() throws SQLException {
        DAOTestHelper.clearAfter(pool);
        pool = null;
    }

    @Test
    @Order(1)
    public void insertAndGet() {
        var deliveryType1 = new DeliveryType(1, "home");
        var deliveryType2 = new DeliveryType(2, "univ");
        var deliveryType3 = new DeliveryType(3, "post");

        DAOTestHelper.insertAndGetTest(new DeliveryTypeDAO(connect), deliveryType1, deliveryType2, deliveryType3);
    }

    @Test
    @Order(2)
    public void edit() {
        var dao = new DeliveryTypeDAO(connect);

        var deliveryType1 = dao.get(1);
        deliveryType1.setDescription("new description");
        dao.edit(deliveryType1);
        assertEquals("new description", dao.get(1).getDescription());

        var deliveryType2 = dao.get(3);
        deliveryType2.setId(1);
        dao.edit(deliveryType2);
        assertEquals(deliveryType2, dao.get(1));
    }

    @Test
    @Order(3)
    public void delete() {
        DAOTestHelper.deleteTest(new DeliveryTypeDAO(connect));
    }

}