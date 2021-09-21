package com.gvm.diy.models;

import java.io.Serializable;

public class Post implements Serializable {
    private String description, time, username, avatar, file, likes,comments, post_id;
    private Boolean is_liked, is_saved;

    public Boolean getIs_liked() {
        return is_liked;
    }

    public void setIs_liked(Boolean is_liked) {
        this.is_liked = is_liked;
    }

    public Boolean getIs_saved() {
        return is_saved;
    }

    public void setIs_saved(Boolean is_saved) {
        this.is_saved = is_saved;
    }

    public Post(String description, String time, String username, String avatar, String file, String likes, String comments, Boolean is_liked, Boolean is_saved, String post_id) {
        this.description = description;
        this.time = time;
        this.username = username;
        this.avatar = avatar;
        this.file = file;
        this.likes = likes;
        this.comments = comments;
        this.post_id = post_id;
        this.is_liked = is_liked;
        this.is_saved = is_saved;
    }

    public Post() {
    }

    public Post(String description, String time, String username, String avatar, String file, String likes, String comments, String post_id) {
        this.description = description;
        this.time = time;
        this.username = username;
        this.avatar = avatar;
        this.file = file;
        this.likes = likes;
        this.comments = comments;
        this.post_id = post_id;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public Post(String description, String username, String avatar, String file, String likes, String comments) {
        this.description = description;
        this.username = username;
        this.avatar = avatar;
        this.file = file;
        this.likes = likes;
        this.comments = comments;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
