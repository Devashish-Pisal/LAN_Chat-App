package com.lanchatapp.lanchatapp.Messages.Objects;

import java.io.Serializable;

public class UserData implements Serializable {
    private String username;
    private String hashedPassword;
    public UserData(String username, String hashedPassword){
        this.username = username;
        this.hashedPassword = hashedPassword;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }
}
