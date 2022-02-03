package ma.ensaf.blooddonationapp.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ma.ensaf.blooddonationapp.Models.User;
import ma.ensaf.blooddonationapp.R;
import ma.ensaf.blooddonationapp.databinding.NewUserDisplayedLayoutBinding;
import ma.ensaf.blooddonationapp.listeners.UserListener;

public class UserHomeAdapter extends RecyclerView.Adapter<UserHomeAdapter.UserHomeViewHolder>{
    private List<User> userList;
    private final UserListener userListener;

    public UserHomeAdapter(List<User> userList, UserListener userListener) {

        this.userList = userList;
        this.userListener = userListener;
    }

    @NonNull
    @Override
    public UserHomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NewUserDisplayedLayoutBinding newUserDisplayedLayoutBinding = NewUserDisplayedLayoutBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UserHomeViewHolder(newUserDisplayedLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHomeViewHolder holder, int position) {
        holder.setUserData(userList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserHomeViewHolder extends RecyclerView.ViewHolder {

        NewUserDisplayedLayoutBinding binding;

        public UserHomeViewHolder(NewUserDisplayedLayoutBinding newUserDisplayedLayoutBinding) {
            super(newUserDisplayedLayoutBinding.getRoot());
            binding = newUserDisplayedLayoutBinding;
        }

        void setUserData(User user) {
            binding.userName.setText(user.getName());
            binding.bloodGroup.setText(user.getBloodGroup());
            if(user.getProfilePictureUrl() != null)
            {
                Glide.with(binding.getRoot())
                        .load(user.getProfilePictureUrl())
                        .override(130,130)
                        .into(binding.userProfileImage);
            }

            if (user.getType().equals("donor")) {
                if (user.isAnonymous()) {
                    binding.userName.setText(R.string.anonymous_donor);
                    Glide.with(binding.getRoot()).load(R.drawable.profile_image).into(binding.userProfileImage);
                }
            }

            binding.getRoot().setOnClickListener(v -> userListener.onUserClicked(user));
        }
    }
}
