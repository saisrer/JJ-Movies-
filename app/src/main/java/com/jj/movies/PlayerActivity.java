package com.jj.movies;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;

public class PlayerActivity extends AppCompatActivity {
    private ExoPlayer player;
    private PlayerView playerView;
    private String streamUrl, movieTitle;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        playerView = findViewById(R.id.playerView);
        streamUrl = getIntent().getStringExtra("stream_url");
        movieTitle = getIntent().getStringExtra("title");
        prefs = getSharedPreferences("JJMoviePrefs", MODE_PRIVATE);

        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        MediaItem mediaItem = MediaItem.fromUri(streamUrl);
        player.setMediaItem(mediaItem);

        // Resume position load cheyadam
        long savedPosition = prefs.getLong(movieTitle + "_pos", 0);
        if (savedPosition > 0) {
            player.seekTo(savedPosition);
        }

        player.prepare();
        player.play();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            // App background ki vellinappudu or exit ayinappudu position save avtundi
            prefs.edit().putLong(movieTitle + "_pos", player.getCurrentPosition()).apply();
            player.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
