package visual.node;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import components.main.MagitController;

public class CommitNodeController {

    @FXML private GridPane commitNodeGridPane;
    @FXML private Label commitTimeStampLabel;
    @FXML private Label messageLabel;
    @FXML private Label committerLabel;
    @FXML private Circle CommitCircle;
    @FXML private Label branchLabel;
    @FXML private Label sha1Label;
    private MagitController magitController;


    @FXML
    void commitNodeOnClick(MouseEvent event) {
        ContextMenu cm = new ContextMenu();
        MenuItem mi1 = new MenuItem("Delete branch");
        cm.getItems().add(mi1);
        mi1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                magitController.setClickedCommit(sha1Label.getText(),mi1.getText());
            }
        });



        MenuItem mi2 = new MenuItem("Checkout branch");
        cm.getItems().add(mi2);
        mi2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                magitController.setClickedCommit(sha1Label.getText(),mi2.getText());
            }
        });
        MenuItem mi3 = new MenuItem("Merge");
        cm.getItems().add(mi3);
        mi3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                magitController.setClickedCommit(sha1Label.getText(),mi3.getText());
            }
        });
        MenuItem mi4 = new MenuItem("Create new branch");
        cm.getItems().add(mi4);
        mi4.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                magitController.setClickedCommit(sha1Label.getText(),mi4.getText());
            }
        });
        commitNodeGridPane.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                if(t.getButton() == MouseButton.SECONDARY) {
                    cm.show(commitNodeGridPane, t.getScreenX(), t.getScreenY());
                }
            }
        });
        magitController.setClickedCommit(sha1Label.getText());

    }

    @FXML
    void branchOnClick(MouseEvent event) {
        magitController.setClickedBranch(sha1Label.getText());
    }


    public void setMagitController(MagitController magitController) {
        this.magitController = magitController;
    }

    public void setCommitTimeStamp(String timeStamp) {
        commitTimeStampLabel.setText(timeStamp);
        commitTimeStampLabel.setTooltip(new Tooltip(timeStamp));
    }

    public void setCommitSha1(String sha1) {
        sha1Label.setText(sha1);
        sha1Label.setTooltip(new Tooltip(sha1));
    }

    public void setCommitBranch(String branch) {
        branchLabel.setText(branch);
        branchLabel.setTooltip(new Tooltip(branch));
    }

    public void setCommitter(String committerName) {
        committerLabel.setText(committerName);
        committerLabel.setTooltip(new Tooltip(committerName));
    }

    public void setCommitMessage(String commitMessage) {
        messageLabel.setText(commitMessage);
        messageLabel.setTooltip(new Tooltip(commitMessage));
    }

    public int getCircleRadius() {
        return (int)CommitCircle.getRadius();
    }

    public Circle getCircle(){return CommitCircle;}
}
