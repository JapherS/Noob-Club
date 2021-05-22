package edu.neu.madcourse.noobclub;

import java.util.List;

public class User {
    private String userName;
    private String UID;
    private String email;
    private int avatarID;
    private List<String> posts;
    private List<String> replies;

    public User(String userName, String UID, String email) {
        this.userName = userName;
        this.UID = UID;
        this.email = email;
        this.avatarID = R.drawable.avatar_default;
    }

    public User() {

    }

    public void addPost(String id) {
        this.posts.add(id);
    }

    public void addReply(String id) {
        this.replies.add(id);
    }

    public String getUserName() {
        return userName;
    }

    public String getUID() {
        return UID;
    }

    public String getEmail() {
        return email;
    }

    public int getAvatarID() {
        return avatarID;
    }

    public List<String> getPosts() {
        return posts;
    }

    public List<String> getReplies() {
        return replies;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAvatarID(int avatarID) {
        this.avatarID = avatarID;
    }

    public void setPosts(List<String> posts) {
        this.posts = posts;
    }

    public void setReplies(List<String> replies) {
        this.replies = replies;
    }
}
