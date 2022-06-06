package servlet.books;

import connection.ConnectionPool;
import connection.TransactionManager;
import connection.dao.BookDAO;
import servlet.AuthorizeHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/books/delete")
public class BooksDeleteServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!AuthorizeHelper.authorizeAdmin(req, resp)) {
            return;
        }

        var idStr =  req.getParameter("id");

        if (idStr == null) {
            resp.sendError(400);
            return;
        }

        var connect = ConnectionPool.getInstance().getConnection();
        TransactionManager.start(connect);

        var bookDao = new BookDAO(connect);
        bookDao.delete(Integer.parseInt(idStr));

        TransactionManager.commit(connect);
        ConnectionPool.getInstance().putConnection(connect);
    }
}
