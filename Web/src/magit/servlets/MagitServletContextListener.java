package magit.servlets;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class MagitServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        File f = new File("c:/magit-ex3");
        if (!f.exists())
            f.mkdir();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        File f = new File("c:/magit-ex3");
        try {FileUtils.deleteDirectory(f);}
        catch (IOException e) {
            System.out.println(e.getStackTrace());}
    }
}
