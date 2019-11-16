package visual.layout;

import com.fxgraph.graph.Graph;
import com.fxgraph.graph.ICell;
import com.fxgraph.layout.Layout;
import engine.Commit;
import visual.node.CommitNode;

import java.util.*;
import java.util.stream.Collectors;



public class CommitTreeLayout implements Layout {
    Map<String, ICell> cellMap;
    Commit firstCommit;
    List<Commit> commitLst;
    public CommitTreeLayout(Map<String, ICell> cellMap, Commit firstCommit, List<Commit> commitLst)
    {
        this.cellMap=cellMap;
        this.firstCommit= firstCommit;
        this.commitLst = commitLst;
    }

    @Override
    public void execute(Graph graph) {
        final List<ICell> cells = graph.getModel().getAllCells();
        List<CommitNode> orderedNodes = cells.stream().map(c ->((CommitNode) c)).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        int startX = 10;
        int startY = 50;
        HashMap<ICell,Integer> cellIntMap = new HashMap<>();
        for(Commit commit:commitLst) {
            if(organize(cellIntMap, commit, startX))
                startX+=50;
        }
        for (CommitNode node : orderedNodes) {
            startY += 50;
            graph.getGraphic(node).relocate(cellIntMap.get(node), startY);
        }
    }

    private boolean organize(HashMap<ICell,Integer> cellIntMap,Commit commit,Integer x)
    {
        String sha1=commit.getSha1();
        if(cellIntMap.get(cellMap.get(commit.getSha1()))==null) {
            while(!sha1.equals(""))
            {
                cellIntMap.put(cellMap.get(sha1),x);

                if (commit.getPrev() == null) {
                    sha1 = "";
                }
                else{
                    sha1=commit.getPrev().getSha1();
                    if(cellIntMap.get(cellMap.get(sha1))!=null)
                        sha1="";
                    commit = commit.getPrev();
                }

            }
            return true;
        }
        return false;
    }
}