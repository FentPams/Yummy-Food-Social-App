package edu.northeastern.numad22fateam26.finalProject.fragments;

import static android.app.Activity.RESULT_OK;
import static edu.northeastern.numad22fateam26.finalProject.ExploreActivity.IS_SEARCHED_USER;
import static edu.northeastern.numad22fateam26.finalProject.ExploreActivity.USER_ID;
import static edu.northeastern.numad22fateam26.finalProject.utils.Constants.PREF_DIRECTORY;
import static edu.northeastern.numad22fateam26.finalProject.utils.Constants.PREF_NAME;
import static edu.northeastern.numad22fateam26.finalProject.utils.Constants.PREF_STORED;
import static edu.northeastern.numad22fateam26.finalProject.utils.Constants.PREF_URL;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marsad.stylishdialogs.StylishAlertDialog;
import com.suke.widget.SwitchButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.northeastern.numad22fateam26.R;
import edu.northeastern.numad22fateam26.finalProject.ExploreActivity;
import edu.northeastern.numad22fateam26.finalProject.ReplacerActivity;
import edu.northeastern.numad22fateam26.finalProject.chat.ChatActivity;
import edu.northeastern.numad22fateam26.finalProject.model.ChatUserModel;
import edu.northeastern.numad22fateam26.finalProject.model.PostImageModel;
import edu.northeastern.numad22fateam26.finalProject.services.PushNotificationService;
import edu.northeastern.numad22fateam26.sticker.Dialog;

public class Profile extends Fragment implements Dialog.DialogListener {

    boolean isMyProfile = true;
    String userUID;
    FirestoreRecyclerAdapter<PostImageModel, PostImageHolder> adapter;
    List<Object> followersList, followingList, followingList_2;
    boolean isFollowed;
    DocumentReference userRef, myRef;
    int count;
    private EditText statusText;
    private TextView nameTv, toolbarNameTv, followingCountTv, followersCountTv, postCountTv, switchText;
    private CircleImageView profileImage;
    private Button followBtn, startChatBtn;
    private AppCompatImageButton signOutbtn;
    private RecyclerView recyclerView;
    private LinearLayout countLayout;
    private FirebaseUser user;
    private ImageButton editProfileBtn, editStatusBtn;
    private FirebaseAuth auth;
    private String receiverToken;

    NavigationView navigationView;
    RelativeLayout relativeLayout;


