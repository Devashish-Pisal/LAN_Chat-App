package com.lanchatapp.lanchatapp.Controllers;

import com.lanchatapp.lanchatapp.Client.Client;
import com.lanchatapp.lanchatapp.Messages.Message;
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
    }
}