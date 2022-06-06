package servlet.books;

import connection.ConnectionPool;
import connection.TransactionManager;
import connection.dao.BookDAO;
import connection.dao.BookStatsDAO;
import entity.book.Book;
import entity.book.BookStats;
import servlet.AuthorizeHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/books/add")
public class BooksAddServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!AuthorizeHelper.authorizeAdmin(req, resp)) {
            return;
        }

        var name =  req.getParameter("name");
        var author =  req.getParameter("author");
        var lang =  req.getParameter("lang");
        var tags =  req.getParameterValues("tag");

        if (name == null || author == null || lang == null) {
            resp.sendError(400);
            return;
        }

        var connect = ConnectionPool.getInstance().getConnection();
        TransactionManager.start(connect);

        var bookDAO = new BookDAO(connect);
        var statsDAO = new BookStatsDAO(connect);

        var book = new Book(name, author, lang, tags);
        if (bookDAO.getId(book) != -1) {
            resp.sendError(409);
            TransactionManager.rollback(connect);
            ConnectionPool.getInstance().putConnection(connect);
            return;
        }

        bookDAO.add(book);

        var book_id = bookDAO.getId(book);
        statsDAO.add(new BookStats(book_id, 0, 0, 0));

        TransactionManager.commit(connect);
        ConnectionPool.getInstance().putConnection(connect);
    }
}
