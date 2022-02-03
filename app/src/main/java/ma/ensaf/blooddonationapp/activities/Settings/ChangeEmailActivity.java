package ma.ensaf.blooddonationapp.activities.Settings;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ma.ensaf.blooddonationapp.R;
import ma.ensaf.blooddonationapp.Utilities.Constants;
import ma.ensaf.blooddonationapp.activities.chat.BaseActivity;
import ma.ensaf.blooddonationapp.databinding.ActivityChangeEmailBinding;

public class ChangeEmailActivity extends BaseActivity {

    ActivityChangeEmailBinding binding;
    DatabaseReference reference;
    FirebaseUser user;

    boolean isPasswordVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setListeners();
    }

    private void init() {
        reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.KEY_COLLECTION_USERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void setListeners() {
        binding.resetButton.setOnClickListener(v -> {
            if (isValidDetails()) {
                resetEmail();
            }
        });

        binding.discard.setOnClickListener(v -> {
            onBackPressed();
        });

        setVisibilityListener(binding.password);
    }

    private void resetEmail() {
        String email = user.getEmail();
        String password = binding.password.getText().toString();
        // Get auth credentials from the user for re-authentication
        AuthCredential credential = EmailAuthProvider.getCredential(email, password); // Current Login Credentials

        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.updateEmail(binding.newEmail.getText().toString()).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        reference.child(Constants.KEY_EMAIL).setValue(binding.newEmail.getText().toString().trim())
                                .addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        showToast("Email changed successfully");
                                        onBackPressed();
                                    } else {
                                        showToast("Error: " + task2.getException().getMessage());
                                    }
                                });
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
        if (TextUtils.isEmpty(binding.password.getText().toString().trim())) {
            binding.password.setError("Password is required");
            return false;
        }

        if (TextUtils.isEmpty(binding.newEmail.getText().toString().trim())) {
            binding.newEmail.setError("Email is required");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(binding.newEmail.getText().toString()).matches()) {
            binding.newEmail.setError("Enter a valid email");
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