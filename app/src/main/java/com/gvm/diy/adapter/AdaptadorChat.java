package com.gvm.diy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gvm.diy.R;
import com.gvm.diy.models.Constant;
import com.gvm.diy.models.MensajeRecibido;
import com.gvm.diy.models.TimelineItem;

import java.util.List;

public class AdaptadorChat extends RecyclerView.Adapter<BaseViewHolder> {

    private List<MensajeRecibido> listMensaje;
    private Context c;
    private SelectedChat selectedChat;
    private List<TimelineItem> mdata;

    public AdaptadorChat(Context c, List<TimelineItem> mdata) {
        this.c = c;
        this.mdata = mdata;
    }

    public AdaptadorChat() {
    }

    public AdaptadorChat(List<TimelineItem> mdata) {
        this.mdata = mdata;
    }

    public List<TimelineItem> getMdata() {
        return mdata;
    }

    public AdaptadorChat(Context c) {
        this.c = c;
    }

    public AdaptadorChat(Context c, List<TimelineItem> mdata, SelectedChat selectedChat1) {
        this.c = c;
        this.mdata = mdata;
        this.selectedChat = selectedChat1;
    }


    public void addMensaje(MensajeRecibido m){
        listMensaje.add(m);
        notifyItemInserted(listMensaje.size());
    }
    public interface SelectedChat{
        void selectedChat(List<TimelineItem> mdata);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch(viewType){
            case (Constant.ITEM_PALM_PHOTO_VIEWTYPE):
                view = LayoutInflater.from(c).inflate(R.layout.item_message_received2,parent,false);
                return new TextViewHolder(view);
            case (Constant.ITEM_TEXT_VIEWTYPE):
                view = LayoutInflater.from(c).inflate(R.layout.item_message_sent,parent,false);
                return new HolderChat(view);

            default: throw new IllegalArgumentException();
        }
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, int position) {

        holder.setData(mdata.get(position));
    }

    @Override
    public int getItemCount() {

        if(mdata!=null){
            return mdata.size();
        }else {
            return 0;
        }
    }

    public void setItems(List<TimelineItem> mdata) {
        this.mdata = mdata;
    }

    @Override
    public int getItemViewType(int position) {
        return mdata.get(position).getViewType();
    }

}
