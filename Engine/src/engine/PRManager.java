package engine;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PRManager {
    private final Map<String,Map<Integer,PR>> PRMap;
    public PRManager(){PRMap = new HashMap<>(); }

    public Map<Integer,PR> getPRs(String username){return PRMap.get(username);}

    public PR addPR (String username,String message,Branch base, Branch target, Repository lr, Repository rr, String lrusername){
        if (PRMap.get(username) == null)
            PRMap.put(username,new HashMap<>());
        PR pr = new PR(message,base,target, lr, rr, lrusername);
        PRMap.get(username).put(pr.getId(),pr);
        return pr;
    }

    public void removePR(String username, PR pr){
        PRMap.get(username).remove(pr.getId());
    }
}
