package ma.ensaf.blooddonationapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ma.ensaf.blooddonationapp.Models.User;
import ma.ensaf.blooddonationapp.R;
import ma.ensaf.blooddonationapp.Utilities.Constants;
import ma.ensaf.blooddonationapp.activities.Settings.ProfileActivity;
import ma.ensaf.blooddonationapp.activities.Settings.SettingsActivity;
import ma.ensaf.blooddonationapp.activities.chat.BaseActivity;
import ma.ensaf.blooddonationapp.activities.chat.ChatActivity;
import ma.ensaf.blooddonationapp.activities.chat.MainChatActivity;
import ma.ensaf.blooddonationapp.adapters.UserHomeAdapter;
import ma.ensaf.blooddonationapp.databinding.ActivityNewMainBinding;
import ma.ensaf.blooddonationapp.listeners.UserListener;
import ma.ensaf.blooddonationapp.listeners.UserListenerBeta;

public class NewMainActivity extends BaseActivity implements UserListener, UserListenerBeta, NavigationView.OnNavigationItemSelectedListener {

    ActivityNewMainBinding binding;


    private DatabaseReference userRef;

    private List<User> userList;
    private List<User> compatibleUsersList;

    private UserHomeAdapter userAdapter;
    private UserHomeAdapter compatibleUsersAdapter;

    private CircleImageView navProfileImage;
    private TextView navFullName, navEmail, navBloodGroup, navType;

    private boolean loggedIn = true;

