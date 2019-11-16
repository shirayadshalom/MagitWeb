package engine;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

//import static engine.Settings.repoPath;

public class Branch  {
    private Commit commit;
    private String name;
    private Boolean isRemote;
    private Boolean RTB;

    public Branch (String name, Set<Branch> branches, Commit head, String repoPath) throws IOException{
        this.name = name;
        commit = head;
        if (branches != null)
            branches.add(this);

        try (Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(repoPath + "\\.magit" + "\\branches" + "\\" + name), "UTF-8"))) {
            if (commit == null){
                out.write("");
            }
            else{
                out.write(commit.Sha1());
            }
        }

        isRemote = false;
        RTB = false;
    }

    public Branch (String name, Set<Branch> branches, Commit head, String rrName, String repoPath) throws IOException{
        if (name.contains("\\"))
            this.name = name;
        else {
            this.name = rrName + "\\" + name;
        }

        commit = head;
        File f = new File(repoPath + "\\.magit\\branches\\" + rrName);
        if (!f.exists())
            f.mkdir();

        try (Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(repoPath + "\\.magit\\branches\\" + rrName + "\\" + name), "UTF-8"))) {
            if (commit == null){
                out.write("");
            }
            else{
                out.write(commit.Sha1());
            }
        }
        isRemote = true;
        if (branches != null)
            branches.add(this);
        RTB = false;
    }

    public Commit getCommit(){return commit;}

    public String getName(){return name;}

    public Boolean isRTB(){return RTB;}

    public void setCommit (Commit commit, String repoPath) throws IOException{
        this.commit=commit;

        try (Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(repoPath + "\\.magit" + "\\branches" + "\\" + name), "UTF-8"))) {
            if (commit == null){
                out.write("");
            }
            else{
                out.write(commit.Sha1());
            }
        }
    }

    public void getHistoryOfBranch(Commit head, List<String> res){

        if (head == null)
            return;

        res.add(head.toStringSpecified());
        if (head.getPrev()!= null)
            getHistoryOfBranch(head.getPrev(),res);
        if (head.getPrev2()!= null)
            getHistoryOfBranch(head.getPrev2(),res);
    }

    public String toString(){
        return "Name: " + name + "\nCommit's SHA1: " + commit.Sha1() + " \nCommit's message: "
                + commit.getMessage() + "\nIs remote: " + isRemote + "\nIs remote tracking: " + RTB;
    }

    public String toStringSpecified(){
        return name;
    }

    public void setRTB(){RTB = true;}

    public Boolean getIsRemote () {return isRemote;}

    public void allBranchOfCommit(String sha1,Set<String> branchesName, Commit com){
        if (com != null){
            if (com.Sha1().equals(sha1))
                branchesName.add(name);
            else{
                allBranchOfCommit(sha1, branchesName, com.getPrev());
                allBranchOfCommit(sha1, branchesName, com.getPrev2());
            }
        }
    }

    public Boolean isExists (Commit com){
        return isExistsRec (commit, com);
    }

    private Boolean isExistsRec (Commit curr, Commit com){
        if (curr!=null){
            return (curr == com || isExistsRec(curr.getPrev(),com) || isExistsRec(curr.getPrev2(),com));
        }

        return false;
    }



}
