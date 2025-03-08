package com.lanchatapp.lanchatapp.Messages.Objects;

import java.io.Serializable;
import java.util.SequencedCollection;

public class LogOutData implements Serializable {
    private String username;
    private String roomName;
    public LogOutData(String user, String room){
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
