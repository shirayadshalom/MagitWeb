package components.merge;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import components.main.MagitController;
import engine.Blob;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import engine.Utils;

import java.io.IOException;
import java.util.*;

import static engine.Utils.*;

public class MergeController {

    private Stage primaryStage;
    private MagitController magitController;
    Map<String, Map<Integer, Blob>> conflicts;
    Map<Integer, Blob> currentConflict;
    String currentPath;

    @FXML
    private TextArea headTextArea;

    @FXML
    private TextArea mergeTextArea;

    @FXML
    private TextField resultTextField;

    @FXML
    private TextArea ancestorTextArea;

    private SimpleStringProperty ancestorTextAreaProperty;
    private SimpleStringProperty resultTextFieldProperty;
    private SimpleStringProperty mergeTextAreaProperty;
    private SimpleStringProperty headTextAreaProperty;


    public MergeController(){
        ancestorTextAreaProperty = new SimpleStringProperty();
        //resultTextFieldProperty = new SimpleStringProperty();
        mergeTextAreaProperty = new SimpleStringProperty();
        headTextAreaProperty = new SimpleStringProperty();

    }

    @FXML
    private void initialize() {
        ancestorTextArea.textProperty().bind(ancestorTextAreaProperty);
        //resultTextField.textProperty().bind(resultTextFieldProperty);
        mergeTextArea.textProperty().bind(mergeTextAreaProperty);
        headTextArea.textProperty().bind( headTextAreaProperty);

    }

    @FXML
    void doneButtonAction(ActionEvent event)  throws IOException{
        currentConflict.get(HEAD).setContent(resultTextField.getText());
        resultTextField.setText("");
        conflicts.remove(currentPath);
        try {
            currentConflict.get(HEAD).deploy(magitController.getMagit().getRepoPath());
        } catch (IOException e) {
            System.out.println(e);
        }
        showNextConflict();
    }

    @FXML
    void startButtonAction(ActionEvent event) throws IOException {
        showNextConflict();
    }

    void showNextConflict() throws IOException{


        if (conflicts.keySet().size() != 0){
            List<String> conflictsPaths = new LinkedList<>();
            conflicts.keySet().stream().forEach(p -> conflictsPaths.add(p));
            ChoiceDialog<String> choiceDialog = new ChoiceDialog<>(conflictsPaths.get(0),conflictsPaths);
            choiceDialog.setContentText("Choose conflicts to deal with");
            choiceDialog.setHeaderText("Coflicts");
            Optional<String> conflictPath = choiceDialog.showAndWait();
            currentConflict = conflicts.get(conflictPath.get());
            currentPath = conflictPath.get();
            if (currentConflict.get(HEAD) != null)
                headTextAreaProperty.set(currentConflict.get(HEAD).getContent());
            else
                headTextAreaProperty.set("file doesnt exists");
            if (currentConflict.get(MERGE) != null)
                mergeTextAreaProperty.set(currentConflict.get(MERGE).getContent());
            else
                mergeTextAreaProperty.set("file doesnt exists");
            if (currentConflict.get(ANCESTOR) != null)
                ancestorTextAreaProperty.set(currentConflict.get(ANCESTOR).getContent());
            else
                ancestorTextAreaProperty.set("file doesnt exists");
        }
        else {
            primaryStage.close();
            magitController.commit();
        }
    }

    public void setPrimaryStage(Stage primaryStage) {this.primaryStage = primaryStage;}
    public void setMagitController(MagitController magitContoller) {this.magitController = magitContoller;}
    public void setConflicts(Map<String, Map<Integer, Blob>> conflicts){this.conflicts = conflicts;}

}
