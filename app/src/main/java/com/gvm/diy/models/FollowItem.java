package com.gvm.diy.models;

import java.io.Serializable;

public class FollowItem implements Serializable {

    private String file, about, website, followers, following, favourites, name;
    private String avatar, tag, last_trend_time, use_num;
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

    public FollowItem(String tag, String last_trend_time, String use_num, String user_id) {
        this.tag = tag;
        this.last_trend_time = last_trend_time;
        this.use_num = use_num;
        this.user_id = user_id;

    }

    public FollowItem(String avatar, String time_text, String username, String is_following, String user_id,
                      String about, String website, String followers, String following, String favourites, String name) {
        this.avatar = avatar;
        this.time_text = time_text;
        this.username = username;
        this.is_following = is_following;
        this.user_id = user_id;
        this.about = about;
        this.website = website;
        this.followers = followers;
        this.following = following;
        this.favourites = favourites;
        this.name = name;

    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getLast_trend_time() {
        return last_trend_time;
    }

    public void setLast_trend_time(String last_trend_time) {
        this.last_trend_time = last_trend_time;
    }

    public String getUse_num() {
        return use_num;
    }

    public void setUse_num(String use_num) {
        this.use_num = use_num;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public String getFavourites() {
        return favourites;
    }

    public void setFavourites(String favourites) {
        this.favourites = favourites;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
