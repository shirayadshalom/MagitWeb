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
import java.util.Map;

@WebServlet("/pr/changes")
public class PRChangesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        final String id = request.getParameter("id").trim();
        String sha1 = request.getParameter("sha1").trim();
        String username = SessionUtils.getUsername(request);
        username = username.trim();
        PRManager prManager = ServletUtils.getPRManager(getServletContext());
        PR pr = prManager.getPRs(username).get(Integer.parseInt(id));
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            try {
                Commit base = pr.getBase().getCommit();
                Commit chosen = Utils.unzipCommit(pr.getRR().getObjects().get(sha1));
                Map<Integer, List<String>> changes = pr.getRR().showStatusCommit(chosen,base);
                String json = gson.toJson(changes);
                out.println(json);
                out.flush();
            }

            catch (ClassNotFoundException e){
                System.out.println(e.getMessage());
            }
        }
    }
}