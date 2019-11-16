package engine;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import static engine.Utils.MERGE;


public class Blob extends MagitObject implements Sha1able, Serializable {
    private String content;

    public Blob(String userName, String name, String path) throws IOException{
        super(userName, name,Blob.class, path);
        Date today = Calendar.getInstance().getTime();
        super.setModifiedDate(new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:SSS").format(today));
        content = new String(Files.readAllBytes(Paths.get(path)));
        SHA1 = Sha1();
    }

    public Blob(String userName, String name, String path, String content, String date) throws IOException {
        super(userName, name,Blob.class,path);
        super.setModifiedDate(date);
        this.content = content;
        SHA1 = Sha1();
    }

    @Override
    public String Sha1(){
        return DigestUtils.shaHex(content);
    }

    public String getContent(){return content;}

    public void setContent(String content){this.content = content;}

    public void updateState(String username) throws IOException {
        content = new String(Files.readAllBytes(Paths.get(path)));
        if (!(SHA1.equals(Sha1())))
            setModifiedBy(username);
        SHA1 = Sha1();
    }

    @Override
    public void addList(List<String> lst){
        lst.add(toStringSpecified());
    }

    @Override
    public void addListPath(List<String> lst){ lst.add(path + ",blob"); }

    public Boolean isExists(MagitObject obj){
        return path.equals(obj.path);
    }

    public Boolean containsKey(String sha1){
        return sha1.equals(SHA1);
    }

    @Override
    public void deploy(String repoPath) throws IOException {
         try (Writer out = new BufferedWriter(
                  new OutputStreamWriter(
                   new FileOutputStream(path), "UTF-8"))) {
             out.write(content);

         }
    }

    @Override
    public void delete() throws IOException{
        File file = new File(path);
        if (file.exists())
            file.delete();
    }
    @Override
    public void buildTree (TreeItem<MagitObject> parent){
        TreeItem<MagitObject> treeblob = new TreeItem<>(this);
        final Node icon =  new ImageView(new Image(getClass().getResourceAsStream("\\Resources\\blob.png")));
        ((ImageView) icon).setFitWidth(15);
        ((ImageView) icon).setFitHeight(13);
        treeblob.setGraphic(icon);
        parent.getChildren().add(treeblob);
    }

    @Override
    public void merge (Map<Integer, List<String>> headLists, Map<Integer, List<String>> mergeLists, Map<String,Map<Integer,Blob>> conflicts, int flag) throws IOException{
        int state = state(headLists, mergeLists);

        if (flag == MERGE){
            if (state == 4 || state == 2)
                deploy("");
            if (state == 1)
                delete();
        }

        if (state == 12 || state == 33 || state == 36 || state == 18)
            if (conflicts.get(path) == null)
                conflicts.put(path, new HashMap<>());

    }

    public void updatePath (String srcRepoPath, String destRepoPath){
        path = path.replace(srcRepoPath,destRepoPath);
    }

}



