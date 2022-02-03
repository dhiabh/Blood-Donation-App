package ma.ensaf.blooddonationapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import ma.ensaf.blooddonationapp.Models.User;
import ma.ensaf.blooddonationapp.R;
import ma.ensaf.blooddonationapp.Utilities.Constants;
import ma.ensaf.blooddonationapp.adapters.LoginAdapter;
import ma.ensaf.blooddonationapp.databinding.ActivityNewLoginBinding;
import ma.ensaf.blooddonationapp.fragments.thirdPartyLogin.thirdPartySignUp;

public class NewLoginActivity extends AppCompatActivity {

    ActivityNewLoginBinding binding;

    float v = 0;

    private GoogleSignInClient mGoogleSignInClient;
    private final static int  RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        createRequest();
        startAnimation();
        mAuth= FirebaseAuth.getInstance();
        setListeners();
    }

    private void setListeners() {
        binding.fabGoogle.setOnClickListener(v->{
            signIn();
            /*
            Intent intent = new Intent(getApplicationContext(), thirdPartySignUp.class);
            startActivity(intent);
            finish();
             */
        });
    }

    private void createRequest() {
        // To show all email accounts
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(getApplicationContext(), thirdPartySignUp.class);
                            startActivity(intent);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private Boolean flag=false;
    private boolean UserExists() {

        String CurrentUserEmail = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.KEY_COLLECTION_USERS);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);

                    assert user != null;
                    if(user.getEmail().equals(CurrentUserEmail)){
                        flag=true;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return flag;
    }


    private void startAnimation() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Login"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("New Recipient"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("New Donor"));

        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final LoginAdapter adapter = new LoginAdapter(getSupportFragmentManager(), this, binding.tabLayout.getTabCount());
        binding.viewPager.setAdapter(adapter);

        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));

        //binding.fabFacebook.setTranslationY(300);
        binding.fabGoogle.setTranslationY(300);
        //binding.fabTwiiter.setTranslationY(300);
        binding.tabLayout.setTranslationY(300);

        //binding.fabFacebook.setAlpha(v);
        //binding.fabTwiiter.setAlpha(v);
        binding.fabGoogle.setAlpha(v);
        binding.tabLayout.setAlpha(v);

        //binding.fabFacebook.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();
        binding.fabGoogle.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(600).start();
        //binding.fabTwiiter.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(800).start();
        binding.tabLayout.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(1000).start();

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}