    public Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);


        myRef = FirebaseFirestore.getInstance().collection("Users").document(user.getUid());
        com.suke.widget.SwitchButton switchButton = (com.suke.widget.SwitchButton) view.findViewById(R.id.switchTv);

        switchButton.setChecked(false);
        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                USER_ID = auth.getUid();
                IS_SEARCHED_USER = false;
                Intent intent = new Intent(getActivity(), ExploreActivity.class);
                intent.putExtra("init_view_pager_item", 4);
                startActivity(intent);
            }
        });

        if (IS_SEARCHED_USER && !USER_ID.equals(user.getUid())) {
            isMyProfile = false;
            switchButton.setVisibility(View.VISIBLE);
            switchText.setVisibility(View.VISIBLE);
            userUID = USER_ID;
            loadData();
        } else {
            isMyProfile = true;
            switchButton.setVisibility(View.INVISIBLE);
            switchText.setVisibility(View.INVISIBLE);
            userUID = user.getUid();
        }

        if (isMyProfile) {
            editProfileBtn.setVisibility(View.VISIBLE);
            editStatusBtn.setVisibility(View.VISIBLE);
            statusText.setFocusableInTouchMode(true);
            followBtn.setVisibility(View.GONE);
            countLayout.setVisibility(View.VISIBLE);

            //Hide chat btn
            startChatBtn.setVisibility(View.GONE);

        } else {
            editProfileBtn.setVisibility(View.GONE);
            editStatusBtn.setVisibility(View.GONE);
            statusText.setFocusable(false);
            followBtn.setVisibility(View.VISIBLE);
            //            countLayout.setVisibility(View.GONE);
        }
        userRef = FirebaseFirestore.getInstance().collection("Users").document(userUID);

        loadBasicData();
        recyclerView.setItemAnimator(null);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        loadPostImages();

        recyclerView.setAdapter(adapter);

        clickListener();
        readReceiverToken();

    }

    private void loadData() {

        myRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.e("Tag_b", error.getMessage());
                    return;
                }

                if (value == null || !value.exists()) {
                    return;
                }

                followingList_2 = (List<Object>) value.get("following");


            }
        });

    }

    private void clickListener() {

        editStatusBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(getActivity()).setMessage("Do you want to update the status?").setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    DocumentReference reference = FirebaseFirestore.getInstance().collection("Users").document(user.getUid());

                    reference.get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists() && documentSnapshot.contains("status")) {
                            reference.update("status", statusText.getText().toString()).addOnSuccessListener(v -> Toast.makeText(getActivity(), "Update Succeed!", Toast.LENGTH_SHORT).show());
                        }
                    });
                }
            }).setNegativeButton(android.R.string.no, null).show();
        });

        signOutbtn.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(getActivity(), ReplacerActivity.class));
        });

        //        switchTv.setOnClickListener(v -> {
        //            //getActivity().finish();
        //            USER_ID = auth.getUid();
        //            IS_SEARCHED_USER = false;
        //            Intent intent = new Intent(getActivity(), ExploreActivity.class);
        //            intent.putExtra("init_view_pager_item", 4);
        //            startActivity(intent);
        //        });


        followBtn.setOnClickListener(v -> {

            if (isFollowed) {

                followersList.remove(user.getUid()); //opposite user

                followingList_2.remove(userUID); //us

                final Map<String, Object> map_2 = new HashMap<>();
                map_2.put("following", followingList_2);


                Map<String, Object> map = new HashMap<>();
                map.put("followers", followersList);


                userRef.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            followBtn.setText("Follow");

                            myRef.update(map_2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "UnFollowed", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.e("Tag_3", task.getException().getMessage());
                                    }
                                }
                            });

                        } else {
                            Log.e("Tag", "" + task.getException().getMessage());
                        }
                    }
                });


            } else {

                createNotification();

                followersList.add(user.getUid()); //opposite user

                followingList_2.add(userUID); //us

                final Map<String, Object> map_2 = new HashMap<>();
                map_2.put("following", followingList_2);


                Map<String, Object> map = new HashMap<>();
                map.put("followers", followersList);


                userRef.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            followBtn.setText("UnFollow");

                            myRef.update(map_2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Followed", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Log.e("tag_3_1", task.getException().getMessage());
                                    }
                                }
                            });


                        } else {
                            Log.e("Tag", "" + task.getException().getMessage());
                        }
                    }
                });


            }

        });

        assert getContext() != null;

        editProfileBtn.setOnClickListener(v -> CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(getContext(), Profile.this));

        startChatBtn.setOnClickListener(v -> {
            queryChat();
        });

    }

    void queryChat() {

        StylishAlertDialog alertDialog = new StylishAlertDialog(getContext(), StylishAlertDialog.PROGRESS);
        alertDialog.setTitleText("Starting Chat...");
        alertDialog.setCancellable(false);
        alertDialog.show();

        CollectionReference reference = FirebaseFirestore.getInstance().collection("Messages");

        reference.get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                boolean chatExist = false;

                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    ChatUserModel model = documentSnapshot.toObject(ChatUserModel.class);
                    if (model.getUid().containsAll(List.of(userUID, user.getUid()))) {
                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        intent.putExtra("uid", userUID);
                        intent.putExtra("id", documentSnapshot.getId()); //return doc id
                        startActivity(intent);
                        chatExist = true;
                        break;
                    }
                }

                if (!chatExist) {
                    startChat(alertDialog);
                }
            } else {
                alertDialog.dismissWithAnimation();
            }
        });

    }

    void startChat(StylishAlertDialog alertDialog) {


        CollectionReference reference = FirebaseFirestore.getInstance().collection("Messages");

        List<String> list = new ArrayList<>();

        list.add(0, user.getUid());
        list.add(1, userUID);

        String pushID = reference.document().getId();

        Map<String, Object> map = new HashMap<>();
        map.put("id", pushID);
        map.put("lastMessage", "Hi");
        map.put("time", FieldValue.serverTimestamp());
        map.put("uid", list);

        reference.document(pushID).update(map).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

            } else {
                reference.document(pushID).set(map);
            }
        });

        //todo - ---- - -- - -
        //Message

        CollectionReference messageRef = FirebaseFirestore.getInstance().collection("Messages").document(pushID).collection("Messages");

        String messageID = messageRef.document().getId();

        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("id", messageID);
        messageMap.put("message", "Hi");
        messageMap.put("senderID", user.getUid());
        messageMap.put("time", FieldValue.serverTimestamp());

        messageRef.document(messageID).set(messageMap);

        new Handler().postDelayed(() -> {

            alertDialog.dismissWithAnimation();

            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra("uid", userUID);
            intent.putExtra("id", pushID);
            startActivity(intent);

        }, 3000);

    }

    private void init(View view) {

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        assert getActivity() != null;
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        nameTv = view.findViewById(R.id.nameTv);
        statusText = view.findViewById(R.id.statusTV);
        toolbarNameTv = view.findViewById(R.id.toolbarNameTV);
        followersCountTv = view.findViewById(R.id.followersCountTv);
        followingCountTv = view.findViewById(R.id.followingCountTv);
        postCountTv = view.findViewById(R.id.postCountTv);
        switchText = view.findViewById(R.id.switchText);
        profileImage = view.findViewById(R.id.profileImage);
        followBtn = view.findViewById(R.id.followBtn);
        recyclerView = view.findViewById(R.id.recyclerView);
        countLayout = view.findViewById(R.id.countLayout);
        editProfileBtn = view.findViewById(R.id.edit_profileImage);
        startChatBtn = view.findViewById(R.id.startChatBtn);
        signOutbtn = view.findViewById(R.id.signOutbtn);
        //        navigationView = view.findViewById(R.id.nav_view);
        relativeLayout = view.findViewById(R.id.relativeLayout);
        editStatusBtn = view.findViewById(R.id.edit_status);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    private void loadBasicData() {

        userRef.addSnapshotListener((value, error) -> {

            if (error != null) {
                Log.e("Tag_0", error.getMessage());
                return;
            }

            assert value != null;
            if (value.exists()) {

                String name = value.getString("name");
                String status = value.getString("status");

                final String profileURL = value.getString("profileImage");

                nameTv.setText(name);
                toolbarNameTv.setText(name);
                statusText.setText(status);

                followersList = (List<Object>) value.get("followers");
                followingList = (List<Object>) value.get("following");


                followersCountTv.setText("" + followersList.size());
                followingCountTv.setText("" + followingList.size());

                try {

                    Glide.with(getContext().getApplicationContext()).load(profileURL).placeholder(R.drawable.ic_person).circleCrop().listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                            Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                            storeProfileImage(bitmap, profileURL);
                            return false;
                        }
                    }).timeout(6500).into(profileImage);


                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (followersList.contains(user.getUid())) {
                    followBtn.setText("UnFollow");
                    isFollowed = true;
                    startChatBtn.setVisibility(View.VISIBLE);


                } else {
                    isFollowed = false;
                    followBtn.setText("Follow");

                    startChatBtn.setVisibility(View.GONE);

                }


            }

        });

    }

    private void storeProfileImage(Bitmap bitmap, String url) {

        SharedPreferences preferences = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean isStored = preferences.getBoolean(PREF_STORED, false);
        String urlString = preferences.getString(PREF_URL, "");

        SharedPreferences.Editor editor = preferences.edit();

        if (isStored && urlString.equals(url))
            return;

        if (IS_SEARCHED_USER)
            return;

        ContextWrapper contextWrapper = new ContextWrapper(getContext().getApplicationContext());

        File directory = contextWrapper.getDir("image_data", Context.MODE_PRIVATE);

        if (!directory.exists())
            directory.mkdirs();

        File path = new File(directory, "profile.png");

        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(path);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {

            try {
                assert outputStream != null;
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        editor.putBoolean(PREF_STORED, true);
        editor.putString(PREF_URL, url);
        editor.putString(PREF_DIRECTORY, directory.getAbsolutePath());
        editor.apply();


    }

    private void loadPostImages() {

        DocumentReference reference = FirebaseFirestore.getInstance().collection("Users").document(userUID);

        Query query = reference.collection("Post Images").orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<PostImageModel> options = new FirestoreRecyclerOptions.Builder<PostImageModel>().setQuery(query, PostImageModel.class).build();

        adapter = new FirestoreRecyclerAdapter<PostImageModel, PostImageHolder>(options) {
            @NonNull
            @Override
            public PostImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_image_items, parent, false);
                return new PostImageHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull PostImageHolder holder, int position, @NonNull PostImageModel model) {

                Glide.with(holder.itemView.getContext().getApplicationContext()).load(model.getImageUrl()).timeout(6500).into(holder.imageView);
                count = getItemCount();
                postCountTv.setText("" + count);

            }

            @Override
            public int getItemCount() {

                return super.getItemCount();
            }
        };


    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri uri = result.getUri();
            uploadImage(uri);
        }
    }

    private void uploadImage(Uri uri) {

        final StorageReference reference =
                FirebaseStorage.getInstance().getReference().child("Profile Images").child(user.getUid());

        reference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful()) {

                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageURL = uri.toString();

                            UserProfileChangeRequest.Builder request = new UserProfileChangeRequest.Builder();
                            request.setPhotoUri(uri);

                            user.updateProfile(request.build());

                            Map<String, Object> map = new HashMap<>();
                            map.put("profileImage", imageURL);

                            FirebaseFirestore.getInstance().collection("Users").document(user.getUid()).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful())
                                        Toast.makeText(getContext(), "Updated Successful", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });

                        }
                    });

                } else {
                    Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    void createNotification() {

        CollectionReference reference = FirebaseFirestore.getInstance().collection("Notifications");

        String id = reference.document().getId();
        Map<String, Object> map = new HashMap<>();
        map.put("time", FieldValue.serverTimestamp());
        map.put("notification", user.getDisplayName() + " followed you.");
        map.put("id", id);
        map.put("uid", userUID);


        reference.document(id).set(map);
        PushNotificationService.sendNotification(getContext(), receiverToken, "Awesome", (String) map.get("notification"));

    }

    @Override
    public void applyTexts(String message, String senderName, int position) {

    }

    private static class PostImageHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public PostImageHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);

        }

    }


    public void openDialog() {
        Dialog dialog = new Dialog(0);
        dialog.show(getActivity().getSupportFragmentManager(), "Dialog");
    }

    void readReceiverToken() {
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("users").child(userUID);
        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    receiverToken = snapshot.child("FCMToken").getValue().toString();
                    Log.v("TAG", "sender id: " + receiverToken);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}