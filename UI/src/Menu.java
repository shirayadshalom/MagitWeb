import engine.Magit;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.*;


public class Menu {

    private static Magit m;

    public static void main(String[] args) {

        m = new Magit();
        boolean isValidInput=false;
        while (!isValidInput){
            try {
                printMenu(isValidInput);
                isValidInput=true;
            }
            catch (IOException e) {
                System.out.println("The path is incorrect: ");
            } catch (InputMismatchException e) {
                System.out.println("Invalid input: ");
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
            finally {
                if (!isValidInput) {
                    System.out.println("Please try again");
                }
            }
        }}

    static public void printMenu(Boolean isValidInput) throws IOException, ClassNotFoundException,InputMismatchException,Exception {
        Scanner s = new Scanner(System.in);
        int choice;
        boolean fContiune = true;

        do {
            System.out.println("Magit Menu            Current User: " + m.getUserName());
            System.out.println("Choose one of the following opitions:");
            System.out.println("1. Change user name");
            System.out.println("2. Load from XML");
            System.out.println("3. Switch repository");
            System.out.println("4. Show current commit file system information");
            System.out.println("5. Working copy status");
            System.out.println("6. Commit");
            System.out.println("7. List available branches");
            System.out.println("8. Create new branch");
            System.out.println("9. Delete branch");
            System.out.println("10. Checkout branch");
            System.out.println("11. Show current branch history");
            System.out.println("12. Create new repository");
            System.out.println("13. Reset branch");
            System.out.println("14. Exit");

            System.out.print("Enter your choice --> ");
            choice = s.nextInt();

            switch (choice) {
                case 1: {
                    setActiveUserName();
                    break;
                }
                case 2:
                    LoadFromXML();
                    break;
                case 3:
                    changeActiveRepo();
                    break;

                case 4:
                    ShowCurrentBranch();
                    break;

                case 5:
                    showStatus();
                    break;

                case 6:
                    commit();
                    break;

                case 7:
                    showBranches();
                    break;

                case 8:
                    createNewBranch();
                    break;

                case 9:
                    deleteBranch();
                    break;

                case 10:
                    checkoutBranch();
                break;

                case 11:
                    ShowHistoryOfActiveBranch();
                    break;

                case 12:
                    createRepo();
                    break;

                case 13:
                    resetBranch();
                    break;

                case 14:
                    fContiune = false;
                    isValidInput=true;
                    break;

                default:
                    System.out.print("Invalid option ");
                    break;

            }
            System.out.println();
        } while (fContiune);
        System.out.print("Goodbye! ");
    }

    public static void LoadFromXML()throws IOException, JAXBException, Exception {
        System.out.println("Please enter path of xml file: ");
        Scanner s = new Scanner(System.in);
        String path = s.nextLine();
        if (m.checkXml(path))
            m.LoadFromXML(path);
        else {
            System.out.println("There is an existing repository in the given path, do you wish to override it? Y\\N");
            String answer = s.nextLine();
            if ((answer.equals("y")) || (answer.equals("Y"))) {
                m.LoadFromXML(path);
            }
        }


    }

    public static void checkoutBranch()throws IOException,ClassNotFoundException,Exception {
        isRepo();
        isCommit();
        System.out.println("Please enter a branch name: ");
        Scanner s = new Scanner(System.in);
        String branchName = s.nextLine();
        if (!(m.isBranchExists(branchName))){
            System.out.println("The branch name not exists in the system");
        }
        else {
            if (m.checkChanges()){
                System.out.println("It appears you have pending changes, are you sure you want ot continue? Y\\N");
                String answer = s.nextLine();
                if ((answer.equals("y")) || (answer.equals("Y"))) {
                    m.checkOutBranch(branchName);
                }
                else{
                    System.out.println("No checkout was made");
                }

            } else {
                m.checkOutBranch(branchName);
            }
        }
    }

    public static void ShowHistoryOfActiveBranch()throws Exception{
        isRepo();
        isCommit();
        List<String> HistoryOfCommit=m.getHistoryOfActiveBranch();
        HistoryOfCommit.stream().forEach(str -> System.out.println(str));
    }

    public static void resetBranch() throws IOException, ClassNotFoundException, Exception{
        isRepo();
        System.out.println("Please enter SHA1: ");
        Scanner s = new Scanner(System.in);
        String sha1 = s.nextLine();
        if (m.IsCommitExists(sha1)){
            if (m.checkChanges()) {
                System.out.println("you have pending changes, do you wish to proceed? Y\\N");
                String answer = s.nextLine();
                if ((answer.equals("y")) || (answer.equals("Y"))){
                    m.resetBranch(sha1);
                    ShowCurrentBranch();}
                else
                    System.out.println("No checkout was made");
            }
            else{
                m.resetBranch(sha1);
                ShowCurrentBranch();
            }
        }
        else
            System.out.println("Commit doesnt exists!");


    }

    public static void ShowCurrentBranch() throws ClassNotFoundException, IOException,Exception{
        isRepo();
        isCommit();
        System.out.println(m.aboutTheCommit());
        List<String> commitState=m.ShowCurrentBranch();
        commitState.stream().forEach(str -> System.out.println(str));
    }

    public static void createNewBranch()throws IOException, ClassNotFoundException,Exception{
        isRepo();
        isCommit();
        System.out.println("Please enter a branch name: ");
        Scanner s = new Scanner(System.in);
        String branchName = s.nextLine();
        if (!(m.isBranchExists(branchName))){
            m.createNewBranch(branchName);
            System.out.println("Do you want to checkout to this branch? Y\\N");
            String answer = s.nextLine();
            if ((answer.equals("y")) || (answer.equals("Y"))){
                if (m.checkChanges()){
                    System.out.println("You have pending changes, no checkout was made");
                }
                else{m.checkOutBranch(branchName);}
            }
        }
        else {System.out.println("The branch name exists in the system");}
    }

    public static void showStatus() throws IOException, ClassNotFoundException,Exception{
        isRepo();
        isCommit();

        System.out.println("User name: " + m.getUserName() + "\n");
        System.out.println("Repository name: " + m.getRepoName() + "\n");
        System.out.println("Repository path: "+m.getRepoPath()+"\n");
        if(!m.isDelta()){System.out.println("No changes! \n");return; }
        Map<Integer, List<String>> changes = m.showStatus();
        List<String> created = changes.get(1);
        List<String> changed = changes.get(2);
        List<String> deleted = changes.get(3);

        System.out.println("objects created: "  + "\n");
        created.stream().forEach(str -> System.out.println(str));
        System.out.println("\nobjects changed: " + "\n");
        changed.stream().forEach(str -> System.out.println(str));
        System.out.println("\nobjects deleted: " + "\n");
        deleted.stream().forEach(str -> System.out.println(str));
    }

    public static void setActiveUserName() throws InputMismatchException {
        System.out.println("Please enter a user name: ");
        Scanner s = new Scanner(System.in);
        String name = s.nextLine();
        m.setUserName(name);
    }

    public static void changeActiveRepo() throws IOException, ClassNotFoundException {
        System.out.println("Please enter full path: ");
        Scanner s = new Scanner(System.in);
        String path = s.nextLine();
        if (m.isDelta()){
            System.out.println("It appears you have pending changes, are you sure you want ot continue? Y\\N");
            String answer = s.nextLine();
            if ((answer.equals("y")) || (answer.equals("Y"))){
                Boolean res = m.changeActiveRepo(path);
                if (!res){
                    System.out.println("not a valid repo path");
                }
            }
        }
        else{
            Boolean res = m.changeActiveRepo(path);
            if (!res){
                System.out.println("not a valid repo path");
            }
        }
    }

    public static void createRepo() throws ClassNotFoundException,InputMismatchException,Exception{
        System.out.println("Please enter path: ");
        Scanner s = new Scanner(System.in);
        String path = s.nextLine();
        System.out.println("Please enter repo name: ");
        String name = s.nextLine();
        m.createRepo(path, name);
    }

    public static void commit() throws IOException,Exception {
        isRepo();
        System.out.println("Please enter commit message: ");
        Scanner s = new Scanner(System.in);
        String message = s.nextLine();
        m.commit(message);
    }

    public static void showBranches()throws Exception{
        isRepo();
        isCommit();
        System.out.println("Branches:\n");
        List <String> branches = m.getBranches();
        branches.forEach(str -> System.out.println(str));
    }

    public static void deleteBranch()throws Exception{
        isRepo();
        isCommit();
        System.out.println("Enter the name of branch to delete: ");
        Scanner s = new Scanner(System.in);
        String branch = s.nextLine();
        if (m.isHead(branch))
            System.out.println(branch + " is the head branch, cant erase!");
        else{
            if (m.deleteBranch(branch))
                System.out.println(branch + " deleted");
            else
                System.out.println(branch + " does not exists");
        }
    }

    public static void isRepo()throws Exception{
        if (!m.isRepo()){
            throw new Exception ("You must create repository!");
        }
    }

    public static void isCommit()throws Exception{
        if (!m.isCommit()){
            throw new Exception ("You must create commit!");
        }
    }
}

