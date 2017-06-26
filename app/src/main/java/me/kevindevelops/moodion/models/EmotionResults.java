package me.kevindevelops.moodion.models;

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

    public EmotionResults() {
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
    }

    public double getAnger() {
        return anger;
    }

    public void setAnger(double anger) {
        this.anger = anger;
    }

    public double getContempt() {
        return contempt;
    }

    public void setContempt(double contempt) {
        this.contempt = contempt;
    }

    public double getDisgust() {
        return disgust;
    }

    public void setDisgust(double disgust) {
        this.disgust = disgust;
    }

    public double getFear() {
        return fear;
    }

    public void setFear(double fear) {
        this.fear = fear;
    }

    public double getHappiness() {
        return happiness;
    }

    public void setHappiness(double happiness) {
        this.happiness = happiness;
    }

    public double getNeutral() {
        return neutral;
    }

    public void setNeutral(double neutral) {
        this.neutral = neutral;
    }

    public double getSadness() {
        return sadness;
    }

    public void setSadness(double sadness) {
        this.sadness = sadness;
    }

    public double getSurprise() {
        return surprise;
    }

    public void setSurprise(double surprise) {
        this.surprise = surprise;
    }

    public Map.Entry<Double,String> getMaxEmotion(){
        HashMap<Double,String> map = new HashMap<>();

        map.put(getAnger(),"Anger");
        map.put(getContempt(),"Contempt");
        map.put(getDisgust(),"Disgust");
        map.put(getFear(),"Fear");
        map.put(getHappiness(),"Happiness");
        map.put(getNeutral(),"Neutral");
        map.put(getSadness(),"Sadness");
        map.put(getSurprise(),"Surprise");

        Iterator<Map.Entry<Double,String>> iterator = map.entrySet().iterator();
        Map.Entry<Double,String> maxMap;
        maxMap = (Map.Entry) iterator.next();
        while(iterator.hasNext()) {
            Map.Entry<Double,String> pair = iterator.next();
            if(maxMap.getKey().compareTo(pair.getKey()) < 0 ) {
                maxMap = pair;
            }
        }

        return maxMap;
    }
}
