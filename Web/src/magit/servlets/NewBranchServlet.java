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

@WebServlet("/branch/newbranch")
public class NewBranchServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        String reponame = request.getParameter("repo");
        reponame = reponame.trim();
        String username = SessionUtils.getUsername(request);
        username = username.trim();
        String branchname = request.getParameter("branchname");
        branchname = branchname.trim();
        MagitManager magitManager = ServletUtils.getMagitManager(getServletContext());
        Magit magit = magitManager.getMagit(username);
        try (PrintWriter out = response.getWriter()){

            try {

                synchronized (this) {
                    magit.changeActiveRepo("c:/magit-ex3/" + username + "/" + reponame);
                    if (magit.isBranchExists(branchname)){
                        out.println("error");
                    }
                    else{
                        magit.createNewBranch(branchname);
                        out.println("success");
                    }

                }

            }

            catch (ClassNotFoundException e) {
                System.out.println(e);
            }
        }

    }
}
