package servlet.stats;

import com.google.gson.Gson;
import connection.ConnectionPool;
import connection.dao.BookStatsDAO;
import servlet.AuthorizeHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/stats")
public class StatsServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!AuthorizeHelper.authorize(req, resp)) {
            return;
        }

        var connect = ConnectionPool.getInstance().getConnection();
        var statsDAO = new BookStatsDAO(connect);
        var out = resp.getWriter();

        var bookIDStr = req.getParameter("book_id");
        if (bookIDStr != null) {
            var bookID = Integer.parseInt(bookIDStr);
            var statsItem = statsDAO.getByBookId(bookID);
            out.println(gson.toJson(statsItem));

            out.flush();
            ConnectionPool.getInstance().putConnection(connect);
            return;
        }

        var idStr = req.getParameter("id");

        if (idStr == null || idStr.equals("-1")) {
            var stats = statsDAO.getAll();
            out.print(gson.toJson(stats));
        } else {
            var id = Integer.parseInt(idStr);
            var statsItem = statsDAO.get(id);
            out.println(gson.toJson(statsItem));
        }

        out.flush();
        ConnectionPool.getInstance().putConnection(connect);
    }
}
