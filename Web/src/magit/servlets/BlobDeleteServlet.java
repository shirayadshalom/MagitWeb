package magit.servlets;

import com.google.gson.Gson;
import engine.Utils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/blob/delete")
public class BlobDeleteServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        String path = request.getParameter("path");
        path = path.trim();
        try (PrintWriter out = response.getWriter()) {
            File f = new File(path);
            if (f.exists()) {
                f.delete();
                out.println("success");
            }
            else
                throw new IOException("blob doesnt exists!");
        }
    }
}