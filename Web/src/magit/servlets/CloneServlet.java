package magit.servlets;

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

@WebServlet("/magit/clone")
public class CloneServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        String RRname = request.getParameter("RR");
        RRname = RRname.trim();
        String remoteUserName = request.getParameter("name");
        remoteUserName = remoteUserName.trim();
        String username = SessionUtils.getUsername(request);
        username = username.trim();
        MagitManager magitManager = ServletUtils.getMagitManager(getServletContext());
        NotificationsManager notificationsManager = ServletUtils.getNotificationsManager(getServletContext());
        Magit magit = magitManager.getMagit(username);
        Magit RRmagit = magitManager.getMagit(remoteUserName);
        try (PrintWriter out = response.getWriter()) {
            try {
                synchronized (this){
                    magit.clone(RRmagit.getRepo(RRname).getPath(), "c:\\magit-ex3\\" + username + "\\" + RRname, RRname);
                    notificationsManager.addFork(remoteUserName,RRname + " was forked by " + username);
                    out.println("success");
                }

            } catch (ClassNotFoundException e) {
                out.println(e.getMessage());
            }
        }

    }
}
