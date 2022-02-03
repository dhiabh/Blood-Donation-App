package ma.ensaf.blooddonationapp.activities.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ma.ensaf.blooddonationapp.R;
import ma.ensaf.blooddonationapp.Utilities.Constants;
import ma.ensaf.blooddonationapp.activities.NewLoginActivity;
import ma.ensaf.blooddonationapp.activities.chat.BaseActivity;
import ma.ensaf.blooddonationapp.databinding.ActivitySettingsBinding;
import ma.ensaf.blooddonationapp.databinding.ChangeBloodGroupSpinnerBinding;

public class SettingsActivity extends BaseActivity {

    ActivitySettingsBinding binding;
    ChangeBloodGroupSpinnerBinding changeBloodGroupSpinnerBinding;

    Dialog dialog;

    DatabaseReference reference;

    private boolean loggedIn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setListeners();
    }

    private void init() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.KEY_COLLECTION_USERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        changeBloodGroupSpinnerBinding = ChangeBloodGroupSpinnerBinding.inflate(getLayoutInflater());

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

    private void setListeners() {
        fetchDataListener();

        binding.profileLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
        });

        editAnonymousListener();

        binding.editEmailLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ChangeEmailActivity.class);
            startActivity(intent);
        });

        binding.editPasswordLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
            startActivity(intent);
        });

        binding.editBloodGroupLayout.setOnClickListener(v -> {
            selectBloodGroup();
        });

        binding.editPhoneNumberLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ChangePhoneNumberActivity.class);
            startActivity(intent);
        });

        binding.logout.setOnClickListener(v -> {
            status("offline");
            loggedIn = false;
            signOut();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), NewLoginActivity.class);
            startActivity(intent);
            finish();
        });

    }



    private void fetchDataListener() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    binding.name.setText(snapshot.child(Constants.KEY_NAME).getValue().toString());
                    binding.bloodGroup.setText(snapshot.child(Constants.KEY_BLOOD_GROUP).getValue().toString());
                    binding.email.setText(snapshot.child(Constants.KEY_EMAIL).getValue().toString());

                    if (snapshot.child(Constants.KEY_PROFILE_PICTURE_URL).getValue() != null) {
                        Glide.with(getApplicationContext())
                                .load(snapshot.child(Constants.KEY_PROFILE_PICTURE_URL)
                                        .getValue().toString())
                                .into(binding.profileImage);
                    }

                    if(snapshot.child(Constants.KEY_PHONE_NUMBER).getValue() != null)
                    {
                        binding.phoneNumber.setText(snapshot.child(Constants.KEY_PHONE_NUMBER).getValue().toString());
                    }

                    if (snapshot.child(Constants.KEY_TYPE).getValue().toString().equals("donor")) {
                        binding.anonymousLayout.setVisibility(View.VISIBLE);
                        if (snapshot.child(Constants.KEY_ANONYMOUS).getValue().toString().equals("true")) {
                            binding.anonymousSwitch.setChecked(true);
                        } else {
                            binding.anonymousSwitch.setChecked(false);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void selectBloodGroup() {
        dialog = new Dialog(SettingsActivity.this);

        if (changeBloodGroupSpinnerBinding.getRoot().getParent() != null) {
            ((ViewGroup) changeBloodGroupSpinnerBinding.getRoot().getParent()).removeView(changeBloodGroupSpinnerBinding.getRoot());
        }
        dialog.setContentView(changeBloodGroupSpinnerBinding.getRoot());
        dialog.getWindow().setLayout(500, 600);
        dialog.show();

        Resources res = getResources();
        String[] bloodGroupsList = new String[res.getStringArray(R.array.bloodGroups).length - 1];
        for (int i = 1; i < res.getStringArray(R.array.bloodGroups).length; i++) {
            bloodGroupsList[i - 1] = res.getStringArray(R.array.bloodGroups)[i];
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(
                        SettingsActivity.this,
                        android.R.layout.simple_list_item_1,
                        bloodGroupsList) {
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text = view.findViewById(android.R.id.text1);

                        text.setTextColor(Color.WHITE);

                        return view;
                    }
                };


        changeBloodGroupSpinnerBinding.bloodGroupListView.setAdapter(adapter);


        changeBloodGroupSpinnerBinding.bloodGroupListView.setOnItemClickListener((parent, view, position, id) -> {
            reference.child(Constants.KEY_BLOOD_GROUP).setValue(adapter.getItem(position));

            reference.child(Constants.KEY_TYPE).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    reference.child(Constants.KEY_SEARCH).setValue(
                            task.getResult().getValue().toString() +
                                    adapter.getItem(position));
                }
            });


            dialog.dismiss();
        });
    }

    private void editAnonymousListener() {
        binding.anonymousSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            reference.child(Constants.KEY_ANONYMOUS).setValue(isChecked);
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void status(String status) {
        if(loggedIn){
            super.status(status);
        }

    }
}