package ma.ensaf.blooddonationapp.activities.Settings;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ma.ensaf.blooddonationapp.R;
import ma.ensaf.blooddonationapp.activities.chat.BaseActivity;
import ma.ensaf.blooddonationapp.databinding.ActivityChangePasswordBinding;

public class ChangePasswordActivity extends BaseActivity {

    ActivityChangePasswordBinding binding;

    FirebaseUser user;

    boolean isPasswordVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setListeners();
    }

    private void init() {

        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void setListeners() {
        binding.resetButton.setOnClickListener(v -> {
            if (isValidDetails()) {
                resetPassword();
            }
        });

        binding.discard.setOnClickListener(v -> {
            onBackPressed();
        });

        setVisibilityListener(binding.currentPassword);
        setVisibilityListener(binding.newPassword);
        setVisibilityListener(binding.confirmPassword);
    }

    private void resetPassword() {
        String email = user.getEmail();
        String password = binding.currentPassword.getText().toString();
        // Get auth credentials from the user for re-authentication
        AuthCredential credential = EmailAuthProvider.getCredential(email, password); // Current Login Credentials

        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                user.updatePassword(binding.newPassword.getText().toString()).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        showToast("Password changed successfully");
                        onBackPressed();
                    } else {
                        showToast("Error: " + task1.getException().getMessage());
                    }

                });
            } else {
                showToast("Error: " + task.getException().getMessage());
            }


        });
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private Boolean isValidDetails() {
        if (TextUtils.isEmpty(binding.currentPassword.getText().toString().trim())) {
            binding.currentPassword.setError("Current password is required");
            return false;
        }

        if (TextUtils.isEmpty(binding.newPassword.getText().toString().trim())) {
            binding.newPassword.setError("New password is required");
            return false;
        }

        if(TextUtils.isEmpty(binding.confirmPassword.getText().toString().trim()))
        {
            binding.confirmPassword.setError("Confirm your password");
            return false;
        }
        if(!binding.newPassword.getText().toString().equals(binding.confirmPassword.getText().toString()))
        {
            showToast("Passwords do not match ");
            return false;
        }

        return true;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setVisibilityListener(EditText editText)
    {
        editText.setOnTouchListener((v1, event) -> {
            final int RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[RIGHT].getBounds().width())) {
                    int selection = editText.getSelectionEnd();
                    if (isPasswordVisible) {
                        // set drawable image
                        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_24, 0);
                        // hide Password
                        editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        isPasswordVisible = false;
                    } else  {
                        // set drawable image
                        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_off_24, 0);
                        // show Password
                        editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        isPasswordVisible = true;
                    }
                    editText.setSelection(selection);
                    return true;
                }
            }
            return false;
        });
    }
}