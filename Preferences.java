package net.kaufmanndesigns.view;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "preferences_table")
public class Preferences {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String newsChannel;

    private String url;

    public Preferences(String newsChannel, String url) {
        this.newsChannel = newsChannel;
        this.url = url;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setNewsChannel(String newsChannel) {
        this.newsChannel = newsChannel;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNewsChannel() {
        return newsChannel;
    }

    public String getUrl() {
        return url;
    }

}
