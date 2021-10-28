package com.gvm.diy.adapter;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.gvm.diy.MyBounceInterpolator;
import com.gvm.diy.R;
import com.gvm.diy.models.MediaObject;
import com.gvm.diy.ui.FollowersActivity;
import com.gvm.diy.ui.PostViewerActivity;
import com.gvm.diy.ui.ProfileViewerActivity;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VideoPlayerViewHolder extends RecyclerView.ViewHolder {

    FrameLayout media_container;
    ImageView thumbnail, volumeControl;
    ProgressBar progressBar;
    View parent;
    RequestManager requestManager;
    TextView time, username, like, comment;
    ImageButton imageButtonMore;
    ReadMoreTextView description;
    ImageView imageViewLike, imageViewReply, imageViewComment, imageViewFav;
    RoundedImageView user_profile_image;
    private String access_token, server_key = "1539874186", post_id, user_id, web,current_user;
    private String is_liked, is_saved, likes;
    RequestBody body;
    Request request;

    public VideoPlayerViewHolder(@NonNull View itemView) {
        super(itemView);
        parent = itemView;
        media_container = itemView.findViewById(R.id.media_container);
        thumbnail = itemView.findViewById(R.id.post_image);
        progressBar = itemView.findViewById(R.id.progressBar);
        volumeControl = itemView.findViewById(R.id.volume_control);

        user_profile_image = itemView.findViewById(R.id.user_profile_image);
        like = itemView.findViewById(R.id.likes);
        comment = itemView.findViewById(R.id.comments);
        username = itemView.findViewById(R.id.username);
        description = itemView.findViewById(R.id.description);
        imageButtonMore = itemView.findViewById(R.id.more);
        imageViewLike = itemView.findViewById(R.id.like);
        imageViewComment = itemView.findViewById(R.id.comment);
        imageViewFav = itemView.findViewById(R.id.imageViewFav);
        time = itemView.findViewById(R.id.publisher_date);

        imageViewReply = itemView.findViewById(R.id.imageViewReply);

    }

    public void onBind(MediaObject mediaObject, RequestManager requestManager, Context mContext, String access_token) {
        this.requestManager = requestManager;
        parent.setTag(this);
        this.requestManager
                .load("https://diys.co/"+mediaObject.getFile())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                .into(thumbnail);
        this.requestManager
                .load(mediaObject.getAvatar())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                .into(user_profile_image);


        is_liked = mediaObject.getIs_liked();
        is_saved = mediaObject.getIs_saved();
        post_id = mediaObject.getPost_id();
        user_id = mediaObject.getUser_id();
        likes = mediaObject.getLikes();
        if(likes.equals("null")) {
            likes = "0";
        }

        JSONArray commentsArray = null;
        try {
            commentsArray = new JSONArray(mediaObject.getComments());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        description.setText(mediaObject.getDescription());
        username.setText(mediaObject.getUsername());
        like.setText(mediaObject.getLikes()+" likes");
        comment.setText(commentsArray.length()+" comments");
        time.setText("Hace "+mediaObject.getTime_text().split(" ")[0]+" días");

        ClipboardManager clipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);

        if (is_liked.equals("true"))
            imageViewLike.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_favorite_red));
        if (is_saved.equals("true"))
            imageViewFav.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_star_yellow));

        //Cargamos la animcion del boton
        final Animation myAnim = AnimationUtils.loadAnimation(mContext,R.anim.bounce);

        //Usamos el BounceInterpolator con una amplitud de 0.2 y frecuencia de 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2,20);
        myAnim.setInterpolator(interpolator);

        thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
            }
        });

        imageViewComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentComments = new Intent(mContext, FollowersActivity.class);
                intentComments.putExtra("function", "comments");
                intentComments.putExtra("post_id", mediaObject.getPost_id());
                intentComments.putExtra("access_token", access_token);
                intentComments.putExtra("user_id", user_id);
                mContext.startActivity(intentComments);
            }
        });

        imageViewLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int numLike = Integer.parseInt(likes);
                Log.e("numLike", String.valueOf(numLike)+is_liked);
                imageViewLike.startAnimation(myAnim);
                if(is_liked.equals("true")){
                    is_liked = "false";
                    imageViewLike.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_favorite_border_24));
                    likes = String.valueOf(numLike-1);
                }
                else{
                    is_liked = "true";
                    imageViewLike.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_favorite_red));
                    likes = String.valueOf(numLike+1);
                }
                like.setText(likes+" likes");

                OkHttpClient LikeClient = new OkHttpClient.Builder().build();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("server_key",server_key)
                        .addFormDataPart("post_id",mediaObject.getPost_id())
                        .addFormDataPart("access_token",access_token)
                        .build();
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url("https://diys.co/endpoints/v1/post/like_post")
                        .post(requestBody)
                        .build();


                LikeClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        String mMessage = e.getMessage().toString();
                        ((Activity)mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                imageViewLike.startAnimation(myAnim);
                                int numLike = Integer.parseInt(likes);
                                if(is_liked.equals("true")){
                                    is_liked = "false";
                                    imageViewLike.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_favorite_border_24));
                                    Log.e("numLike", String.valueOf(numLike-1));
                                    likes = String.valueOf(numLike-1);
                                }
                                else{
                                    is_liked = "true";
                                    imageViewLike.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_favorite_red));
                                    Log.e("numLike", String.valueOf(numLike+1));
                                    likes = String.valueOf(numLike+1);
                                }
                                like.setText(likes+" likes");
                                Toast.makeText(mContext, "Revisa tu conexión e inténtalo de nuevo: "+mMessage, Toast.LENGTH_LONG).show();
                            }
                        });

                        Log.e("failure Response", mMessage);
                    }

                    @Override
                    public void onResponse(Call call, okhttp3.Response response) throws IOException {
                        final String mMessage = response.body().string();
                        Log.e("Like Response", mMessage);
                        ((Activity)mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });

                    }
                });
            }
        });

        imageViewFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewFav.startAnimation(myAnim);
                if (is_saved.equals("true")) {
                    is_saved = "false";
                    imageViewFav.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_star_outline_24));
                } else {
                    is_saved = "true";
                    imageViewFav.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_star_yellow));
                }

                OkHttpClient client = new OkHttpClient.Builder().build();
                body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("server_key", server_key)
                        .addFormDataPart("post_id", mediaObject.getPost_id())
                        .addFormDataPart("access_token", access_token)
                        .build();

                request = new Request.Builder()
                        .url("https://diys.co/endpoints/v1/post/add_to_favorite")
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        String mMessage = e.getMessage().toString();
                        ((Activity)mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageViewFav.startAnimation(myAnim);
                                if (is_saved.equals("true")) {
                                    is_saved = "false";
                                    imageViewFav.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_star_outline_24));
                                } else {
                                    is_saved = "true";
                                    imageViewFav.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_star_yellow));
                                }
                                Toast.makeText(mContext, "Error de red: " + mMessage, Toast.LENGTH_LONG).show();
                            }
                        });
                        Log.e("failure Response", mMessage);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String mMessage = response.body().string();
                        Log.e("fav Response", mMessage);
                        JSONObject array = null;
                    }
                });
            }
        });

        imageViewReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                String message = mediaObject.getDescription()+"https://diys.co//post/"+post_id;
                share.putExtra(Intent.EXTRA_SUBJECT,"App");
                share.putExtra(Intent.EXTRA_TEXT,message);
                mContext.startActivity(Intent.createChooser(share,"Compartir vía"));

            }
        });

        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentProfileViewer = new Intent(mContext, ProfileViewerActivity.class);
                intentProfileViewer.putExtra("access_token", access_token);
                intentProfileViewer.putExtra("user_id", user_id);
                intentProfileViewer.putExtra("web", web);
                intentProfileViewer.putExtra("username", mediaObject.getUsername());
                intentProfileViewer.putExtra("avatar", mediaObject.getAvatar());
                intentProfileViewer.putExtra("name", mediaObject.getName());
                intentProfileViewer.putExtra("following", mediaObject.getFollowing());
                intentProfileViewer.putExtra("followers", mediaObject.getFollowers());
                intentProfileViewer.putExtra("favourites", mediaObject.getFavourites());
                intentProfileViewer.putExtra("about", mediaObject.getAbout());
                intentProfileViewer.putExtra("isFollowing", mediaObject.getIsFollowing());

                mContext.startActivity(intentProfileViewer);
            }
        });

        imageViewReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                String message = description+"https://diys.co//post/"+mediaObject.getPost_id();
                share.putExtra(Intent.EXTRA_SUBJECT,"App");
                share.putExtra(Intent.EXTRA_TEXT,message);
                mContext.startActivity(Intent.createChooser(share,"Compartir vía"));

            }
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLikes = new Intent(mContext, FollowersActivity.class);
                intentLikes.putExtra("access_token", access_token);
                intentLikes.putExtra("function", "likes");
                intentLikes.putExtra("post_id", mediaObject.getPost_id());
                mContext.startActivity(intentLikes);
            }
        });

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentComments = new Intent(mContext, FollowersActivity.class);
                intentComments.putExtra("function", "comments");
                intentComments.putExtra("post_id", mediaObject.getPost_id());
                intentComments.putExtra("user_id", mediaObject.getUser_id());
                intentComments.putExtra("access_token", access_token);
                mContext.startActivity(intentComments);
            }
        });
    }


}








