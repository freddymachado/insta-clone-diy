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
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        /*
        if(current_user.equals(mediaObjects.get(i).getUser_id())){
            builder.setTitle("Post");
            builder.setItems(new String[]{"Borrar Post", "Editar Post", "Ir al Post", "Reportar Post", "Copiar"}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            OkHttpClient client = new OkHttpClient.Builder().build();
                            body = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("server_key", server_key)
                                    .addFormDataPart("post_id", mediaObjects.get(i).getPost_id())
                                    .addFormDataPart("access_token", access_token)
                                    .build();

                            request = new Request.Builder()
                                    .url("https://diys.co/endpoints/v1/post/delete_post")
                                    .post(body)
                                    .build();

                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    String mMessage = e.getMessage().toString();
                                    ((Activity) mContext).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(mContext, "Error de red: " + mMessage, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    Log.e("failure Response", mMessage);
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    final String mMessage = response.body().string();
                                    Log.e("Like Response", mMessage);
                                    ((Activity) mContext).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //VideoPlayerRecyclerAdapter.removeItem(getAdapterPosition());
                                        }
                                    });
                                }
                            });
                            break;
                        case 1:
                            //TODO:Diseño pantalla editPost (puede ser la misma FollowActivity)
                            break;
                        case 2:
                            //postListener.postImageOnClick(v,position);
                            Intent intentPostViewer = new Intent(mContext, PostViewerActivity.class);
                            intentPostViewer.putExtra("access_token", access_token);
                            intentPostViewer.putExtra("is_liked", is_liked);
                            intentPostViewer.putExtra("is_saved", is_saved);
                            intentPostViewer.putExtra("post_id", mediaObject.getPost_id());
                            intentPostViewer.putExtra("user_id", user_id);
                            intentPostViewer.putExtra("username", mediaObject.getUsername());
                            intentPostViewer.putExtra("avatar", mediaObject.getAvatar());
                            intentPostViewer.putExtra("post_image", "https://diys.co/" + mediaObject.getFile());
                            intentPostViewer.putExtra("description", mediaObject.getDescription());
                            intentPostViewer.putExtra("comment", mediaObject.getComments());
                            intentPostViewer.putExtra("likes", likes);
                            intentPostViewer.putExtra("web", web);
                            intentPostViewer.putExtra("name", mediaObject.getName());
                            intentPostViewer.putExtra("following", mediaObject.getFollowing());
                            intentPostViewer.putExtra("followers", mediaObject.getFollowers());
                            intentPostViewer.putExtra("favourites", mediaObject.getFavourites());
                            intentPostViewer.putExtra("about", mediaObject.getAbout());
                            intentPostViewer.putExtra("isFollowing", mediaObject.getIsFollowing());
                            mContext.startActivity(intentPostViewer);
                            break;
                        case 3:
                            //TODO: Probar cuando pueda debuggear con varios users
                            client = new OkHttpClient.Builder().build();
                            body = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("server_key", server_key)
                                    .addFormDataPart("post_id", mediaObject.getPost_id())
                                    .addFormDataPart("access_token", access_token)
                                    .build();

                            request = new Request.Builder()
                                    .url("https://diys.co/endpoints/v1/post/report_post")
                                    .post(body)
                                    .build();

                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    String mMessage = e.getMessage().toString();
                                    ((Activity) mContext).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(mContext, "Error de red: " + mMessage, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    Log.e("failure Response", mMessage);
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    final String mMessage = response.body().string();
                                    Log.e("Like Response", mMessage);
                                }
                            });
                            break;
                        case 4:
                            ClipData clip = ClipData.newPlainText("ir al post", "https://diys.co//post/" + mediaObject.getPost_id());

                            Toast.makeText(mContext, "Texto copiado en el portapapeles" + "https://diys.co//post/" + mediaObject.getPost_id(), Toast.LENGTH_SHORT).show();
                            clipboardManager.setPrimaryClip(clip);
                            break;
                        default:
                            break;
                    }
                }
            });

        }else{
            builder.setTitle("Post")
                    .setItems(new String[]{"Ir al Post","Reportar Post", "Copiar"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case 0:
                                    //postListener.postImageOnClick(v,position);
                                    Intent intentPostViewer = new Intent(mContext, PostViewerActivity.class);
                                    intentPostViewer.putExtra("access_token", access_token);
                                    intentPostViewer.putExtra("is_liked", is_liked);
                                    intentPostViewer.putExtra("is_saved", is_saved);
                                    intentPostViewer.putExtra("post_id", mediaObject.getPost_id());
                                    intentPostViewer.putExtra("user_id", user_id);
                                    intentPostViewer.putExtra("username", mediaObject.getUsername());
                                    intentPostViewer.putExtra("avatar", mediaObject.getAvatar());
                                    intentPostViewer.putExtra("post_image", "https://diys.co/"+mediaObject.getFile());
                                    intentPostViewer.putExtra("description", mediaObject.getDescription());
                                    intentPostViewer.putExtra("comment", mediaObject.getComments());
                                    intentPostViewer.putExtra("likes", likes);
                                    intentPostViewer.putExtra("web", web);
                                    intentPostViewer.putExtra("name", mediaObject.getName());
                                    intentPostViewer.putExtra("following", mediaObject.getFollowing());
                                    intentPostViewer.putExtra("followers", mediaObject.getFollowers());
                                    intentPostViewer.putExtra("favourites", mediaObject.getFavourites());
                                    intentPostViewer.putExtra("about", mediaObject.getAbout());
                                    intentPostViewer.putExtra("isFollowing", mediaObject.getIsFollowing());
                                    mContext.startActivity(intentPostViewer);
                                    break;
                                case 1:
                                    //TODO: Probar cuando pueda debuggear con varios users
                                    OkHttpClient client = new OkHttpClient.Builder().build();
                                    body = new MultipartBody.Builder()
                                            .setType(MultipartBody.FORM)
                                            .addFormDataPart("server_key", server_key)
                                            .addFormDataPart("post_id", mediaObject.getPost_id())
                                            .addFormDataPart("access_token", access_token)
                                            .build();

                                    request = new Request.Builder()
                                            .url("https://diys.co/endpoints/v1/post/report_post")
                                            .post(body)
                                            .build();

                                    client.newCall(request).enqueue(new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            String mMessage = e.getMessage().toString();
                                            ((Activity)mContext).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(mContext, "Error de red: " + mMessage, Toast.LENGTH_LONG).show();
                                                }
                                            });
                                            Log.e("failure Response", mMessage);
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            final String mMessage = response.body().string();
                                            Log.e("Like Response", mMessage);
                                        }
                                    });
                                    break;
                                case 2:
                                    ClipData clip = ClipData.newPlainText("ir al post","https://diys.co//post/"+mediaObject.getPost_id());

                                    Toast.makeText(mContext, "Texto copiado en el portapapeles"+"https://diys.co//post/"+mediaObject.getPost_id(), Toast.LENGTH_SHORT).show();
                                    clipboardManager.setPrimaryClip(clip);
                                    break;
                                default:
                                    break;
                            }
                        }
                    });


        }
        viewHolder.imageButtonMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edmt.dev.videoplayer.VideoPlayerViewHolder.builder.show();
            }
        });*/
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






