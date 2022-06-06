package servlet.stats;

import com.google.gson.Gson;
import connection.ConnectionPool;
import connection.TransactionManager;
import connection.dao.BookStatsDAO;
import connection.dao.BookStatsHistoryDAO;
import entity.book.history.BookStatsHistoryRecord;
import servlet.AuthorizeHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet(urlPatterns = "/stats/history")
public class StatsHistoryServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!AuthorizeHelper.authorizeAdmin(req, resp)) {
            return;
        }

        var bookIDStr = req.getParameter("book_id");
        var periodStartStr = req.getParameter("period_start");
        var periodEndStr = req.getParameter("period_end");

        var connect = ConnectionPool.getInstance().getConnection();
        var historyDAO = new BookStatsHistoryDAO(connect);
        var out = resp.getWriter();

        List<BookStatsHistoryRecord> history;
        if (bookIDStr != null) {
            var bookID = Integer.parseInt(bookIDStr);
            history = historyDAO.getByBookId(bookID);
        } else if (periodStartStr != null && periodEndStr != null) {
            var periodStart = LocalDate.parse(periodStartStr);
            var periodEnd = LocalDate.parse(periodEndStr);
            history = historyDAO.getInPeriod(periodStart, periodEnd);
        } else {
            history = historyDAO.getAll();
        }

        out.print(gson.toJson(history));
        out.flush();
        ConnectionPool.getInstance().putConnection(connect);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!AuthorizeHelper.authorizeAdmin(req, resp)) {
            return;
        }

        var connect = ConnectionPool.getInstance().getConnection();
        TransactionManager.start(connect);

        var statsDAO = new BookStatsDAO(connect);
        var historyDAO = new BookStatsHistoryDAO(connect);

        var history = statsDAO.getAll();
        for (var item : history) {
            historyDAO.add(new BookStatsHistoryRecord(LocalDate.now(), item));
        }

        TransactionManager.commit(connect);
        ConnectionPool.getInstance().putConnection(connect);
    }
}
