package edu.neu.madcourse.noobclub;


// this is a single class used for storing user log-in/log-out information
public class UserSignInStatus {
    private static UserSignInStatus instance = null;

    private String username;
    private String email;
    private String uid;
    private int avatarID;


    private boolean isSignedIn;

    // don't allow multiple instantiation
    private UserSignInStatus() {}

    public static UserSignInStatus getInstance() {
        if (instance == null) {
            instance = new UserSignInStatus();
        }
        return instance;
    }

    public boolean getSignInStatus() {
        return isSignedIn;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public int getAvatarID() {
        return avatarID;
    }
    public void setSignInStatus(boolean status) {
        isSignedIn = status;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setAvatarID(int avatarID) {
        this.avatarID = avatarID;
    }

}
