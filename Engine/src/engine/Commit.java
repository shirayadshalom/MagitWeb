package engine;

import com.fxgraph.edges.Edge;
import com.fxgraph.graph.ICell;
import com.fxgraph.graph.Model;
import components.main.MagitController;
import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import org.apache.commons.codec.digest.DigestUtils;
import puk.team.course.magit.ancestor.finder.CommitRepresentative;
import visual.node.CommitNode;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static engine.Utils.zipAndAddList;

public class Commit implements Sha1able, Serializable, CommitRepresentative, Comparable<Commit>  {
    private String rootFolderSHA1;
    private Commit prev;
    private Commit prev2;
    private String message;
    private String createDate;
    private String author;

    public Commit(String message, String author, Folder rootFolder, Commit prev){
        this.message = message;
        this.author = author;
        this.prev = prev;
        prev2 = null;
        Date today = Calendar.getInstance().getTime();
        createDate = (new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:SSS")).format(today);
        if (rootFolder == null){
            rootFolderSHA1 = "";
        }
        else { rootFolderSHA1 = rootFolder.SHA1; }
    }

    public Commit(String message, String author, Folder rootFolder, Commit prev, String createDate){
        this.message = message;
        this.author = author;
        this.prev = prev;
        prev2 = null;
        this.createDate = createDate;
        if (rootFolder == null){
            rootFolderSHA1 = "";
        }
        else { rootFolderSHA1 = rootFolder.SHA1; }
    }

    public Commit(String message, String author, Folder rootFolder, Commit prev, Commit prev2, String createDate){
        this.message = message;
        this.author = author;
        this.prev = prev;
        this.prev2 = prev2;
        this.createDate = createDate;
        if (rootFolder == null){
            rootFolderSHA1 = "";
        }
        else { rootFolderSHA1 = rootFolder.SHA1; }
    }

    public Commit(String message, String author, Folder rootFolder, Commit prev, Commit prev2){
        this.message = message;
        this.author = author;
        this.prev = prev;
        this.prev2 = prev2;
        Date today = Calendar.getInstance().getTime();
        createDate = (new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:SSS")).format(today);
        if (rootFolder == null){
            rootFolderSHA1 = "";
        }
        else { rootFolderSHA1 = rootFolder.SHA1; }
    }

    public String getRootFolderSHA1(){ return rootFolderSHA1;}

    public String getMessage(){ return message;}

    public Commit getPrev() { return prev; }

    public Commit getPrev2() { return prev2; }

    public String getAuthor() {return author;}

    @Override
    public String Sha1(){
        String str = "";
        str = str.concat(rootFolderSHA1).concat(message).concat(author);
        return DigestUtils.shaHex(str);
    }

    public List<String> ShowCurrentBranch(String magitPath) throws IOException,ClassNotFoundException{
        List<String> lst = new LinkedList<>();
        if (rootFolderSHA1.equals(""))
            return lst;
        else {
            File f = new File(magitPath + "\\objects");
            for (File child : f.listFiles()) {
                if (child.getName().equals(rootFolderSHA1)) {
                    return zipAndAddList(rootFolderSHA1, lst);
                }
            }
            return lst;
        }
    }

    public String toString(){return "Commit's message:" +message+"\n"+"Commited By: "+author; }

    public String toStringSpecified(){return "SHA-1: "+Sha1()+"\n"+"Commit's message: "+message+"\n"+"Commited By: "+author+"\n"+
            "When was it created: " +createDate + "\n"
            +"-----------------------------"+"\n"; }


    public void buildTree (TreeItem<String> parent){
        TreeItem <String> com = new TreeItem<>("Commit " + Sha1());
        parent.getChildren().add(com);
        if (prev != null)
            prev.buildTree(parent);
        if (prev2 != null)
            prev2.buildTree(parent);
    }

    public void buildGraph (Model model, String branch, Map<Integer,Edge> edges,Map<String,ICell> commitNodes, MagitController magitController, List<Commit> commitList){
        commitList.add(this);
        ICell cur = commitNodes.get(Sha1());
        if (cur == null) {
            cur = new CommitNode(createDate, author, message, magitController.getMagit().allBranches(getSha1(),magitController.getMagit().getActiverepo().getBranches()), Sha1(), magitController);
           // cur = new CommitNode(createDate, author, message, branch, Sha1(), magitController);
            //model.addCell(cur);
            commitNodes.put(Sha1(), cur);
        }

        if (prev != null) {
            ICell prevNode = commitNodes.get(prev.getSha1());
            if (prevNode == null) {
                prevNode = new CommitNode(prev.createDate, prev.author, prev.message, magitController.getMagit().allBranches(prev.getSha1(),magitController.getMagit().getActiverepo().getBranches()),prev.Sha1(), magitController);
                //model.addCell(prevNode);
                commitNodes.put(prev.getSha1(), prevNode);
            }

            if (edges.get(cur.hashCode() + prevNode.hashCode()) == null){
                final Edge edge = new Edge(cur, prevNode);
                //model.addEdge(edge);
                edges.put(cur.hashCode() + prevNode.hashCode(), edge);
            }
            prev.buildGraph(model, branch, edges, commitNodes, magitController, commitList);
        }

        if (prev2 != null) {
            ICell prevNode = commitNodes.get(prev2.getSha1());
            if (prevNode == null) {
                prevNode = new CommitNode(prev2.createDate, prev2.author, prev2.message, magitController.getMagit().allBranches(prev2.getSha1(),magitController.getMagit().getActiverepo().getBranches()), prev2.Sha1(), magitController);
                //model.addCell(prevNode);
                commitNodes.put(prev2.getSha1(), prevNode);
            }

            if (edges.get(cur.hashCode() + prevNode.hashCode()) == null) {
                final Edge edge = new Edge(cur, prevNode);
                //model.addEdge(edge);
                edges.put(cur.hashCode() + prevNode.hashCode(), edge);
            }
            prev2.buildGraph(model, branch, edges, commitNodes, magitController, commitList);
        }

    }

    public void prevSha1 (List<String> lst){
        if (prev != null){
            lst.add(prev.Sha1());
            }
        if (prev2 != null){
            lst.add(prev2.Sha1());
        }
    }

    @Override
    public String getSha1() {
        return Sha1();
    }

    @Override
    public String getFirstPrecedingSha1() {
        if (prev!= null)
            return prev.Sha1();
        return "";
    }

    @Override
    public String getSecondPrecedingSha1() {
        if (prev2!= null)
            return prev2.Sha1();
        return "";
    }

    public Long getDate(){
        Long date = null;
        try {
        date = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:SSS").parse(createDate).getTime();}
        catch (java.text.ParseException e) { System.out.println(e);}
        return date;
    }

    public String getDateString () {return createDate;}

    @Override
    public int compareTo(Commit other) {
        Long key1 = null,key2=null;
        try{key1 = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:SSS").parse(createDate).getTime();
            key2 = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:SSS").parse(other.createDate).getTime();

        }
        catch (java.text.ParseException e) { System.out.println(e);};
        return key1.compareTo(key2);
    }

}

