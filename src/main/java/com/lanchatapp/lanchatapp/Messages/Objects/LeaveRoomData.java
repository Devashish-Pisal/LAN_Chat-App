package com.lanchatapp.lanchatapp.Messages.Objects;

import java.io.Serializable;

public class LeaveRoomData implements Serializable {
    private String roomName;
    private String username;
    public LeaveRoomData(String user, String room){
        this.roomName = room;
        this.username = user;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getUsername() {
        return username;
    }
}
