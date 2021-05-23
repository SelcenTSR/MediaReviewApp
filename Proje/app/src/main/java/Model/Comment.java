package Model;

public class Comment {
    private String yorum;
    private String gonderen;
    private String yorumid;

    public Comment(String yorum, String gonderen, String yorumid) {
        this.yorum = yorum;
        this.gonderen = gonderen;
        this.yorumid = yorumid;
    }

    public Comment() {
    }

    public String getYorum() {
        return yorum;
    }

    public void setYorum(String yorum) {
        this.yorum = yorum;
    }

    public String getGonderen() {
        return gonderen;
    }

    public void setGonderen(String gonderen) {
        this.gonderen = gonderen;
    }

    public String getYorumid() {
        return yorumid;
    }

    public void setYorumid(String yorumid) {
        this.yorumid = yorumid;
    }
}
