package engine;

import generated.*;
import javafx.application.Platform;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPOutputStream;


//import static engine.Settings.repoPath;
import static engine.Utils.*;

public class Xml {
    static Boolean invalid = false;
    static String message = "";
    static Map<String, String> objectsXml = new HashMap<>();

    public static void checkValidity(MagitRepository xmlRepo) throws Exception {
        List<String> idLst = new LinkedList<>();
        xmlRepo.getMagitBlobs().getMagitBlob().stream().forEach(obj -> idLst.add(obj.getId()));
        if (!checkDup(idLst)) {
            invalid = true;
            message = message.concat("There is a duplicate blob id! \n");
        }
        idLst.clear();
        xmlRepo.getMagitFolders().getMagitSingleFolder().stream().forEach(obj -> idLst.add(obj.getId()));
        if (!checkDup(idLst)) {
            invalid = true;
            message = message.concat("There is a duplicate folder id! \n");
        }
        idLst.clear();
        xmlRepo.getMagitCommits().getMagitSingleCommit().stream().forEach(obj -> idLst.add(obj.getId()));
        if (!checkDup(idLst)) {
            invalid = true;
            message = message.concat("There is a duplicate commit id! \n");
        }
        if (!checkPointerBlob(xmlRepo.getMagitFolders().getMagitSingleFolder(), xmlRepo.getMagitBlobs().getMagitBlob())) {
            invalid = true;
            message = message.concat("There is a blob pointer that doesnt exists! \n");
        }
        if (!checkPointerFolder(xmlRepo.getMagitFolders().getMagitSingleFolder())) {
            invalid = true;
            message = message.concat("There is a folder pointer that doesnt exists! \n");
        }
        if (!checkIdFolder(xmlRepo.getMagitFolders().getMagitSingleFolder())) {
            invalid = true;
            message = message.concat("There is a folder that points to itself! \n");
        }
        if (!checkIdCommit(xmlRepo.getMagitCommits().getMagitSingleCommit(), xmlRepo.getMagitFolders().getMagitSingleFolder())) {
            invalid = true;
            message = message.concat("There is a commit that points to a non existent root folder! \n");
        }
        if (!checkIsRoot(xmlRepo.getMagitCommits().getMagitSingleCommit(), xmlRepo.getMagitFolders().getMagitSingleFolder())) {
            invalid = true;
            message = message.concat("There is a commit that points to a non root folder! \n");
        }
        if (!checkIdBranch(xmlRepo.getMagitCommits().getMagitSingleCommit(), xmlRepo.getMagitBranches().getMagitSingleBranch())) {
            invalid = true;
            message = message.concat("There is a branch that points to a non existent commit! \n");
        }
        if (!checkHead(xmlRepo.getMagitBranches().getHead(), xmlRepo.getMagitBranches().getMagitSingleBranch())) {
            invalid = true;
            message = message.concat("The head points to a non existent branch! \n");
        }

        if (!checkMagitReference(xmlRepo.getMagitRemoteReference())) {
            invalid = true;
            message = message.concat("The remote magit reference doesnt points to a repository! \n");
        }
        if (!checkBranchesTracking(xmlRepo.getMagitBranches().getMagitSingleBranch())) {
            invalid = true;
            message = message.concat("there is an RTB branch that tracks a non existing or non remote branch!  \n");
        }
        if (invalid) {
            String message2 = message;
            message = "";
            throw new Exception(message2);
             }

    }

    public static Boolean checkHead(String head, List<MagitSingleBranch> branchLst) {
        for (MagitSingleBranch branch : branchLst) {
            if (branch.getName().equals(head)) {
                return true;
            }
        }
        return false;
    }

