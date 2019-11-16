package magit.servlets;

//taken from: http://www.servletworld.com/servlet-tutorials/servlet3/multipartconfig-file-upload-example.html
// and http://docs.oracle.com/javaee/6/tutorial/doc/glraq.html

import engine.Magit;
import engine.MagitManager;
import magit.utils.ServletUtils;
import magit.utils.SessionUtils;
import org.apache.commons.io.FileUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.util.Collection;
import java.util.Scanner;

@WebServlet("/magit/upload")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class UploadRepoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("/pages/magit/magit.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        MagitManager magitManager = ServletUtils.getMagitManager(getServletContext());
        Magit magit = magitManager.getMagit(SessionUtils.getUsername(request));
        Collection<Part> parts = request.getParts();

        StringBuilder fileContent = new StringBuilder();

        for (Part part : parts) {
            //to write the content of the file to a string
            fileContent.append(readFromInputStream(part.getInputStream()));
        }

        File f = new File("c:\\magit-ex3\\file.xml");
        try (Writer outfile = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream("c:\\magit-ex3\\file.xml"), "UTF-8"))) {
            outfile.write(fileContent.toString());
        }

        try{
            if (!magit.checkXml("c:\\magit-ex3/\\file.xml")) {
                out.println("there is a repo with that name already, overriding it");
            }
        }
        catch (Exception e) {
            out.println(e.getMessage());
            f.delete();
            return;}

        File fol = new File("c:\\magit-ex3\\" + magit.getUserName());
        if (!fol.exists())
            fol.mkdir();
        try{
            magit.loadFromXML("c:\\magit-ex3\\file.xml");
            out.println("\nrepo created");
        }
        catch (Exception e) {
            System.out.println(e.getStackTrace());}

        finally{f.delete();}
    }

    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("/Z").next();
    }
}