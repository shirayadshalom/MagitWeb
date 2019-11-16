package magit.servlets;

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

@WebServlet("/remote/pull")
public class PullServlet extends HttpServlet {
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
            try {

                synchronized (this) {
                    magit.getRepo(reponame).pull();
                    out.println("success");
                }
            }
            catch (ClassNotFoundException e) {
                out.println(e.getMessage());
            }
        }
    }
}
