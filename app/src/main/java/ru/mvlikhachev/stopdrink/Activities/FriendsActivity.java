//package ru.mvlikhachev.stopdrink.Activities;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.bumptech.glide.Glide;
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.ChildEventListener;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//
//import ru.mvlikhachev.stopdrink.Model.User;
//import ru.mvlikhachev.stopdrink.R;
//import ru.mvlikhachev.stopdrink.Utils.FriendsAdapter;
//import ru.mvlikhachev.stopdrink.Utils.Utils;
//
//
//public class FriendsActivity extends AppCompatActivity {
//    //////////////////////// Constants ////////////////////////////////////
//    // Константа файла сохранения настроек
//    public static final String APP_PREFERENCES = "datasetting";
//    public static final String APP_PREFERENCES_KEY_NAME = "nameFromDb";
//    public static final String APP_PREFERENCES_KEY_DATE = "dateFromDb";
//    public static final String APP_PREFERENCES_KEY_ABOUT_ME = "aboutMeFromDb";
//    public static final String APP_PREFERENCES_KEY_PROFILE_IMAGE = "profileImageFromDb";
//    public static final String APP_PREFERENCES_KEY_USERID = "userIdFromDb";
//    public static final String APP_PREFERENCES_KEY_CLICKED_USERID = "clickedUserIdFromDb";
//
//
//    private DatabaseReference usersDatabaseReference;
//    private ChildEventListener usersChildEventListener;
//
//    private ArrayList<User> userArrayList;
//    private RecyclerView userRecyclerView;
//    private FriendsAdapter friendAdapter;
//    private RecyclerView.LayoutManager friendLayoutManager;
//
//    private FirebaseAuth auth;
//    private ImageView avatarView;
//    private ArrayList<String> usersId;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_friends);
//
//        avatarView = findViewById(R.id.avatarCardImageView);
//
//        auth = FirebaseAuth.getInstance();
//
//        userArrayList = new ArrayList<>();
//        usersId = new ArrayList<>();
//        attachUserDatabaseReferenceListener();
//        buildRecyclerView();
//
//
////        showBottomNavigation(R.id.friends_page);
//    }
//
//    private void attachUserDatabaseReferenceListener() {
//        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
//        if (usersChildEventListener == null) {
//            usersChildEventListener = new ChildEventListener() {
//                @Override
//                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                    User user = snapshot.getValue(User.class);
//                    if (!user.getId().equals(auth.getCurrentUser().getUid())) {
//                        // Set Profile Avatar
//                        String avatar = user.getProfileImage();
//                        Log.d("avatar", "User: " + user.getName() +"\n" +
//                                "Avatar: " + avatar);
//                        if (user.getProfileImage() == null) {
//                            user.setAvatarMockUpResource(R.drawable.ic_baseline_person_black);
//                        } else {
//                            user.setAvatarMockUpResource(R.drawable.me);
//                        }
//                        userArrayList.add(user);
//                        friendAdapter.notifyDataSetChanged();
//                    }
//
//                }
//
//                @Override
//                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                }
//
//                @Override
//                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//                }
//
//                @Override
//                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            };
//            usersDatabaseReference.addChildEventListener(usersChildEventListener);
//        }
//    }
//
//    private void buildRecyclerView() {
//        final String[] friendUserId = {""};
//
//        userRecyclerView = findViewById(R.id.friendsListRecyclerView);
//        userRecyclerView.setHasFixedSize(true);
//        friendLayoutManager = new LinearLayoutManager(this);
//        friendAdapter = new FriendsAdapter(userArrayList);
//
//        userRecyclerView.setLayoutManager(friendLayoutManager);
//        userRecyclerView.setAdapter(friendAdapter);
//
//        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
//
//
//
//            friendAdapter.setOnUserClickListener(new FriendsAdapter.OnUserClickListener() {
//                @Override
//                public void onUserClick(int position) {
//                    goToProfile(position);
//                }
//            });
//
//    }
//
//    private void goToProfile(int position) {
//        String clickedUserId = userArrayList.get(position).getId();
//        String clickedGlobalUserId = Utils.getClickedUserId(this, clickedUserId);
//
//
//        Log.d("getUserId", "\n Получили user id : " + usersId + "\n");
//
//
//    }
//
//    // Show bottom navighation menu
//    private void showBottomNavigation(int currentMenu) {
//        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
//        bottomNavigationView.setSelectedItemId(currentMenu);
//        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
//            switch (item.getItemId()) {
//                case R.id.profile_page:
//                    startActivity(new Intent(getApplicationContext(),
//                            ProfileActivity.class));
//                    overridePendingTransition(0, 0);
//                    return true;
//                case R.id.main_page:
//                    startActivity(new Intent(getApplicationContext(),
//                            MainActivity.class));
//                    overridePendingTransition(0, 0);
//                    return true;
//                case R.id.friends_page:
//                    startActivity(new Intent(getApplicationContext(),
//                            SettingActivity.class));
//                    overridePendingTransition(0, 0);
//                    return true;
//            }
//            return false;
//        });
//    }
//}