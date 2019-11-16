package engine;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import generated.*;
import org.apache.commons.io.FileUtils;
import puk.team.course.magit.ancestor.finder.AncestorFinder;

import java.util.List;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

//import static engine.Settings.repoPath;
import static engine.Utils.*;
import static engine.Xml.*;

public class Repository {
    private Folder rootFolder;
    private Branch headBranch;
    private Map<String, String> objects;
    private Set<Branch> branches;
    private String repoPath;
    private String magitPath;
    private String name;
    private String remoteRepoPath;
    private String remoteRepoName;


    public Repository(String repoPath) throws IOException, ClassNotFoundException {
        this.repoPath = repoPath;
        magitPath = repoPath + "\\.magit";
        File f;
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(magitPath + "\\" + "repoName")))) {

            name = in.readLine();

        }

        branches = new HashSet<>();
        objects = new HashMap<>();

        f = new File(magitPath + "\\objects");
        for (File file : f.listFiles()) {
            objects.put(file.getName(), file.getPath());
        }

        f = new File(magitPath + "\\branches");
        File rem = new File (magitPath + "\\remoteRepoPath");
        String headName = "";
        if (!rem.exists()) {

            for (File fileBranch : f.listFiles()) {
                if (!(fileBranch.getName().equals("head"))) {
                    String sha1 = "";
                    sha1 = readFromFile(fileBranch.getPath());
                    Commit com = null;
                    String comPath = objects.get(sha1);
                    com = Utils.unzipCommit(comPath);
                    Branch branch = new Branch(fileBranch.getName(), branches, com, repoPath);
                } else {
                    headName = readFromFile(magitPath + "\\branches\\head");
                }
            }
        }

        else{

            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(magitPath + "\\remoteRepoPath")))) {

                remoteRepoPath = in.readLine();
            }
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(remoteRepoPath + "\\.magit\\repoName")))) {

                remoteRepoName = in.readLine();
            }
            File remotes = new File (magitPath + "\\branches\\" + remoteRepoName);
            for (File remote : remotes.listFiles()){
                String sha1 = "";
                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(repoPath + "\\.magit" + "\\branches\\" + remoteRepoName + "\\" + remote.getName())))) {

                    sha1 = in.readLine();
                }
                Commit com = null;
                String comPath = objects.get(sha1);
                com = Utils.unzipCommit(comPath);
                Branch branch = new Branch(remote.getName(), branches, com, remoteRepoName, repoPath);
            }
            for (File fileBranch : f.listFiles()) {
                if ((!(fileBranch.getName().equals("head"))) && (!(fileBranch.getName().equals(remoteRepoName)))) {
                    String sha1 = "";
                    sha1 = readFromFile(fileBranch.getPath());
                    Commit com = null;
                    String comPath = objects.get(sha1);
                    com = Utils.unzipCommit(comPath);
                    Branch branch = new Branch(fileBranch.getName(), branches, com, repoPath);
                    if (branches.stream().anyMatch(br -> (remoteRepoName + "\\" + branch.getName()).equals(br.getName())))
                        branch.setRTB();
                } else {
                    headName = readFromFile(magitPath + "\\branches\\head");
                }
            }
        }

        final String headName2 = headName;
        branches.stream().filter(br -> br.getName().equals(headName2)).forEach(br -> headBranch = br);
        String rootFolderPath = objects.get(headBranch.getCommit().getRootFolderSHA1());
        rootFolder=unzip(rootFolderPath);
    }

    public Repository(String rrPath, String name, String rrName, String repoPath) throws IOException, ClassNotFoundException {
        this.repoPath = repoPath.toLowerCase();
        this.name = name;
        magitPath = repoPath.toLowerCase() + "\\.magit";
        remoteRepoName = rrName;
        File f;
        try (Writer out = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(repoPath + "\\.magit\\repoName"), "UTF-8"))) {
            out.write(name);
        }

        f = new File(magitPath + "\\branches\\" + rrName);
        f.mkdir();

        branches = new HashSet<>();
        objects = new HashMap<>();

        f = new File(magitPath + "\\objects");
        for (File file : f.listFiles()) {
            objects.put(file.getName(), file.getPath());
        }



        String headName = "";
        headName = readFromFile(magitPath + "\\branches\\head");
        String sha1 = "";
        sha1=readFromFile( magitPath + "\\branches\\" + headName);
        Commit com = null;
        String comPath = objects.get(sha1);
        com = Utils.unzipCommit(comPath);
        Branch branch = new Branch(headName, branches, com, repoPath);
        branch.setRTB();

        f = new File(magitPath + "\\branches");

        for (File fileBranch : f.listFiles()) {
            if ((!(fileBranch.getName().equals("head"))) && (!(fileBranch.getName().equals(rrName)))) {
                sha1=readFromFile( fileBranch.getPath());
                comPath = objects.get(sha1);
                com = Utils.unzipCommit(comPath);
                branch = new Branch(fileBranch.getName(), branches, com, rrName, repoPath);
            }
        }

        final String headName2 = headName;
        branches.stream().filter(br -> br.getName().equals(headName2)).forEach(br -> headBranch = br);
        String rootFolderPath = objects.get(headBranch.getCommit().getRootFolderSHA1());
        updatePaths(rrPath,repoPath);
        rootFolder=unzip(rootFolderPath);
        this.remoteRepoPath = rrPath;
        try (Writer out = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(repoPath + "\\.magit\\remoteRepoPath"), "UTF-8"))) {
            out.write(rrPath.toLowerCase());
        }
    }

    public Repository(String userName, String name, String repoPath) throws IOException, Exception {
        this.repoPath = repoPath.toLowerCase();
        this.name = name;
        magitPath = repoPath.toLowerCase() + "\\.magit";
        File f = new File(magitPath);
        if (f.exists()) {
            throw new Exception("The path already exists!");
            }
        createFolders (name, repoPath.toLowerCase());
        writeToFile("master", magitPath + "\\branches" + "\\head");
        branches = new HashSet<>();
        headBranch = new Branch("master", branches, null, repoPath.toLowerCase());
        objects = new HashMap<>();
        rootFolder = new Folder("root",userName,repoPath.toLowerCase());
        remoteRepoPath = "";
        remoteRepoName = "";
    }

    public String getMagitPath(){return magitPath;}

    public String getUserName(){
        return repoPath.split("\\\\")[2];
    }

    public String getRemoteUserName(){
        return remoteRepoPath.split("\\\\")[2];
    }

    public String getRemoteRepoName(){return remoteRepoName;}

    public Map<String,String> getObjects(){return objects;}

    public String getPath(){return repoPath;}

    public Folder getRootFolder(){return rootFolder;}

    public Branch getHead() { return headBranch; }

    public String getName() { return name; }

    public Set<Branch> getBranches(){return branches;}

    public String getRemoteRepoPath(){return remoteRepoPath;}

    public Boolean isDelta(String username) throws IOException{
        rootFolder.updateState(username);
        return !(rootFolder.SHA1.equals(headBranch.getCommit().getRootFolderSHA1())); }

    public Boolean isHead(String name){ return headBranch.getName().equals(name); }

    public void addObject(MagitObject obj) throws IOException {

        if (obj == null || objects.containsKey(obj.SHA1))
            return;
        String objPath = magitPath + "\\" + "objects" + "\\" + obj.SHA1;
        objects.put(obj.SHA1, objPath);
        zipAndWriteObj (obj, objPath);
        if (obj.getType() == Folder.class) {
            for (MagitObject fof : ((Folder) obj).getFiles()) {
                addObject(fof);
            }
        }
    }

    public void addObject(Commit obj) throws IOException {
        if (obj == null || objects.containsKey(obj.Sha1()))
            return;

        String objPath = magitPath + "\\" + "objects" + "\\" + obj.Sha1();
        objects.put(obj.getSha1(), objPath);
        zip(objPath,obj);
    }

    public Boolean commit(String username, String message, Commit prev2) throws IOException {
        rootFolder.updateState(username);
        if (headBranch.getCommit() == null || !(rootFolder.SHA1.equals(headBranch.getCommit().getRootFolderSHA1()))) {
            addObject(rootFolder);
            Commit com = null;
            if (prev2 == null)
                com = new Commit(message, username, rootFolder, headBranch.getCommit());
            else{
                com = new Commit(message, username, rootFolder, headBranch.getCommit(),prev2);
            }
            addObject(com);
            headBranch.setCommit(com, repoPath);
            return true;

        }
        else{
            return false;

        }
    }

    public Map<Integer, List<String>> showStatus(String username) throws IOException, ClassNotFoundException,Exception {
        if(headBranch.getCommit() == null)
            throw new Exception("You didnt commited anything yet!");
        rootFolder.updateState(username);
        Folder prevRootFolder = null;
        Map<Integer, List<String>> changes = new HashMap<>();
        changes.put(1, new LinkedList<>());
        changes.put(2, new LinkedList<>());
        changes.put(3, new LinkedList<>());

        if (!(headBranch.getCommit().getRootFolderSHA1().equals(""))) {

            String rootFolderPath = objects.get(headBranch.getCommit().getRootFolderSHA1());
            try (ObjectInputStream in =
                         new ObjectInputStream(
                                 new GZIPInputStream(
                                         new FileInputStream(rootFolderPath)))) {
                prevRootFolder = (Folder) in.readObject();
            }

            for (MagitObject obj : rootFolder.getFiles()){
                changedAndCreated(obj, prevRootFolder, changes);
            }

            for (MagitObject obj : prevRootFolder.getFiles()){
                deleted(obj, rootFolder, changes);
            }
        }

        else {
            for (MagitObject obj : rootFolder.getFiles()){
                created(obj,changes);
            }
        }

        return changes;
    }

    public Map<Integer, List<String>> showStatusCommit(Commit commit, Commit prev) throws IOException, ClassNotFoundException {
        Folder curRootFolder = null;
        Folder prevRootFolder = null;
        Map<Integer, List<String>> changes = new HashMap<>();
        changes.put(1, new LinkedList<>());
        changes.put(2, new LinkedList<>());
        changes.put(3, new LinkedList<>());

        if (prev == null)
            changes = null;
        else {
            if (!(prev.getRootFolderSHA1().equals(""))) {
                String curRootFolderPath = objects.get(commit.getRootFolderSHA1());
                String prevRootFolderPath = objects.get(prev.getRootFolderSHA1());
                curRootFolder = Utils.unzip(curRootFolderPath);
                prevRootFolder = Utils.unzip(prevRootFolderPath);

                for (MagitObject obj : curRootFolder.getFiles()) {
                    changedAndCreated(obj, prevRootFolder, changes);
                }

                for (MagitObject obj : prevRootFolder.getFiles()) {
                    deleted(obj, curRootFolder, changes);
                }
            } else {
                for (MagitObject obj : curRootFolder.getFiles()) {
                    created(obj, changes);
                }
            }
        }

        return changes;
    }

    public Map<Integer, List<String>> showStatusCommitPath(Commit commit, Commit prev) throws IOException, ClassNotFoundException {
        Folder curRootFolder = null;
        Folder prevRootFolder = null;
        Map<Integer, List<String>> changes = new HashMap<>();
        changes.put(1, new LinkedList<>());
        changes.put(2, new LinkedList<>());
        changes.put(3, new LinkedList<>());

        if (prev == null)
            changes = null;
        else {
            if (!(prev.getRootFolderSHA1().equals(""))) {
                String curRootFolderPath = objects.get(commit.getRootFolderSHA1());
                String prevRootFolderPath = objects.get(prev.getRootFolderSHA1());
                curRootFolder = Utils.unzip(curRootFolderPath);
                prevRootFolder = Utils.unzip(prevRootFolderPath);

                for (MagitObject obj : curRootFolder.getFiles()) {
                    changedAndCreatedPath(obj, prevRootFolder, changes);
                }

                for (MagitObject obj : prevRootFolder.getFiles()) {
                    deletedPath(obj, curRootFolder, changes);
                }
            } else {
                for (MagitObject obj : curRootFolder.getFiles()) {
                    createdPath(obj, changes);
                }
            }
        }

        return changes;
    }

    public void createdPath(MagitObject obj,Map<Integer, List<String>> changes) {
        changes.get(1).add(obj.path);
        if(obj.getType()==Folder.class) {
            for (MagitObject child : ((Folder) obj).getFiles()) {
                createdPath(child, changes);
            }
        }
    }

    public void changedAndCreatedPath (MagitObject obj, Folder prevRootFolder, Map<Integer, List<String>> changes){
        if (prevRootFolder.isExists(obj)) {
            if (!(prevRootFolder.containsKey(obj.SHA1))) {
                changes.get(2).add(obj.path);} //obj changed

        } else { changes.get(1).add(obj.path);} //obj created
        if (obj.getType() == Folder.class){
            for (MagitObject child : ((Folder)obj).getFiles()){
                changedAndCreatedPath(child,prevRootFolder,changes);
            }
        }
    }

    public void deletedPath (MagitObject obj, Folder rootFolder, Map<Integer, List<String>> changes){
        if (!(rootFolder.isExists(obj))) {
            changes.get(3).add(obj.path);} //obj deleted
        if (obj.getType() == Folder.class){
            for (MagitObject child : ((Folder)obj).getFiles()){
                deletedPath(child,rootFolder,changes); }
        }
    }

    public void created(MagitObject obj,Map<Integer, List<String>> changes) {
        changes.get(1).add(obj.toStringStatus());
        if(obj.getType()==Folder.class) {
        for (MagitObject child : ((Folder) obj).getFiles()) {
            created(child, changes);
        }
    }
    }

    public void changedAndCreated (MagitObject obj, Folder prevRootFolder, Map<Integer, List<String>> changes){
        if (prevRootFolder.isExists(obj)) {
            if (!(prevRootFolder.containsKey(obj.SHA1))) {
                changes.get(2).add(obj.toStringStatus());} //obj changed

        } else { changes.get(1).add(obj.toStringStatus());} //obj created
        if (obj.getType() == Folder.class){
            for (MagitObject child : ((Folder)obj).getFiles()){
                changedAndCreated(child,prevRootFolder,changes);
            }
        }
    }

    public void deleted (MagitObject obj, Folder rootFolder, Map<Integer, List<String>> changes){
        if (!(rootFolder.isExists(obj))) {
            changes.get(3).add(obj.toStringStatus());} //obj deleted
        if (obj.getType() == Folder.class){
            for (MagitObject child : ((Folder)obj).getFiles()){
                deleted(child,rootFolder,changes); }
        }
    }

    public Boolean deleteBranch(String name){
        Boolean res = branches.stream().filter(br -> br.getName().equals(name)).count() != 0;
        File file = new File(repoPath + "\\.magit\\branches\\" + name);
        if (file.exists())
            file.delete();
        branches = branches.stream().filter(br -> !(br.getName().equals(name))).collect(Collectors.toSet());
        return res;
    }

    public Boolean isBranchExists (String branchName){
        for (Branch branchTemp : branches){
            if(branchTemp.getName().equals(branchName)){
                return true;
            }
        }
        return false;
    }

    public List<String> getHistoryOfActiveBranch() {
        List<String> lst = new LinkedList<>();
        headBranch.getHistoryOfBranch(headBranch.getCommit(), lst);
        return lst;
    }

    public Boolean checkChanges(String username) throws IOException{
        rootFolder.updateState(username);
        return (!rootFolder.SHA1.equals(headBranch.getCommit().getRootFolderSHA1()));
    }

    public void checkOutBranch(String branchName) throws IOException,ClassNotFoundException{
        Branch branch=null;
        for (Branch branchTemp : branches){
            if(branchTemp.getName().equals(branchName)) {
                branch=branchTemp;
            }
        }
        headBranch = branch;
        writeToFile(headBranch.getName(), magitPath + "\\branches" + "\\head");
        rootFolder.deleteChilds();
        rootFolder=unzip(magitPath + "\\" + "objects" + "\\" + headBranch.getCommit().getRootFolderSHA1());
        rootFolder.deploy(repoPath);
    }

    public Boolean isCommitExists (String sha1){ return objects.containsKey(sha1); }

    public void resetBranch (String sha1) throws IOException, ClassNotFoundException{
        Commit com;
        com = Utils.unzipCommit(objects.get(sha1));
        headBranch.setCommit(com, repoPath);
        rootFolder.deleteChilds();
        rootFolder = unzip(objects.get(com.getRootFolderSHA1()));
        rootFolder.deploy(repoPath);

    }

    public Map<String,Map<Integer,Blob>> merge (Branch branch) throws IOException, ClassNotFoundException{
        AncestorFinder ancestorFinder = new AncestorFinder(this::sha1ToCommit);
        String ancestorSha1 = ancestorFinder.traceAncestor(getHead().getCommit().Sha1(), branch.getCommit().Sha1());
        Commit merge = branch.getCommit();
        Commit ancestor = unzipCommit(objects.get(ancestorSha1));
        Folder mergeRootFolder = unzip(objects.get(merge.getRootFolderSHA1()));
        Folder ancestorRootFolder = unzip(objects.get(ancestor.getRootFolderSHA1()));
        Map<Integer, List<String>> headLists = showStatusCommitPath(getHead().getCommit(), ancestor);
        Map<Integer, List<String>> mergeLists = showStatusCommitPath(merge, ancestor);
        Map<String,Map<Integer,Blob>> conflicts = new HashMap<>();
        for (MagitObject obj : rootFolder.getFiles())
            obj.merge(headLists, mergeLists, conflicts, HEAD);
        for (MagitObject obj : mergeRootFolder.getFiles())
            obj.merge(headLists, mergeLists, conflicts, MERGE);

        for (String path : conflicts.keySet()){
            conflicts.get(path).put(HEAD,rootFolder.getFile(path));
            conflicts.get(path).put(MERGE,mergeRootFolder.getFile(path));
            conflicts.get(path).put(ANCESTOR,ancestorRootFolder.getFile(path));
        }

        return conflicts;

    }

    public Commit sha1ToCommit(String sha1) {
        try{return unzipCommit(objects.get(sha1));}
        catch (Exception e) { return null;}
    }

    public Boolean checkFF (Commit com1, Commit com2){
        Boolean res=false;
        if (com2!=null)
            res=(com1.getSha1().equals(com2.getSha1())||(checkFF(com1,com2.getPrev()))||(checkFF(com1,com2.getPrev2())));
        return res;
    }

    public void resetSpecificBranch (Branch br, String path) throws IOException, ClassNotFoundException{
        Commit com = unzipCommit(path);
        br.setCommit(com,repoPath);
    }

    public void fetch() throws IOException, ClassNotFoundException {
        File r = new File(remoteRepoPath + "\\.magit\\objects");
        File l = new File(magitPath + "\\objects");
        for (File child : r.listFiles()){
            if (!objects.keySet().contains(child.getName())){
                FileUtils.copyFile(child,new File(magitPath + "\\objects\\" + child.getName()));
                objects.put(child.getName(),magitPath + "\\objects\\" + child.getName());
            }
        }

        r = new File(remoteRepoPath + "\\.magit\\branches");
        for (File br : r.listFiles()){
            if (!(br.getName().equals("head"))){
                String sha1;
                try (BufferedReader in2 = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(br.getPath())))) {

                    sha1 = in2.readLine();
                }
                Commit com = unzipCommit(objects.get(sha1));
                if (branches.stream().anyMatch(branch -> branch.getName().equals(remoteRepoName + "\\" + br.getName()))) {
                    for (Branch branch : branches) {
                        if (branch.getName().equals(remoteRepoName + "\\" + br.getName()) && branch.getIsRemote()) {
                            branch.setCommit(com, repoPath);
                            try (Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(repoPath + "\\.magit" + "\\branches" + "\\" + branch.getName()), "UTF-8"))) {
                                    out.write(sha1);
                            }
                        }
                    }
                }
                else{
                    Branch branch1 = new Branch(br.getName(), branches, com,remoteRepoName);
                }

                }
            }
        updatePaths(remoteRepoPath,repoPath);
        }

    public void pull() throws IOException, ClassNotFoundException{


        Repository remoteRepo = new Repository(remoteRepoPath);
        Branch rrBranch = null;
        for (Branch br : remoteRepo.branches){
            if (br.getName().equals(headBranch.getName()))
                rrBranch = br;
        }
        importObjects(remoteRepo, this, rrBranch.getCommit());
        headBranch.setCommit(remoteRepo.getHead().getCommit(), repoPath);

        Branch remote = null;
        for (Branch br : branches){
            if (br.getName().equals(remoteRepoName + "\\" + headBranch.getName()))
                remote = br;
        }

        remote.setCommit(remoteRepo.getHead().getCommit(), repoPath);
        updatePaths(remoteRepoPath,repoPath);
        rootFolder.deleteChilds();
        rootFolder = unzip(objects.get(headBranch.getCommit().getRootFolderSHA1()));
        rootFolder.deploy(repoPath);

   }

    public void push() throws IOException, ClassNotFoundException{
        Repository remoteRepo = new Repository(remoteRepoPath);
        importObjects(this,remoteRepo,headBranch.getCommit());
        Branch remote = null;
        Branch rr = null;
        for (Branch br : branches){
            if (br.getName().equals(remoteRepoName + "\\" + headBranch.getName()))
                remote = br;
        }
        for (Branch br : remoteRepo.branches){
            if (br.getName().equals(headBranch.getName()))
                rr = br;
        }

        remote.setCommit(headBranch.getCommit(),repoPath);
        rr.setCommit(headBranch.getCommit(),remoteRepoPath);
        remoteRepo.updatePaths(repoPath, remoteRepoPath);
        remoteRepo.rootFolder.deleteChilds();
        remoteRepo.rootFolder = unzip(remoteRepo.objects.get(remoteRepo.headBranch.getCommit().getRootFolderSHA1()));
        remoteRepo.rootFolder.deploy(remoteRepoPath);


    }

    public void pushNew (Repository RRrepo) throws IOException, ClassNotFoundException{
        importObjects(this,RRrepo,headBranch.getCommit());
        headBranch.setRTB();
        Branch rb = new Branch (headBranch.getName(),branches,headBranch.getCommit(),remoteRepoName,repoPath);
        Branch rr = new Branch (headBranch.getName(),RRrepo.getBranches(),headBranch.getCommit(),remoteRepoPath);
        RRrepo.updatePaths(repoPath,remoteRepoPath);
    }

    public void newAndPush (Branch branch) throws IOException, ClassNotFoundException{
        Repository remoteRepo = new Repository(remoteRepoPath);
        importObjects(this,remoteRepo,branch.getCommit());
        branch.setRTB();
        Branch rr = new Branch(branch.getName(),remoteRepo.getBranches(),branch.getCommit(),remoteRepoPath);
        Branch remote = new Branch (remoteRepoName + "\\" + branch.getName(),branches,branch.getCommit(),remoteRepoName, repoPath);
        remoteRepo.updatePaths(repoPath, remoteRepoPath);
    }

    public Boolean readyToPush (Repository remoteRepo){
        Branch remote = null;
        for (Branch br : branches){
            if (br.getName().equals(remoteRepoName + "\\" + headBranch.getName()))
                remote = br;
        }

        return remote.getCommit().Sha1().equals(remoteRepo.getHead().getCommit().Sha1());
    }

   public void importObjects(Repository srcRepo, Repository destRepo, Commit commit) throws IOException, ClassNotFoundException{
        if (commit!=null) {
            destRepo.addObject(commit);
            destRepo.addObject(unzip(srcRepo.getObjects().get(commit.getRootFolderSHA1())));
            importObjects(srcRepo, destRepo, commit.getPrev());
            importObjects(srcRepo, destRepo, commit.getPrev2());
        }


   }

    public void updatePaths(String srcRepoPath, String destRepoPath){
        for (String path : objects.values()){
            try {
                MagitObject obj = unzip(path);
                obj.updatePath(srcRepoPath, destRepoPath);
                zipAndWriteObj(obj,path);
            }

            catch (Exception e){}
        }


    }

    public Boolean isHeadPushed(){
        Branch remote = null;
        for (Branch br : branches){
            if (br.getName().equals(remoteRepoName + "\\" + headBranch.getName()))
                remote = br;
        }

        return headBranch.getCommit().Sha1().equals(remote.getCommit().Sha1());
    }

    public void makeCommitList(List <Commit> commits){
        for (String path : objects.values()){
            try {
                Commit obj = unzipCommit(path);
                commits.add(obj);
            }

            catch (Exception e){}
        }
    }

    public Branch getBranch(String name){
        for (Branch br : branches){
            if (br.getName().equals(name))
                return br;
        }
        return null;
    }

}


