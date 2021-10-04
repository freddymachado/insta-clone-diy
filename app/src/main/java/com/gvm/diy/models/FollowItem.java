package com.gvm.diy.models;

import java.io.Serializable;

public class FollowItem implements Serializable {

    private String file;
    private String avatar;
    private String time_text;
    private String username, user_id, is_following, is_blocked;

    public FollowItem(String avatar, String time_text, String username) {
        this.avatar = avatar;
        this.time_text = time_text;
        this.username = username;
    }

    public FollowItem(String avatar, String time_text, String username, String is_following, String user_id) {
        this.avatar = avatar;
        this.time_text = time_text;
        this.username = username;
        this.is_following = is_following;
        this.user_id = user_id;

    }

    public FollowItem(String avatar, String time_text, String username, String user_id) {
        this.avatar = avatar;
        this.time_text = time_text;
        this.username = username;
        this.user_id = user_id;

    }

    public String getIs_following() {
        return is_following;
    }

    public void setIs_following(String is_following) {
        this.is_following = is_following;
    }

    public String getIs_blocked() {
        return is_blocked;
    }

    public void setIs_blocked(String is_blocked) {
        this.is_blocked = is_blocked;
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

    public String getTime_text() {
        return time_text;
    }

    public void setTime_text(String time_text) {
        this.time_text = time_text;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public FollowItem(String file) {
        this.file = file;
    }

    public FollowItem() {
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
