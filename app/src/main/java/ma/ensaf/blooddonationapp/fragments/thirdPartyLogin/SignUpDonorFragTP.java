package ma.ensaf.blooddonationapp.fragments.thirdPartyLogin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import ma.ensaf.blooddonationapp.Utilities.Constants;
import ma.ensaf.blooddonationapp.activities.NewMainActivity;
import ma.ensaf.blooddonationapp.databinding.ActivitySignUpDonorFragTPBinding;

public class SignUpDonorFragTP extends Fragment {

    ActivitySignUpDonorFragTPBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivitySignUpDonorFragTPBinding.inflate(getLayoutInflater());
        setListeners();
        return binding.getRoot();
    }



    private void setListeners() {
        binding.registerButton.setOnClickListener(v->{
            if(isValidSignUpDetails())
            {
                SignUp();
            }
        });
    }

    private void showToast(String message)
    {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void SignUp() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constants.KEY_COLLECTION_USERS).child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(Constants.KEY_ID, firebaseUser.getUid());
        hashMap.put(Constants.KEY_NAME, binding.registerFullName.getText().toString());
        hashMap.put(Constants.KEY_EMAIL, firebaseUser.getEmail());
        hashMap.put(Constants.KEY_BLOOD_GROUP, binding.bloodGroupSpinner.getSelectedItem().toString());
        hashMap.put(Constants.KEY_TYPE, "donor");
        hashMap.put(Constants.KEY_SEARCH, "donor" + binding.bloodGroupSpinner.getSelectedItem().toString());
        hashMap.put(Constants.KEY_ANONYMOUS, binding.anonymousSwitch.isChecked());
        reference.updateChildren(hashMap);

        Intent intent = new Intent(getContext(), NewMainActivity.class);
        startActivity(intent);
    }



    private boolean isValidSignUpDetails() {

        if(TextUtils.isEmpty(binding.registerFullName.getText().toString().trim()))
        {
            binding.registerFullName.setError("Full name is required");
            return false;
        }

        if(TextUtils.equals(binding.bloodGroupSpinner.getSelectedItem().toString(), "Select your blood group"))
        {
            showToast("Select blood group");
            return false;
        }

        return true;
    }
}
