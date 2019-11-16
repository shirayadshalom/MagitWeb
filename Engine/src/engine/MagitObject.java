package engine;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import javafx.scene.control.TreeItem;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static engine.Utils.*;


//itamar made public
public abstract class MagitObject implements Serializable{
    protected String name;
    protected String SHA1;
    private String modifiedBy;
    private String modifiedDate;
    private Class type;
    protected String path;
    protected String relPath;

    MagitObject(String modifiedBy, String name,Class type, String path){
        this.modifiedBy = modifiedBy;
        this.name = name;
        this.type = type;
        SHA1 = null;
        this.path = path.toLowerCase();
        relPath = path.split("\\\\", 4)[3];
    }

    public Class getType(){return this.type;}

    public void setModifiedDate(String date) {modifiedDate = date;}

    public void setModifiedBy(String username){modifiedBy = username;}

    public String toStringStatus(){
        return "Type (Folder or Blob): "+type.getSimpleName()+"<br>"+"Name: "+name+"<br>"+"Who made the last change: "+modifiedBy+
                "<br>"+"When was the last update: "+modifiedDate+"<br><br>";
    }

    public String toString(){
        return name;
    }

    public String toStringSpecified(){
        return "• Name (full path): "+path+ "\n"+"• Type (Folder or Blob): "+type.getSimpleName()+ "\n"+"• SHA-1: "
                +SHA1+"\n"+"• Who made the last change: "+modifiedBy+"\n"+"• When was the last update: "+modifiedDate+"\n"
                +"-----------------------------"+"\n";
    }

    abstract public Boolean isExists(MagitObject obj);

    abstract public Boolean containsKey(String sha1);

    abstract public void addList(List<String> lst);

    abstract public void addListPath(List<String> lst);

    abstract public void deploy(String repoPath) throws IOException;

    abstract public void delete() throws IOException;

    abstract public void buildTree (TreeItem<MagitObject> parent);

    abstract public void updatePath (String srcRepoPath, String destRepoPath);

    abstract public void merge (Map<Integer, List<String>> headLists, Map<Integer, List<String>> mergeLists, Map<String,Map<Integer,Blob>> conflicts, int flag) throws IOException;

    public int state (Map<Integer, List<String>> headLists, Map<Integer, List<String>> mergeLists) {
        int res = 0;
        int x = 1;
        if (mergeLists.get(DELETED).contains(path))
            res = res | x;
        x = x*2;
        if (mergeLists.get(CREATED).contains(path))
            res = res | x;
        x = x*2;
        if (mergeLists.get(CHANGED).contains(path))
            res = res | x;
        x = x*2;
        if (headLists.get(DELETED).contains(path))
            res = res | x;
        x = x*2;
        if (headLists.get(CREATED).contains(path))
            res = res | x;
        x = x*2;
        if (headLists.get(CHANGED).contains(path))
            res = res | x;

        return res;

    }

}
