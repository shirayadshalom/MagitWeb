package magit.servlets;

import com.google.gson.Gson;
import engine.Magit;
import engine.MagitManager;
import engine.PR;
import engine.PRManager;
import magit.utils.ServletUtils;
import magit.utils.SessionUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/remote/PRlist")
public class PRListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        final String reponame = request.getParameter("repo").trim();
        String username = SessionUtils.getUsername(request);
        username = username.trim();
        PRManager prManager = ServletUtils.getPRManager(getServletContext());

        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            if (prManager.getPRs(username) != null) {
                List<PR> prs = prManager.getPRs(username).values().stream().collect(Collectors.toList());
                List<String> lst = new LinkedList<>();
                for (PR pr : prs.stream().filter(pr -> pr.getRR().getName().equals(reponame)).collect(Collectors.toList())){
                    lst.add(pr.toString());
                }
                String json = gson.toJson(lst);
                out.println(json);
                out.flush();
            }

            else{
                String json = gson.toJson(new LinkedList<>());
                out.println(json);
                out.flush();
            }
        }
    }
}
