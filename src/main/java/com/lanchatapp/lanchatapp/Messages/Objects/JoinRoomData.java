package com.lanchatapp.lanchatapp.Messages.Objects;

import java.io.Serializable;

public class JoinRoomData implements Serializable {
    private String username;
    private String roomName;
    public JoinRoomData(String user, String room){
        this.roomName = room;
        this.username = user;
    }

    public String getUsername() {
        return username;
    }

    public String getRoomName() {
        return roomName;
    }
}
