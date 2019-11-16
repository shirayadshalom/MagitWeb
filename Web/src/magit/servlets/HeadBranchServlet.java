package magit.servlets;

import engine.Utils;
import magit.utils.SessionUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/branch/headbranch")
public class HeadBranchServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        String repo = request.getParameter("repo");
        repo = repo.trim();
        String username = SessionUtils.getUsername(request);
        username = username.trim();
        try (PrintWriter out = response.getWriter()) {
            out.println(Utils.readFromFile("c:/magit-ex3/" + username + "/" + repo + "/.magit/branches/head"));
        }
    }
}
