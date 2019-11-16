package engine;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NotificationsManager {
    private final Map<String,Map<String, List<String>>> notifications;
    public NotificationsManager(){notifications = new HashMap<>(); }

    public Map<String, List<String>> getNotifications(String username){return notifications.get(username);}

    public void addFork (String username,String message){
        if (notifications.get(username) == null)
            notifications.put(username,new HashMap<>());
        if (notifications.get(username).get("forks") == null)
            notifications.get(username).put("forks",new LinkedList<>());
        notifications.get(username).get("forks").add(message);
    }

    public void addPR (String username,PR pr){
        if (notifications.get(username) == null)
            notifications.put(username,new HashMap<>());
        if (notifications.get(username).get("PRs") == null)
            notifications.get(username).put("PRs",new LinkedList<>());
        notifications.get(username).get("PRs").add(pr.toString());
    }

    public void removePR (String username, PR pr){
        for (String prToString : notifications.get(username).get("PRs")){
            if (prToString.equals(pr.toString()))
                notifications.get(username).get("PRs").remove(prToString);

        }
    }
}
