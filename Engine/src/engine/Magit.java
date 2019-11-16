package engine;


import components.main.MagitController;
import generated.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.*;

//import static engine.Settings.repoPath;
import static engine.Utils.readFromFile;
import static engine.Utils.unzip;
import static engine.Xml.*;

public class Magit {
    String username;
    Map<String, Repository> repos;
    Repository activerepo;
    private MagitController controller;
    private Task<Boolean> currentRunningTask;

    public Magit(){
        username = "Administrator";
        repos = new HashMap<>();
        activerepo = null;
    }

   public Magit(MagitController magitController){
        this.controller = magitController;
         username = "Administrator";
        repos = new HashMap<>();
        activerepo = null;
    }


    public Collection<Repository> getRepos(){return repos.values();}
    public Repository getRepo(String name){
        return repos.get(name);
    }
    public Repository getActiverepo(){return activerepo;}

    public String getRepoName(){return activerepo.getName();}

    public String getUserName(){ return username;};

    public String getRepoPath(){ return activerepo.getPath();}

    public void setUserName(String username){ this.username = username; }

    public void createRepo(String path, String reponame) throws Exception {
        activerepo = new Repository(username, reponame, path);
        repos.put(reponame,activerepo);
    }

    public Boolean commit(String message, Commit prev2)throws IOException{
        return activerepo.commit(username, message, prev2);
    }

    public Boolean changeActiveRepo(String path) throws IOException, ClassNotFoundException{

        File f = new File(path + "\\.magit");
        if (f.exists()){
            String reponame = readFromFile(path + "\\.magit\\RepoName");
            if (repos.containsKey(reponame))
                activerepo = repos.get(reponame);
            else {
                activerepo = new Repository(path);
                repos.put(reponame,activerepo);
            }
        }
        else{
            return false;
        }
        return  true;
    }

    public Boolean isDelta() throws IOException{
        if (activerepo == null)
            return false;
        else
            return activerepo.isDelta(username);
    }

    public Map<Integer, List<String>> showStatus() throws  Exception {
        return activerepo.showStatus(username);
    }

    public List<String> getBranches(){
        Branch head = activerepo.getHead();
        Set<Branch> branches = activerepo.getBranches();
        List<String> lst = new LinkedList<>();
        lst.add(head.toString() + " - Head");
        branches.stream().filter(br -> br != head).forEach(br -> lst.add(br.toString()));
        return lst;
    }

    public List<String> getBranchesName(){
        Branch head = activerepo.getHead();
        Set<Branch> branches = activerepo.getBranches();
        List<String> lst = new LinkedList<>();
        lst.add(head.toStringSpecified());
        branches.stream().filter(br -> br != head&&!(br.getIsRemote())).forEach(br -> lst.add(br.toStringSpecified()));
        return lst;
    }

    public List<String> getAllBranchesName(){
        Branch head = activerepo.getHead();
        Set<Branch> branches = activerepo.getBranches();
        List<String> lst = new LinkedList<>();
        lst.add(head.toStringSpecified());
        branches.stream().filter(br -> br != head).forEach(br -> lst.add(br.toStringSpecified()));
        return lst;
    }

    public List<String> getBranchesNameWithoutHead(){
        Branch head = activerepo.getHead();
        Set<Branch> branches = activerepo.getBranches();
        List<String> lst = new LinkedList<>();
        branches.stream().filter(br -> br != head&&!(br.getIsRemote())).forEach(br -> lst.add(br.toStringSpecified()));
        return lst;
    }

    public void allBranchOfCommit(String sha1,Set<String> branchesName,Set<Branch> branches){
        for (Branch branch: branches){
            branch.allBranchOfCommit(sha1,branchesName, branch.getCommit());}
    }

    public String allBranches(String sha1,Set<Branch> branches){
        Set<String> allBranches=new HashSet<>();
        allBranchOfCommit(sha1,allBranches,branches);
        String str="";
        for(String branchName:allBranches){
            str=str.concat(" "+branchName);
        }
        return str;
    }

    public List<String> getListSha1(){
        Branch head = activerepo.getHead();
        List<String> lst = new LinkedList<>();
        Commit com= head.getCommit();
        while (com.getPrev()!=null){
            lst.add(com.getPrev().Sha1());
            com=com.getPrev();
        }
        while (com.getPrev2()!=null){
            lst.add(com.getPrev2().Sha1());
            com=com.getPrev2();
        }

        return lst;
    }

