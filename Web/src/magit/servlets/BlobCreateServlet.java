package magit.servlets;

import engine.Utils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/blob/create")
public class BlobCreateServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        String path = request.getParameter("path");
        path = path.trim();
        String content = request.getParameter("content");
        content = content.trim();
        try (PrintWriter out = response.getWriter()) {
            Utils.writeToFile(content, path);
            out.println("success");
        }
    }
}