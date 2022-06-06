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

@WebServlet(urlPatterns = "/books/update")
public class BooksUpdateServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!AuthorizeHelper.authorizeAdmin(req, resp)) {
            return;
        }

        var idVar =  req.getParameterValues("id");

        if (idVar == null) {
            resp.sendError(400);
            return;
        }

        var connect = ConnectionPool.getInstance().getConnection();
        TransactionManager.start(connect);

        var bookDao = new BookDAO(connect);
        var book = bookDao.get(Integer.parseInt(idVar[0]));

        var name =  req.getParameter("name");
        var author =  req.getParameter("author");
        var lang =  req.getParameter("lang");
        var tags =  req.getParameterValues("tag");

        if (name != null){
            book.setName(name);
        }
        if (author != null){
            book.setAuthor(author);
        }
        if (lang != null){
            book.setLang(lang);
        }
        if (tags != null){
            book.setTags(tags);
        }

        bookDao.edit(book);

        TransactionManager.commit(connect);
        ConnectionPool.getInstance().putConnection(connect);
    }
}
