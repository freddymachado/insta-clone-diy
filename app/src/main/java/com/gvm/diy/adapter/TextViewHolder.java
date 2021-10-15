package com.gvm.diy.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TextViewHolder extends BaseViewHolder {

    private TextView mensaje;
    private TextView hora;

    @Override
    public void setData(TimelineItem item) {

        TextItem textItem = item.getTextItem();
        mensaje.setText(textItem.getMensaje());
        Long codigoHora = textItem.getHora();
        Date d = new Date(codigoHora);
        SimpleDateFormat today = new SimpleDateFormat("hh:mm a");//a = pm o am
        hora.setText(today.format(d));

    }

    public TextViewHolder(@NonNull View itemView) {
        super(itemView);

        mensaje = (TextView) itemView.findViewById(R.id.text_message_body);
        hora = (TextView) itemView.findViewById(R.id.text_message_time);
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
