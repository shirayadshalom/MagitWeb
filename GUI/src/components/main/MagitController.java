package components.main;
import com.fxgraph.edges.Edge;
import com.fxgraph.graph.Graph;
import com.fxgraph.graph.ICell;
import com.fxgraph.graph.Model;
import com.fxgraph.graph.PannableCanvas;
import components.merge.MergeController;
import engine.*;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import visual.layout.CommitTreeLayout;
import visual.node.CommitNode;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;



public class MagitController {

    @FXML
    private Button showCommitFileButton;

    @FXML
    private Button showPrevSha1Button;

    @FXML
    private Button showDeltaButton;

    @FXML
    private TreeView<MagitObject> wcTree;

    @FXML
    private TreeView<String> commitTree;

    @FXML
    private Menu commitMainMenuButton;

    @FXML
    private Menu branchMenuButton;

    @FXML
    private Button pullButton;

    @FXML
    private Button pushButton;

    @FXML
    private Button mergeButton;

    @FXML
    private Button fetchButton;

    @FXML
    private Button commitButton;

    @FXML
    private Label selectedUserNameLabel;

    @FXML
    private Label selectedFileNameLabel;

    @FXML
    private Label blobNameLabel;

    @FXML
    private TextArea blobContentTextArea;

    @FXML
    private Label branchOrCommitNameLabel;

    @FXML
    private TextArea branchOrCommitContentTextArea;

    @FXML
    private ScrollPane graphicScrollPane;

    @FXML
    private TextArea sha1OrdeltaTextArea;

    @FXML
    private Menu remoteMenuButton;


    private SimpleStringProperty sha1OrdeltaProperty;
    private SimpleStringProperty branchOrCommitNameProperty;
    private SimpleStringProperty branchOrCommitContentProperty;
    private SimpleStringProperty blobNameProperty;
    private SimpleStringProperty blobContentProperty;
    private SimpleStringProperty userNameProperty;
    private SimpleStringProperty repositoryPathProperty;
    private SimpleBooleanProperty isRepositoryloaded;
    private SimpleBooleanProperty isRepositoryNow;
    private SimpleBooleanProperty isTwoBranchesNow;
    private SimpleBooleanProperty isCommitNow;
    private SimpleBooleanProperty isCommitDone;
    private SimpleBooleanProperty isCommitSelected;
    private SimpleBooleanProperty isBlueSkin;
    private SimpleBooleanProperty isPinkSkin;

    private Stage primaryStage;
    private Magit magit;
    private Commit clickedCommit;
    private Graph tree;
    private Map<Integer, Edge> edges;
    private Map<String, ICell> commitNodes;
    private Commit currentMergedCommit;

    public MagitController() {
        magit = new Magit();
        clickedCommit = null;
        sha1OrdeltaProperty = new SimpleStringProperty();
        repositoryPathProperty = new SimpleStringProperty();
        userNameProperty = new SimpleStringProperty();
        blobNameProperty = new SimpleStringProperty();
        blobContentProperty = new SimpleStringProperty();
        branchOrCommitNameProperty = new SimpleStringProperty();
        branchOrCommitContentProperty = new SimpleStringProperty();
        isCommitDone = new SimpleBooleanProperty(false);
        isCommitSelected = new SimpleBooleanProperty(false);
        isRepositoryloaded = new SimpleBooleanProperty(false);
        isRepositoryNow = new SimpleBooleanProperty(false);
        isCommitNow = new SimpleBooleanProperty(false);
        isBlueSkin=new SimpleBooleanProperty(false);
        isPinkSkin=new SimpleBooleanProperty(false);
        isTwoBranchesNow=new SimpleBooleanProperty(false);
            isRepositoryNow.addListener((observable, oldValue, newValue) -> {
            if (newValue == true) {

                try {
                    deployFileSystem();
                } catch (IOException e) {
                    System.out.println(e);
                }
                if(magit.getBranchesName().size()>=2){
                    isTwoBranchesNow.set(true);
                }

                if(magit.getActiverepo().getHead().getCommit()!=null){
                    deployCommitTree();
                    edges = new HashMap<>();
                    commitNodes = new HashMap<>();
                    tree = new Graph();
                    PannableCanvas canvas = tree.getCanvas();
                    graphicScrollPane.setContent(canvas);
                    if (isPinkSkin.get()){
                        graphicScrollPane.setStyle("-fx-background-color: #ffe7e0");
                    }
                    if (isBlueSkin.get()){
                        graphicScrollPane.setStyle("-fx-background-color: #d4e6ff;");
                    }
                    createCommits(tree);
                    Platform.runLater(() -> {
                        tree.getUseViewportGestures().set(false);
                        tree.getUseNodeGestures().set(false);
                    });
                }
                else{
                    tree = new Graph();
                    PannableCanvas canvas = tree.getCanvas();
                    graphicScrollPane.setContent(canvas);
                    commitTree.setRoot(null);
                }

            }
        });

        edges = new HashMap<>();
        commitNodes = new HashMap<>();
    }

