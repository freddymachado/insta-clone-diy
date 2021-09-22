package com.gvm.diy.adapter;

import android.app.AlertDialog;
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
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gvm.diy.MyBounceInterpolator;
import com.gvm.diy.R;
import com.gvm.diy.fragments.HomeFragment;
import com.gvm.diy.models.Post;
import com.gvm.diy.ui.FollowersActivity;
import com.gvm.diy.ui.MainActivity;
import com.gvm.diy.ui.PostViewerActivity;
import com.gvm.diy.ui.ProfileViewerActivity;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ImageViewHolder>{

    private Context mContext;
    private List<Post> mPosts;
    private String access_token, server_key = "1539874186", post_id, user_id;
    private Boolean is_liked, is_saved;

    AlertDialog.Builder builder;

    OkHttpClient client;
    RequestBody body;
    Request request;


    public PostAdapter(Context mContext, List<Post> mPosts, String access_token) {
        this.mContext = mContext;
        this.mPosts = mPosts;
        this.access_token = access_token;
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

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView user_profile_image, post_image;
        TextView time, username, description, like, comment;
        ImageButton imageButtonMore;
        ImageView imageViewLike, imageViewComment;

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

            is_liked = mPosts.get(getAdapterPosition()).getIs_liked();
            is_saved = mPosts.get(getAdapterPosition()).getIs_saved();
            post_id = mPosts.get(getAdapterPosition()).getPost_id();
            user_id = mPosts.get(getAdapterPosition()).getPost_id();

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
                                    //TODO: Verificar comportamiento
                                    break;
                                default:
                                    break;
                            }
                        }
                    });

            post_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intentPostViewer = new Intent(mContext, PostViewerActivity.class);
                    intentPostViewer.putExtra("access_token", access_token);
                    intentPostViewer.putExtra("is_liked", is_liked);
                    intentPostViewer.putExtra("is_saved", is_saved);
                    intentPostViewer.putExtra("post_id", post_id);
                    intentPostViewer.putExtra("user_id", user_id);
                    mContext.startActivity(intentPostViewer);
                }
            });

            username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: Probar
                    Intent intentProfileViewer = new Intent(mContext, ProfileViewerActivity.class);
                    intentProfileViewer.putExtra("access_token", access_token);
                    intentProfileViewer.putExtra("user_id", user_id);
                    mContext.startActivity(intentProfileViewer);
                }
            });

            imageButtonMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: Probar MoreDialog
                    builder.show();
                    Toast.makeText(mContext, "more", Toast.LENGTH_SHORT).show();
                }
            });

            //TODO: Verificar respuesta para ver cómo darle color al botón
            imageViewLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Cargamos la animcion del boton
                    final Animation myAnim = AnimationUtils.loadAnimation(mContext,R.anim.bounce);

                    //Usamos el BounceInterpolator con una amplitud de 0.2 y frecuencia de 20
                    MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2,20);
                    myAnim.setInterpolator(interpolator);

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
                            imageViewLike.startAnimation(myAnim);
                            if(is_liked){
                                is_liked = false;
                                imageViewLike.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_favorite_border_24));
                            }
                            else{
                                is_liked = true;
                                imageViewLike.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_favorite_red));
                            }

                        }
                    });
                }
            });

            imageViewComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: Probar
                    Intent intentComments = new Intent(mContext, FollowersActivity.class);
                    intentComments.putExtra("function", "comments");
                    intentComments.putExtra("post_id", post_id);
                    intentComments.putExtra("access_token", access_token);
                    mContext.startActivity(intentComments);
                }
            });

            description.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: Alargar descripcion
                }
            });

            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: Probar
                    Intent intentLikes = new Intent(mContext, FollowersActivity.class);
                    intentLikes.putExtra("function", "likes");
                    intentLikes.putExtra("post_id", post_id);
                    mContext.startActivity(intentLikes);
                }
            });

            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: Probar
                    Intent intentComments = new Intent(mContext, FollowersActivity.class);
                    intentComments.putExtra("function", "comments");
                    intentComments.putExtra("post_id", post_id);
                    intentComments.putExtra("access_token", access_token);
                    mContext.startActivity(intentComments);
                }
            });

        }
    }


    public interface PostAdapterCallback{
        void onClickCallback(Post post, String view);
    }
}