    private SignInClient googleSignInClient;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        setListeners();

    }

    private void init()
    {
        setSupportActionBar(binding.toolbar);

        navProfileImage = binding.navView.getHeaderView(0).findViewById(R.id.navUserImage);
        navFullName = binding.navView.getHeaderView(0).findViewById(R.id.navUserFullName);
        navEmail = binding.navView.getHeaderView(0).findViewById(R.id.navUserEmail);
        navBloodGroup = binding.navView.getHeaderView(0).findViewById(R.id.navUserBloodGroup);
        navType = binding.navView.getHeaderView(0).findViewById(R.id.navUserType);

        userRef = FirebaseDatabase.getInstance().getReference()
                .child(Constants.KEY_COLLECTION_USERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);


        LinearLayoutManager layoutManagerCompatible = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerViewCompatible.setLayoutManager(layoutManagerCompatible);

        userList = new ArrayList<>();
        compatibleUsersList = new ArrayList<>();



    }

    private void setListeners()
    {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(NewMainActivity.this, binding.drawerLayout,
                binding.toolbar, R.string.navigation_drawer_open , R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        fetchDataListener();

        binding.navView.setNavigationItemSelectedListener(this);

        fetchUsersData();
        getCompatibleUsers();

        binding.settings.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        });

        binding.chat.setOnClickListener(v-> {
            Intent intent = new Intent(getApplicationContext(), MainChatActivity.class);
            startActivity(intent);
        });

        bloodGroupClickListener();

    }

    private void showToast(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void fetchDataListener()
    {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String name = snapshot.child(Constants.KEY_NAME).getValue().toString();
                    navFullName.setText(name);

                    String email = snapshot.child(Constants.KEY_EMAIL).getValue().toString();
                    navEmail.setText(email);

                    String bloodGroup = snapshot.child(Constants.KEY_BLOOD_GROUP).getValue().toString();
                    navBloodGroup.setText(bloodGroup);

                    String type = snapshot.child(Constants.KEY_TYPE).getValue().toString();
                    navType.setText(type);

                    if(snapshot.hasChild(Constants.KEY_PROFILE_PICTURE_URL))
                    {
                        String imageUrl = snapshot.child(Constants.KEY_PROFILE_PICTURE_URL).getValue().toString();
                        Glide.with(getApplicationContext()).load(imageUrl).into(navProfileImage);
                    } else {
                        navProfileImage.setImageResource(R.drawable.profile_image);
                    }



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchUsersData()
    {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String type = snapshot.child(Constants.KEY_TYPE).getValue().toString();
                if (type.equals("donor"))
                {
                    binding.textType.setText(R.string.recipients);
                    readRecipients();
                } else {
                    binding.textType.setText(R.string.donors);
                    readDonors();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readRecipients() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.KEY_COLLECTION_USERS);
        Query query = reference.orderByChild(Constants.KEY_TYPE).equalTo("recipient");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    User user = dataSnapshot.getValue(User.class);
                    userList.add(user);
                }

                if(userList.isEmpty())
                {
                    showToast("No recipients");
                } else {
                    userAdapter = new UserHomeAdapter(userList,NewMainActivity.this);
                    binding.recyclerView.setAdapter(userAdapter);

                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readDonors() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.KEY_COLLECTION_USERS);
        Query query = reference.orderByChild(Constants.KEY_TYPE).equalTo("donor");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    User user = dataSnapshot.getValue(User.class);
                    userList.add(user);
                }


                if(userList.isEmpty())
                {
                    showToast("No recipients");
                } else {
                    userAdapter = new UserHomeAdapter(userList,NewMainActivity.this);
                    binding.recyclerView.setAdapter(userAdapter);

                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCompatibleUsers()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(Constants.KEY_COLLECTION_USERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String result;
                String type = snapshot.child(Constants.KEY_TYPE).getValue().toString();
                if(type.equals("donor"))
                {
                    result = "recipient";
                } else {
                    result = "donor";
                }

                String bloodGroup = snapshot.child(Constants.KEY_BLOOD_GROUP).getValue().toString();

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                        .child(Constants.KEY_COLLECTION_USERS);

                ArrayList<String> compatiblePeople;
                if(type.equals("recipient"))
                {
                    compatiblePeople = getCompatibleDonors(bloodGroup);
                }
                else
                {
                    compatiblePeople = getCompatibleRecipients(bloodGroup);
                }

                compatibleUsersList.clear();
                for(String group : compatiblePeople)
                {
                    Query query = reference.orderByChild(Constants.KEY_SEARCH).equalTo(result + group);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot : snapshot.getChildren())
                            {
                                User user = dataSnapshot.getValue(User.class);
                                compatibleUsersList.add(user);
                            }

                            if(compatibleUsersList.isEmpty())
                            {
                                binding.notFoundLayout.setVisibility(View.VISIBLE);
                            } else {
                                compatibleUsersAdapter = new UserHomeAdapter(compatibleUsersList, NewMainActivity.this);
                                binding.recyclerViewCompatible.setAdapter(compatibleUsersAdapter);

                                compatibleUsersAdapter.notifyDataSetChanged();

                                binding.notFoundLayout.setVisibility(View.GONE);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private ArrayList<String> getCompatibleDonors(String recipientGroup)
    {
        ArrayList<String> compatibleDonors = new ArrayList<>();
        switch (recipientGroup)
        {
            case "A+":
                compatibleDonors.add("A+");
                compatibleDonors.add("A-");
                compatibleDonors.add("O+");
                compatibleDonors.add("O-");
                break;
            case "A-":
                compatibleDonors.add("A-");
                compatibleDonors.add("O-");
                break;
            case "B+":
                compatibleDonors.add("B+");
                compatibleDonors.add("B-");
                compatibleDonors.add("O+");
                compatibleDonors.add("O-");
                break;
            case "B-":
                compatibleDonors.add("B-");
                compatibleDonors.add("O-");
                break;
            case "AB+":
                compatibleDonors.add("A+");
                compatibleDonors.add("A-");
                compatibleDonors.add("B+");
                compatibleDonors.add("B-");
                compatibleDonors.add("AB+");
                compatibleDonors.add("AB-");
                compatibleDonors.add("O+");
                compatibleDonors.add("O-");
                break;
            case "AB-":
                compatibleDonors.add("AB-");
                compatibleDonors.add("A-");
                compatibleDonors.add("B-");
                compatibleDonors.add("O-");
                break;
            case "O+":
                compatibleDonors.add("O+");
                compatibleDonors.add("O-");
                break;
            case "O-":
                compatibleDonors.add("O-");
                break;
        }
        return compatibleDonors;
    }

    private ArrayList<String> getCompatibleRecipients(String donorGroup)
    {
        ArrayList<String> compatibleRecipients = new ArrayList<>();
        switch (donorGroup)
        {
            case "A+":
                compatibleRecipients.add("A+");
                compatibleRecipients.add("AB+");
                break;
            case "A-":
                compatibleRecipients.add("A+");
                compatibleRecipients.add("A-");
                compatibleRecipients.add("AB+");
                compatibleRecipients.add("AB-");
                break;
            case "B+":
                compatibleRecipients.add("B+");
                compatibleRecipients.add("AB+");
                break;
            case "B-":
                compatibleRecipients.add("B+");
                compatibleRecipients.add("B-");
                compatibleRecipients.add("AB+");
                compatibleRecipients.add("AB-");
                break;
            case "AB+":
                compatibleRecipients.add("AB+");
                break;
            case "AB-":
                compatibleRecipients.add("AB+");
                compatibleRecipients.add("AB-");
                break;
            case "O+":
                compatibleRecipients.add("A+");
                compatibleRecipients.add("B+");
                compatibleRecipients.add("AB+");
                compatibleRecipients.add("O+");
                break;
            case "O-":
                compatibleRecipients.add("A+");
                compatibleRecipients.add("A-");
                compatibleRecipients.add("B+");
                compatibleRecipients.add("B-");
                compatibleRecipients.add("AB+");
                compatibleRecipients.add("AB-");
                compatibleRecipients.add("O+");
                compatibleRecipients.add("O-");
                break;
        }
        return compatibleRecipients;
    }

    @Override
    public void onUserClicked(User user) {

        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
    }



    private void bloodGroupClickListener()
    {
        binding.aPlusImage.setOnClickListener(v -> {
            startIntent(Constants.VALUE_A_PLUS);
        });

        binding.aMinusImage.setOnClickListener(v -> {
            startIntent(Constants.VALUE_A_MINUS);
        });

        binding.bPlusImage.setOnClickListener(v -> {
            startIntent(Constants.VALUE_B_PLUS);
        });

        binding.bMinusImage.setOnClickListener(v -> {
            startIntent(Constants.VALUE_B_MINUS);
        });

        binding.abPlusImage.setOnClickListener(v -> {
            startIntent(Constants.VALUE_AB_PLUS);
        });

        binding.abMinusImage.setOnClickListener(v -> {
            startIntent(Constants.VALUE_AB_MINUS);
        });

        binding.oPlusImage.setOnClickListener(v -> {
            startIntent(Constants.VALUE_O_PLUS);
        });

        binding.oMinusImage.setOnClickListener(v -> {
            startIntent(Constants.VALUE_O_MINUS);
        });
    }

    private void startIntent(String bloodGroup)
    {
        Intent intent = new Intent(getApplicationContext(), CategorySelectedActivity.class);
        intent.putExtra(Constants.KEY_BLOOD_GROUP, bloodGroup);
        startActivity(intent);
    }

    private void signOut() {
        GoogleSignIn.getClient(this,new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build())
                .signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent9 = new Intent(getApplicationContext(), NewLoginActivity.class);
                startActivity(intent9);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.aPlus:
                Intent intent = new Intent(getApplicationContext(), CategorySelectedActivity.class);
                intent.putExtra(Constants.KEY_BLOOD_GROUP, Constants.VALUE_A_PLUS);
                startActivity(intent);
                break;
            case R.id.aMinus:
                Intent intent1 = new Intent(getApplicationContext(), CategorySelectedActivity.class);
                intent1.putExtra(Constants.KEY_BLOOD_GROUP, Constants.VALUE_A_MINUS);
                startActivity(intent1);
                break;
            case R.id.bPlus:
                Intent intent2 = new Intent(getApplicationContext(), CategorySelectedActivity.class);
                intent2.putExtra(Constants.KEY_BLOOD_GROUP, Constants.VALUE_B_PLUS);
                startActivity(intent2);
                break;
            case R.id.bMinus:
                Intent intent3 = new Intent(getApplicationContext(), CategorySelectedActivity.class);
                intent3.putExtra(Constants.KEY_BLOOD_GROUP, Constants.VALUE_B_MINUS);
                startActivity(intent3);
                break;
            case R.id.abPlus:
                Intent intent4 = new Intent(getApplicationContext(), CategorySelectedActivity.class);
                intent4.putExtra(Constants.KEY_BLOOD_GROUP, Constants.VALUE_AB_PLUS);
                startActivity(intent4);
                break;
            case R.id.abMinus:
                Intent intent5 = new Intent(getApplicationContext(), CategorySelectedActivity.class);
                intent5.putExtra(Constants.KEY_BLOOD_GROUP, Constants.VALUE_AB_MINUS);
                startActivity(intent5);
                break;
            case R.id.oPlus:
                Intent intent6 = new Intent(getApplicationContext(), CategorySelectedActivity.class);
                intent6.putExtra(Constants.KEY_BLOOD_GROUP, Constants.VALUE_O_PLUS);
                startActivity(intent6);
                break;
            case R.id.oMinus:
                Intent intent7 = new Intent(getApplicationContext(), CategorySelectedActivity.class);
                intent7.putExtra(Constants.KEY_BLOOD_GROUP, Constants.VALUE_O_MINUS);
                startActivity(intent7);
                break;
            case R.id.profile:
                Intent intent8 = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent8);
                break;
            case R.id.logout:
                status("offline");
                loggedIn = false;
                signOut();
                FirebaseAuth.getInstance().signOut();
                Intent intent9 = new Intent(getApplicationContext(), NewLoginActivity.class);
                startActivity(intent9);
                finish();
                break;
            case R.id.compatible:
                Intent intent10 = new Intent(getApplicationContext(), CategorySelectedActivity.class);
                intent10.putExtra(Constants.KEY_BLOOD_GROUP, "Compatible with me");
                startActivity(intent10);
                break;
            case R.id.settings:
                Intent intent13 = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent13);
                break;
            case R.id.chat:
                Intent intent14 = new Intent(getApplicationContext(), MainChatActivity.class);
                startActivity(intent14);
                break;
            case R.id.about:
                Intent intent15 = new Intent(getApplicationContext(),AboutActivity.class);
                startActivity(intent15);
                break;
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void status(String status) {
        if(loggedIn){
            super.status(status);
        }

    }


    @Override
    public void onUserClickedBeta(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);

    }
}