package com.jj.movies;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;

public class MainActivity extends AppCompatActivity {

    private ExoPlayer player;
    private StyledPlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerView = findViewById(R.id.playerView);

        // Initialize ExoPlayer
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        // Telegram Public Channel Direct Stream Link Example
        String videoUrl = "YOUR_TELEGRAM_PUBLIC_STREAM_URL_HERE"; 

        PlayVideo(videoUrl);
    }

    private void PlayVideo(String url) {
        if (url != null && !url.isEmpty()) {
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(url));
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
        } else {
            Toast.makeText(this, "Stream URL not found!", Toast.LENGTH_SHORT).show();
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
