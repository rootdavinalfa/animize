package ml.dvnlabs.animize.model;

import java.util.List;

public class packageinfo {
    String pack, name, syn,tot, rate, mal;
    List<String>genre;

    public packageinfo(String pack, String name, String sysnop, String tot, String rate, String mal, List<String>genre){
        this.pack = pack;
        this.name = name;
        this.syn = sysnop;
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

    public String getSynopsis() {
        return syn;
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
