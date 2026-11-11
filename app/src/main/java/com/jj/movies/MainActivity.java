package com.jj.movies;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private List<MovieModel> movieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        movieList = new ArrayList<>();
        // Sample static data for testing (Supabase fetch kosam tarvatha connect cheyochu)
        movieList.add(new MovieModel("Sample Movie 1", "https://via.placeholder.com/300", "This is a sample movie description.", "https://www.sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4"));
        
        adapter = new MovieAdapter(movieList, movie -> {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("title", movie.getTitle());
            intent.putExtra("poster", movie.getPoster());
            intent.putExtra("desc", movie.getDescription());
            intent.putExtra("stream_url", movie.getStreamUrl());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
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
