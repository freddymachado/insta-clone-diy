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
import com.gvm.diy.models.FollowItem;
import com.gvm.diy.models.Post;
import com.gvm.diy.models.ProfileItem;

import java.util.List;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.FollowViewHolder>{

    private Context mContext;
    private List<FollowItem> followItems;

    public FollowAdapter(Context mContext, List<FollowItem> followItems) {
        this.mContext = mContext;
        this.followItems = followItems;
    }

    public FollowAdapter(List<FollowItem> followItems) {
        this.followItems = followItems;
    }

    public FollowAdapter() {
    }

    @NonNull
    @Override
    public FollowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FollowViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.follow_item,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull FollowViewHolder holder, int position) {
        holder.setProfileImageView(followItems.get(position));
        FollowItem followItem = followItems.get(position);
        holder.textViewName.setText(followItem.getUsername());
        holder.textViewLastSeen.setText("Última visita hace "+followItem.getTime_text().split(" ")[0]+" días");
    }

    @Override
    public int getItemCount() {
        return followItems.size();
    }

    class FollowViewHolder extends RecyclerView.ViewHolder{

        ImageView imageViewavatar;
        TextView textViewName, textViewLastSeen;

        FollowViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewavatar = itemView.findViewById(R.id.imageViewAvatar);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewLastSeen = itemView.findViewById(R.id.textViewLastSeen);
        }

        void setProfileImageView(FollowItem followItem){

            try{
                Glide.with(mContext).load(followItem.getAvatar())
                        .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                        .into(imageViewavatar);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
