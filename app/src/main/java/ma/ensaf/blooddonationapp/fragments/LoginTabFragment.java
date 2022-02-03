package ma.ensaf.blooddonationapp.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ma.ensaf.blooddonationapp.R;
import ma.ensaf.blooddonationapp.activities.NewMainActivity;
import ma.ensaf.blooddonationapp.activities.Settings.ForgotPasswordActivity;
import ma.ensaf.blooddonationapp.databinding.LoginTabFragmentBinding;

public class LoginTabFragment extends Fragment {
    LoginTabFragmentBinding binding;

    float v = 0;
    boolean isPasswordVisible;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = LoginTabFragmentBinding.inflate(getLayoutInflater());
        //ViewGroup root = (ViewGroup) inflater.inflate(R.layout.login_tab_fragment, container, false);
        init(binding.getRoot());
        startAnimation();
        setListener();

        return binding.getRoot();
    }



    private void init(ViewGroup root) {

        mAuth = FirebaseAuth.getInstance();
    }


    private void setListener()
    {
        setPasswordVisibilityListener();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if(user != null)
                {
                    Intent intent = new Intent(getContext(), NewMainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    //getActivity().finish();
                }
            }
        };

        binding.loginButton.setOnClickListener(v -> {
            if(isValidSignInDetails())
            {
                signIn();
            }
        });

        binding.forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ForgotPasswordActivity.class);
            startActivity(intent);
        });

        /*
        binding.fabGoogle.setOnClickListener(v -> {
            showToast("Google Login");
        });

        binding.fabFacebook.setOnClickListener(v -> {
            showToast("Facebook Login");
        });

        binding.fabTwiiter.setOnClickListener(v ->{
            showToast("Twitter Login");
        });
    */
    }

    private void signIn()
    {
        /*loader.setMessage("Log in in progress");
        loader.setCanceledOnTouchOutside(false);
        loader.show();*/

        mAuth.signInWithEmailAndPassword(binding.loginEmail.getText().toString().trim(),
                binding.loginPassword.getText().toString().trim())
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        showToast("You're logged in successfully");
                        Intent intent = new Intent(getContext(), NewMainActivity.class);
                        startActivity(intent);
                        //finish();
                    } else {
                        showToast( task.getException().getMessage());
                    }
                });
    }


    private void showToast(String message)
    {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private Boolean isValidSignInDetails()
    {
        if(TextUtils.isEmpty(binding.loginEmail.getText().toString().trim()))
        {
            binding.loginEmail.setError("Email is required");
            return false;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(binding.loginEmail.getText().toString()).matches())
        {
            binding.loginEmail.setError("Enter a valid email");
            return false;
        }

        if(TextUtils.isEmpty(binding.loginPassword.getText().toString().trim()))
        {
            binding.loginPassword.setError("Password is required");
            return false;
        }
        return true;
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(authStateListener);
    }

    private void startAnimation()
    {
        binding.loginEmail.setTranslationX(800);
        binding.loginPassword.setTranslationX(800);
        binding.forgotPassword.setTranslationX(800);
        binding.loginButton.setTranslationX(800);

        binding.loginEmail.setAlpha(v);
        binding.loginPassword.setAlpha(v);
        binding.forgotPassword.setAlpha(v);
        binding.loginButton.setAlpha(v);

        binding.loginEmail.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        binding.loginPassword.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        binding.forgotPassword.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        binding.loginButton.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setPasswordVisibilityListener()
    {
        binding.loginPassword.setOnTouchListener((v1, event) -> {
            final int RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (binding.loginPassword.getRight() - binding.loginPassword.getCompoundDrawables()[RIGHT].getBounds().width())) {
                    int selection = binding.loginPassword.getSelectionEnd();
                    if (isPasswordVisible) {
                        // set drawable image
                        binding.loginPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_24, 0);
                        // hide Password
                        binding.loginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        isPasswordVisible = false;
                    } else  {
                        // set drawable image
                        binding.loginPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_off_24, 0);
                        // show Password
                        binding.loginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        isPasswordVisible = true;
                    }
                    binding.loginPassword.setSelection(selection);
                    return true;
                }
            }
            return false;
        });
    }
}
