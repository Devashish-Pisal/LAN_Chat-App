package com.lanchatapp.lanchatapp.Messages;

import java.io.Serializable;

public class Message implements Serializable {
    private String msgType;
    private Object data;
    public Message(String type, Object data){
        this.data = data;
        this.msgType = type;
    }

    public String getMsgType() {
        return msgType;
    }

    public Object getData() {
        return data;
    }
}

/*

 MESSAGE TYPES :

 1) CREDENTIALS_CHECK : For validating input credentials of the user.
 */



