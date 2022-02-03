package ma.ensaf.blooddonationapp.activities.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import ma.ensaf.blooddonationapp.R;
import ma.ensaf.blooddonationapp.Utilities.Constants;
import ma.ensaf.blooddonationapp.activities.chat.BaseActivity;
import ma.ensaf.blooddonationapp.databinding.ActivityEditProfileBinding;

public class EditProfileActivity extends BaseActivity {

    ActivityEditProfileBinding binding;

    DatabaseReference reference;
    private FirebaseAuth mAuth;

    Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setListeners();
    }

    private void init()
    {
        setSupportActionBar(binding.toolbar);

        reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.KEY_COLLECTION_USERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mAuth = FirebaseAuth.getInstance();
    }

    private void setListeners()
    {
        fetchDataListener();

        binding.profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });

        binding.changePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });

        binding.save.setOnClickListener(v -> {
            changeNameListener();
            changePhotoListener();

            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
            finish();
        });

        binding.discard.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
            finish();
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null)
        {
            resultUri = data.getData();
            binding.profileImage.setImageURI(resultUri);
        }
    }

    private void fetchDataListener()
    {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    binding.editName.setText(snapshot.child(Constants.KEY_NAME).getValue().toString());

                    if(snapshot.child(Constants.KEY_PROFILE_PICTURE_URL).getValue() != null)
                    {
                        Glide.with(getApplicationContext())
                                .load(snapshot.child(Constants.KEY_PROFILE_PICTURE_URL)
                                        .getValue().toString())
                                .into(binding.profileImage);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void changeNameListener()
    {
        reference.child(Constants.KEY_NAME).setValue(binding.editName.getText().toString())
                .addOnCompleteListener(task -> {
                    if(!task.isSuccessful())
                    {
                        showToast("Error " + task.getException().getMessage());
                    }
                });
    }

    private void changePhotoListener()
    {
        if(resultUri != null)
        {
            String currentUserId = mAuth.getCurrentUser().getUid();
            final StorageReference filePath = FirebaseStorage.getInstance().getReference()
                    .child("profile images").child(currentUserId);
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);


            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.addOnFailureListener(e -> {
                showToast("Image Upload Failed");
            });

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                if(taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference() != null)
                {
                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                    result.addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();

                        reference.child(Constants.KEY_PROFILE_PICTURE_URL).setValue(imageUrl)
                                .addOnCompleteListener(task -> {
                                    if(!task.isSuccessful())
                                    {
                                        showToast("Error " + task.getException().getMessage());
                                    }
                                });
                    });
                }
            });

        }
    }
    private void showToast(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}