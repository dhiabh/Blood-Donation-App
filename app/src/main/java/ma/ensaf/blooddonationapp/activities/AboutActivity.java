package ma.ensaf.blooddonationapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ma.ensaf.blooddonationapp.databinding.ActivityAboutBinding;

public class AboutActivity extends AppCompatActivity {

    ActivityAboutBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListeners();
    }

    private void setListeners() {

        binding.discard.setOnClickListener(v->{
            onBackPressed();
        });
    }
}