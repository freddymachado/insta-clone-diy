package com.gvm.diy.models;


import java.io.Serializable;

public class TextItem extends Mensaje implements Serializable {
    private Long hora, time;
  private String text, user_id, avatar;
  private int to_id, from_id;

    public TextItem() {
    }

    public TextItem(Long hora) {
        this.hora = hora;
    }

    public TextItem(String mensaje, String tipo, String urlFoto, Long hora) {
        super(mensaje, tipo, urlFoto);
        this.hora = hora;
    }

  public TextItem(String text, String user_id, String avatar, long time, int to_id, int from_id) {
    this.text = text;
    this.user_id = user_id;
    this.avatar = avatar;
    this.time = time;
    this.to_id = to_id;
    this.from_id = from_id;

  }

  public Long getTime() {
    return time;
  }

  public void setTime(Long time) {
    this.time = time;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getUser_id() {
    return user_id;
  }

  public void setUser_id(String user_id) {
    this.user_id = user_id;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public int getTo_id() {
    return to_id;
  }

  public void setTo_id(int to_id) {
    this.to_id = to_id;
  }

  public int getFrom_id() {
    return from_id;
  }

  public void setFrom_id(int from_id) {
    this.from_id = from_id;
  }

  public Long getHora() {
        return hora;
    }

    public void setHora(Long hora) {
        this.hora = hora;
    }
}
