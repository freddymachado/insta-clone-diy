package com.gvm.diy.models;

public class ProfileItem {

    private String file;
    private String avatar;
    private String time_text;
    private String username;

    public ProfileItem(String avatar, String time_text, String username) {
        this.avatar = avatar;
        this.time_text = time_text;
        this.username = username;
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

    public ProfileItem(String file) {
        this.file = file;
    }

    public ProfileItem() {
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
