package Model;

public class Notification {
    private String kullaniciid;
    private String text;
    private String postid;
    private boolean ispost;

    public Notification(String kullaniciid, String text, String incelemeid, boolean ispost) {
        this.kullaniciid = kullaniciid;
        this.text = text;
        this.postid = incelemeid;
        this.ispost = ispost;
    }

    public Notification() {
    }

    public String getKullaniciid() {
        return kullaniciid;
    }

    public void setKullaniciid(String kullaniciid) {
        this.kullaniciid = kullaniciid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public boolean isIspost() {
        return ispost;
    }

    public void setIspost(boolean ispost) {
        this.ispost = ispost;
    }
}
