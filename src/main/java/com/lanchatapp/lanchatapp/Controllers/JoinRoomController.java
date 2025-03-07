package com.lanchatapp.lanchatapp.Controllers;

import com.lanchatapp.lanchatapp.Client.Client;
import com.lanchatapp.lanchatapp.Messages.Message;
import com.lanchatapp.lanchatapp.Messages.Objects.JoinRoomData;
import com.lanchatapp.lanchatapp.SceneManager;
import com.lanchatapp.lanchatapp.Server.SessionManager;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class JoinRoomController implements Initializable {

    @FXML
    public ListView<String> availableRooms;

    @FXML
    private Button backToLobbyButton;

    @FXML
    private Label warningLabel;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Client c = Client.getClientInstance();
        Message msg = new Message("GET_ALL_ROOMS",null);
        c.sendMessage(msg);

        availableRooms.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){

            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                warningLabel.setText("");
                String selectedItem = availableRooms.getSelectionModel().getSelectedItem();
                int index = availableRooms.getSelectionModel().getSelectedIndex();
                if(index != 0) {
                    String roomName = extractRoomName(selectedItem);
                    int roomCapacity = extractMaxSizeOfRoom(selectedItem);
                    int currentSize = extractCurrentSizeOfRoom(selectedItem);
                    if (roomCapacity == currentSize) {
                        warningLabel.setText("Room is full!");
                    }else{
                        JoinRoomData data = new JoinRoomData(Client.getClientInstance().getUsername(), roomName);
                        Message msg = new Message("JOIN_ROOM", data);
                        Client.getClientInstance().sendMessage(msg);
                    }
                }
            }
        });
    }

    @FXML
    public void onBackToLobbyButtonClick(){
        SceneManager.getInstance().switchScene("lobby.fxml", "Lobby");
    }


    private String extractRoomName(String name){
        String result = "";
        for(int i = 0; i<name.length(); i++){
            if(name.substring(i,i+1).matches("^[0-9]$")){
                break;
            }else{
                result = result + name.substring(i,i+1);
            }
        }
        result = result.trim();
        return result;
    }

    private int extractCurrentSizeOfRoom(String name){
        String result = "";
        for(int i = 0; i<name.length(); i++){
            if(name.charAt(i) == '/'){
                return Integer.parseInt(result);
            }
            if(name.substring(i,i+1).matches("^[0-9]$")){
                result = result + name.substring(i,i+1);
            }
        }
        return Integer.parseInt(result);
    }

    private int extractMaxSizeOfRoom(String name){
        String result = "";
        boolean flag = false;
        for(int i = 0; i<name.length(); i++){
            if(flag){
                if(name.substring(i,i+1).matches("^[0-9]$")){
                    result = result + name.substring(i,i+1);
                }
            }
            if(name.charAt(i) == '/'){
                flag = true;
            }
        }
        return Integer.parseInt(result);
    }

    public void clearWarningLabel(){
        warningLabel.setText("");
    }

}