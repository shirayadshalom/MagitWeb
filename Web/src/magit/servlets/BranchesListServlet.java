package magit.servlets;

import com.google.gson.Gson;
import engine.Magit;
import engine.MagitManager;
import engine.Repository;
import engine.Utils;
import magit.utils.ServletUtils;
import magit.utils.SessionUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/branch/brancheslist")
public class BranchesListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String reponame = request.getParameter("repo");
        reponame = reponame.trim();
        String username = request.getParameter("username");
        username = username.trim();
        MagitManager magitManager = ServletUtils.getMagitManager(getServletContext());
        Magit magit = magitManager.getMagit(username);
        try (PrintWriter out = response.getWriter()){
            Gson gson = new Gson();
            synchronized (this) {
                try {
                    magit.changeActiveRepo("c:/magit-ex3/" + username + "/" + reponame);
                    String json = gson.toJson(magit.getAllBranchesName());
                    out.println(json);
                    out.flush();
                } catch (ClassNotFoundException e) {
                    System.out.println(e);
                }
            }
        }
    }

}

