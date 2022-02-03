package ma.ensaf.blooddonationapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import ma.ensaf.blooddonationapp.Models.Message;
import ma.ensaf.blooddonationapp.Models.User;
import ma.ensaf.blooddonationapp.R;
import ma.ensaf.blooddonationapp.Utilities.Constants;
import ma.ensaf.blooddonationapp.databinding.UserDisplayedChatLayoutBinding;
import ma.ensaf.blooddonationapp.listeners.UserListener;

public class UserAdapterChat extends RecyclerView.Adapter<UserAdapterChat.UserChatViewHolder>{

    private Context context;
    private List<User> userList;
    private UserListener messageListener;

    private String lastMessage;


    public UserAdapterChat(Context context, List userList,UserListener messageListener) {
        this.context = context;
        this.userList = userList;
        this.messageListener = messageListener;
    }


    @NonNull
    @Override
    public UserChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        UserDisplayedChatLayoutBinding userDisplayedChatLayoutBinding = UserDisplayedChatLayoutBinding.inflate(
                LayoutInflater.from(parent.getContext())
                ,parent,
                false
        );
        return new UserChatViewHolder(userDisplayedChatLayoutBinding);

    }


    @Override
    public void onBindViewHolder(@NonNull UserChatViewHolder holder, int position) {
        holder.setUserData(userList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public  class UserChatViewHolder extends RecyclerView.ViewHolder{

        UserDisplayedChatLayoutBinding binding;

        public UserChatViewHolder(UserDisplayedChatLayoutBinding userDisplayedChatLayoutBinding) {
            super(userDisplayedChatLayoutBinding.getRoot());
            binding = userDisplayedChatLayoutBinding;
        }

        public void setUserData(User user){
            if(user.isAnonymous()){
                Glide.with(context).load(R.drawable.profile_image).into(binding.userPorfileImageChat);
                binding.userNameChat.setText("Anonymous");
            }else {
                Glide.with(context).load(user.getProfilePictureUrl()).into(binding.userPorfileImageChat);
                binding.userNameChat.setText(user.getName());
            }
            lastMessage(user.getId());


            if(user.getAvailability().equals("online")) {
                binding.userPorfileImageChat.setBorderColor(Color.parseColor("#45FF00"));
                binding.userPorfileImageChat.setBorderWidth(4);
            }


            binding.getRoot().setOnClickListener(v -> messageListener.onUserClicked(user));
        }

        private void lastMessage(String userId){
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constants.KEY_TABLE_CHAT);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Message message =dataSnapshot.getValue(Message.class);

                        if(message.getReceiverId().equals(firebaseUser.getUid()) && message.getSenderId().equals(userId) ||
                                message.getReceiverId().equals(userId) && message.getSenderId().equals(firebaseUser.getUid()) ){
                            lastMessage = message.getMessage();
                        }
                    }
                    if(lastMessage==null){ lastMessage=""; }
                    binding.LastMessage.setText(lastMessage);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
