package com.lanchatapp.lanchatapp.Client;

import com.lanchatapp.lanchatapp.Controllers.*;
import com.lanchatapp.lanchatapp.Messages.Message;
import com.lanchatapp.lanchatapp.SceneManager;
import com.lanchatapp.lanchatapp.Server.UserAuthServer;
import javafx.application.Platform;
import javafx.scene.Scene;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.foreign.PaddingLayout;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Client {
    private static Client clientInstance;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String username;
    private String currentRoom;

    private Client(){};
    public static Client getClientInstance(){
        if(clientInstance == null){
            clientInstance = new Client();
        }
        return clientInstance;
    }

    public void connectToServer(String host, int port, String username){
        try{
            socket = new Socket(host,port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            this.username = username;
            this.currentRoom = "LOBBY";

            new Thread(this::listenForMessages).start();
            //send login message immediately

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void sendMessage(Object obj){
       /*
        if(obj instanceof Message){
            Message msg = (Message) obj;
            if(msg.getType().equals("BACK_TO_LOBBY")){
                this.gameSign = null;
                this.info = null;
                this.connectedClientsOnServer = new ArrayList<>();
                this.currentSession = "LOBBY";
                requestConnectedClients();
            }
        }*/
        try{
            out.writeObject(obj);
            out.flush();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void listenForMessages() {
        try {
            while (true) {
                Object message = in.readObject();
                handleServerMessage(message);
            }
        } catch (Exception e) {
            // Throwing EOFException after closing client
            // Don't know, how to handle
        }
    }

    public void handleServerMessage(Object message){
        if(message instanceof Message){
            Message msg = (Message) message;
            String type = msg.getMsgType();
            if(type.equals("ERROR-001")){
                Platform.runLater(()->{
                    Object controller = SceneManager.getInstance().getController("login-page.fxml");
                    LoginPageController lc = (LoginPageController) controller;
                    lc.warningLabel.setText("Invalid Credentials, Try again!");
                });
            }else if(type.equals("ERROR-002")){
                Platform.runLater(()->{
                    Object controller = SceneManager.getInstance().getController("login-page.fxml");
                    LoginPageController lc = (LoginPageController) controller;
                    lc.warningLabel.setText("User with username '" + lc.usernameTextField.getText() + "' already logged in!");
                });
            }else if(type.equals("SUCCESS")){
                Platform.runLater(()->{
                    Object controller = SceneManager.getInstance().getController("login-page.fxml");
                    LoginPageController lc = (LoginPageController) controller;
                    Message loginSuccess = new Message("LOGIN_SUCCESS", lc.usernameTextField.getText());
                    this.username = lc.usernameTextField.getText();
                    this.currentRoom = "LOBBY";
                    sendMessage(loginSuccess);
                    SceneManager.getInstance().switchScene("lobby.fxml","Lobby");
                });

            }else if(type.equals("ERROR-003")){
                Platform.runLater(() ->{
                    Object controller = SceneManager.getInstance().getController("register-page.fxml");
                    RegisterPageController rpc = (RegisterPageController) controller;
                    rpc.warningLabel.setText("Username is already taken!");
                });
            }else if(type.equals("REGISTERED_SUCCESSFULLY")){
                Platform.runLater(()->{
                    String name = (String) msg.getData();
                    this.username = name;
                    this.currentRoom = "LOBBY";
                    SceneManager.getInstance().switchScene("lobby.fxml", "Lobby");
                });
            }else if(type.equals("LOBBY_MESSAGE")){
                Platform.runLater(()->{
                    Object controller = SceneManager.getInstance().getController("lobby.fxml");
                    LobbyController lc = (LobbyController) controller;
                    lc.globalServerTextArea.appendText((String) msg.getData());
                });
            }else if(type.equals("ERROR-004")){
                Platform.runLater(()->{
                    Object controller = SceneManager.getInstance().getController("room-creation-window.fxml");
                    RoomCreationController rc = (RoomCreationController) controller;
                    rc.warningLabel.setText("Room Name is already taken!");
                });
            }else if(type.equals("ROOM_CREATED_SUCCESSFULLY")){
                Platform.runLater(()->{
                    this.currentRoom = (String) msg.getData();
                    SceneManager.getInstance().switchScene("chat-room.fxml","Chat-Room");
                });
            }else if(type.equals("ROOM_MESSAGE")){
                Platform.runLater(()->{
                    Object controller = SceneManager.getInstance().getController("chat-room.fxml");
                    RoomController rc = (RoomController) controller;
                    rc.chatTextArea.appendText((String) msg.getData());
                });
            }else if(type.equals("UPDATE_ONLINE_ROOMS")){
                representRoomsInLobby((HashMap<String, String>) msg.getData());
            }else if(type.equals("JOIN_CHAT_ROOM_LIST")){
                representRoomsInJoinRoom((HashMap<String, String>) msg.getData());
            }
        }
    }

    public String getUsername() {
        return username;
    }

    public String getCurrentRoom(){
        return currentRoom;
    }

    public void handleDisconnect() {
    }

    private void representRoomsInLobby(HashMap<String,String> onlineRooms){
        if(currentRoom.equals("LOBBY")) {
            Platform.runLater(() -> {
                Object controller = SceneManager.getInstance().getController("lobby.fxml");
                LobbyController lc = (LobbyController) controller;
                lc.chatRoomsTextArea.clear();
                String header = "ROOM-NAME        PARTICIPANTS\n";
                String blankLine = "\n";
                lc.chatRoomsTextArea.appendText(header);
                lc.chatRoomsTextArea.appendText(blankLine);
                for(String key: onlineRooms.keySet()){
                    int spaces = 19-key.length();
                    String entry = key + " ".repeat(spaces) + onlineRooms.get(key) + "\n";
                    lc.chatRoomsTextArea.appendText(entry);
                }
            });
        }
    }

    private void representRoomsInJoinRoom(HashMap<String,String>map){
        Platform.runLater(()->{
            Object controller = SceneManager.getInstance().getController("join-room.fxml");
            JoinRoomController jrc = (JoinRoomController) controller;
            jrc.availableRooms.getItems().clear();
            List<String> roomList = new ArrayList<>();
            String item = "ROOM-NAME        PARTICIPANTS";
            roomList.add(item);
            for(String key : map.keySet()){
                int spaces = 20-key.length();
                item = key + " ".repeat(spaces) + map.get(key);
                roomList.add(item);
            }
            jrc.availableRooms.getItems().addAll(roomList);
        });
    }
}
