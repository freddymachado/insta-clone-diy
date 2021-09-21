package com.gvm.diy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gvm.diy.R;
import com.gvm.diy.models.ProfileItem;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>{

    private Context mContext;
    private List<ProfileItem> profileItems;

    public ProfileAdapter(Context mContext, List<ProfileItem> profileItems) {
        this.mContext = mContext;
        this.profileItems = profileItems;
    }

    public ProfileAdapter(List<ProfileItem> profileItems) {
        this.profileItems = profileItems;
    }

    public ProfileAdapter() {
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProfileViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.profile_item,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        holder.setProfileImageView(profileItems.get(position));
    }

    @Override
    public int getItemCount() {
        return profileItems.size();
    }

    class ProfileViewHolder extends RecyclerView.ViewHolder{

        ImageView profileImageView;

        ProfileViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImageView = itemView.findViewById(R.id.profileImage);
        }

        void setProfileImageView(ProfileItem profileItem){

            try{
                Glide.with(mContext).load(profileItem.getFile()+".jpg")
                        .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                        .into(profileImageView);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
