package nus.iss.vttp5_ssf_day16w_practice.model;

public class BoardGame {

    private Integer gid;
    private String name;
    private Integer year;

    //constructors
    public BoardGame() {
    }

    public BoardGame(Integer gid, String name, Integer year) {
        this.gid = gid;
        this.name = name;
        this.year = year;
    }

    // getters and setters
    public Integer getGid() {
        return gid;
    }

    public void setGid(Integer gid) {
        this.gid = gid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    // toString() method
    @Override
    public String toString() {
        return gid + "," + name + "," + year;
    }
    
}
