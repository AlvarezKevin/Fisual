package me.kevindevelops.moodion.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 6/20/2017.
 */

public class Track {
    private String imageUrl;
    private String trackName;
    private String songUri;
    private List<String> artistName;
    private String songId;
    private String songURL;

    public Track() {
        artistName = new ArrayList<>();
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getSongUri() {
        return songUri;
    }

    public void setSongUri(String songUri) {
        this.songUri = songUri;
    }

    public List<String> getArtistName() {
        return artistName;
    }

    public void setArtistName(List<String> artistName) {
        this.artistName = artistName;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getSongURL() {
        return songURL;
    }

    public void setSongURL(String songURL) {
        this.songURL = songURL;
    }
}
