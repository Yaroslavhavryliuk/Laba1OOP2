package servlet.request;

import connection.ConnectionPool;
import connection.TransactionManager;
import connection.dao.BookBalanceLogDAO;
import connection.dao.BookRequestDAO;
import connection.dao.BookStatsDAO;
import entity.book.history.BookBalanceLogRecord;
import entity.book.request.RequestState;
import servlet.AuthorizeHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Locale;

@WebServlet(urlPatterns = "/request/update")
public class RequestUpdateServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!AuthorizeHelper.authorizeAdmin(req, resp)) {
            return;
        }

        var idStr = req.getParameter("id");
        var stateStr = req.getParameter("state");

        if (idStr == null || stateStr == null) {
            resp.sendError(400);
            return;
        }

        var connect = ConnectionPool.getInstance().getConnection();
        TransactionManager.begin(connect);

        var bookRequestDAO = new BookRequestDAO(connect);

        var id = Integer.parseInt(idStr);
        RequestState state;
        try {
            state = RequestState.valueOf(stateStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            resp.sendError(400);
            TransactionManager.rollback(connect);
            ConnectionPool.getInstance().putConnection(connect);
            return;
        }

        var request = bookRequestDAO.get(id);
        if (request.getState() == RequestState.PROCESSED) {
            resp.sendError(403);
            TransactionManager.rollback(connect);
            ConnectionPool.getInstance().putConnection(connect);
            return;
        }

        request.setState(state);
        bookRequestDAO.editState(request);

        if (state == RequestState.PROCESSED) {
            var statsDAO = new BookStatsDAO(connect);
            var balanceLogDAO = new BookBalanceLogDAO(connect);

            var stats = statsDAO.getByBookId(request.getBookID());
            if (stats.getAmount() == 0) {
                resp.sendError(400);
                TransactionManager.rollback(connect);
                ConnectionPool.getInstance().putConnection(connect);
                return;
            }
            stats.setAmount(stats.getAmount() - 1);
            stats.setTotalRequests(stats.getTotalRequests() + 1);
            statsDAO.edit(stats);

            balanceLogDAO.add(new BookBalanceLogRecord(LocalDateTime.now(), request.getBookID(), -1, "book request"));
        }

        TransactionManager.commit(connect);
        ConnectionPool.getInstance().putConnection(connect);
    }
}
