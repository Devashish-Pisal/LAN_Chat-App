package com.lanchatapp.lanchatapp.Server;
import com.lanchatapp.lanchatapp.Messages.Message;
import com.lanchatapp.lanchatapp.Messages.Objects.JoinRoomData;
import com.lanchatapp.lanchatapp.Messages.Objects.LeaveRoomData;
import com.lanchatapp.lanchatapp.Messages.Objects.RoomData;
import com.lanchatapp.lanchatapp.Messages.Objects.UserData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientHandler implements Runnable{
    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String username;

    private String currentRoom = "LOBBY";

    public ClientHandler(Socket socket, Server server){
        this.clientSocket = socket;
        try{
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        try {
            while (true) {
                Object msg = in.readObject();
                handleClientMessage(msg);
            }
        }catch (Exception e){
            e.printStackTrace();
            handleClientHandlerDisconnect();
        }
    }

    public void handleClientMessage(Object msg){
        if(msg instanceof Message){
            Message msg1 = (Message) msg;
            String type = msg1.getMsgType();
            if(type.equals("CREDENTIALS_CHECK")){
                UserData data = (UserData) msg1.getData();
                Boolean usernamePasswordExists = UserAuthServer.getInstance().verifyPassword(data.getUsername(),data.getHashedPassword());
                Boolean isLoggedOut = UserAuthServer.getInstance().verifyLoggedOutStatus(data.getUsername());
                if(!usernamePasswordExists){
                    Message message = new Message("ERROR-001","INVALID_CREDENTIALS");
                    sendMessage(message);
                    return;
                }else if (usernamePasswordExists && !isLoggedOut){
                    Message message = new Message("ERROR-002", "USER_ALREADY_LOGGED_IN");
                    sendMessage(message);
                    return;
                }else if (usernamePasswordExists && isLoggedOut){
                    Message message = new Message("SUCCESS", "LOGIN_SUCCESS");
                    sendMessage(message);
                    return;
                }
            }else if(type.equals("LOGIN_SUCCESS")) {
                this.username = (String) msg1.getData();
                this.currentRoom = "LOBBY";
                UserAuthServer.getInstance().changeStatusTo(username, "LOGGED_IN");
                SessionManager.getInstance().addClientToSession("LOBBY", this);
                Message message = new Message("LOBBY_MESSAGE","Server : " + username + " joined global chat! \n");
                sendMessageInRoom("LOBBY",message);

            }else if(type.equals("CHECK_DUPLICATE_USERNAME")){
                UserData data = (UserData) msg1.getData();
                String name = (String) data.getUsername();
                String hashedPass = (String) data.getHashedPassword();
                Boolean playerExists = UserAuthServer.getInstance().checkExistingUser(name);
                if(playerExists){
                    Message message = new Message("ERROR-003", "DUPLICATE_USERNAME");
                    sendMessage(message);
                    return;
                }else{
                    UserAuthServer.getInstance().insertUser(name,hashedPass);
                    Message message= new Message("REGISTERED_SUCCESSFULLY",name);
                    this.username = name;
                    this.currentRoom = "LOBBY";
                    UserAuthServer.getInstance().changeStatusTo(username, "LOGGED_IN");
                    SessionManager.getInstance().addClientToSession("LOBBY", this);
                    sendMessage(message);
                    Message message1 = new Message("LOBBY_MESSAGE","Server : " + username + " joined global chat! \n");
                    sendMessageInRoom("LOBBY", message1);
                }
            }else if(type.equals("LOBBY_MESSAGE_REQUEST")){
                Message newMessage = new Message("LOBBY_MESSAGE", (String) msg1.getData());
                sendMessageInRoom("LOBBY", newMessage);
            }else if(type.equals("CREATE_ROOM_REQUEST")){
                RoomData data = (RoomData) msg1.getData();
                boolean roomExists = SessionManager.getInstance().checkAvailableRoom(data.getRoomName());
                if(roomExists){
                    Message message = new Message("ERROR-004", "Room already exists!");
                    sendMessage(message);
                    return;
                }else{
                    ClientHandler ch = SessionManager.getInstance().removeClientHandlerFromRoom("LOBBY",username);
                    SessionManager.getInstance().addClientToSession(data.getRoomName(),ch);
                    this.currentRoom = data.getRoomName();
                    SessionManager.getInstance().addRoomCapacity(data.getRoomName(), data.getMax());
                    Message msg3 = new Message("ROOM_CREATED_SUCCESSFULLY",data.getRoomName());
                    sendMessage(msg3);
                    Message msg2 = new Message("LOBBY_MESSAGE", "Server : " + username + " created room '" + data.getRoomName() + "'! \n");
                    sendMessageInRoom("LOBBY", msg2);
                    Message msg4 = new Message("ROOM_MESSAGE", "Server : " + username + " created room '" + data.getRoomName() + "'! \n" );
                    sendMessageInRoom(data.getRoomName(),msg4);
                    Message msg5 = new Message("UPDATE_ONLINE_ROOMS",SessionManager.getInstance().createOnlineRoomList());
                    sendMessageInRoom("LOBBY",msg5);
                }
            }else if(type.equals("ROOM_MESSAGE_REQUEST")){
                Message message = new Message("ROOM_MESSAGE",(String) msg1.getData());
                sendMessageInRoom(currentRoom,message);
            }else if(type.equals("GET_ALL_ROOMS")){
                Message message = new Message("JOIN_CHAT_ROOM_LIST", SessionManager.getInstance().createOnlineRoomList());
                sendMessage(message);
            }else if(type.equals("GET_ROOMS_LIST")){
                Message msg2 = new Message("UPDATE_ONLINE_ROOMS",SessionManager.getInstance().createOnlineRoomList());
                sendMessageInRoom("LOBBY",msg2);
            }else if(type.equals("JOIN_ROOM")){
                JoinRoomData data = (JoinRoomData) msg1.getData();
                ClientHandler ch = SessionManager.getInstance().removeClientHandlerFromRoom(this.currentRoom,this.username);
                SessionManager.getInstance().addClientToSession(data.getRoomName(),ch);
                this.currentRoom = data.getRoomName();
                Message msg3 = new Message("ROOM_JOINING_SUCCESSFUL",data.getRoomName());
                sendMessage(msg3);
                Message msg2 = new Message("UPDATE_ONLINE_ROOMS",SessionManager.getInstance().createOnlineRoomList());
                sendMessageInRoom("LOBBY",msg2);
                Message msg4 = new Message("ROOM_MESSAGE", "Server : " + this.username + " joined chat room!\n" );
                sendMessageInRoom(data.getRoomName(),msg4);
                Message msg5 = new Message("LOBBY_MESSAGE", "Server : " + this.username + " joined chat room: " + this.currentRoom +   "\n");
                sendMessageInRoom("LOBBY", msg5);
            }else if(type.equals("LEAVE_ROOM")){
                LeaveRoomData data = (LeaveRoomData) msg1.getData();
                ClientHandler ch = SessionManager.getInstance().removeClientHandlerFromRoom(data.getRoomName(),data.getUsername());
                SessionManager.getInstance().addClientToSession("LOBBY",ch);
                Message msg2 = new Message("ROOM_MESSAGE","Server : " + this.username + " left chat room!\n");
                sendMessageInRoom(data.getRoomName(), msg2);
                this.currentRoom = "LOBBY";
                Message msg3 = new Message("ROOM_LEFT_SUCCESSFULLY",this.username);
                sendMessage(msg3);
                Message msg4 = new Message("UPDATE_ONLINE_ROOMS",SessionManager.getInstance().createOnlineRoomList());
                sendMessageInRoom("LOBBY",msg4);
                Message msg5 = new Message("LOBBY_MESSAGE", "Server : " + this.username + " left chat room: " + data.getRoomName() + "!\n");
                sendMessageInRoom("LOBBY",msg5);
            }
        }
    }

    public void sendMessage(Object msg){
        try{
            out.writeObject(msg);
            out.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendMessageInRoom(String roomName, Object msg){
        List<ClientHandler> list = SessionManager.getInstance().getClientHandlerList(roomName);
        if(list != null) {
            for (ClientHandler ch : list) {
                ch.sendMessage(msg);
            }
        }
    }

    private void handleClientHandlerDisconnect(){
        try{
            out.close();
            in.close();
            clientSocket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getUsername(){
        return username;
    }
}
