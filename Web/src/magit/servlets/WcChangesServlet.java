package magit.servlets;

import com.google.gson.Gson;
import engine.Magit;
import engine.MagitManager;
import magit.utils.ServletUtils;
import magit.utils.SessionUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/commit/changes")
public class WcChangesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String repo = request.getParameter("repo");
        repo = repo.trim();
        String username = SessionUtils.getUsername(request);
        username = username.trim();
        MagitManager magitManager = ServletUtils.getMagitManager(getServletContext());
        Magit magit = magitManager.getMagit(username);
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            try {
                synchronized (this){
                    magit.changeActiveRepo("c:/magit-ex3/" + username + "/" + repo);
                    Map<Integer, List<String>> changes = magit.showStatus();

                    String json = gson.toJson(changes);
                    out.println(json);
                    out.flush();
                }
            }
            catch (Exception e) { System.out.println(e);}
        }
    }
}
