package ma.ensaf.blooddonationapp.fragments.thirdPartyLogin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ma.ensaf.blooddonationapp.Utilities.Constants;
import ma.ensaf.blooddonationapp.activities.NewMainActivity;
import ma.ensaf.blooddonationapp.adapters.SignUpTPAdapter;
import ma.ensaf.blooddonationapp.databinding.ActivityThirdPartySignUpBinding;

public class thirdPartySignUp extends AppCompatActivity {
    ActivityThirdPartySignUpBinding binding;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.KEY_COLLECTION_USERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.child(Constants.KEY_BLOOD_GROUP).get().addOnCompleteListener(task1 -> {
            if (task1.getResult().getValue() != null) {
                Intent intent = new Intent(getApplicationContext(), NewMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            else {
                binding = ActivityThirdPartySignUpBinding.inflate(getLayoutInflater());
                setContentView(binding.getRoot());

                binding.tabLayout.addTab(binding.tabLayout.newTab().setText("New Donor"));
                binding.tabLayout.addTab(binding.tabLayout.newTab().setText("New Recipient"));

                binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

                final SignUpTPAdapter adapter = new SignUpTPAdapter(getSupportFragmentManager(), this, binding.tabLayout.getTabCount());
                binding.viewPager.setAdapter(adapter);

                binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));

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

        });



    }
}
