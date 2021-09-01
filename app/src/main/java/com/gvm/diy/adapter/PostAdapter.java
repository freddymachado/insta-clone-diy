package com.gvm.diy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gvm.diy.R;
import com.gvm.diy.models.Post;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ImageViewHolder>{

    private Context mContext;
    private List<Post> mPosts;

    public PostAdapter(Context context, List<Post> postList) {
        mContext = context;
        mPosts = postList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item,parent,false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        Post post = mPosts.get(position);

        try{
            Glide.with(mContext).load("https://diys.co/"+post.getFile())
                    .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                    .into(holder.post_image);
            Glide.with(mContext).load("https://diys.co/"+post.getAvatar())
                    .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                    .into(holder.user_profile_image);
        }catch (Exception e){
            e.printStackTrace();
        }

        holder.description.setText(post.getDescription());
        holder.username.setText(post.getUsername());
        holder.like.setText(post.getLikes()+" likes");
        holder.comment.setText(post.getComments()+" comments");

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        ImageView user_profile_image, post_image;
        TextView time, username, description, like, comment;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            user_profile_image = itemView.findViewById(R.id.user_profile_image);
            post_image = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.likes);
            comment = itemView.findViewById(R.id.comments);
            username = itemView.findViewById(R.id.username);
            description = itemView.findViewById(R.id.description);
        }
    }
}
