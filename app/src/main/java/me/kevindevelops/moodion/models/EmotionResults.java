package me.kevindevelops.moodion.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Kevin on 6/14/2017.
 */

public class EmotionResults {
    private double anger;
    private double contempt;
    private double disgust;
    private double fear;
    private double happiness;
    private double neutral;
    private double sadness;
    private double surprise;

    private ArrayList<Integer> facesPoints;

    HashMap<Double, String> map = new HashMap<>();

    public EmotionResults() {
        map = new HashMap<>();
    }

    public EmotionResults(float anger, float contempt, float disgust, float fear, float happiness, float neutral, float sadness, float surprise) {
        this.anger = anger;
        this.contempt = contempt;
        this.disgust = disgust;
        this.fear = fear;
        this.happiness = happiness;
        this.neutral = neutral;
        this.sadness = sadness;
        this.surprise = surprise;

        map = new HashMap<>();

        map.put(getAnger(), "Anger");
        map.put(getContempt(), "Contempt");
        map.put(getDisgust(), "Disgust");
        map.put(getFear(), "Fear");
        map.put(getHappiness(), "Happiness");
        map.put(getNeutral(), "Neutral");
        map.put(getSadness(), "Sadness");
        map.put(getSurprise(), "Surprise");

        facesPoints = new ArrayList<>();
    }

    public double getAnger() {
        return anger;
    }

    public void setAnger(double anger) {
        this.anger = anger;
        map.put(getAnger(), "Anger");
    }

    public double getContempt() {
        return contempt;
    }

    public void setContempt(double contempt) {
        this.contempt = contempt;
        map.put(getContempt(), "Contempt");
    }

    public double getDisgust() {
        return disgust;
    }

    public void setDisgust(double disgust) {
        this.disgust = disgust;
        map.put(getDisgust(), "Disgust");
    }

    public double getFear() {
        return fear;
    }

    public void setFear(double fear) {
        this.fear = fear;
        map.put(getFear(), "Fear");
    }

    public double getHappiness() {
        return happiness;
    }

    public void setHappiness(double happiness) {
        this.happiness = happiness;
        map.put(getHappiness(), "Happiness");
    }

    public double getNeutral() {
        return neutral;
    }

    public void setNeutral(double neutral) {
        this.neutral = neutral;
        map.put(getNeutral(), "Neutral");
    }

    public double getSadness() {
        return sadness;
    }

    public void setSadness(double sadness) {
        this.sadness = sadness;
        map.put(getSadness(), "Sadness");
    }

    public double getSurprise() {
        return surprise;
    }

    public void setSurprise(double surprise) {
        this.surprise = surprise;
        map.put(getSurprise(), "Surprise");
    }

    public Map.Entry<Double, String> getMaxEmotion() {

        Iterator<Map.Entry<Double, String>> iterator = map.entrySet().iterator();
        Map.Entry<Double, String> maxMap = null;

        while (iterator.hasNext()) {
            Map.Entry<Double, String> pair = iterator.next();
            if (maxMap == null) {
                maxMap = pair;
            } else if (maxMap.getKey().compareTo(pair.getKey()) < 0) {
                maxMap = pair;
            }
        }

        return maxMap;
    }
}
