package me.kevindevelops.moodion;

/**
 * Created by Kevin on 6/25/2017.
 */

public class Utilities {
    public static int getEmotionDrawable(String emotion) {
        switch (emotion) {
            case "Anger":
                return R.drawable.ic_anger;
            case "Contempt":
                return R.drawable.ic_contempt;
            case "Disgust":
                return R.drawable.ic_disgust;
            case "Fear":
                return R.drawable.ic_fear;
            case "Happiness":
                return R.drawable.ic_happy;
            case "Neutral":
                return R.drawable.ic_neutral;
            case "Sadness":
                return R.drawable.ic_sad;
            case "Surprise":
                return R.drawable.ic_surprise;
        }
        return 0;
    }
}
