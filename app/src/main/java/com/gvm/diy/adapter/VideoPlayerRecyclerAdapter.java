package com.gvm.diy.adapter;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.gvm.diy.R;
import com.gvm.diy.models.MediaObject;
import com.gvm.diy.ui.PostViewerActivity;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VideoPlayerRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;
    private ArrayList<MediaObject> mediaObjects;
    private RequestManager requestManager;
    private String access_token, current_user;


    public VideoPlayerRecyclerAdapter(ArrayList<MediaObject> mediaObjects, RequestManager requestManager, Context mContext, String access_token, String current_user) {
        this.mediaObjects = mediaObjects;
        this.requestManager = requestManager;
        this.mContext = mContext;
        this.access_token = access_token;
        this.current_user = current_user;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new VideoPlayerViewHolder(
                LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_video_list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((VideoPlayerViewHolder)viewHolder).onBind(mediaObjects.get(i), requestManager,mContext,access_token,current_user);
    }

    @Override
    public int getItemCount() {
        return mediaObjects.size();
    }

    public void removeItem(int position){
        mediaObjects.remove(position);
        notifyItemRemoved(position);
    }

}






