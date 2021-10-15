package com.gvm.diy.models;

import java.io.Serializable;

public class ChatList implements Serializable {
    private String tipo;
    private String hora;
    private String mensaje;
    private String id;

    public ChatList() {
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public ChatList(String tipo, String hora, String mensaje, String id) {
        this.tipo = tipo;
        this.hora = hora;
        this.mensaje = mensaje;
        this.id = id;
    }

    public ChatList(String tipo, String hora) {
        this.tipo = tipo;
        this.hora = hora;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }
}
