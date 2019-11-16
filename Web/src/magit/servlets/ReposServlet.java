package magit.servlets;

//taken from: http://www.servletworld.com/servlet-tutorials/servlet3/multipartconfig-file-upload-example.html
// and http://docs.oracle.com/javaee/6/tutorial/doc/glraq.html

import com.google.gson.Gson;
import engine.Commit;
import engine.Magit;
import engine.MagitManager;
import engine.Repository;
import magit.utils.ServletUtils;
import magit.utils.SessionUtils;
import org.apache.commons.io.FileUtils;
import users.UserManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.util.*;

@WebServlet("/magit/repos")
public class ReposServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        String username = request.getParameter("username");
        username = username.trim();
        MagitManager magitManager = ServletUtils.getMagitManager(getServletContext());
        Magit magit = magitManager.getMagit(username);
        List<Map<String, String>> repos = new LinkedList<>();
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            for (Repository repo : magit.getRepos()) {
                Map<String, String> curr = new HashMap<>();
                curr.put("name", repo.getName());
                curr.put("activeBranch", repo.getHead().getName());
                curr.put("numOfBranches", ((Integer) repo.getBranches().size()).toString());
                repos.add(curr);
                List<Commit> commits = new ArrayList<>();
                repo.makeCommitList(commits);
                Collections.sort(commits, Collections.reverseOrder());
                Commit first = commits.get(0);
                curr.put("lastCommitDate", first.getDateString());
                curr.put("lastCommitMessage", first.getMessage());
            }

            String json = gson.toJson(repos);
            out.println(json);
            out.flush();
        }
    }




    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("/pages/magit/magit.html");
    }
}