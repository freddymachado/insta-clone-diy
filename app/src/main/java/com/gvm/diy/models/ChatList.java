package com.gvm.diy.models;

import java.io.Serializable;

public class ChatList implements Serializable {
    private String tipo;
    private String hora;
    private String mensaje;
    private String id, avatar, new_message, username, time, last_message, user_id;

    public ChatList(String username, String time, String last_message, String user_id, String avatar,
                    String new_message) {
        this.username = username;
        this.time = time;
        this.last_message = last_message;
        this.user_id = user_id;
        this.avatar = avatar;
        this.new_message = new_message;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNew_message() {
        return new_message;
    }

    public void setNew_message(String new_message) {
        this.new_message = new_message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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
