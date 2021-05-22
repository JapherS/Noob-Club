package edu.neu.madcourse.noobclub;


import com.google.android.material.imageview.ShapeableImageView;

public class PostCard {
    private int avatar;
    private String postTitle;
    private String postAuthor;
    private String postGameType;
    private String postCreatedTime;
    private String postNumReply;

    // constructor
    public PostCard(int avatar, String postTitle, String postAuthor,
                    String postGameType, String postCreatedTime, String postNumReply) {
        this.avatar = avatar;
        this.postTitle = postTitle;
        this.postAuthor = postAuthor;
        this.postGameType = postGameType;
        this.postCreatedTime = postCreatedTime;
        this.postNumReply = postNumReply;
    }

    public int getAvatar() {
        return avatar;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public String getPostAuthor() {
        return postAuthor;
    }

    public String getPostGameType() {
        return postGameType;
    }

    public String getPostCreatedTime() {
        return postCreatedTime;
    }

    public String getPostNumReply() {
        return postNumReply;
    }
}
