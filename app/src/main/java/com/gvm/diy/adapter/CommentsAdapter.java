package com.gvm.diy.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
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
    private String access_token, server_key = "1539874186", comment_id, user_id;
    private String is_liked, is_saved;

    AlertDialog.Builder builder;

    public CommentsAdapter(Context mContext, List<CommentsItem> commentsItems, String access_token, String user_id) {
        this.mContext = mContext;
        this.commentsItems = commentsItems;
        this.access_token = access_token;
        this.user_id = user_id;
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


        is_liked = commentsItem.getIs_liked();
        comment_id = commentsItem.getId();

        //Cargamos la animcion del boton
        final Animation myAnim = AnimationUtils.loadAnimation(mContext,R.anim.bounce);

        //Usamos el BounceInterpolator con una amplitud de 0.2 y frecuencia de 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2,20);
        myAnim.setInterpolator(interpolator);

        holder.imageButtonLike.setOnClickListener(new View.OnClickListener() {
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
                        ((Activity)mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "Error de red: " + mMessage, Toast.LENGTH_LONG).show();
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
                                holder.imageButtonLike.startAnimation(myAnim);
                                if(is_liked.equals("true")){
                                    is_liked = "false";
                                    holder.imageButtonLike.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_favorite_border_red));
                                }
                                else{
                                    is_liked = "true";
                                    holder.imageButtonLike.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_favorite_red));
                                }
                            }
                        });

                    }
                });

            }
        });
        holder.imageButtonMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Probar diseño MoreDialog (Cuando se obtenga erspuesta en FollowersActivity)
                builder.show();
            }
        });

        try{
            Glide.with(mContext).load("https://diys.co/"+commentsItem.getAvatar())
                    .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                    .into(holder.imageViewAvatar);
        }catch (Exception e){
            e.printStackTrace();
        }
        ClipboardManager clipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);

        if(user_id.equals(commentsItem.getUser_id())){
            builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Post")
                    .setItems(new String[]{"Copiar","Borrar"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                //TODO: Probar (Cuando se obtenga erspuesta en FollowersActivity)
                                case 0:
                                    ClipData clip = ClipData.newPlainText("comentario",commentsItem.getText());

                                    clipboardManager.setPrimaryClip(clip);
                                    break;
                                case 1:
                                    //TODO: Probar borrado de comentario (last entrega, debuggear con el envío de comentarios)
                                    OkHttpClient eraseComment = new OkHttpClient.Builder().build();
                                    RequestBody requestBody = new MultipartBody.Builder()
                                            .setType(MultipartBody.FORM)
                                            .addFormDataPart("server_key",server_key)
                                            .addFormDataPart("comment_id",commentsItem.getId())
                                            .addFormDataPart("access_token",access_token)
                                            .build();
                                    okhttp3.Request request = new okhttp3.Request.Builder()
                                            .url("https://diys.co/endpoints/v1/post/delete_comment")
                                            .post(requestBody)
                                            .build();


                                    eraseComment.newCall(request).enqueue(new Callback() {
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
                                            removeItem(holder.getAdapterPosition());

                                        }
                                    });
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
        }else{
            builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Post")
                    .setItems(new String[]{"Copiar"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                //TODO: Probar (Cuando se obtenga erspuesta en FollowersActivity)
                                case 0:
                                    ClipData clip = ClipData.newPlainText("comentario",commentsItem.getText());

                                    clipboardManager.setPrimaryClip(clip);
                                    break;
                                default:
                                    break;
                            }
                        }
                    });

        }

        holder.textViewLikes.setText("Me gusta("+commentsItem.getLikes()+")");
        holder.textViewComment.setText(commentsItem.getText());
        holder.textViewTime.setText(commentsItem.getTime_text());
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
        }
    }

    public void removeItem(int position){
        commentsItems.remove(position);
        notifyItemRemoved(position);
    }
}
