package edu.neu.madcourse.noobclub;

import java.util.ArrayList;
import java.util.List;

enum LOBBY_STATUS {
    ACTIVE,
    CLOSED
}

public class MatchLobby {
    private String lobbyID;
    private String startTime;
    private long startTimeLong;
    private String gameName;
    private int capacity;
    private List<String> userList;
    private LOBBY_STATUS lobbyStatus;

    // Constructor of MatchLobby
    public MatchLobby(String lobbyID, String startTime, long startTimeLong, String gameName) {
        this.lobbyID = lobbyID;
        this.startTime = startTime;
        this.startTimeLong = startTimeLong;
        this.gameName = gameName;
        userList = new ArrayList<>();
        lobbyStatus = LOBBY_STATUS.ACTIVE;
        capacity = 2;
    }

    // None-argument constructor
    public MatchLobby() {

    }



    public void addUser(String userName) {
        this.userList.add(userName);
    }

    public void removeUser(String userName) {
        this.userList.remove(userName);
    }

    public List<String> getUserList() {
        return this.userList;
    }

    public void close() {
        this.lobbyStatus = LOBBY_STATUS.CLOSED;
    }



    public void setLobbyID(String lobbyID) {
        this.lobbyID = lobbyID;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setStartTimeLong(long startTimeLong) {
        this.startTimeLong = startTimeLong;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setUserList(List<String> userList) {
        this.userList = userList;
    }

    public void setLobbyStatus(LOBBY_STATUS lobbyStatus) {
        this.lobbyStatus = lobbyStatus;
    }


    public String getLobbyID() {
        return lobbyID;
    }

    public String getStartTime() {
        return startTime;
    }

    public long getStartTimeLong() {
        return startTimeLong;
    }

    public String getGameName() {
        return gameName;
    }

    public int getCapacity() {
        return capacity;
    }

    public LOBBY_STATUS getLobbyStatus() {
        return lobbyStatus;
    }
}
