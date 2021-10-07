package com.gvm.diy.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gvm.diy.MyBounceInterpolator;
import com.gvm.diy.R;
import com.gvm.diy.models.Post;
import com.gvm.diy.ui.FollowersActivity;
import com.gvm.diy.ui.PostViewerActivity;
import com.gvm.diy.ui.ProfileViewerActivity;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ImageViewHolder>{

    Context mContext;
    private List<Post> mPosts;
    private String access_token, server_key = "1539874186", post_id, user_id, web, place;
    private String is_liked, is_saved, likes;
    String likess;
    public PostListener postListener;


    OkHttpClient client;
    RequestBody body;
    Request request;

    int Numberlikes;


    public PostAdapter(Context mContext, List<Post> mPosts, String accessToken, String place) {
        this.mContext = mContext;
        this.mPosts = mPosts;
        this.access_token = accessToken;
        this.place = place;
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
        holder.setIsRecyclable(false);

        web = post.getWebsite();

        is_liked = post.getIs_liked();
        is_saved = post.getIs_saved();
        post_id = post.getPost_id();
        user_id = post.getUser_id();
        likes = post.getLikes();

        if(likes.equals("null")) {
            likes = "0";
        }

        try{
            Glide.with(mContext).load("https://diys.co/"+post.getFile())
                    .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                    .into(holder.post_image);
            
            Glide.with(mContext).load(post.getAvatar())
                    .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                    .into(holder.user_profile_image);

        }catch (Exception e){
            e.printStackTrace();
        }

        JSONArray commentsArray = null;
        try {
            commentsArray = new JSONArray(post.getComments());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.description.setText(post.getDescription());
        holder.username.setText(post.getUsername());
        holder.like.setText(post.getLikes()+" likes");
        holder.comment.setText(commentsArray.length()+" comments");
        holder.time.setText("Hace "+post.getTime_text().split(" ")[0]+" días");

        ClipboardManager clipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);

        if (is_liked.equals("true"))
            holder.imageViewLike.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_favorite_red));
        if (is_saved.equals("true"))
            holder.imageViewFav.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_star_yellow));

        //Cargamos la animcion del boton
        final Animation myAnim = AnimationUtils.loadAnimation(mContext,R.anim.bounce);

        //Usamos el BounceInterpolator con una amplitud de 0.2 y frecuencia de 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2,20);
        myAnim.setInterpolator(interpolator);

        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //postListener.postImageOnClick(v,position);
                Intent intentPostViewer = new Intent(mContext, PostViewerActivity.class);
                intentPostViewer.putExtra("access_token", access_token);
                intentPostViewer.putExtra("is_liked", is_liked);
                intentPostViewer.putExtra("is_saved", is_saved);
                intentPostViewer.putExtra("post_id", post.getPost_id());
                intentPostViewer.putExtra("user_id", user_id);
                intentPostViewer.putExtra("username", post.getUsername());
                intentPostViewer.putExtra("avatar", post.getAvatar());
                intentPostViewer.putExtra("post_image", "https://diys.co/"+post.getFile());
                intentPostViewer.putExtra("description", post.getDescription());
                intentPostViewer.putExtra("comment", post.getComments());
                intentPostViewer.putExtra("likes", likes);
                intentPostViewer.putExtra("web", web);
                intentPostViewer.putExtra("name", post.getName());
                intentPostViewer.putExtra("following", post.getFollowing());
                intentPostViewer.putExtra("followers", post.getFollowers());
                intentPostViewer.putExtra("favourites", post.getFavourites());
                intentPostViewer.putExtra("about", post.getAbout());
                intentPostViewer.putExtra("isFollowing", post.getIsFollowing());
                mContext.startActivity(intentPostViewer);
            }
        });

        holder.imageViewComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentComments = new Intent(mContext, FollowersActivity.class);
                intentComments.putExtra("function", "comments");
                intentComments.putExtra("post_id", post.getPost_id());
                intentComments.putExtra("access_token", access_token);
                intentComments.putExtra("user_id", user_id);
                mContext.startActivity(intentComments);
            }
        });

        //TODO: Guardar número de likes para ver si de esa manera se guarda la variable
        holder.imageViewLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.imageViewLike.startAnimation(myAnim);
                if(is_liked.equals("true")){
                    is_liked = "false";
                    holder.imageViewLike.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_favorite_border_24));
                    int numLike =- Integer.parseInt(likes);
                    Log.e("failure Response", String.valueOf(numLike));
                    likes = String.valueOf(numLike);
                    holder.like.setText(likes+" likes");
                }
                else{
                    is_liked = "true";
                    holder.imageViewLike.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_favorite_red));
                    int numLike =+ Integer.parseInt(likes);
                    Log.e("failure Response", String.valueOf(numLike));
                    likes = String.valueOf(numLike);
                    holder.like.setText(likes+" likes");
                }

                OkHttpClient LikeClient = new OkHttpClient.Builder().build();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("server_key",server_key)
                        .addFormDataPart("post_id",post.getPost_id())
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

                                holder.imageViewLike.startAnimation(myAnim);
                                if(is_liked.equals("true")){
                                    is_liked = "false";
                                    holder.imageViewLike.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_favorite_border_24));
                                    int numLike =- Integer.parseInt(likes);
                                    likes = String.valueOf(numLike);
                                    holder.like.setText(likes+" likes");
                                }
                                else{
                                    is_liked = "true";
                                    holder.imageViewLike.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_favorite_red));
                                    int numLike =+ Integer.parseInt(likes);
                                    likes = String.valueOf(numLike);
                                    holder.like.setText(likes+" likes");
                                }
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

        holder.imageViewFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Probar

                holder.imageViewFav.startAnimation(myAnim);
                if (is_saved.equals("true")) {
                    is_saved = "false";
                    holder.imageViewFav.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_star_outline_24));
                } else {
                    is_saved = "true";
                    holder.imageViewFav.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_star_yellow));
                }

                OkHttpClient client = new OkHttpClient.Builder().build();
                body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("server_key", server_key)
                        .addFormDataPart("post_id", post.getPost_id())
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
                                holder.imageViewFav.startAnimation(myAnim);
                                if (is_saved.equals("true")) {
                                    is_saved = "false";
                                    holder.imageViewFav.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_star_outline_24));
                                } else {
                                    is_saved = "true";
                                    holder.imageViewFav.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_star_yellow));
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



        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
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
                                intentPostViewer.putExtra("post_id", post.getPost_id());
                                intentPostViewer.putExtra("user_id", user_id);
                                intentPostViewer.putExtra("username", post.getUsername());
                                intentPostViewer.putExtra("avatar", post.getAvatar());
                                intentPostViewer.putExtra("post_image", "https://diys.co/"+post.getFile());
                                intentPostViewer.putExtra("description", post.getDescription());
                                intentPostViewer.putExtra("comment", post.getComments());
                                intentPostViewer.putExtra("likes", likes);
                                intentPostViewer.putExtra("web", web);
                                intentPostViewer.putExtra("name", post.getName());
                                intentPostViewer.putExtra("following", post.getFollowing());
                                intentPostViewer.putExtra("followers", post.getFollowers());
                                intentPostViewer.putExtra("favourites", post.getFavourites());
                                intentPostViewer.putExtra("about", post.getAbout());
                                intentPostViewer.putExtra("isFollowing", post.getIsFollowing());
                                mContext.startActivity(intentPostViewer);
                                break;
                            case 1:
                                //TODO: Probar cuando pueda debuggear con varios users
                                OkHttpClient client = new OkHttpClient.Builder().build();
                                body = new MultipartBody.Builder()
                                        .setType(MultipartBody.FORM)
                                        .addFormDataPart("server_key", server_key)
                                        .addFormDataPart("post_id", post.getPost_id())
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
                                        Toast.makeText(mContext, "Error de red: " + mMessage, Toast.LENGTH_LONG).show();
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
                                ClipData clip = ClipData.newPlainText("ir al post","https://diys.co//post/"+post.getPost_id());

                                Toast.makeText(mContext, "Texto copiado en el portapapeles"+"https://diys.co//post/"+post.getPost_id(), Toast.LENGTH_SHORT).show();
                                clipboardManager.setPrimaryClip(clip);
                                break;
                            default:
                                break;
                        }
                    }
                });


        holder.imageButtonMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.show();
            }
        });

            holder.imageViewReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                String message = post.getDescription()+"https://diys.co//post/"+post_id;
                share.putExtra(Intent.EXTRA_SUBJECT,"App");
                share.putExtra(Intent.EXTRA_TEXT,message);
                mContext.startActivity(Intent.createChooser(share,"Compartir vía"));

                }
            });

        holder.username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentProfileViewer = new Intent(mContext, ProfileViewerActivity.class);
                    intentProfileViewer.putExtra("access_token", access_token);
                    intentProfileViewer.putExtra("user_id", user_id);
                    intentProfileViewer.putExtra("web", web);
                    intentProfileViewer.putExtra("username", post.getUsername());
                    intentProfileViewer.putExtra("avatar", post.getAvatar());
                    intentProfileViewer.putExtra("name", post.getName());
                    intentProfileViewer.putExtra("following", post.getFollowing());
                    intentProfileViewer.putExtra("followers", post.getFollowers());
                    intentProfileViewer.putExtra("favourites", post.getFavourites());
                    intentProfileViewer.putExtra("about", post.getAbout());
                    intentProfileViewer.putExtra("isFollowing", post.getIsFollowing());

                    mContext.startActivity(intentProfileViewer);
                }
            });
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView  post_image;
        TextView time, username, like, comment;
        ImageButton imageButtonMore;
        ReadMoreTextView description;
        ImageView imageViewLike, imageViewReply, imageViewComment, imageViewFav;
        RoundedImageView user_profile_image;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            user_profile_image = itemView.findViewById(R.id.user_profile_image);
            post_image = itemView.findViewById(R.id.post_image);
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


            imageViewReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                String message = description+"https://diys.co//post/"+post_id;
                share.putExtra(Intent.EXTRA_SUBJECT,"App");
                share.putExtra(Intent.EXTRA_TEXT,message);
                mContext.startActivity(Intent.createChooser(share,"Compartir vía"));

                }
            });

            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: Probar
                    Intent intentLikes = new Intent(mContext, FollowersActivity.class);
                    intentLikes.putExtra("access_token", access_token);
                    intentLikes.putExtra("function", "likes");
                    intentLikes.putExtra("post_id", post_id);
                    mContext.startActivity(intentLikes);
                }
            });

            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentComments = new Intent(mContext, FollowersActivity.class);
                    intentComments.putExtra("function", "comments");
                    intentComments.putExtra("post_id", post_id);
                    intentComments.putExtra("user_id", user_id);
                    intentComments.putExtra("access_token", access_token);
                    mContext.startActivity(intentComments);
                }
            });

        }
    }

    public interface PostListener{
        void postImageOnClick(View v, int position);
    }
}