    @FXML
    private void initialize() {
        sha1OrdeltaTextArea.textProperty().bind(sha1OrdeltaProperty);
        selectedFileNameLabel.textProperty().bind(repositoryPathProperty);
        selectedUserNameLabel.textProperty().bind(userNameProperty);
        userNameProperty.set("User name: Administrator");
        blobNameLabel.textProperty().bind(blobNameProperty);
        blobContentTextArea.textProperty().bind(blobContentProperty);
        branchOrCommitContentTextArea.textProperty().bind(branchOrCommitContentProperty);
        branchOrCommitNameLabel.textProperty().bind(branchOrCommitNameProperty);
        branchMenuButton.disableProperty().bind(isRepositoryloaded.not());
        commitMainMenuButton.disableProperty().bind(isRepositoryloaded.not());
        commitButton.disableProperty().bind(isRepositoryloaded.not());
        showCommitFileButton.disableProperty().bind(isCommitSelected.not());
        showDeltaButton.disableProperty().bind(isCommitSelected.not());
        showPrevSha1Button.disableProperty().bind(isCommitSelected.not());
        pullButton.disableProperty().bind(isRepositoryloaded.not());
        remoteMenuButton.disableProperty().bind(isRepositoryloaded.not());
        pushButton.disableProperty().bind(isRepositoryloaded.not());
        wcTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> onClickNode(newValue));
        wcTree.setShowRoot(false);
        commitTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> onClickNodebranch(newValue));
        commitTree.setShowRoot(false);
        mergeButton.disableProperty().bind(isTwoBranchesNow.not());
        fetchButton.disableProperty().bind(isRepositoryloaded.not());
        branchMenuButton.disableProperty().bind(isRepositoryloaded.not());

        tree = new Graph();
        PannableCanvas canvas = tree.getCanvas();
        graphicScrollPane.setContent(canvas);
        tree.getUseViewportGestures().set(false);
        tree.getUseNodeGestures().set(false);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Magit getMagit () {return magit;}

    public void setMagit(Magit magit) {
        this.magit = magit;
        //magit.fileNameProperty().bind(repositoryPathProperty); //Get the path!!!!!
    }

    public void setClickedCommit(String sha1) {
        try {
            clickedCommit = Utils.unzipCommit(magit.getActiverepo().getObjects().get(sha1));
            isCommitSelected.set(true);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void setClickedBranch(String sha1) {
        try {
            clickedCommit = Utils.unzipCommit(magit.getActiverepo().getObjects().get(sha1));
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Want to choose a branch and highlight all the commits that belong to the branch?");
            alert.setContentText("Are you ok with this?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                Set <String> branchesName=new HashSet<>();
                magit.allBranchOfCommit(sha1,branchesName,magit.getActiverepo().getBranches());
                List<String> lst = new ArrayList<>();
                for (String str : branchesName)
                    lst.add(str);
                ChoiceDialog<String> choiceDialog = new ChoiceDialog<String>(lst.get(0), branchesName);
                choiceDialog.setContentText("Choose branch");
                choiceDialog.setHeaderText(" ");
                Optional<String> branch = choiceDialog.showAndWait();
                CommitNode curr=null;
                String str;
                for (ICell cur : commitNodes.values()) {
                    curr=(CommitNode)cur;
                    str=curr.getBranch();
                    if (str.contains(branch.get())){
                        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.5),curr.getCommitNodeController().getCircle());
                        scaleTransition.setAutoReverse(true);
                        scaleTransition.setCycleCount(4);
                        scaleTransition.setToX(2);
                        scaleTransition.setToY(2);
                        scaleTransition.play();
                    }

                }

            } else { return; }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void setClickedCommit(String sha1,String choice) {
        try {
            clickedCommit = Utils.unzipCommit(magit.getActiverepo().getObjects().get(sha1));
            isCommitSelected.set(true);
            if (choice.equals("Create new branch")){
                TextInputDialog dialog = new TextInputDialog("");
                dialog.setTitle("Create new branch");
                dialog.setHeaderText("Enter branch name: ");
                dialog.setContentText("Name:");
                Optional<String> branchName = dialog.showAndWait();
                if (branchName.isPresent()) {
                    if (!(magit.isBranchExists(branchName.get()))) {
                        magit.createNewBranch(branchName.get(),clickedCommit);
                        isRepositoryNow.set(true);
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Checkout");
                        alert.setHeaderText("Do you want to checkout to this branch?");
                        alert.setContentText("Are you ok with this?");
                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.get() == ButtonType.OK) {
                            if (magit.checkChanges()) {
                                alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Information Dialog");
                                alert.setHeaderText(null);
                                alert.setContentText("You have pending changes, no checkout was made!");

                                alert.showAndWait();

                            } else {
                                magit.checkOutBranch(branchName.get());
                            }


                        }
                    } else {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Information Dialog");
                        alert.setHeaderText(null);
                        alert.setContentText("The branch exists");
                        alert.showAndWait();
                    }
                }
                return;

            }
            Set <String> branchesName=new HashSet<>();
            magit.allBranchOfCommit(sha1,branchesName,magit.getActiverepo().getBranches());
            List<String> lst = new ArrayList<>();
            for (String str : branchesName)
                if(magit.getBranchesName().contains(str) ) { lst.add(str);}
            ChoiceDialog<String> choiceDialog = new ChoiceDialog<String>(lst.get(0), lst);
            choiceDialog.setContentText("Choose branch");
            choiceDialog.setHeaderText(choice);
            Optional<String> branch = choiceDialog.showAndWait();
            if (choice.equals("Delete branch")){
                if (magit.isHead(branch.get())) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText(branch.get() + " is the head branch, cant erase!");
                    alert.showAndWait();
                } else {
                    magit.deleteBranch(branch.get());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText(branch.get() + " deleted");
                    alert.showAndWait();
                    isRepositoryNow.set(true);
                }
            }
            if (choice.equals("Checkout branch")){
                if (magit.checkChanges()) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Checkout");
                    alert.setHeaderText("you have pending changes,are you sure you want ot continue?");
                    alert.setContentText("Are you ok with this?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        magit.checkOutBranch(branch.get());
                        isRepositoryNow.set(true);
                    } else {
                        alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Information Dialog");
                        alert.setHeaderText(null);
                        alert.setContentText("No checkout was made!");

                    }


                } else {
                    magit.checkOutBranch(branch.get());
                    isRepositoryNow.set(true);
                }

            }
            if (choice.equals("Merge")){
                Branch branch1 = null;
                Map<String, Map<Integer, Blob>> conflicts;
                for (Branch br : magit.getActiverepo().getBranches()) {
                    if (br.toStringSpecified().equals(branch.get())) {
                        branch1 = br;
                    }
                }
                currentMergedCommit = branch1.getCommit();

                if (magit.isDelta()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText("there is active changes!");
                    alert.showAndWait();
                } else {
                    if (magit.getActiverepo().checkFF(magit.getActiverepo().getHead().getCommit(),branch1.getCommit())){
                        magit.resetBranch(branch1.getCommit().Sha1());
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Information Dialog");
                        alert.setHeaderText(null);
                        alert.setContentText("Fast Forward Merge-merged commit");
                        alert.showAndWait();
                        return;
                    }
                    if (magit.getActiverepo().checkFF(branch1.getCommit(),magit.getActiverepo().getHead().getCommit())){
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Information Dialog");
                        alert.setHeaderText(null);
                        alert.setContentText("Fast Forward Merge-head");
                        alert.showAndWait();
                        return;

                    }
                    conflicts = magit.getActiverepo().merge(branch1);
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    URL url = getClass().getResource("\\components\\merge\\merge.fxml");
                    fxmlLoader.setLocation(url);
                    ScrollPane root = fxmlLoader.load();
                    MergeController mergeController = fxmlLoader.getController();
                    mergeController.setConflicts(conflicts);
                    Scene mergeScene = new Scene(root, 1000, 600);
                    Stage mergeStage = new Stage();
                    mergeController.setPrimaryStage(mergeStage);
                    mergeController.setMagitController(this);
                    mergeStage.setTitle("Conflicts");
                    mergeStage.setScene(mergeScene);
                    if (isPinkSkin.get()){
                        mergeStage.getScene().getStylesheets().add(getClass().getResource("MagitControllerPink.css").toExternalForm());
                    }
                    if (isBlueSkin.get()){
                        mergeStage.getScene().getStylesheets().add(getClass().getResource("MagitControllerBlue.css").toExternalForm());
                    }
                    mergeStage.show();
                }

            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @FXML
    void refreshButtonAction(ActionEvent event) throws IOException {
        deployFileSystem();
    }

    @FXML
    void showCommitFileButtonAction(ActionEvent event) {
        deployCommit();
    }

    private void createCommits(Graph graph) {
        final Model model = graph.getModel();
        graph.beginUpdate();
        List <Commit> commitList = new ArrayList<>();
        for (Branch branch : magit.getActiverepo().getBranches()) {
            branch.getCommit().buildGraph(model, branch.getName(), edges, commitNodes, this, commitList);
        }
        for (String sha1 : commitNodes.keySet()) {
            model.addCell(commitNodes.get(sha1));
        }

        Collections.sort(commitList, Collections.reverseOrder());

        edges.values().stream().forEach(edge -> model.addEdge(edge));
        graph.endUpdate();
        graph.layout(new CommitTreeLayout(commitNodes, commitList.get(0), commitList));

    }

    void deployCommitTree() {
        if(magit.getActiverepo().getHead().getCommit()!=null) {
            Set<Branch> branches = magit.getActiverepo().getBranches();
            String head = "head";
            TreeItem<String> treeRoot = new TreeItem<>(head);
            for (Branch branch : branches) {
                TreeItem<String> item;
                if (branch.toStringSpecified().equals(magit.getActiverepo().getHead().toStringSpecified())) {
                    item = new TreeItem<>("Branch: " + branch.toStringSpecified() + " - Head");
                } else {
                    item = new TreeItem<>("Branch: " + branch.toStringSpecified());
                }

                final Node icon = new ImageView(new Image(getClass().getResourceAsStream("\\Resources\\branch.png")));
                ((ImageView) icon).setFitWidth(15);
                ((ImageView) icon).setFitHeight(13);
                item.setGraphic(icon);
                branch.getCommit().buildTree(item);
                treeRoot.getChildren().add(item);
            }
            treeRoot.setExpanded(true);
            commitTree.setRoot(treeRoot);
        }

    }

    void deployCommit() {
        Folder root = null;
        if(clickedCommit!=null){
        try {
            root = Utils.unzip(magit.getActiverepo().getObjects().get(clickedCommit.getRootFolderSHA1()));
        } catch (Exception e) {
            System.out.println(e);
        }
        TreeItem<MagitObject> treeRoot = new TreeItem<>(root);
        root.buildTree(treeRoot);
        treeRoot.setExpanded(true);
        wcTree.setRoot(treeRoot);
        }
    }

    private void onClickNodebranch(TreeItem<String> node) {
        if (node == null) {
            branchOrCommitNameProperty.set("");
            branchOrCommitContentProperty.set("");
        } else {

            if (node.getValue().contains("Branch")) {
                Branch branch = null;
                Set<Branch> branches = magit.getActiverepo().getBranches();
                String name = node.getValue().split(" ")[1];
                for (Branch item : branches) {
                    if (name.equals(item.getName())) {
                        branch = item;
                    }
                }
                branchOrCommitNameProperty.set(node.getValue());
                branchOrCommitContentProperty.set(branch.toString());
            } else {
                String sha1 = node.getValue().split(" ")[1];
                try {
                    clickedCommit = Utils.unzipCommit(magit.getActiverepo().getObjects().get(sha1));
                    isCommitSelected.set(true);
                } catch (Exception e) {
                    System.out.println(e);
                }
                branchOrCommitNameProperty.set("");
                branchOrCommitContentProperty.set(clickedCommit.toStringSpecified());

            }


        }
    }

    private void onClickNode(TreeItem<MagitObject> node) {
        if (node == null) {
            blobNameProperty.set("");
            blobContentProperty.set("");
        } else {
            if (node.getValue().getType() == Blob.class) {
                blobNameProperty.set("File Name: " + node.getValue().toString());
                blobContentProperty.set((((Blob) (node.getValue())).getContent()));
            }
        }
    }

    void deployFileSystem() throws IOException {
        Folder root = magit.getActiverepo().getRootFolder();
        root.updateState(magit.getUserName());
        TreeItem<MagitObject> treeRoot = new TreeItem<>(root);
        root.buildTree(treeRoot);
        treeRoot.setExpanded(true);
        wcTree.setRoot(treeRoot);
        if (isRepositoryNow.getValue() == true)
            isRepositoryNow.set(false);
    }

    @FXML
    void changeActiveRepo() throws ClassNotFoundException, IOException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select repository ");
        File selectedFile = directoryChooser.showDialog(primaryStage);
        if (selectedFile == null) {
            return;
        }
        String absolutePath = selectedFile.getAbsolutePath();
        if (magit.changeActiveRepo(absolutePath)) {
            repositoryPathProperty.set("RepoPath: " + absolutePath);
            isRepositoryloaded.set(true);
            isRepositoryNow.set(true);
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("not a valid repo path");
            alert.showAndWait();
        }
    }

    @FXML
    void cloneAction() throws IOException,ClassNotFoundException{

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select source repository ");
        File rr = directoryChooser.showDialog(primaryStage);
        if (rr == null) {
            return;
        }
        File f = new File(rr.getAbsolutePath() + "\\.magit");
        if (!f.exists()){

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("not a valid repo path");
            alert.showAndWait();
            return;
        }

        directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Destination repository location ");
        File lr = directoryChooser.showDialog(primaryStage);
        if (lr == null) {
            return;
        }
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Repository name");
        dialog.setHeaderText("Enter destination repository name:");
        dialog.setContentText("Name:");
        Optional<String> name = dialog.showAndWait();
        magit.clone(rr.getAbsolutePath(),lr.getAbsolutePath(),name.get());
        isRepositoryloaded.set(true);
        isRepositoryNow.set(true);
        repositoryPathProperty.set("RepoPath: " + magit.getActiverepo().getPath());
    }

    @FXML
    void fetchButtonAction() throws IOException, ClassNotFoundException{
        magit.fetch();
        isRepositoryNow.set(true);
    }

    @FXML
    void pullButtonAction(ActionEvent event) throws IOException, ClassNotFoundException {
        if (magit.checkChanges()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("You have pending changes!");
            alert.showAndWait();
            return;
        }
        if (!magit.getActiverepo().getHead().isRTB()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("The head branch is not a remote tracking branch!");
            alert.showAndWait();
            return;
        }
        if (!magit.getActiverepo().isHeadPushed()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("There are unpushed changes on the head branch!");
            alert.showAndWait();
            return;
        }

        magit.pull();
        isRepositoryNow.set(true);

    }

    @FXML
    void pushButtonAction() throws IOException, ClassNotFoundException{
        Repository remoteRepo = new Repository(magit.getActiverepo().getRemoteRepoPath());
        if (remoteRepo.isDelta(magit.getUserName())){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("There are pending changes on the remote repo!");
            alert.showAndWait();
            return;
        }

        if (!magit.getActiverepo().getHead().isRTB()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("the local head branch is not remote tracking!");
            alert.showAndWait();
            return;
        }

        if (remoteRepo.getBranches().stream().noneMatch(br -> br.getName().equals(magit.getActiverepo().getHead().getName()))){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("the remote tracking branch does not exists on the remote repository!");
            alert.showAndWait();
            return;
        }

        if (!magit.readyToPush(remoteRepo)){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("the remote branch and the head branch of the remote repo is not synced");
            alert.showAndWait();
            return;
        }

        magit.push();
        isRepositoryNow.set(true);
    }

    @FXML
    public void LoadFromXML() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select repository");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile == null) {
            return;
        }
        String absolutePath = selectedFile.getAbsolutePath();
        try {
            if (magit.checkXml(absolutePath)) {
                magit.loadFromXMLTask(absolutePath,() -> {
                    isRepositoryloaded.set(true);
                    isRepositoryNow.set(true);
                        }
                );


            } else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Existing repository");
                alert.setHeaderText("There is an existing repository in the given path, do you wish to override it?");
                alert.setContentText("Are you ok with this?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    magit.loadFromXMLTask(absolutePath,() -> {
                                isRepositoryloaded.set(true);
                                isRepositoryNow.set(true);
                            }
                    );
                    /*magit.LoadFromXML(absolutePath);
                    repositoryPathProperty.set(magit.getRepoPath());
                    isRepositoryloaded.set(true);
                    isRepositoryNow.set(true);*/


                }
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Look, an Error Dialog");
            alert.setContentText(e.getMessage() + "\n" + "Try again!");
            alert.showAndWait();
        }
    }

    @FXML
    public void setActiveUserName() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Set active user name");
        dialog.setHeaderText("Enter user name:");
        dialog.setContentText("Name:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            magit.setUserName(result.get());
            userNameProperty.set("User name: " + result.get());
        }
    }

    @FXML
    public void pushNewBranchButtonAction() throws IOException, ClassNotFoundException{
        magit.getActiverepo().newAndPush(createNewBranch());
        isRepositoryNow.set(true);
    }

    @FXML
    public void createRepo() throws Exception {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select repository location ");
        File selectedFile = directoryChooser.showDialog(primaryStage);
        if (selectedFile == null) {
            return;
        }
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Repository name");
        dialog.setHeaderText("Enter repository name:");
        dialog.setContentText("Name:");
        Optional<String> result = dialog.showAndWait();
        String absolutePath = selectedFile.getPath()+ result.get();
        String newRepoName = result.get();
        repositoryPathProperty.set(absolutePath);
        magit.createRepo(absolutePath, newRepoName);
        isRepositoryloaded.set(true);
        isRepositoryNow.set(true);
    }

    @FXML
    public void deleteBranch() {
        List<String> branchesName = magit.getBranchesName();
        ChoiceDialog<String> choiceDialog = new ChoiceDialog<String>(branchesName.get(0), branchesName);
        choiceDialog.setContentText("Choose branch to delete");
        choiceDialog.setHeaderText("Delete branch");
        Optional<String> branch = choiceDialog.showAndWait();
        if (magit.isHead(branch.get())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText(branch.get() + " is the head branch, cant erase!");
            alert.showAndWait();
        } else {
            magit.deleteBranch(branch.get());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText(branch.get() + " deleted");
            alert.showAndWait();
            isRepositoryNow.set(true);
        }

    }

    @FXML
    public Branch createNewBranch() throws IOException, ClassNotFoundException {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Create new branch");
        dialog.setHeaderText("Enter branch name: ");
        dialog.setContentText("Name:");
        Optional<String> branchName = dialog.showAndWait();
        Branch newb = null;
        if (branchName.isPresent()) {
            if (!(magit.isBranchExists(branchName.get()))) {
                newb = magit.createNewBranch(branchName.get());
                isRepositoryNow.set(true);
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Checkout");
                alert.setHeaderText("Do you want to checkout to this branch?");
                alert.setContentText("Are you ok with this?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    if (magit.checkChanges()) {
                        alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Information Dialog");
                        alert.setHeaderText(null);
                        alert.setContentText("You have pending changes, no checkout was made!");

                        alert.showAndWait();

                    } else {
                        magit.checkOutBranch(branchName.get());
                    }


                }
                return newb;
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("The branch exists");
                alert.showAndWait();
            }
        }
        return null;
    }

    @FXML
    public void resetBranch() throws IOException, ClassNotFoundException {
        List<String> listSha1Name = magit.getListSha1();
        ChoiceDialog<String> choiceDialog = new ChoiceDialog<String>(listSha1Name.get(0), listSha1Name);
        choiceDialog.setContentText("Choose SHA1:");
        choiceDialog.setHeaderText("Reset branch");
        Optional<String> sha1 = choiceDialog.showAndWait();
            if (sha1.isPresent()) {
            if (magit.IsCommitExists(sha1.get())) {
                if (magit.checkChanges()) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Checkout");
                    alert.setHeaderText("you have pending changes, do you wish to proceed?");
                    alert.setContentText("Are you ok with this?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        magit.resetBranch(sha1.get());
                        ShowCurrentBranch();
                        isRepositoryNow.set(true);
                    } else {
                        alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Information Dialog");
                        alert.setHeaderText(null);
                        alert.setContentText("No checkout was made!");
                        alert.showAndWait();
                    }
                } else {
                    magit.resetBranch(sha1.get());
                    isRepositoryNow.set(true);
                    ShowCurrentBranch();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("Commit doesnt exists!");
                alert.showAndWait();

            }

        }

    }

    @FXML
    public void checkoutBranch() throws IOException, ClassNotFoundException {
        List<String> branchesName = magit.getBranchesName();
        ChoiceDialog<String> choiceDialog = new ChoiceDialog<String>(branchesName.get(0), branchesName);
        choiceDialog.setContentText("Choose branch to checkout");
        choiceDialog.setHeaderText("Checkout branch");
        Optional<String> branchName = choiceDialog.showAndWait();
        if (magit.checkChanges()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Checkout");
            alert.setHeaderText("you have pending changes,are you sure you want ot continue?");
            alert.setContentText("Are you ok with this?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                magit.checkOutBranch(branchName.get());
                isRepositoryNow.set(true);
            } else {
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("No checkout was made!");

            }


        } else {
            magit.checkOutBranch(branchName.get());
            isRepositoryNow.set(true);
        }
    }

    @FXML
    public void commit() throws IOException {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Commit");
        dialog.setHeaderText("Enter commit message: ");
        dialog.setContentText("commit message:");
        Optional<String> message = dialog.showAndWait();
        if (message.isPresent()) {
            if (currentMergedCommit != null) {
                if (magit.commit(message.get(), currentMergedCommit)){
                    currentMergedCommit = null;}
                 else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText("No change from the last commit! Not created new Commit");
                    alert.showAndWait();
                    }


            }
            else
            if (magit.commit(message.get(), null)){}
            else{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("No change from the last commit! Not created new Commit");
                alert.showAndWait();
            }
            isCommitDone.set(true);
            isRepositoryNow.set(true);
        }
    }

    @FXML
    public void showBranches() {
        List<String> branches = magit.getBranches();
        String str = "";
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Branches");
        alert.setHeaderText(null);
        for (String branch : branches) {
            str = str.concat(branch + "\n");
        }
        alert.setContentText(str);
        alert.showAndWait();
    }

    @FXML
    public void ShowCurrentBranch() throws IOException, ClassNotFoundException {
        String str = "";
        str = magit.aboutTheCommit() + "\n";
        List<String> commitState = magit.ShowCurrentBranch();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Current branch");
        alert.setHeaderText(null);
        for (String branch : commitState) {
            str = str.concat(branch + "\n");
        }
        alert.setContentText(str);
        alert.showAndWait();

        isRepositoryNow.set(true);
        isCommitNow.set(true);
    }

    @FXML
    public void showStatus() throws Exception {
        String str = "";
        if (!magit.isDelta()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("No changes!!");
            alert.showAndWait();
            return;
        }
        str = "User name: " + magit.getUserName() + "\n" + "Repository name: " + magit.getRepoName() + "\n" + "Repository path: " + magit.getRepoPath() + "\n";
        Map<Integer, List<String>> changes = magit.showStatus();
        List<String> created = changes.get(1);
        List<String> changed = changes.get(2);
        List<String> deleted = changes.get(3);
        str = str.concat("objects created: " + "\n");
        for (String item : created) {
            str = str.concat(item + "\n");
        }
        str = str.concat("\nobjects changed: " + "\n" + "\n");
        for (String item : changed) {
            str = str.concat(item + "\n");
        }
        str = str.concat("\nobjects deleted: " + "\n");
        for (String item : deleted) {
            str = str.concat(item + "\n");
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Working copy status");
        alert.setHeaderText(null);
        alert.setContentText(str);
        alert.showAndWait();

    }

    @FXML
    public void ShowHistoryBranches() {
        String str = "";
        List<String> HistoryOfCommit = magit.getHistoryOfActiveBranch();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Show history");
        alert.setHeaderText(null);
        for (String branch : HistoryOfCommit) {
            str = str.concat(branch + "\n");
        }
        alert.setContentText(str);
        alert.showAndWait();
    }

    @FXML
    void showprevsha1ButtonAction() {
        if(magit.getActiverepo().getHead().getCommit()!=null) {
            List<String> lst = new ArrayList<>();
            clickedCommit.prevSha1(lst);
            String str = "";
            for (String sha1 : lst) {
                str = str.concat("\n" + sha1);
            }
            sha1OrdeltaProperty.set(str);
            branchOrCommitContentProperty.set(str);
        }
    }

    @FXML
    void showDeltaButtonAction() throws IOException, ClassNotFoundException {

        List<String> lst = new ArrayList<>();
        clickedCommit.prevSha1(lst);
        if (lst.size() != 0) {
            ChoiceDialog<String> choiceDialog = new ChoiceDialog<String>(lst.get(0), lst);
            choiceDialog.setContentText("Choose prev ");
            choiceDialog.setHeaderText("");
            Optional<String> prev = choiceDialog.showAndWait();
            Commit prevCommit = null;
            if (clickedCommit.getPrev().Sha1().equals(prev.get()))
                prevCommit = clickedCommit.getPrev();
            else
                prevCommit = clickedCommit.getPrev2();

            Map<Integer, List<String>> changes = magit.getActiverepo().showStatusCommit(clickedCommit, prevCommit);
            String str = "";
            if (changes == null)
                str = "there is no previous commit";
            else {
                List<String> created = changes.get(1);
                List<String> changed = changes.get(2);
                List<String> deleted = changes.get(3);

                str = str.concat("objects created: " + "\n");
                for (String item : created) {
                    str = str.concat(item + "\n");
                }
                str = str.concat("\nobjects changed: " + "\n" + "\n");
                for (String item : changed) {
                    str = str.concat(item + "\n");
                }
                str = str.concat("\nobjects deleted: " + "\n");
                for (String item : deleted) {
                    str = str.concat(item + "\n");
                }
            }
            sha1OrdeltaProperty.set(str);
            branchOrCommitContentProperty.set(str); /////////shira
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Look, an Error Dialog");
            alert.setContentText("Ooops, no prev!");

            alert.showAndWait();

        }
    }

    @FXML
    void mergeButtonAction() throws IOException, ClassNotFoundException {
        List<String> branchesName = magit.getBranchesNameWithoutHead();
        ChoiceDialog<String> choiceDialog = new ChoiceDialog<>(branchesName.get(0), branchesName);
        choiceDialog.setContentText("Choose branch to merge with");
        choiceDialog.setHeaderText("Merge");
        Optional<String> branch = choiceDialog.showAndWait();
        Branch branch1 = null;
        Map<String, Map<Integer, Blob>> conflicts;
        for (Branch br : magit.getActiverepo().getBranches()) {
            if (br.toStringSpecified().equals(branch.get())) {
                branch1 = br;
            }
        }
        currentMergedCommit = branch1.getCommit();

        if (magit.isDelta()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("there is active changes!");
            alert.showAndWait();
        } else {
            if (magit.getActiverepo().checkFF(magit.getActiverepo().getHead().getCommit(),branch1.getCommit())){
                magit.resetBranch(branch1.getCommit().Sha1());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("Fast Forward Merge-merged commit");
                alert.showAndWait();
                return;
            }
            if (magit.getActiverepo().checkFF(branch1.getCommit(),magit.getActiverepo().getHead().getCommit())){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("Fast Forward Merge-head");
                alert.showAndWait();
                return;

            }
            conflicts = magit.getActiverepo().merge(branch1);
            FXMLLoader fxmlLoader = new FXMLLoader();
            URL url = getClass().getResource("\\components\\merge\\merge.fxml");
            fxmlLoader.setLocation(url);
            ScrollPane root = fxmlLoader.load();
            MergeController mergeController = fxmlLoader.getController();
            mergeController.setConflicts(conflicts);
            Scene mergeScene = new Scene(root, 1000, 600);
            Stage mergeStage = new Stage();
            mergeController.setPrimaryStage(mergeStage);
            mergeController.setMagitController(this);
            mergeStage.setTitle("Conflicts");
            mergeStage.setScene(mergeScene);
            mergeStage.show();
            isRepositoryNow.set(true);
            }


    }

    public void bindTaskToUIComponents(Task<Boolean> aTask, Runnable onFinish) {
        repositoryPathProperty.set(magit.getRepoPath());
        onTaskFinished(Optional.ofNullable(onFinish));
    }

    public void onTaskFinished(Optional<Runnable> onFinish) {
        onFinish.ifPresent(Runnable::run);
    }

    @FXML
    void changeSkin() {
        List<String> choices = new ArrayList<>();
        choices.add("pink");
        choices.add("blue");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("pink", choices);
        dialog.setTitle("Change Skin");
        dialog.setHeaderText("");
        dialog.setContentText("Choose color:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){

            if(result.get().equals("pink")){
                primaryStage.getScene().getStylesheets().add(getClass().getResource("\\components\\main\\MagitControllerPink.css").toExternalForm());
                isPinkSkin.set(true);
                isBlueSkin.set(false);
            }
            else {
                primaryStage.getScene().getStylesheets().add(getClass().getResource("\\components\\main\\MagitControllerBlue.css").toExternalForm());
                isBlueSkin.set(true);
                isPinkSkin.set(false);
            }
        }




    }
}