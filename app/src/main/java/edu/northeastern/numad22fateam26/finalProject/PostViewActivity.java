package edu.northeastern.numad22fateam26.finalProject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

import java.net.URL;

import edu.northeastern.numad22fateam26.R;
import edu.northeastern.numad22fateam26.finalProject.fragments.Recommendation;
import edu.northeastern.numad22fateam26.finalProject.model.PostImageModel;

public class PostViewActivity extends AppCompatActivity {

    TextView adminStory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);
        adminStory = findViewById(R.id.culture_stories);

        Intent intent = getIntent();

        String action = intent.getAction();

        Uri uri = intent.getData();

        String scheme = uri.getScheme();
        String host = uri.getHost();
        String path = uri.getPath();
        String query = uri.getQuery();

          //  URL url = new URL(scheme + "://" + host + path.replace("Post Images", "Post%20Images") + "?" + query);

        FirebaseStorage.getInstance().getReference().child(uri.getLastPathSegment())
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ImageView imageView = findViewById(R.id.imageView);

                        Glide.with(PostViewActivity.this)
                                .load(uri.toString())
                                .timeout(6500)
                                .into(imageView);
                    }
                });



        String lastPost = Recommendation.loadAdminPosts();
        adminStory.setText(String.format("Topic for this Week: /n", lastPost));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(PostViewActivity.this, ExploreActivity.class));
        }else
            startActivity(new Intent(PostViewActivity.this, ReplacerActivity.class));
    }
}