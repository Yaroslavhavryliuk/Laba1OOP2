package servlet.delivery;

import connection.ConnectionPool;
import connection.TransactionManager;
import connection.dao.DeliveryTypeDAO;
import entity.misc.DeliveryType;
import servlet.AuthorizeHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/delivery_types/add")
public class DeliveryTypeAddServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!AuthorizeHelper.authorizeAdmin(req, resp)) {
            return;
        }

        var description = req.getParameter("description");

        if (description == null) {
            resp.sendError(400);
            return;
        }

        var connect = ConnectionPool.getInstance().getConnection();
        TransactionManager.start(connect);

        var deliveryTypeDAO = new DeliveryTypeDAO(connect);
        deliveryTypeDAO.add(new DeliveryType(description));

        TransactionManager.commit(connect);
        ConnectionPool.getInstance().putConnection(connect);
    }
}
