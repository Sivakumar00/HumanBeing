package siva.com.weengineers;

/**
 * Created by MANIKANDAN on 27-08-2017.
 */

public class Blog {

    private String title,image,desc,username,userimage,uid,userprofile;

    public Blog(){

    }
    public Blog(String title, String image, String desc,String uid) {
        this.title = title;
        this.image = image;
        this.desc = desc;
        this.username = username;
        this.uid=uid;
        this.userprofile=userprofile;

    }

    public String getUserimage() {
        return userimage;
    }

    public String getUserprofile() {
        return userprofile;
    }

    public void setUserprofile(String userprofile) {
        this.userprofile = userprofile;
    }

    public void setUid(String uid){
        this.uid=uid;
    }
    public String getUid(){return uid;}

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
