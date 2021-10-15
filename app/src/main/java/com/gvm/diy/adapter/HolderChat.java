package com.gvm.diy.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.gvm.diy.R;
import com.gvm.diy.models.MensajeRecibido;
import com.gvm.diy.models.TimelineItem;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HolderChat extends BaseViewHolder {

    private TextView mensaje;
    private TextView hora;
    private ImageView fotoMensaje;

    @Override
    public void setData(TimelineItem item) {
        if (item.getMensajeRecibidoItem().getTipo().equals("2")) {
            fotoMensaje.setVisibility(View.VISIBLE);
            mensaje.setVisibility(View.VISIBLE);
            Glide.with(itemView.getContext()).load(item.getMensajeRecibidoItem().getUrlFoto()).into(fotoMensaje);
        }else if (item.getMensajeRecibidoItem().getTipo().equals("1")) {
            fotoMensaje.setVisibility(View.GONE);
            mensaje.setVisibility(View.VISIBLE);

        }

        MensajeRecibido mensajeRecibido = item.getMensajeRecibidoItem();
        mensaje.setText(mensajeRecibido.getMensaje());
        Long codigoHora = mensajeRecibido.getHora();
        Date d = new Date(codigoHora);
        SimpleDateFormat today = new SimpleDateFormat("hh:mm a");//a = pm o am
        hora.setText(today.format(d));

    }

    public HolderChat(@NonNull View itemView) {
        super(itemView);

        mensaje = (TextView) itemView.findViewById(R.id.text_message_body);
        hora = (TextView) itemView.findViewById(R.id.text_message_time);
        fotoMensaje = (ImageView) itemView.findViewById(R.id.mensajeFoto);
    }


    public ImageView getFotoMensaje() {
        return fotoMensaje;
    }

    public void setFotoMensaje(ImageView fotoMensaje) {
        this.fotoMensaje = fotoMensaje;
    }

    public TextView getMensaje() {
        return mensaje;
    }

    public void setMensaje(TextView mensaje) {
        this.mensaje = mensaje;
    }

    public TextView getHora() {
        return hora;
    }

    public void setHora(TextView hora) {
        this.hora = hora;
    }
}
