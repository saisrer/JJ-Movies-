package com.jj.movies;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ExoPlayer player;
    private StyledPlayerView playerView;
    
    // Supabase Credentials setup
    private static final String SUPABASE_URL = "https://nwvjbdiifmdqiyparcbm.supabase.co/rest/v1/movies?select=*";
    private static final String SUPABASE_KEY = "Sb_publishable_wrzX413fjkXqz6P-WplB6Q_bN5lAJLh";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerView = findViewById(R.id.playerView);

        // Initialize ExoPlayer
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        // Fetch Movie Stream URL from Supabase Database
        fetchMovieFromSupabase();
    }

    private void fetchMovieFromSupabase() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(SUPABASE_URL)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to connect to database!", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(responseData);
                        if (jsonArray.length() > 0) {
                            // Supabase లో ఉన్న మొదటి సినిమా లింక్ ని తీసుకుంటుంది
                            JSONObject firstMovie = jsonArray.getJSONObject(0);
                            String streamUrl = firstMovie.getString("stream_url");

                            // Play video on Main UI Thread
                            runOnUiThread(() -> playVideo(streamUrl));
                        } else {
                            runOnUiThread(() -> Toast.makeText(MainActivity.this, "No movies found in database!", Toast.LENGTH_SHORT).show());
                        }
                    } catch (Exception e) {
                        Log.e("SupabaseError", e.getMessage());
                    }
                }
            }
        });
    }

    private void playVideo(String url) {
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
