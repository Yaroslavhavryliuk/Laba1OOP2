package servlet.stats;

import connection.ConnectionPool;
import connection.TransactionManager;
import connection.dao.BookBalanceLogDAO;
import connection.dao.BookStatsDAO;
import entity.book.history.BookBalanceLogRecord;
import servlet.AuthorizeHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet(urlPatterns = "/stats/update")
public class StatsUpdateServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!AuthorizeHelper.authorizeAdmin(req, resp)) {
            return;
        }

        var idStr =  req.getParameter("id");
        var bookIDStr =  req.getParameter("book_id");

        if (idStr == null && bookIDStr == null) {
            resp.sendError(400);
            return;
        }

        var connect = ConnectionPool.getInstance().getConnection();
        TransactionManager.start(connect);

        var statsDAO = new BookStatsDAO(connect);
        var balanceLogDAO = new BookBalanceLogDAO(connect);
        var stats = (idStr != null) ? statsDAO.get(Integer.parseInt(idStr)) : statsDAO.getByBookId(Integer.parseInt(bookIDStr));

        var amountStr =  req.getParameter("amount");
        if (amountStr != null) {
            var login = req.getParameter("login");
            var amount = Integer.parseInt(amountStr);
            if (amount < 0) {
                resp.sendError(400);
                TransactionManager.rollback(connect);
                ConnectionPool.getInstance().putConnection(connect);
                return;
            }

            var amountDiff = amount - stats.getAmount();
            stats.setAmount(amount);

            statsDAO.edit(stats);
            if (amountDiff != 0) {
                balanceLogDAO.add(new BookBalanceLogRecord(LocalDateTime.now(), stats.getBookID(), amountDiff, "admin: " + login));
            }
        }


        TransactionManager.commit(connect);
        ConnectionPool.getInstance().putConnection(connect);
    }
}
