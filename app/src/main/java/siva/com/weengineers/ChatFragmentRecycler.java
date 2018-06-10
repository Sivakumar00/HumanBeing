package siva.com.weengineers;

public class ChatFragmentRecycler {
    String lastmessage;
    String name;
    String thumb;
    String uid;
    String alert;

    public ChatFragmentRecycler(String alert) {
        this.alert = alert;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public ChatFragmentRecycler() {

    }

    public String getLastmessage() {
        return this.lastmessage;
    }

    public void setLastmessage(String lastmessage) {
        this.lastmessage = lastmessage;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getThumb() {
        return this.thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ChatFragmentRecycler(String lastmessage, String uid, String thumb, String name) {
        this.lastmessage = lastmessage;
        this.uid = uid;
        this.thumb = thumb;
        this.name = name;
    }
}
