package servlet.returnRequest;

import connection.ConnectionPool;
import connection.TransactionManager;
import connection.dao.BookBalanceLogDAO;
import connection.dao.BookRequestDAO;
import connection.dao.BookStatsDAO;
import connection.dao.ReturnRequestDAO;
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

@WebServlet(urlPatterns = "/return_request/update")
public class ReturnRequestUpdateServlet extends HttpServlet {
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
        TransactionManager.start(connect);

        var returnRequestDAO = new ReturnRequestDAO(connect);

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

        var returnRequest = returnRequestDAO.get(id);
        if (returnRequest.getState() == RequestState.PROCESSED) {
            resp.sendError(403);
            TransactionManager.rollback(connect);
            ConnectionPool.getInstance().putConnection(connect);
            return;
        }

        returnRequest.setState(state);
        returnRequestDAO.editState(returnRequest);

        if (state == RequestState.PROCESSED) {
            var bookID = new BookRequestDAO(connect).get(returnRequest.getGetBookRequestID()).getBookID();

            var statsDAO = new BookStatsDAO(connect);
            var balanceLogDAO = new BookBalanceLogDAO(connect);

            var stats = statsDAO.getByBookId(bookID);
            stats.setAmount(stats.getAmount() + 1);
            statsDAO.edit(stats);

            balanceLogDAO.add(new BookBalanceLogRecord(LocalDateTime.now(), bookID, 1, "book returned"));
        }

        TransactionManager.commit(connect);
        ConnectionPool.getInstance().putConnection(connect);
    }
}
