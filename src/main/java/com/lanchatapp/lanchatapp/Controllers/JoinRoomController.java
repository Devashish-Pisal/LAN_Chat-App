package com.lanchatapp.lanchatapp.Controllers;

import com.lanchatapp.lanchatapp.Client.Client;
import com.lanchatapp.lanchatapp.Messages.Message;
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
                String selectedItem = availableRooms.getSelectionModel().getSelectedItem();
                System.out.println(extractRoomName(selectedItem));

            }
        });
    }

    @FXML
    public void onBackToLobbyButtonClick(){
        Platform.runLater(()->{
            SceneManager.getInstance().switchScene("lobby.fxml", "Lobby");
        });
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
}