package com.gvm.diy.adapter;

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
    private String is_liked, is_saved;
    public PostListener postListener;

    AlertDialog.Builder builder;

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

        holder.description.setText(post.getDescription());
        holder.username.setText(post.getUsername());
        holder.like.setText(post.getLikes()+" likes");
        holder.comment.setText(post.getComments()+" comments");

        ClipboardManager clipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);

        builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Post")
                .setItems(new String[]{"Reportar Post", "Copiar"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                body = new MultipartBody.Builder()
                                        .setType(MultipartBody.FORM)
                                        .addFormDataPart("server_key", server_key)
                                        .addFormDataPart("post_id", post_id)
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
                            case 1:
                                ClipData clip = ClipData.newPlainText("description",post.getDescription());

                                Toast.makeText(mContext, "Texto copiado en el portapapeles", Toast.LENGTH_SHORT).show();
                                clipboardManager.setPrimaryClip(clip);
                                break;
                            default:
                                break;
                        }
                    }
                });

        web = post.getWebsite();

        is_liked = post.getIs_liked();
        is_saved = post.getIs_saved();
        post_id = post.getPost_id();
        user_id = post.getUser_id();

        if (is_liked.equals("1"))
            holder.imageViewLike.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_favorite_red));
        if (is_saved.equals("1"))
            holder.imageViewFav.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_star_yellow));

        Numberlikes = Integer.parseInt(post.getLikes());

        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //postListener.postImageOnClick(v,position);
                Intent intentPostViewer = new Intent(mContext, PostViewerActivity.class);
                intentPostViewer.putExtra("access_token", access_token);
                intentPostViewer.putExtra("is_liked", is_liked);
                intentPostViewer.putExtra("is_saved", is_saved);
                intentPostViewer.putExtra("post_id", post_id);
                intentPostViewer.putExtra("user_id", user_id);
                intentPostViewer.putExtra("username", post.getUsername());
                intentPostViewer.putExtra("avatar", post.getAvatar());
                intentPostViewer.putExtra("post_image", "https://diys.co/"+post.getFile());
                intentPostViewer.putExtra("description", post.getDescription());
                intentPostViewer.putExtra("comment", post.getComments());
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
                intentComments.putExtra("post_id", post_id);
                intentComments.putExtra("access_token", access_token);
                mContext.startActivity(intentComments);
            }
        });

        //TODO: Guardar número de likes para ver si de esa manera se guarda la variable
        holder.imageViewLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cargamos la animcion del boton
                final Animation myAnim = AnimationUtils.loadAnimation(mContext,R.anim.bounce);

                //Usamos el BounceInterpolator con una amplitud de 0.2 y frecuencia de 20
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2,20);
                myAnim.setInterpolator(interpolator);

                holder.imageViewLike.startAnimation(myAnim);
                if(is_liked.equals("1")){
                    is_liked = "0";
                    holder.imageViewLike.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_favorite_border_24));
                }
                else{
                    is_liked = "1";
                    holder.imageViewLike.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_favorite_red));
                }

                OkHttpClient imageUploadClient = new OkHttpClient.Builder().build();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("server_key",server_key)
                        .addFormDataPart("post_id",post_id)
                        .addFormDataPart("access_token",access_token)
                        .build();
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url("https://diys.co/endpoints/v1/post/like_post")
                        .post(requestBody)
                        .build();


                imageUploadClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        String mMessage = e.getMessage().toString();
                        //Toast.makeText(ChatScreen.this, "Error uploading file", Toast.LENGTH_LONG).show();
                        Log.e("failure Response", mMessage);
                    }

                    @Override
                    public void onResponse(Call call, okhttp3.Response response) throws IOException {
                        final String mMessage = response.body().string();
                        Log.e("Like Response", mMessage);

                    }
                });
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
        ImageView imageViewLike, imageViewComment, imageViewFav;
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


            imageButtonMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.show();
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

            imageViewFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: Probar

                    body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("server_key", server_key)
                            .addFormDataPart("post_id", post_id)
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
                            Toast.makeText(mContext, "Error de red: " + mMessage, Toast.LENGTH_LONG).show();
                            Log.e("failure Response", mMessage);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String mMessage = response.body().string();
                            Log.e("fav Response", mMessage);
                            JSONObject array = null;
                            //imageViewFav.startAnimation(myAnim);
                            if (is_saved.equals("1")) {
                                is_saved = "0";
                                imageViewLike.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_star_outline_24));
                            } else {
                                is_saved = "1";
                                imageViewLike.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_star_yellow));
                            }
                        }
                    });
                }
            });

            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentComments = new Intent(mContext, FollowersActivity.class);
                    intentComments.putExtra("function", "comments");
                    intentComments.putExtra("post_id", post_id);
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
