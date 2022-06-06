package connection.dao;

import connection.ConnectionPool;
import entity.book.Book;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookDAOTest {
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
        var book1 = new Book(1, "name1", "author1", "lang1", new String[]{"Tag1", "Tag2"});
        var book2 = new Book(2, "name2", "author2", "lang2", new String[]{"Tag3"});
        var book3 = new Book(3, "name3", "author3", "lang3", new String[]{"Tag1"});

        DAOTestHelper.insertAndGetTest(new BookDAO(connect), book1, book2, book3);
    }

    @Test
    @Order(2)
    public void getId() {
        var dao = new BookDAO(connect);

        var book1 = new Book("name1", "author1", "lang1", new String[]{"Tag1", "Tag2"});
        var book2 = new Book("name2", "author2", "lang2", new String[]{"Tag3"});
        var book3 = new Book("name3", "author3", "lang3", new String[]{"Tag1"});
        var book4 = new Book("name4", "author4", "lang4", new String[]{"Tag1"});

        assertEquals(1,dao.getId(book1));
        assertEquals(2,dao.getId(book2));
        assertEquals(3,dao.getId(book3));
        assertEquals(-1,dao.getId(book4));
    }

    @Test
    @Order(3)
    public void edit() {
        var dao = new BookDAO(connect);

        var book1 = dao.get(1);
        book1.setName("definitely new name");
        dao.edit(book1);
        assertEquals("definitely new name", dao.get(1).getName());

        var book2 = dao.get(2);
        book2.setAuthor("definitely new author");
        book2.setLang("definitely new lang");
        dao.edit(book2);
        assertEquals("definitely new author", dao.get(2).getAuthor());
        assertEquals("definitely new lang", dao.get(2).getLang());

        var book3 = dao.get(3);
        book3.setId(1);
        dao.edit(book3);
        assertEquals(book3, dao.get(1));
    }

    @Test
    @Order(4)
    public void delete() {
        DAOTestHelper.deleteTest(new BookDAO(connect));
    }
}