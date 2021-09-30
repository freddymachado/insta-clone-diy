package com.gvm.diy.models;

import java.io.Serializable;

public class CommentsItem implements Serializable {
    private String avatar, comment, time, likes, comment_id, user_id;
    private Boolean is_liked;

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public CommentsItem(String avatar, String comment, String time, String likes, String comment_id, String user_id, Boolean is_liked) {
        this.avatar = avatar;
        this.comment = comment;
        this.time = time;
        this.likes = likes;
        this.comment_id = comment_id;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public CommentsItem(String avatar, String comment, String time, String likes, Boolean is_liked) {
        this.avatar = avatar;
        this.comment = comment;
        this.time = time;
        this.likes = likes;
        this.is_liked = is_liked;
    }

}
