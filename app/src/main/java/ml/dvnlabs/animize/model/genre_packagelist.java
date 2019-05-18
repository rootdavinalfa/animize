package ml.dvnlabs.animize.model;

import java.util.List;

public class genre_packagelist {
    String pack, name, now,tot, rate, mal;

    public genre_packagelist(String pack, String name, String now, String tot, String rate, String mal){
        this.pack = pack;
        this.name = name;
        this.now = now;
        this.tot= tot;
        this.rate = rate;
        this.mal = mal;
    }

    public String getMal() {
        return mal;
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
