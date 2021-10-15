package com.gvm.diy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gvm.diy.R;
import com.gvm.diy.models.ChatList;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {

    private List<ChatList> chatList;
    private Context context;
    private SelectedChat selectedChat;
    private String id;

    public ChatListAdapter(List<ChatList> chatList,SelectedChat selectedChat1) {
        this.chatList = chatList;
        this.selectedChat = selectedChat1;
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ChatListViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_list_item, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListViewHolder holder, int position) {

        ChatList chat = chatList.get(position);

        String userName = chat.getTipo();
        String mensaje = chat.getMensaje();
        String hora = chat.getHora();
        id = chat.getId();

        holder.textViewUserName.setText(userName);
        holder.textViewHora.setText(hora);
        holder.textViewMensaje.setText(mensaje);
    }

    //Devuelve el numero de items
    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public interface SelectedChat{
        void selectedChat(ChatList chatList,String id);
    }

    public class ChatListViewHolder extends RecyclerView.ViewHolder {

        TextView textViewUserName;
        TextView textViewHora;
        TextView textViewMensaje;
        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewUserName = itemView.findViewById(R.id.text_message_name);
            textViewHora = itemView.findViewById(R.id.text_message_time);
            textViewMensaje = itemView.findViewById(R.id.text_message_body);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedChat.selectedChat(chatList.get(getAdapterPosition()), id);
                }
            });
        }
    }
}
