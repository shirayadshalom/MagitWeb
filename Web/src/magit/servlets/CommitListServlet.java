package magit.servlets;

import com.google.gson.Gson;
import engine.Branch;
import engine.Commit;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@WebServlet("/commit/comlist")
public class CommitListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        String reponame = request.getParameter("repo");
        reponame = reponame.trim();
        String username = SessionUtils.getUsername(request);
        username = username.trim();
        MagitManager magitManager = ServletUtils.getMagitManager(getServletContext());
        Magit magit = magitManager.getMagit(username);
        String branchname = request.getParameter("branch");
        branchname = branchname.trim();
        try (PrintWriter out = response.getWriter()){
            Gson gson = new Gson();
            try {
                magit.changeActiveRepo("c:/magit-ex3/" + username + "/" + reponame);

                Branch br= null;
                for (Branch branch : magit.getActiverepo().getBranches()){
                    if (branch.getName().equals(branchname))
                        br = branch;
                }
                List<Map<String,String>> commits = new LinkedList<>();
                recCom(magit,br.getCommit(),commits);
                String json = gson.toJson(commits);
                out.println(json);
                out.flush();
            }

            catch (ClassNotFoundException e) {
                System.out.println(e);
            }
        }

    }

    /* [ { "SHA-1":"35333ERd33", "Message":"bbb","Commit date": "11/2/29", "Creator":"Or", "List of branches":["master","new","deep"...]},... ] */
    private void recCom(Magit magit, Commit commit, List<Map<String,String>> commits){
        if (commit != null){
            Map<String,String> curr = new HashMap<>();
            curr.put("SHA-1",commit.Sha1());
            curr.put("Message",commit.getMessage());
            curr.put("Commit date",commit.getDateString());
            curr.put("Creator",commit.getAuthor());
            String branches = "";
            Boolean first = true;
            for (Branch branch : magit.getActiverepo().getBranches()){
                if (branch.isExists(commit)) {
                    if (first)
                        branches = branch.getName();
                    else
                        branches = branches + "," + branch.getName();
                    first = false;
                }
            }
            curr.put("List of branches",branches);
            commits.add(curr);
            recCom(magit,commit.getPrev(),commits);
            recCom(magit,commit.getPrev2(),commits);
        }
    }
}
