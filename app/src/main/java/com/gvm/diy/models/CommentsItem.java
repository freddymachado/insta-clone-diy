package com.gvm.diy.models;

import java.io.Serializable;

public class CommentsItem implements Serializable {
    private String avatar, text, time_text, likes, id, user_id;
    private Boolean is_liked;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CommentsItem(String avatar, String text, String time_text, String likes, String id, String user_id, Boolean is_liked) {
        this.avatar = avatar;
        this.text = text;
        this.time_text = time_text;
        this.likes = likes;
        this.id = id;
        this.user_id = user_id;
        this.is_liked = is_liked;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime_text() {
        return time_text;
    }

    public void setTime_text(String time_text) {
        this.time_text = time_text;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public Boolean getIs_liked() {
        return is_liked;
    }

    public void setIs_liked(Boolean is_liked) {
        this.is_liked = is_liked;
    }

    public CommentsItem() {
    }

    public CommentsItem(String avatar, String text, String time_text, String likes, Boolean is_liked) {
        this.avatar = avatar;
        this.text = text;
        this.time_text = time_text;
        this.likes = likes;
        this.is_liked = is_liked;
    }

}
