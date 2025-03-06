package com.lanchatapp.lanchatapp.Messages.Objects;

import java.io.Serializable;

public class RoomData implements Serializable {
    private String roomName;
    private int max;
    public RoomData(String name, int max){
        this.roomName = name;
        this.max = max;
    }

    public String getRoomName() {
        return roomName;
    }

    public int getMax() {
        return max;
    }
}
