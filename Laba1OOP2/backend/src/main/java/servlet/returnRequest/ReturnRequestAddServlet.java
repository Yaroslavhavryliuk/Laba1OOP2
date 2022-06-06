package servlet.returnRequest;

import connection.ConnectionPool;
import connection.TransactionManager;
import connection.dao.BookRateLogDAO;
import connection.dao.BookRequestDAO;
import connection.dao.BookStatsDAO;
import connection.dao.ReturnRequestDAO;
import entity.book.history.BookRateLogRecord;
import entity.book.request.RequestState;
import entity.book.request.ReturnBookRequest;
import servlet.AuthorizeHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet(urlPatterns = "/return_request/add")
public class ReturnRequestAddServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var user = AuthorizeHelper.getUser(req, resp);
        if (user == null) {
            resp.sendError(401);
            return;
        }

        var requestIDStr = req.getParameter("request_id");
        var rateStr = req.getParameter("rate");

        if (requestIDStr == null) {
            resp.sendError(400);
            return;
        }

        var connect = ConnectionPool.getInstance().getConnection();
        TransactionManager.start(connect);

        var returnRequestDAO = new ReturnRequestDAO(connect);
        var requestDAO = new BookRequestDAO(connect);

        var requestID = Integer.parseInt(requestIDStr);
        var request = requestDAO.get(requestID);
        if (request.getUserID() != user.getId()) {
            resp.sendError(403);
            TransactionManager.rollback(connect);
            ConnectionPool.getInstance().putConnection(connect);
            return;
        }
        request.setState(RequestState.RETURNED);

        returnRequestDAO.add(new ReturnBookRequest(LocalDateTime.now(), requestID, RequestState.SENT));
        requestDAO.editState(request);

        if (rateStr != null && !rateStr.isEmpty()) {
            var rate = Double.parseDouble(rateStr);

            var statsDAO = new BookStatsDAO(connect);
            var rateLogDAO = new BookRateLogDAO(connect);

            var bookID = new BookRequestDAO(connect).get(requestID).getBookID();
            var rateCount = rateLogDAO.findByBookID(bookID).size();

            var stats = statsDAO.getByBookId(bookID);
            stats.setRate(((stats.getRate() * rateCount) + rate) / (rateCount + 1));
            statsDAO.edit(stats);

            rateLogDAO.add(new BookRateLogRecord(LocalDateTime.now(), bookID, user.getId(), rate));
        }

        TransactionManager.commit(connect);
        ConnectionPool.getInstance().putConnection(connect);
    }
}
