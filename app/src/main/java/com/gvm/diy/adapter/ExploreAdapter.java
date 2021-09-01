package com.gvm.diy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gvm.diy.models.ExploreItem;
import com.gvm.diy.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.ExploreViewHolder>{

    private Context mContext;
    private List<ExploreItem> exploreItems;

    public ExploreAdapter(Context mContext, List<ExploreItem> exploreItems) {
        this.mContext = mContext;
        this.exploreItems = exploreItems;
    }

    public ExploreAdapter(List<ExploreItem> exploreItems) {
        this.exploreItems = exploreItems;
    }

    public ExploreAdapter() {
    }

    @NonNull
    @Override
    public ExploreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExploreViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.explore_item,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ExploreViewHolder holder, int position) {
        holder.setExploreImageView(exploreItems.get(position));
    }

    @Override
    public int getItemCount() {
        return exploreItems.size();
    }

    class ExploreViewHolder extends RecyclerView.ViewHolder{

        RoundedImageView exploreImageView;

        ExploreViewHolder(@NonNull View itemView) {
            super(itemView);

            exploreImageView = itemView.findViewById(R.id.exploreImage);
        }

        void setExploreImageView(ExploreItem exploreItem){

            try{
                Glide.with(mContext).load("https://diys.co/"+exploreItem.getImage())
                        .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                        .into(exploreImageView);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
