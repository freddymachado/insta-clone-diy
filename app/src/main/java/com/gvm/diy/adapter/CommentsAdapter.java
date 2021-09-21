package com.gvm.diy.adapter;

import android.content.Context;
import android.provider.MediaStore;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gvm.diy.MyBounceInterpolator;
import com.gvm.diy.R;
import com.gvm.diy.models.CommentsItem;
import com.gvm.diy.models.Post;
import com.gvm.diy.ui.PostViewerActivity;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    private Context mContext;
    private List<CommentsItem> commentsItems;
    private String access_token, server_key = "1539874186", comment_id;
    private Boolean is_liked, is_saved;

    public CommentsAdapter(Context mContext, List<CommentsItem> commentsItems, String access_token) {
        this.mContext = mContext;
        this.commentsItems = commentsItems;
        this.access_token = access_token;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_comment,parent,false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentsItem commentsItem = commentsItems.get(position);

        try{
            Glide.with(mContext).load(commentsItem.getAvatar())
                    .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                    .into(holder.imageViewAvatar);
        }catch (Exception e){
            e.printStackTrace();
        }

        holder.textViewLikes.setText("Me gusta("+commentsItem.getLikes()+")");
        holder.textViewComment.setText(commentsItem.getComment());
        holder.textViewTime.setText(commentsItem.getTime());
    }

    @Override
    public int getItemCount() {
        return commentsItems.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        ImageView imageViewAvatar;
        TextView textViewLikes, textViewComment, textViewTime;
        ImageButton imageButtonLike, imageButtonMore;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewAvatar = itemView.findViewById(R.id.imageViewAvatar);
            textViewLikes = itemView.findViewById(R.id.textViewLikes);
            textViewComment = itemView.findViewById(R.id.textViewComment);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            imageButtonLike = itemView.findViewById(R.id.imageButtonLike);
            imageButtonMore = itemView.findViewById(R.id.imageButtonMore);

            is_liked = commentsItems.get(getAdapterPosition()).getIs_liked();
            comment_id = commentsItems.get(getAdapterPosition()).getComment_id();

            //Cargamos la animcion del boton
            final Animation myAnim = AnimationUtils.loadAnimation(mContext,R.anim.bounce);

            //Usamos el BounceInterpolator con una amplitud de 0.2 y frecuencia de 20
            MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2,20);
            myAnim.setInterpolator(interpolator);

            imageButtonLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OkHttpClient imageUploadClient = new OkHttpClient.Builder().build();
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("server_key",server_key)
                            .addFormDataPart("comment_id",comment_id)
                            .addFormDataPart("access_token",access_token)
                            .build();
                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url("https://diys.co/endpoints/v1/post/like_comment")
                            .post(requestBody)
                            .build();


                    imageUploadClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            String mMessage = e.getMessage().toString();
                            Toast.makeText(mContext, "Error de red: "+mMessage, Toast.LENGTH_LONG).show();
                            Log.e("failure Response", mMessage);
                        }

                        @Override
                        public void onResponse(Call call, okhttp3.Response response) throws IOException {
                            final String mMessage = response.body().string();
                            Log.e("Like Response", mMessage);
                            imageButtonLike.startAnimation(myAnim);
                            if(is_liked){
                                is_liked = false;
                                imageButtonLike.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_favorite_border_red));
                            }
                            else{
                                is_liked = true;
                                imageButtonLike.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_favorite_red));
                            }

                        }
                    });

                }
            });
            imageButtonMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: Verificar diseño MoreDialog
                }
            });
        }
    }
}
