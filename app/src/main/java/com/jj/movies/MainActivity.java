package com.jj.movies;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private List<MovieModel> movieList;

    // Supabase Credentials
    private static final String SUPABASE_URL = "https://nwvjbdiifmdqiyparcbm.supabase.co/rest/v1/movies?select=*";
    private static final String SUPABASE_KEY = "Sb_publishable_wrzX413fjkXqz6P-WplB6Q_bN5lAJLh";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        movieList = new ArrayList<>();
        
        adapter = new MovieAdapter(movieList, movie -> {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("title", movie.getTitle());
            intent.putExtra("poster", movie.getPoster());
            intent.putExtra("desc", movie.getDescription());
            intent.putExtra("stream_url", movie.getStreamUrl());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        // Fetch movies from Supabase database
        fetchMoviesFromSupabase();
    }

    private void fetchMoviesFromSupabase() {
        new Thread(() -> {
            try {
                URL url = new URL(SUPABASE_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("apikey", SUPABASE_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_KEY);

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    reader.close();

                    // Parse JSON response
                    JSONArray jsonArray = new JSONArray(result.toString());
                    movieList.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String title = obj.optString("title", "No Title");
                        String poster = obj.optString("poster_url", "");
                        String description = obj.optString("description", "");
                        
                        // For now using empty streamUrl, later we can map movie_links table
                        String streamUrl = obj.optString("stream_url", "");

                        movieList.add(new MovieModel(title, poster, description, streamUrl));
                    }

                    // Update RecyclerView on Main Thread
                    new Handler(Looper.getMainLooper()).post(() -> adapter.notifyDataSetChanged());
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> 
                        Toast.makeText(MainActivity.this, "Failed to load movies", Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Model class
    public static class MovieModel {
        String title, poster, description, streamUrl;
        public MovieModel(String title, String poster, String description, String streamUrl) {
            this.title = title; this.poster = poster; this.description = description; this.streamUrl = streamUrl;
        }
        public String getTitle() { return title; }
        public String getPoster() { return poster; }
        public String getDescription() { return description; }
        public String getStreamUrl() { return streamUrl; }
    }

    // Adapter class
    public static class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
        private List<MovieModel> list;
        private OnItemClickListener listener;
        public interface OnItemClickListener { void onItemClick(MovieModel movie); }

        public MovieAdapter(List<MovieModel> list, OnItemClickListener listener) {
            this.list = list; this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            MovieModel movie = list.get(position);
            holder.title.setText(movie.getTitle());
            Glide.with(holder.img.getContext()).load(movie.getPoster()).into(holder.img);
            holder.itemView.setOnClickListener(v -> listener.onItemClick(movie));
        }

        @Override
        public int getItemCount() { return list.size(); }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView img; TextView title;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                img = itemView.findViewById(R.id.imgPoster);
                title = itemView.findViewById(R.id.txtTitle);
            }
        }
    }
}
