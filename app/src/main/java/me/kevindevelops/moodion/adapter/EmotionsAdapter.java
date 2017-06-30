package me.kevindevelops.moodion.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.kevindevelops.moodion.R;
import me.kevindevelops.moodion.Utilities;
import me.kevindevelops.moodion.models.EmotionResults;

/**
 * Created by Kevin on 6/25/2017.
 */

public class EmotionsAdapter extends RecyclerView.Adapter<EmotionsAdapter.EmotionsViewHolder> {

    private static final String LOG_TAG = EmotionsAdapter.class.getSimpleName();

    private Context context;
    private List<EmotionResults> emotions;
    List<Map.Entry<String, Double>> list;

    public EmotionsAdapter(Context context, List<EmotionResults> emotions) {
        this.context = context;
        this.emotions = emotions;

        list = new ArrayList<>();

        for (EmotionResults item : emotions) {
            HashMap<String, Double> map = new HashMap<>();

            map.put("Anger", item.getAnger());
            map.put("Contempt", item.getContempt());
            map.put("Disgust", item.getDisgust());
            map.put("Fear", item.getFear());
            map.put("Happiness", item.getHappiness());
            map.put("Neutral", item.getNeutral());
            map.put("Sadness", item.getSadness());
            map.put("Surprise", item.getSurprise());

            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Double> emotionEntry = (Map.Entry<String,Double>) it.next();

                int tempPos = getEmotionPos(emotionEntry.getKey());
                if (tempPos != -1) {
                    emotionEntry.setValue(list.get(tempPos).getValue() + emotionEntry.getValue());
                    list.add(tempPos, emotionEntry);
                    list.remove(tempPos + 1);
                } else {
                    list.add(emotionEntry);
                }
                it.remove();
            }
        }
    }

    private int getEmotionPos(String emotion) {
        for (Map.Entry<String, Double> item : list) {
            if (item.getKey().equals(emotion)) {
                return list.indexOf(item);
            }
        }
        return -1;
    }

    @Override
    public EmotionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.emotions_list_item, parent, false);
        return new EmotionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EmotionsViewHolder holder, int position) {
        holder.mEmotionTextView.setText(Double.toString(list.get(position).getValue()));
        holder.mEmotionImageView.setImageResource(Utilities.getEmotionDrawable(list.get(position).getKey()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class EmotionsViewHolder extends RecyclerView.ViewHolder {
        private ImageView mEmotionImageView;
        private TextView mEmotionTextView;

        public EmotionsViewHolder(View view) {
            super(view);
            mEmotionImageView = (ImageView) view.findViewById(R.id.emotions_list_image_view);
            mEmotionTextView = (TextView) view.findViewById(R.id.emotions_list_text_view);
        }
    }
}
