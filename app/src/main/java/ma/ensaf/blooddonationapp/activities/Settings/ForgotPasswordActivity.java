package ma.ensaf.blooddonationapp.activities.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import ma.ensaf.blooddonationapp.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends AppCompatActivity {

    ActivityForgotPasswordBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListeners();
    }

    private void setListeners() {

        binding.resetButton.setOnClickListener(v -> {
            if(isValidEmailDetails())
            {
                sendResetEmail();
            }
        });

        binding.discard.setOnClickListener(v -> {
            onBackPressed();
        });

    }

    private void sendResetEmail() {
        String emailAddress = binding.forgotPasswordEmail.getText().toString();
        FirebaseAuth.getInstance().sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this, "Email sent", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(ForgotPasswordActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private Boolean isValidEmailDetails() {
        if (TextUtils.isEmpty(binding.forgotPasswordEmail.getText().toString().trim())) {
            binding.forgotPasswordEmail.setError("Email is required");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(binding.forgotPasswordEmail.getText().toString()).matches()) {
            binding.forgotPasswordEmail.setError("Enter a valid email");
            return false;
        }
        return true;
    }
}