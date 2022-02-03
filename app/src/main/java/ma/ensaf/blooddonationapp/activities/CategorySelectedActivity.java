package ma.ensaf.blooddonationapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ma.ensaf.blooddonationapp.Models.User;
import ma.ensaf.blooddonationapp.Utilities.Constants;
import ma.ensaf.blooddonationapp.activities.Settings.ProfileActivity;
import ma.ensaf.blooddonationapp.activities.chat.BaseActivity;
import ma.ensaf.blooddonationapp.adapters.UserAdapter;
import ma.ensaf.blooddonationapp.databinding.ActivityCategorySelectedBinding;
import ma.ensaf.blooddonationapp.listeners.UserListener;

public class CategorySelectedActivity extends BaseActivity implements UserListener {

    ActivityCategorySelectedBinding binding;

    private String title = "";

    private List<User> userList;
    private UserAdapter userAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategorySelectedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init()
    {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        binding.recyclerView.setLayoutManager(linearLayoutManager);

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, CategorySelectedActivity.this);
        binding.recyclerView.setAdapter(userAdapter);

        if(getIntent().getExtras() != null)
        {
            title = getIntent().getStringExtra(Constants.KEY_BLOOD_GROUP);

            if(title.equals("Compatible with me"))
            {
                getSupportActionBar().setTitle("Compatible with me");
                getCompatibleUsers();
            } else {
                getSupportActionBar().setTitle("Blood group: " + title);
                readUsers();
            }
        }
    }

    private void getCompatibleUsers() {
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

                userList.clear();
                for(String group : compatiblePeople)
                {
                    Query query = reference.orderByChild(Constants.KEY_SEARCH).equalTo(result + group);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot : snapshot.getChildren())
                            {
                                User user = dataSnapshot.getValue(User.class);
                                userList.add(user);
                            }
                            userAdapter.notifyDataSetChanged();

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

    private void readUsers() {
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

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                        .child(Constants.KEY_COLLECTION_USERS);

                Query query = reference.orderByChild(Constants.KEY_SEARCH).equalTo(result + title);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userList.clear();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren())
                        {
                            User user = dataSnapshot.getValue(User.class);
                            userList.add(user);
                        }
                        userAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
    }
}