    public static Boolean checkIdBranch(List<MagitSingleCommit> commitLst, List<MagitSingleBranch> branchLst) {
        Boolean res = false;
        for (MagitSingleBranch branch : branchLst) {
            for (MagitSingleCommit commit : commitLst) {
                if (branch.getPointedCommit().getId().equals(commit.getId())) {
                    res = true;
                }
            }

            if (res == false) {
                return false;
            }
            res = false;
        }
        return true;
    }

    public static Boolean checkIsRoot(List<MagitSingleCommit> commitLst, List<MagitSingleFolder> folderLst) {
        for (MagitSingleCommit commit : commitLst) {
            for (MagitSingleFolder folder : folderLst) {
                if (folder.getId().equals(commit.getRootFolder().getId())) {
                    if (!(folder.isIsRoot()))
                        return false;
                }
            }
        }
        return true;
    }

    public static Boolean checkIdCommit(List<MagitSingleCommit> commitLst, List<MagitSingleFolder> folderLst) {
        for (MagitSingleCommit commit : commitLst) {
            if (folderLst.stream().filter(folder -> folder.getId().equals(commit.getRootFolder().getId())).count() == 0)
                return false;
        }
        return true;
    }

    public static Boolean checkIdFolder(List<MagitSingleFolder> lst) {
        for (MagitSingleFolder folder : lst) {
            for (Item temp : folder.getItems().getItem()) {
                if (temp.getType().equals("folder")) {
                    if (folder.getId().equals(temp.getId())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static Boolean checkPointerBlob(List<MagitSingleFolder> lst, List<MagitBlob> blobs) {
        Boolean res = false;
        for (MagitSingleFolder folder : lst) {
            for (Item temp : folder.getItems().getItem()) {
                if (temp.getType().equals("blob")) {
                    for (MagitBlob blob : blobs) {
                        if (blob.getId().equals(temp.getId())) {
                            res = true;
                        }

                    }

                    if (res == false)
                        return false;


                }
                res = false;
            }
        }
        return true;
    }

    public static Boolean checkPointerFolder(List<MagitSingleFolder> lst) {
        Boolean res = false;
        for (MagitSingleFolder folder : lst) {
            for (Item temp : folder.getItems().getItem()) {
                if (temp.getType().equals("folder")) {
                    for (MagitSingleFolder folder2 : lst) {
                        if (folder.getId().equals(folder2.getId())) {
                            res = true;
                        }

                    }

                    if (res == false)
                        return false;
                }
                res = false;
            }
        }
        return true;
    }

    public static Boolean checkDup(List<String> lst) {
        Set<String> setId = new HashSet<>();
        lst.stream().forEach(item -> setId.add(item));
        return (setId.size() == lst.size());
    }

    public static Boolean checkMagitReference (MagitRepository.MagitRemoteReference magitRemoteReference){
        if (magitRemoteReference != null && magitRemoteReference.getName() != null) {
            File f = new File(magitRemoteReference.getLocation() + "\\.magit");
            return (f.exists());
        }
        else{
            return true;
        }
    }

    public static Boolean checkBranchesTracking (List<MagitSingleBranch> branches){
        Boolean res = false;
        for (MagitSingleBranch branch : branches) {
            if (branch.isTracking()) {
                for (MagitSingleBranch kaka : branches) {
                    if (kaka.getName().equals(branch.getTrackingAfter()) && kaka.isIsRemote()) {
                        res = true;
                        break;
                    }
                }
                if (res == false)
                    return false;
                res = false;

            }
        }

        return true;
    }
    public static String findPath(MagitBlob blob, MagitRepository xmlRepo) {
        for (MagitSingleFolder folder : xmlRepo.getMagitFolders().getMagitSingleFolder()) {
            for (Item item : folder.getItems().getItem()) {
                if (blob.getId().equals(item.getId()) && item.getType().equals("blob")) {
                    if (folder.isIsRoot())
                        return "";
                     else{
                        String pathFolder= findPath(folder, xmlRepo);
                        if(pathFolder.equals(""))
                            return "\\" + folder.getName();
                        else
                            return findPath(folder, xmlRepo) + "\\" + folder.getName();
                    }

                }
            }

        }
        return "";
    }

    public static String findPath(MagitSingleFolder folder, MagitRepository xmlRepo) {
        for (MagitSingleFolder folder2 : xmlRepo.getMagitFolders().getMagitSingleFolder()) {
            for (Item item : folder2.getItems().getItem()) {
                if (folder.getId().equals(item.getId()) && item.getType().equals("folder")) {
                    if (folder2.isIsRoot())
                        return "";
                    else{
                        String pathFolder= findPath(folder2, xmlRepo);
                        if(pathFolder.equals(""))
                            return "\\" + folder2.getName();
                        else
                            return "\\" + findPath(folder2, xmlRepo) + "\\" + folder2.getName();
                    }
                }
            }

        }
        return "";
    }

    public static MagitBlob getBlob(String id, MagitRepository xmlRepo) {
        for (MagitBlob blob : xmlRepo.getMagitBlobs().getMagitBlob()) {
            if (id.equals(blob.getId()))
                return blob;
        }
        return null;
    }

    public static MagitSingleFolder getFolder(String id, MagitRepository xmlRepo) {
        for (MagitSingleFolder folder : xmlRepo.getMagitFolders().getMagitSingleFolder()) {
            if (id.equals(folder.getId()))
                return folder;
        }
        return null;
    }

    public static MagitSingleCommit getCommit(String id, MagitRepository xmlRepo) {
        for (MagitSingleCommit commit : xmlRepo.getMagitCommits().getMagitSingleCommit()) {
            if (id.equals(commit.getId()))
                return commit;
        }
        return null;
    }

    public static void createobjects(MagitRepository xmlRepo, String path) throws IOException, ClassNotFoundException {

        File f = new File(path);
        if ((f.exists())) {
           for (File child : f.listFiles()){
               if (child.isDirectory())
                   FileUtils.deleteDirectory(child);
               else
                   child.delete();
           }
        }
        createFolders(xmlRepo.getName(), path);
        for (MagitSingleFolder folder : xmlRepo.getMagitFolders().getMagitSingleFolder()) {
            loadFolderFromXml(folder, xmlRepo, path);
        }
        for (MagitSingleCommit commit : xmlRepo.getMagitCommits().getMagitSingleCommit()) {
            loadCommitFromXml(commit, xmlRepo, path);
        }
        writeToFile(xmlRepo.getMagitBranches().getHead(), path + "\\.magit\\branches" + "\\head");
        for (MagitSingleBranch branch : xmlRepo.getMagitBranches().getMagitSingleBranch()) {
            addBranchFromXml(branch, xmlRepo, path);
        }

        if (xmlRepo.getMagitRemoteReference() != null && xmlRepo.getMagitRemoteReference().getName() != null)
            try (Writer out = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(path + "\\.magit\\remoteRepoPath"), "UTF-8"))) {
                out.write(xmlRepo.getMagitRemoteReference().getLocation());
            }
    }

    public static void addBranchFromXml(MagitSingleBranch branch, MagitRepository xmlRepo, String repoPath) throws IOException {
        Commit head = loadCommitFromXml(getCommit(branch.getPointedCommit().getId(), xmlRepo), xmlRepo, repoPath);
        Branch branch1;
        if (branch.isIsRemote())
            branch1 = new Branch(branch.getName(), null, head, xmlRepo.getMagitRemoteReference().getName(), repoPath);
        else{
            branch1 = new Branch(branch.getName(), null, head, repoPath);
        }
        if (branch.isTracking())
            branch1.setRTB();


    }

    public static Commit loadCommitFromXml(MagitSingleCommit commit, MagitRepository xmlRepo, String repoPath) throws IOException {
        Folder rootFolder = loadFolderFromXml(getFolder(commit.getRootFolder().getId(), xmlRepo), xmlRepo, repoPath);
        Commit com;
        if ((commit.getPrecedingCommits() == null)||(commit.getPrecedingCommits().getPrecedingCommit().size()==0))
            com = new Commit(commit.getMessage(), commit.getAuthor(), rootFolder, null, commit.getDateOfCreation());
        else {
            if(commit.getPrecedingCommits().getPrecedingCommit().size() == 2){
                Commit prevCom = loadCommitFromXml(getCommit(commit.getPrecedingCommits().getPrecedingCommit().get(0).getId(), xmlRepo), xmlRepo, repoPath);
                Commit prev2Com = loadCommitFromXml(getCommit(commit.getPrecedingCommits().getPrecedingCommit().get(1).getId(), xmlRepo), xmlRepo, repoPath);
                com = new Commit(commit.getMessage(), commit.getAuthor(), rootFolder, prevCom,prev2Com, commit.getDateOfCreation());
            }
            else {
                Commit prevCom = loadCommitFromXml(getCommit(commit.getPrecedingCommits().getPrecedingCommit().get(0).getId(), xmlRepo), xmlRepo, repoPath);
                com = new Commit(commit.getMessage(), commit.getAuthor(), rootFolder, prevCom, commit.getDateOfCreation());
            }
        }

        addObjectFromXml(com, repoPath);
        return com;
    }

    public static Blob loadBlobFromXml(MagitBlob blob, MagitRepository xmlRepo, String repoPath) throws IOException {
        String path = findPath(blob, xmlRepo);
        Blob b = new Blob(blob.getLastUpdater(), blob.getName(), repoPath + path + "\\" + blob.getName(), blob.getContent(), blob.getLastUpdateDate());
        addObjectFromXml(b,repoPath);
        return b;
    }

    public static Folder loadFolderFromXml(MagitSingleFolder folder, MagitRepository xmlRepo, String repoPath) throws IOException {
        String path = findPath(folder, xmlRepo);
        Folder f;
        if (folder.isIsRoot())
            f = new Folder(folder.getLastUpdater(), "root", repoPath, folder.getLastUpdateDate());
        else
            f = new Folder(folder.getLastUpdater(), folder.getName(), repoPath + path + "\\" + folder.getName(), folder.getLastUpdateDate());
        addFolderXml(f, folder, xmlRepo, repoPath);
        addObjectFromXml(f, repoPath);
        return f;
    }

    public static void addFolderXml(Folder folder, MagitSingleFolder xmlFolder, MagitRepository xmlRepo, String repoPath) throws IOException {
        for (Item item : xmlFolder.getItems().getItem()) {
            if (item.getType().equals("blob")) {
                Blob b = loadBlobFromXml(getBlob(item.getId(), xmlRepo), xmlRepo, repoPath);
                folder.getMap().put(b.path, b);
            } else {
                Folder f = loadFolderFromXml(getFolder(item.getId(), xmlRepo), xmlRepo, repoPath);
                folder.getMap().put(f.path, f);
            }
        }

        folder.SHA1 = folder.Sha1();

    }

    public static void addObjectFromXml(MagitObject obj, String repoPath) throws IOException {

        if (obj == null || objectsXml.containsKey(obj.SHA1))
            return;
        String objPath = repoPath + "\\.magit\\objects\\" + obj.SHA1;
        objectsXml.put(obj.SHA1, objPath);
        zipAndWriteObj (obj, objPath);
        if (obj.getType() == Folder.class) {
            for (MagitObject fof : ((Folder) obj).getFiles()) {
                addObjectFromXml(fof, repoPath);
            }
        }
    }

    public static void addObjectFromXml(Commit obj, String repoPath) throws IOException {
        if (obj == null || objectsXml.containsKey(obj.Sha1()))
            return;

        String objPath = repoPath + "\\" + ".magit" + "\\" + "objects" + "\\" + obj.Sha1();
        zip(objPath, obj);

    }
}
