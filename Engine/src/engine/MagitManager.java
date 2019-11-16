package engine;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MagitManager {
    private final Map<String, Magit> magits;
    public MagitManager(){
        magits = new HashMap<>();
    }

    public synchronized void addMagit(String username) {
        magits.put(username, new Magit());
        magits.get(username).setUserName(username);
    }

    public synchronized Magit getMagit(String username){
        return magits.get(username);
    }
    public synchronized Collection<Magit> getMagits(){
        return magits.values();
    }
}
