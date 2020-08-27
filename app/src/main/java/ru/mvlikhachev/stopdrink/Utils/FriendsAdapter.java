package ru.mvlikhachev.stopdrink.Utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.mvlikhachev.stopdrink.Model.User;
import ru.mvlikhachev.stopdrink.R;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {

    private ArrayList<User> users;
    private OnUserClickListener listener;


    public interface OnUserClickListener {
        void onUserClick(int position);
    }

    public void setOnUserClickListener(OnUserClickListener listener) {
        this.listener = listener;
    }

    public FriendsAdapter(ArrayList<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item, parent, false);
        FriendViewHolder viewHolder = new FriendViewHolder(view, listener);
        return viewHolder;


    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        User currentUser = users.get(position);
        holder.avatarCardImageView.setImageResource(currentUser.getAvatarMockUpResource());
        holder.usernameCardTextView.setText(currentUser.getName());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {

        public ImageView avatarCardImageView;
        public TextView usernameCardTextView;

        public FriendViewHolder(@NonNull View itemView, OnUserClickListener listener) {
            super(itemView);

            avatarCardImageView = itemView.findViewById(R.id.avatarCardImageView);
            usernameCardTextView = itemView.findViewById(R.id.usernameCardTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onUserClick(position);
                        }
                    }
                }
            });
        }
    }
}
