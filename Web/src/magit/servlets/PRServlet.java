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

@WebServlet("/remote/PR")
public class PRServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        String rrname = request.getParameter("RRname");
        rrname = rrname.trim();
        String lrname = request.getParameter("repo");
        lrname = lrname.trim();
        String rrusername = request.getParameter("RRusername");
        rrusername = rrusername.trim();
        String base = request.getParameter("branchBase");
        base = base.trim();
        String target = request.getParameter("branchTarget");
        target = target.trim();
        String message = request.getParameter("message");
        message = message.trim();
        String username = SessionUtils.getUsername(request);
        username = username.trim();
        MagitManager magitManager = ServletUtils.getMagitManager(getServletContext());
        PRManager prManager = ServletUtils.getPRManager(getServletContext());
        NotificationsManager notificationsManager = ServletUtils.getNotificationsManager(getServletContext());
        Magit magit = magitManager.getMagit(username);
        Magit remoteMagit = magitManager.getMagit(rrusername);
        Repository lr = magit.getRepo(lrname);
        Repository rr = remoteMagit.getRepo(rrname);
        try (PrintWriter out = response.getWriter()){
            synchronized (this){
                PR pr = prManager.addPR(rrusername,message,rr.getBranch(base),rr.getBranch(target),lr, rr, username);
                notificationsManager.addPR(rrusername,pr);
                out.println("success");
            }
        }
    }
}