package engine;

import java.io.*;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

//import static engine.Settings.repoPath;

public class Utils {

    static Integer CREATED = 1;
    static Integer CHANGED = 2;
    static Integer DELETED = 3;
    public static Integer HEAD = 1;
    public static Integer MERGE = 2;
    public static Integer ANCESTOR = 3;

    public static void writeToFile(String content, String path) throws IOException {
        try (Writer out = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(path), "UTF-8"))) {
            out.write(content);
        }
    }

    public static List zipAndAddList(String path, List lst) throws IOException, ClassNotFoundException {
        Folder rootFolder = null;
        try (ObjectInputStream in =
                     new ObjectInputStream(
                             new GZIPInputStream(
                                     new FileInputStream(path)))) {
            rootFolder = (Folder) in.readObject();
        }

        for (MagitObject obj : rootFolder.getFiles()) {
            obj.addList(lst);
        }
        return lst;
    }

    public static Folder unzip(String path) throws IOException, ClassNotFoundException {
        Folder rootFolder = null;
        try (ObjectInputStream in =
                     new ObjectInputStream(
                             new GZIPInputStream(
                                     new FileInputStream( path)))) {
            rootFolder = (Folder) in.readObject();
        }
        return rootFolder;
    }

    public static Commit unzipCommit(String path) throws IOException, ClassNotFoundException {
        Commit com = null;
        try (ObjectInputStream in =
                     new ObjectInputStream(
                             new GZIPInputStream(
                                     new FileInputStream( path)))) {
            com = (Commit) in.readObject();
        }
        return com;
    }





    public static void zip(String path,Commit com) throws IOException {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(
                             new GZIPOutputStream(
                                     new FileOutputStream(path)))) {
            out.writeObject(com);
            out.flush();
        }
    }

    public static String readFromFile( String path) throws IOException {
        String content=null;
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(path)))) {
                            String line;
                            content = in.readLine();
                            while ((line = in.readLine()) != null)
                                content = content + "\n" + line;
                            }
        return content;
    }

    public static void createFolders (String name, String repoPath) throws IOException{
        File f = new File(repoPath);
        f.mkdir();
        f = new File(repoPath + "\\.magit");
        f.mkdir();
        f = new File(repoPath + "\\.magit" + "\\objects");
        f.mkdir();
        f = new File(repoPath + "\\.magit" + "\\branches");
        f.mkdir();
        f = new File(repoPath + "\\.magit" + "\\" + "repoName");
        writeToFile(name,  f.getPath().toLowerCase());
    }

    public static void zipAndWriteObj (MagitObject obj,String objPath) throws IOException{
        try (ObjectOutputStream out =
                      new ObjectOutputStream(
                              new GZIPOutputStream(
                                      new FileOutputStream(objPath)))) {
            if (obj.getType() == Blob.class) {

                out.writeObject((Blob) obj);
            } else {
                out.writeObject((Folder) obj);
            }
            out.flush();

        }
    }


}

