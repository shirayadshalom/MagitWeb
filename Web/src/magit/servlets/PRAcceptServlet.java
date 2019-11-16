package magit.servlets;

import com.google.gson.Gson;
import engine.*;
import magit.utils.ServletUtils;
import magit.utils.SessionUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/pr/accept")
public class PRAcceptServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        final String id = request.getParameter("id").trim();
        String username = SessionUtils.getUsername(request);
        username = username.trim();
        PRManager prManager = ServletUtils.getPRManager(getServletContext());
        NotificationsManager notificationsManager = ServletUtils.getNotificationsManager(getServletContext());
        PR pr = prManager.getPRs(username).get(Integer.parseInt(id));
        try (PrintWriter out = response.getWriter()) {
            try {
                pr.accept();
                prManager.removePR(username,pr);
                notificationsManager.addPR(pr.getlrusername(),pr);
                notificationsManager.removePR(username,pr);
                out.println("success");
            }

            catch (ClassNotFoundException e){
                System.out.println(e.getMessage());
            }
        }
    }
}