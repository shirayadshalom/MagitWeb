package magit.servlets;

import com.google.gson.Gson;
import engine.*;
import magit.utils.ServletUtils;
import magit.utils.SessionUtils;

import javax.rmi.CORBA.Util;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

@WebServlet("/commit/files")
public class CommitFilesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        String reponame = request.getParameter("repo");
        reponame = reponame.trim();
        String username = SessionUtils.getUsername(request);
        username = username.trim();
        MagitManager magitManager = ServletUtils.getMagitManager(getServletContext());
        Magit magit = magitManager.getMagit(username);
        String sha1 = request.getParameter("sha1");
        sha1 = sha1.trim();
        try (PrintWriter out = response.getWriter()){
            Gson gson = new Gson();
            try {
                Commit com;
                Folder rootfol;
                synchronized (this) {
                    magit.changeActiveRepo("c:/magit-ex3/" + username + "/" + reponame);

                    if (sha1.equals("wc")) {
                        com = magit.getActiverepo().getHead().getCommit();
                        rootfol = Utils.unzip(magit.getActiverepo().getObjects().get(com.getRootFolderSHA1()));
                        rootfol.updateState(username);
                    }

                    else {
                        com = Utils.unzipCommit(magit.getActiverepo().getObjects().get(sha1));
                        rootfol = Utils.unzip(magit.getActiverepo().getObjects().get(com.getRootFolderSHA1()));
                    }
                }


                List<String> lst = new LinkedList<>();
                for (MagitObject obj : rootfol.getFiles()){
                    obj.addListPath(lst);
                }
                String json = gson.toJson(lst);
                out.println(json);
                out.flush();
            }
            catch (ClassNotFoundException e) {
                System.out.println(e);
            }
        }
    }
}
