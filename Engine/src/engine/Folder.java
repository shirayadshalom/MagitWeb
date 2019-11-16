package engine;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

//import static engine.Settings.repoPath;
import static engine.Utils.MERGE;

public class Folder extends MagitObject implements Sha1able, Serializable {
    private Map<String, MagitObject> files;

    Folder(String name, String userName, String path) {
        super(userName, name, Folder.class, path);
        Date today = Calendar.getInstance().getTime();
        super.setModifiedDate(new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:SSS").format(today));
        files = new TreeMap<>();
        SHA1 = Sha1();
    }

    public Folder(String userName, String name, String path, String date) throws IOException {
        super(userName, name, Folder.class, path);
        super.setModifiedDate(date);
        files = new TreeMap<>();
        SHA1 = Sha1();
    }


    public Collection<MagitObject> getFiles() {
        return files.values();
    }

    public Map<String, MagitObject> getMap() {
        return files;
    }

    public void deploy(String repoPath) throws IOException {
        if (!(path.equals(repoPath))) {
            File dir = new File(path);
            dir.mkdir();
        }
        for (MagitObject file : files.values()) {
            file.deploy(repoPath);
        }
    }

    public void updateState(String username) throws IOException {
        Map<String, MagitObject> curFiles = new TreeMap<>();
        File f = new File(path);
        for (File child : f.listFiles()) {
            if (!(child.getName().equals(".magit"))) {
                MagitObject obj = files.get(child.getPath());
                if (child.isDirectory()) {
                    if (obj == null) {
                        Folder folder = new Folder(child.getName(), username, child.getPath());
                        folder.updateState(username);
                        if (folder.SHA1 != null) {
                            curFiles.put(child.getPath(), folder);
                        }
                    } else {
                        ((Folder) obj).updateState(username);
                        if (((Folder) obj).SHA1 != null) {
                            curFiles.put(child.getPath(), ((Folder) obj));
                        }
                    }
                } else {
                    if (obj == null) {
                        Blob file = new Blob(username, child.getName(), child.getPath());
                        file.updateState(username);
                        curFiles.put(child.getPath(), file);
                    } else {
                        ((Blob) obj).updateState(username);
                        curFiles.put(child.getPath(), ((Blob) obj));
                    }
                }
            }
        }
        files = curFiles;
        SHA1 = Sha1();
    }

    @Override
    public String Sha1() {
        String str = "";
        str = str.concat(name);
        for (String sha1 : files.keySet()) {
            str = str.concat(files.get(sha1).SHA1);
        }
        if (str.equals(""))
            return "";
        return DigestUtils.shaHex(str);
    }

    @Override
    public Boolean isExists(MagitObject obj) {
        Boolean res = obj.relPath.equals(relPath);
        for (MagitObject fof : files.values()) {
            res = (res || fof.isExists(obj));
        }
        return res;
    }

    public Boolean containsKey(String sha1) {
        Boolean res = sha1.equals(SHA1);
        for (MagitObject obj : files.values()) {
            res = res || obj.containsKey(sha1);
        }

        return res;
    }

    @Override
    public void addList(List<String> lst) {
        lst.add(toStringSpecified());
        for (MagitObject obj : files.values()) {
            obj.addList(lst);
        }
    }

    @Override
    public void addListPath(List<String> lst) {
        lst.add(path + ",folder");
        for (MagitObject obj : files.values()) {
            obj.addListPath(lst);
        }
    }

    public void deleteChilds() throws IOException {
        for (MagitObject obj : files.values()) {
            obj.delete();
        }
    }

    @Override
    public void delete() throws IOException{
        File file = new File(path);
        if (file.exists())
            FileUtils.deleteDirectory(file);
    }
    @Override
    public void buildTree(TreeItem<MagitObject> parent) {
        TreeItem<MagitObject> treefol = new TreeItem<>(this);
        final Node icon =  new ImageView(new Image(getClass().getResourceAsStream("\\Resources\\fol.jpg")));
        ((ImageView) icon).setFitWidth(15);
        ((ImageView) icon).setFitHeight(13);
        treefol.setGraphic(icon);

        files.values().stream().forEach(obj -> obj.buildTree(treefol));
        parent.getChildren().add(treefol);
    }

    @Override
    public void merge (Map<Integer, List<String>> headLists, Map<Integer, List<String>> mergeLists, Map<String,Map<Integer,Blob>> conflicts, int flag) throws IOException{

        int state = state(headLists, mergeLists);
        if (flag == MERGE){
            if (state == 4 || state == 2)
                deploy("");

        }

        if (state == 1)
            delete();

        if (state == 12 || state == 33 || state == 36 || state == 18)
            for (MagitObject obj : files.values())
                obj.merge(headLists, mergeLists, conflicts, flag);

    }

    public Blob getFile(String path){
        Blob res = (Blob) files.get(path);
        if (res == null)
            for (MagitObject folder : files.values()){
                if (folder.getType() == Folder.class){
                    res = ((Folder) folder).getFile(path);
                    if (res != null)
                        break;
                }
            }

        return res;
    }

    public void updatePath (String srcRepoPath, String destRepoPath){
        path = path.replace(srcRepoPath,destRepoPath);
        Map <String,MagitObject> temp = new TreeMap<>();
        for (MagitObject obj : files.values()){
            obj.updatePath(srcRepoPath,destRepoPath);
            temp.put(obj.path, obj);
        }

        files = temp;


    }

}
