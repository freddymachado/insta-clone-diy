package com.gvm.diy.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gvm.diy.MyBounceInterpolator;
import com.gvm.diy.R;
import com.gvm.diy.models.FollowItem;
import com.gvm.diy.models.Post;
import com.gvm.diy.models.ProfileItem;
import com.gvm.diy.ui.ProfileViewerActivity;

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

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.FollowViewHolder>{

    private Context mContext;
    private List<FollowItem> followItems;

    RequestBody FollowBody;
    Request FollowRequest;
    OkHttpClient client;

    private String access_token, server_key = "1539874186", user_id;

    public FollowAdapter(Context mContext, List<FollowItem> followItems) {
        this.mContext = mContext;
        this.followItems = followItems;
    }
    public FollowAdapter(Context mContext, List<FollowItem> followItems, String access_token) {
        this.mContext = mContext;
        this.followItems = followItems;
        this.access_token = access_token;
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


        user_id = followItem.getUser_id();
        Log.e("userid", followItem.getUser_id());
        if(followItem.getIs_following().equals("false")){
            holder.buttonFollowing.setText("Follow");
        }
        if (user_id.equals("likes"))
            holder.buttonFollowing.setVisibility(View.GONE);

        //Cargamos la animcion del boton
        final Animation myAnim = AnimationUtils.loadAnimation(mContext, R.anim.bounce);

        //Usamos el BounceInterpolator con una amplitud de 0.2 y frecuencia de 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);

        holder.buttonFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Probar funcionamiento
                holder.buttonFollowing.startAnimation(myAnim);
                if (holder.buttonFollowing.getText().equals("following")) {
                    holder.buttonFollowing.setText("follow");
                } else {
                    holder.buttonFollowing.setText("following");
                }
                client = new OkHttpClient.Builder().build();
                FollowBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("server_key", server_key)
                        .addFormDataPart("user_id", user_id)
                        .addFormDataPart("access_token", access_token)
                        .build();

                FollowRequest = new Request.Builder()
                        .url("https://diys.co/endpoints/v1/user/follow")
                        .post(FollowBody)
                        .build();

                client.newCall(FollowRequest).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        String mMessage = e.getMessage().toString();
                        ((Activity)mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.buttonFollowing.startAnimation(myAnim);
                                if (holder.buttonFollowing.getText().equals("following")) {
                                    holder.buttonFollowing.setText("follow");
                                } else {
                                    holder.buttonFollowing.setText("following");
                                }
                                Toast.makeText(mContext, "Error de red: " + mMessage, Toast.LENGTH_LONG).show();

                            }
                        });
                        Log.e("failure Response", mMessage);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String mMessage = response.body().string();
                        Log.e("Like Response", mMessage);
                        JSONObject array = null;
                        ((Activity)mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return followItems.size();
    }

    class FollowViewHolder extends RecyclerView.ViewHolder{

        ImageView imageViewavatar;
        TextView textViewName, textViewLastSeen;
        Button buttonFollowing;

        FollowViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewavatar = itemView.findViewById(R.id.imageViewAvatar);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewLastSeen = itemView.findViewById(R.id.textViewLastSeen);
            buttonFollowing = itemView.findViewById(R.id.buttonFollowing);
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
