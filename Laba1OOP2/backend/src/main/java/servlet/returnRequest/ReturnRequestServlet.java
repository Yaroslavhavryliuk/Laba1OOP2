package servlet.returnRequest;

import com.google.gson.Gson;
import connection.ConnectionPool;
import connection.dao.BookRequestDAO;
import connection.dao.ReturnRequestDAO;
import entity.misc.User;
import servlet.AuthorizeHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/return_request")
public class ReturnRequestServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var user = AuthorizeHelper.getUser(req, resp);
        if (user == null) {
            resp.sendError(401);
            return;
        }

        var connect = ConnectionPool.getInstance().getConnection();
        var returnRequestDAO = new ReturnRequestDAO(connect);
        var out = resp.getWriter();

        if (user.getRole() == User.Role.ADMIN) {
            var returnRequests = returnRequestDAO.getAll();
            out.print(gson.toJson(returnRequests));
        } else if (user.getRole() == User.Role.USER) {
            var returnRequests = returnRequestDAO.getByUserId(user.getId());
            out.print(gson.toJson(returnRequests));
        } else {
            out.print(gson.toJson(null));
        }

        out.flush();
        ConnectionPool.getInstance().putConnection(connect);
    }
}
