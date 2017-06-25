package me.kevindevelops.moodion.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 6/19/2017.
 */

public class MoodPlaylist {

    private String emotion;
    private String name;
    private String ownerId;
    private String id;
    private String url;
    private String apiUrl;
    private int amountOfTracks;
    private String imageUrl;
    private List<Track> tracksList;
    private String uri;

    public MoodPlaylist() {
        tracksList = new ArrayList<Track>();
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public int getAmountOfTracks() {
        return amountOfTracks;
    }

    public void setAmountOfTracks(int amountOfTracks) {
        this.amountOfTracks = amountOfTracks;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Track> getTracksList() {
        return tracksList;
    }

    public void setTracksList(List<Track> tracksList) {
        this.tracksList = tracksList;
    }

    public void addTrack(Track track) {
        this.tracksList.add(track);
    }
}
