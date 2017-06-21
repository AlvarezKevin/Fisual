package me.kevindevelops.moodion;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import me.kevindevelops.moodion.models.MoodPlaylist;

/**
 * Created by Kevin on 6/19/2017.
 */

public class GetPlaylistLoader extends AsyncTaskLoader<List<MoodPlaylist>> {

    public GetPlaylistLoader(Context context) {
        super(context);
    }

    @Override
    public List<MoodPlaylist> loadInBackground() {
        return null;
    }
}
