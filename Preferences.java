package net.kaufmanndesigns.view;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "preferences_table")
public class Preferences {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String newsChannel;
    private String url;
    private String calendarStatus;
    private String powerMode;
    private String playbackStatus;
    private String financeStatus;
    private String username;
    private String slideshow;

    public Preferences(String newsChannel, String url, String calendarStatus, String financeStatus, String playbackStatus, String powerMode, String username, String slideshow) {
        this.newsChannel = newsChannel;
        this.url = url;
        this.calendarStatus = calendarStatus;
        this.financeStatus = financeStatus;
        this.playbackStatus = playbackStatus;
        this.powerMode = powerMode;
        this.username = username;
        this.slideshow = slideshow;

    }


    public void setCalendarStatus(String calendarStatus) {
        this.calendarStatus = calendarStatus;
    }

    public void setPowerMode(String powerMode) {
        this.powerMode = powerMode;
    }

    public void setPlaybackStatus(String playbackStatus) {
        this.playbackStatus = playbackStatus;
    }

    public void setFinanceStatus(String financeStatus) {
        this.financeStatus = financeStatus;
    }

    public void setSlideshow(String slideshow) {
        this.slideshow = slideshow;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNewsChannel(String newsChannel) {
        this.newsChannel = newsChannel;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public String getNewsChannel() {
        return newsChannel;
    }

    public String getUrl() {
        return url;
    }

    public String getCalendarStatus() {
        return calendarStatus;
    }

    public String getPowerMode() {
        return powerMode;
    }

    public String getPlaybackStatus() {
        return playbackStatus;
    }

    public String getFinanceStatus() {
        return financeStatus;
    }

    public String getUsername() {
        return username;
    }

    public String getSlideshow() {
        return slideshow;
    }


}
