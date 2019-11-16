package magit.servlets;

import engine.Magit;
import engine.MagitManager;
import engine.Repository;
import magit.utils.ServletUtils;
import magit.utils.SessionUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/remote/push")
public class PushServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        String reponame = request.getParameter("repo");
        String RRusername = request.getParameter("RRusername");
        RRusername = RRusername.trim();
        reponame = reponame.trim();
        String username = SessionUtils.getUsername(request);
        username = username.trim();
        MagitManager magitManager = ServletUtils.getMagitManager(getServletContext());

        Repository repo = magitManager.getMagit(username).getRepo(reponame);
        Repository RRrepo = magitManager.getMagit(RRusername).getRepo(repo.getRemoteRepoName());
        try (PrintWriter out = response.getWriter()){
            synchronized (this) {
                if (RRrepo.isBranchExists(repo.getHead().getName()))
                    out.println("branch exists on rr!");
                else{
                    try {
                        repo.pushNew(RRrepo);
                        out.println("success");
                    }
                    catch (ClassNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                }


            }
        }
    }
}