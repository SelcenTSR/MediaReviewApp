package Model;

public class Post {
    private String gonderiid;
    private String gonderiimage;
    private String inceleme;
    private String paylasan;

    public Post(String gonderiid, String gonderiimage, String inceleme, String paylasan) {
        this.gonderiid = gonderiid;
        this.gonderiimage = gonderiimage;
        this.inceleme = inceleme;
        this.paylasan = paylasan;
    }

    public Post() {
    }

    public String getGonderiid() {
        return gonderiid;
    }

    public void setGonderiid(String gonderiid) {
        this.gonderiid = gonderiid;
    }

    public String getGonderiimage() {
        return gonderiimage;
    }

    public void setGonderiimage(String gonderiimage) {
        this.gonderiimage = gonderiimage;
    }

    public String getInceleme() {
        return inceleme;
    }

    public void setInceleme(String inceleme) {
        this.inceleme = inceleme;
    }

    public String getPaylasan() {
        return paylasan;
    }

    public void setPaylasan(String paylasan) {
        this.paylasan = paylasan;
    }
}