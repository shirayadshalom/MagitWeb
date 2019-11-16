package magit.servlets;

import com.google.gson.Gson;
import engine.Magit;
import engine.MagitManager;
import engine.NotificationsManager;
import magit.utils.ServletUtils;
import magit.utils.SessionUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/notifications")
public class NotificationsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String username = SessionUtils.getUsername(request);
        username = username.trim();
        NotificationsManager notificationsManager = ServletUtils.getNotificationsManager(getServletContext());
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            String json = gson.toJson(notificationsManager.getNotifications(username));
            out.println(json);
            out.flush();
        }

    }
}
