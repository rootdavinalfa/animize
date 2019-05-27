package ml.dvnlabs.animize.model;

import java.util.List;

public class packagelist {
    String pack, name, now,tot, rate, mal;
    List<String>genre;

    public packagelist(String pack, String name, String now, String tot, String rate,String mal, List<String>genre){
        this.pack = pack;
        this.name = name;
        this.now = now;
        this.tot= tot;
        this.rate = rate;
        this.mal = mal;
        this.genre = genre;
    }

    public String getMal() {
        return mal;
    }

    public List<String> getGenre() {
        return this.genre;
    }

    public String getname() {
        return name;
    }

    public String getNow() {
        return now;
    }

    public String getPack() {
        return pack;
    }

    public String getRate() {
        return rate;
    }

    public String getTot() {
        return tot;
    }
}