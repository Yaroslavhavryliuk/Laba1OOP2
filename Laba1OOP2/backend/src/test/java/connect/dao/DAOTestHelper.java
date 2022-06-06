package connection.dao;

import connection.ConnectionPool;
import connection.dao.interfaces.BaseDAO;
import connection.dao.interfaces.InsertFindDAO;
import entity.Entity;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DAOTestHelper {
    public static void clearAfter(ConnectionPool pool) throws SQLException {
        var connect = pool.getConnection();
        var callableStatement = connect.prepareCall("CALL truncate_all()");
        callableStatement.execute();
    }

    public static <E extends Entity> void insertAndGetTest (InsertFindDAO<E> dao, E e1, E e2, E e3) {
        assertEquals(0, dao.getAll().size());

        dao.add(e1);
        assertEquals(e1, dao.get(1));
        assertEquals(1, dao.getAll().size());
        assertTrue(dao.getAll().contains(e1));

        dao.add(e2);
        assertEquals(e2, dao.get(2));
        assertEquals(2, dao.getAll().size());
        assertTrue(dao.getAll().contains(e1));
        assertTrue(dao.getAll().contains(e2));

        dao.add(e3);
        assertEquals(e3, dao.get(3));
        assertEquals(3, dao.getAll().size());
        assertTrue(dao.getAll().contains(e1));
        assertTrue(dao.getAll().contains(e2));
        assertTrue(dao.getAll().contains(e3));
    }

    public static <E extends Entity> void deleteTest (BaseDAO<E> dao) {
        var book1 = dao.get(1);
        var book2 = dao.get(2);
        var book3 = dao.get(3);

        assertEquals(3, dao.getAll().size());
        assertTrue(dao.getAll().contains(book1));
        assertTrue(dao.getAll().contains(book2));
        assertTrue(dao.getAll().contains(book3));

        dao.delete(2);
        assertEquals(2, dao.getAll().size());
        assertTrue(dao.getAll().contains(book1));
        assertTrue(dao.getAll().contains(book3));

        dao.delete(1);
        assertEquals(1, dao.getAll().size());
        assertTrue(dao.getAll().contains(book3));

        dao.delete(2);
        assertEquals(1, dao.getAll().size());
        assertTrue(dao.getAll().contains(book3));

        dao.delete(3);
        assertEquals(0, dao.getAll().size());
    }
}
