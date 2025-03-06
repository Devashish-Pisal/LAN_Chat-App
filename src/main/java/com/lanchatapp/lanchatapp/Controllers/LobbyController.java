package com.lanchatapp.lanchatapp.Controllers;

import com.lanchatapp.lanchatapp.Client.Client;
import com.lanchatapp.lanchatapp.Messages.Message;
import com.lanchatapp.lanchatapp.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class LobbyController implements Initializable {

    @FXML
    public TextArea chatRoomsTextArea;

    @FXML
    private Button createChatRoomButton;

    @FXML
    private Text emptyUsernameField;

    @FXML
    public TextArea globalServerTextArea;

    @FXML
    private TextArea inputMessageTextArea;

    @FXML
    private Button joinChatRoomButton;

    @FXML
    private Button sendButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        Client c = Client.getClientInstance();
        emptyUsernameField.setText(c.getUsername());

    }

    @FXML
    public void onSendButtonClick(){
        String msg = inputMessageTextArea.getText();
        if(!msg.isEmpty()){
            msg = msg.trim();
            msg = msg.replaceAll("\n", " ");
            Client c = Client.getClientInstance();
            msg = c.getUsername() + " : " + msg + "\n";
            Message msg1 = new Message("LOBBY_MESSAGE_REQUEST",msg);
            c.sendMessage(msg1);
            inputMessageTextArea.clear();
        }
    }
    @FXML
    public void onCreateRoomButtonClick(){

        SceneManager.getInstance().switchScene("room-creation-window.fxml","Create Chat Room");
    }

    @FXML
    public void onJoinChatButtonClick(){
        SceneManager.getInstance().switchScene("join-room.fxml","Join Chat Room");
    }
}
