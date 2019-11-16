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
import java.util.Map;

@WebServlet("/repo/isRR")
public class IsrrServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        String reponame = request.getParameter("repo");
        reponame = reponame.trim();
        String username = SessionUtils.getUsername(request);
        username = username.trim();
        MagitManager magitManager = ServletUtils.getMagitManager(getServletContext());
        Magit magit = magitManager.getMagit(username);
        try (PrintWriter out = response.getWriter()){
            Gson gson = new Gson();
            synchronized (this) {
                Map<String, String> res = new HashMap<>();
                res.put("RRname",magit.getRepo(reponame).getRemoteRepoName());
                if (res.get("RRname") == null || res.get("RRname").equals(""))
                    throw new IOException("doesnt have rr");
                else {
                    res.put("user name",magit.getRepo(reponame).getRemoteUserName());
                    String json = gson.toJson(res);
                    out.println(json);
                    out.flush();
                }
            }
        }
    }
}
