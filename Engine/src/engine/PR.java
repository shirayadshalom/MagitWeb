package engine;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PR {
    private String message;
    private String status;
    private Branch base;
    private Branch target;
    private String date;
    private Repository lr;
    private Repository rr;
    private String lrusername;
    private String reason;
    int id;
    static int num = 0;
    PR (String message, Branch base, Branch target, Repository lr, Repository rr, String lrusername) {
        id = ++num;
        this.message = message;
        this.base = base;
        this.target = target;
        this.lr = lr;
        this.rr = rr;
        this.lrusername = lrusername;
        reason = "";
        status = "open";
        Date today = Calendar.getInstance().getTime();
        date = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:SSS").format(today);
    }

    public String toString(){
        return "ID: " + id +"," + "LR: " + lr.getName() + "," + "RR: " + rr.getName() + "," + "message : " + message + "," + "base : " + base.getName() + "," + "target : " + target.getName() + "," + "date : " + date + "," + "reason : " + reason + "," + "status : " + status;
    }

    public Repository getRR(){return rr;}

    public String getDate(){return date;}

    public void accept() throws IOException, ClassNotFoundException {
        rr.resetSpecificBranch(base,rr.getObjects().get(target.getCommit().getSha1()));

        status = "accepted";
    }

    public void reject(String reason) throws IOException, ClassNotFoundException {
        status = "rejected";
        this.reason = reason;
    }

    public String getlrusername(){return lrusername;}

    public void setReason(String reason){this.reason = reason;}

    public int getId(){return id;}

    public Branch getBase(){return base;}
}
