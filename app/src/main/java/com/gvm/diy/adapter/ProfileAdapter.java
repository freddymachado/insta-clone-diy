package com.gvm.diy.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gvm.diy.R;
import com.gvm.diy.models.Post;
import com.gvm.diy.models.ProfileItem;
import com.gvm.diy.ui.PostViewerActivity;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>{

    private Context mContext;
    private List<ProfileItem> profileItems;
    private List<Post> profileItems2;
    String access_token,username, user_id, avatar, server_key = "1539874186", name, favourites, following,
            fname, lname, about, website, followers, is_liked, is_saved, post_id, likes, web;


    public ProfileAdapter(Context mContext, List<ProfileItem> profileItems) {
        this.mContext = mContext;
        this.profileItems = profileItems;
    }

    public ProfileAdapter(List<ProfileItem> profileItems) {
        this.profileItems = profileItems;
    }

    public ProfileAdapter() {
    }

    public ProfileAdapter(Context mContext, List<Post> profileItems2, String access_token) {
        this.mContext = mContext;
        this.profileItems2 = profileItems2;
        this.access_token = access_token;

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
        Post profileItem = profileItems2.get(position);

        is_liked = profileItem.getIs_liked();
        is_saved = profileItem.getIs_saved();
        post_id = profileItem.getPost_id();
        user_id = profileItem.getUser_id();
        likes = profileItem.getLikes();
        web = profileItem.getWebsite();
        //holder.setProfileImageView(profileItems2.get(position));

        try{
            Glide.with(mContext).load("https://diys.co/"+profileItem.getFile())
                    .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                    .into(holder.profileImageView);
        }catch (Exception e){
            e.printStackTrace();
        }

        holder.profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPostViewer = new Intent(mContext, PostViewerActivity.class);
                intentPostViewer.putExtra("access_token", access_token);
                intentPostViewer.putExtra("is_liked", is_liked);
                intentPostViewer.putExtra("is_saved", is_saved);
                intentPostViewer.putExtra("post_id", post_id);
                intentPostViewer.putExtra("user_id", user_id);
                intentPostViewer.putExtra("username", profileItem.getUsername());
                intentPostViewer.putExtra("avatar", profileItem.getAvatar());
                intentPostViewer.putExtra("post_image", "https://diys.co/"+profileItem.getFile());
                intentPostViewer.putExtra("description", profileItem.getDescription());
                intentPostViewer.putExtra("comment", profileItem.getComments());
                intentPostViewer.putExtra("likes", likes);
                intentPostViewer.putExtra("web", web);
                intentPostViewer.putExtra("name", profileItem.getName());
                intentPostViewer.putExtra("following", profileItem.getFollowing());
                intentPostViewer.putExtra("followers", profileItem.getFollowers());
                intentPostViewer.putExtra("favourites", profileItem.getFavourites());
                intentPostViewer.putExtra("about", profileItem.getAbout());
                intentPostViewer.putExtra("isFollowing", profileItem.getIsFollowing());
                mContext.startActivity(intentPostViewer);

            }
        });
    }

    @Override
    public int getItemCount() {
        return profileItems2.size();
    }

    class ProfileViewHolder extends RecyclerView.ViewHolder{

        ImageView profileImageView;

        ProfileViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImageView = itemView.findViewById(R.id.profileImage);
        }

        void setProfileImageView(Post profileItem){

        }
    }
}
