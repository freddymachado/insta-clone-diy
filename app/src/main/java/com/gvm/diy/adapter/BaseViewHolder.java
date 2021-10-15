package com.gvm.diy.adapter;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gvm.diy.models.TimelineItem;

import java.util.List;

public abstract class BaseViewHolder extends RecyclerView.ViewHolder {
    private AdaptadorChat.SelectedChat selectedChat;
    private List<TimelineItem> mdata;

    public BaseViewHolder(@NonNull View itemView, AdaptadorChat.SelectedChat selectedChat1, List<TimelineItem> mdata) {
        super(itemView);
        this.selectedChat = selectedChat1;
        this.mdata = mdata;
    }

    abstract  void setData(TimelineItem item);

    public BaseViewHolder(@NonNull final View itemView) {
        super(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ViewHolderListener", "onClick：" + getAdapterPosition());
                //Debo implementar un metodo que lea el mensaje del item seleccionado y envie un intent a otro activity si consigue una palabra en especifico.

            }
        });
    }
}
