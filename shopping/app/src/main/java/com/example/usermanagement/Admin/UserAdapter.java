package com.example.usermanagement.Admin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import usermanagement.R;

import com.example.usermanagement.Views.Member;
import com.example.usermanagement.Views.User;

import java.util.List;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ImageViewHolder> {
    private final Context mContext;
    public static UserAdapterListener mClickListener;
    private final List<Member> mUser;
    public UserAdapter(Context context, List<Member> Users, UserAdapterListener listener) {
        mContext = context;
        mUser = Users;
        this.mClickListener = listener;
    }
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.show_users, parent, false);
        return new ImageViewHolder(v);
    }



    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Member user = mUser.get(position);
        holder.name.setText(user.getName());
        holder.phone.setText(String.valueOf(user.getMobile()));
        holder.address.setText(user.getLocation());
        holder.email.setText(user.getEmail());
    }
    @Override
    public int getItemCount() {
        return mUser.size();
    }
    public static class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name,address,dob,nic,email,phone;
        public ImageButton delete;
        public LinearLayout linearLayoutManager;

        @Override
        public void onClick(View v) {


        }

        public ImageViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            phone = itemView.findViewById(R.id.phone);
            address = itemView.findViewById(R.id.location);
            linearLayoutManager = (LinearLayout)itemView.findViewById(R.id.show_details);
            linearLayoutManager.setOnClickListener(v -> mClickListener
                    .userClick(v,ImageViewHolder.this.getAdapterPosition()));
            itemView.setOnClickListener(this);
        }

    }

    public interface UserAdapterListener{
        void userClick(View v, int position);
    }
  

}

