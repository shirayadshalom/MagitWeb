package visual.node;

import com.fxgraph.cells.AbstractCell;
import com.fxgraph.graph.Graph;
import com.fxgraph.graph.IEdge;
import components.main.MagitController;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;

public class CommitNode extends AbstractCell implements Comparable<CommitNode>  {

    private String timestamp;
    private String committer;
    private String message;
    private String branch;
    private String sha1;
    private CommitNodeController commitNodeController;
    private MagitController magitController;

    public CommitNode(String timestamp, String committer, String message, String branch, String sha1, MagitController magitController) {
        this.timestamp = timestamp;
        this.committer = committer;
        this.message = message;
        this.branch = branch;
        this.sha1 = sha1;
        this.magitController = magitController;
    }

    public String getBranch(){return branch;}

    public CommitNodeController getCommitNodeController(){return  commitNodeController;}

    @Override
    public Region getGraphic(Graph graph) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            URL url = getClass().getResource("commitNode.fxml");
            fxmlLoader.setLocation(url);
            GridPane root = fxmlLoader.load(url.openStream());
            commitNodeController = fxmlLoader.getController();
            commitNodeController.setCommitMessage(message);
            commitNodeController.setCommitter(committer);
            commitNodeController.setCommitTimeStamp(timestamp);
            commitNodeController.setCommitBranch(branch);
            commitNodeController.setMagitController(magitController);
            commitNodeController.setCommitSha1(sha1);

            return root;
        } catch (IOException e) {
            return new Label("Error when tried to create graphic node !");
        }
    }

    @Override
    public DoubleBinding getXAnchor(Graph graph, IEdge edge) {
        final Region graphic = graph.getGraphic(this);
        return graphic.layoutXProperty().add(commitNodeController.getCircleRadius());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommitNode that = (CommitNode) o;

        return timestamp != null ? timestamp.equals(that.timestamp) : that.timestamp == null;
    }

    @Override
    public int hashCode() {
        return timestamp != null ? timestamp.hashCode() : 0;
    }

    @Override
    public int compareTo(CommitNode other) {
        Long key1 = null,key2=null;
        try{key1 = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:SSS").parse(timestamp).getTime();
            key2 = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:SSS").parse(other.timestamp).getTime();

        }
        catch (java.text.ParseException e) { System.out.println(e);};
        return key1.compareTo(key2);
    }
}
