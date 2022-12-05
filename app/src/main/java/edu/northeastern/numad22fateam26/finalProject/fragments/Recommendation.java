package edu.northeastern.numad22fateam26.finalProject.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import edu.northeastern.numad22fateam26.R;
import edu.northeastern.numad22fateam26.finalProject.model.PostImageModel;


public class Recommendation extends Fragment {

    private static final String TAG = "Recommendation";
    List<PostImageModel> postsModelList;
    private TextView adminStory;
    private TextView adminRecipe;
    private ImageView adminPic;


    public Recommendation() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recommendation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
    }


    private void init(View view) {
        adminStory = view.findViewById(R.id.culture_stories);
        adminRecipe = view.findViewById(R.id.detailed_recipe);
        adminPic = view.findViewById(R.id.admin_pic);

        postsModelList = new ArrayList<>();
        loadAdminPosts();
    }


    public void loadAdminPosts() {
        CollectionReference postRef = FirebaseFirestore.getInstance().collection("Users")
                .document("qUs1aFODabPiCVLakZq1KmG0naJ3").collection("Post Images");
        postRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            PostImageModel model = document.toObject(PostImageModel.class);
                            postsModelList.add(model);
                        }
                    }
                    int n = postsModelList.size();
                    if (n != 0) {
                        adminStory.setText(String.format("Topic for this Week: %s\n",
                                postsModelList.get(n - 1).getDescription()));
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

}