    public Boolean isHead(String name){
        return activerepo.isHead(name);
    }

    public Boolean deleteBranch(String name){
        return activerepo.deleteBranch(name);
    }

    public List<String> ShowCurrentBranch()throws ClassNotFoundException, IOException
    { return activerepo.getHead().getCommit().ShowCurrentBranch(activerepo.getMagitPath());}

    public String aboutTheCommit(){return activerepo.getHead().getCommit().toString();}

    public Branch createNewBranch(String branchName) throws IOException{
        Branch newBranch= new Branch(branchName,activerepo.getBranches(),activerepo.getHead().getCommit(),activerepo.getPath());
        return newBranch;
    }

    public void createNewBranch(String branchName,Commit com) throws IOException{
        Branch newBranch= new Branch(branchName,activerepo.getBranches(),com,activerepo.getPath());
    }

    public Boolean checkChanges () throws IOException{
       return activerepo.checkChanges(username);
    }

    public void checkOutBranch(String branchName) throws IOException,ClassNotFoundException{ activerepo.checkOutBranch(branchName); }

    public Boolean isBranchExists (String branchName){
        return activerepo.isBranchExists(branchName);
    }

    public List<String> getHistoryOfActiveBranch() {
        return activerepo.getHistoryOfActiveBranch();
    }

    public Boolean checkXml (String path) throws JAXBException, Exception{
        MagitRepository xmlRepo = null;

            if (!(FilenameUtils.getExtension(path).equals("xml"))){
                throw new Exception("The file is not an xml file!");
            }
        try {
            File file = new File(path);
            JAXBContext jaxbContext = JAXBContext.newInstance(MagitRepository.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            xmlRepo = (MagitRepository) jaxbUnmarshaller.unmarshal(file);

        }

        finally {}

        checkValidity(xmlRepo);
        File file = new File("c:\\magit-ex3\\" + username + "\\" + xmlRepo.getName() + "\\" + ".magit");
        return (!(file.exists()));
    }

    public void loadFromXML (String path) throws JAXBException, Exception{
        MagitRepository xmlRepo = null;
        try {

            File file = new File(path);

            JAXBContext jaxbContext = JAXBContext.newInstance(MagitRepository.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            xmlRepo = (MagitRepository) jaxbUnmarshaller.unmarshal(file);
        }

        finally {}

        String destPath = "c:\\magit-ex3\\" + username + "\\" + xmlRepo.getName();
        createobjects(xmlRepo, destPath);
        activerepo = new Repository(destPath);
        repos.put(xmlRepo.getName(),activerepo);
        activerepo.getRootFolder().deploy(destPath);
        objectsXml.clear();
    }

    public void resetBranch (String sha1) throws IOException,ClassNotFoundException{
        activerepo.resetBranch(sha1);
    }

    public Boolean IsCommitExists (String sha1){ return activerepo.isCommitExists(sha1); }

    public Boolean isRepo(){return !(activerepo==null);}

    public Boolean isCommit(){return !(activerepo.getHead().getCommit()==null);}

    public void clone (String rrPath, String lrPath, String destName) throws IOException, ClassNotFoundException{
        File lr = new File(lrPath);
        File rr = new File(rrPath);
        FileUtils.copyDirectory(rr,lr);
        String srcName;
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(rrPath + "\\.magit\\repoName")))) {

            srcName = in.readLine();

        }
        activerepo = new Repository(rrPath, destName, srcName, lrPath);
        repos.put(activerepo.getName(),activerepo);


    }

    public void fetch () throws IOException, ClassNotFoundException{
        activerepo.fetch();

    }

    public void pull() throws IOException, ClassNotFoundException{
       activerepo.pull();

    }

    public void push() throws IOException, ClassNotFoundException{
        activerepo.push();

    }

    public void loadFromXMLTask (String path, Runnable onFinish) throws Exception{

        currentRunningTask = new LoadFromXMLTask(path,this);

        controller.bindTaskToUIComponents(currentRunningTask, onFinish);

        //new Thread(currentRunningTask).start();
    }

    public Boolean readyToPush (Repository remoteRepo){
        return activerepo.readyToPush(remoteRepo);
    }


}
