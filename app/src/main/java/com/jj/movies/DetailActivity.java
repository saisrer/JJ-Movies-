
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView img = findViewById(R.id.imgDetailPoster);
        TextView title = findViewById(R.id.txtDetailTitle);
        TextView desc = findViewById(R.id.txtDetailDesc);
        Button btnWatchNow = findViewById(R.id.btnWatchNow);

        String mTitle = getIntent().getStringExtra("title");
        String mPoster = getIntent().getStringExtra("poster");
        String mDesc = getIntent().getStringExtra("desc");
        String mStreamUrl = getIntent().getStringExtra("stream_url");

        title.setText(mTitle);
        desc.setText(mDesc);
        Glide.with(this).load(mPoster).into(img);

        btnWatchNow.setOnClickListener(v -> {
            Intent intent = new Intent(DetailActivity.this, PlayerActivity.class);
            intent.putExtra("stream_url", mStreamUrl);
            intent.putExtra("title", mTitle);
            startActivity(intent);
        });
    }
}
