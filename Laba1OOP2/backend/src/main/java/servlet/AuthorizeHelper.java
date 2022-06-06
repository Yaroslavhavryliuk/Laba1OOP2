package servlet;

import connection.ConnectionPool;
import connection.dao.UserDAO;
import entity.misc.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthorizeHelper {
    public static User getUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var login = req.getParameter("login");
        var password = req.getParameter("password");

        if (login == null || password == null) {
            return null;
        }

        var connect = ConnectionPool.getInstance().getConnection();
        var user = new UserDAO(conn).get(login, password);
        ConnectionPool.getInstance().putConnection(connect);

        return user;
    }

    public static boolean authorize(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var user = getUser(req, resp);

        if (user == null) {
            resp.sendError(401);
            return false;
        } else {
            return true;
        }
    }

    public static boolean authorizeAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var user = getUser(req, resp);

        if (user == null || user.getRole() != User.Role.ADMIN) {
            resp.sendError(401);
            return false;
        } else {
            return true;
        }
    }


}
