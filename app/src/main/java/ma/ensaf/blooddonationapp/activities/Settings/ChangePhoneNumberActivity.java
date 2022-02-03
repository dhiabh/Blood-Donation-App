package ma.ensaf.blooddonationapp.activities.Settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ma.ensaf.blooddonationapp.R;
import ma.ensaf.blooddonationapp.Utilities.Constants;
import ma.ensaf.blooddonationapp.activities.chat.BaseActivity;
import ma.ensaf.blooddonationapp.databinding.ActivityChangePhoneNumberBinding;

public class ChangePhoneNumberActivity extends BaseActivity {

    ActivityChangePhoneNumberBinding binding;

    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setListeners();
    }

    private void init()
    {
        reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.KEY_COLLECTION_USERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    private void setListeners()
    {
        binding.resetButton.setOnClickListener(v -> {
            changePhoneNumberListener();
        });

        binding.discard.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void changePhoneNumberListener()
    {
        reference.child(Constants.KEY_PHONE_NUMBER).setValue(binding.newPhoneNumber.getText().toString())
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        showToast("Phone number changed successfully");
                        onBackPressed();
                    }
                    else {
                        showToast("Error " + task.getException().getMessage());

                    }
                });
    }

    private void showToast(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}