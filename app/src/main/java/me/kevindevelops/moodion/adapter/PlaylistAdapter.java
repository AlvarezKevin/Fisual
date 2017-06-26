package me.kevindevelops.moodion.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import me.kevindevelops.moodion.R;
import me.kevindevelops.moodion.models.MoodPlaylist;
import me.kevindevelops.moodion.models.Track;

/**
 * Created by Kevin on 6/26/2017.
 */

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>{

    Context context;
    MoodPlaylist playlist;
    List<Track> trackList;

    public PlaylistAdapter(Context context, MoodPlaylist playlist) {
        this.context = context;
        this.playlist = playlist;
        this.trackList = playlist.getTracksList();
    }

    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.playlist_list_item,parent,false);
        return new PlaylistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PlaylistViewHolder holder, int position) {
        Glide.with(context)
                .load(trackList.get(position).getImageUrl())
                .into(holder.mSongIV);
        holder.mSongNameTV.setText(trackList.get(position).getTrackName());
        for(String artist : trackList.get(position).getArtistName()) {
            holder.mSongArtistTV.append(artist + ", ");
        }
    }

    @Override
    public int getItemCount() {
        return playlist.getAmountOfTracks();
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder {

        private ImageView mSongIV;
        private TextView mSongNameTV;
        private TextView mSongArtistTV;

        public PlaylistViewHolder(View itemView) {
            super(itemView);
            mSongIV = (ImageView)itemView.findViewById(R.id.playlist_song_image_list_item);
            mSongNameTV = (TextView)itemView.findViewById(R.id.playlist_song_name_list_item);
            mSongArtistTV = (TextView)itemView.findViewById(R.id.playlist_song_artist_list_item);
        }
    }
}
