package com.lanchatapp.lanchatapp.Controllers;

import com.lanchatapp.lanchatapp.Client.Client;
import com.lanchatapp.lanchatapp.Messages.Message;
import com.lanchatapp.lanchatapp.Messages.Objects.LeaveRoomData;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

public class RoomController implements Initializable {

    @FXML
    public TextArea chatTextArea;

    @FXML
    private TextArea inputTextField;

    @FXML
    private Button leaveRoomButton;

    @FXML
    private ListView<String> membersList;

    @FXML
    private Label roomNameTextField;

    @FXML
    private Button sendButton;


    @FXML
    public void onSendButtonClick(){
        String msg = inputTextField.getText();
        if(!msg.isEmpty()){
            msg = msg.trim();
            msg = msg.replaceAll("\n", " ");
            Client c = Client.getClientInstance();
            msg = c.getUsername() + " : " + msg + "\n";
            Message msg1 = new Message("ROOM_MESSAGE_REQUEST",msg);
            c.sendMessage(msg1);
            inputTextField.clear();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Client c = Client.getClientInstance();
        roomNameTextField.setText(c.getCurrentRoom());
    }

    @FXML
    public void onLeaveRoomButtonClick(){
        Client c = Client.getClientInstance();
        LeaveRoomData data = new LeaveRoomData(c.getUsername(), c.getCurrentRoom());
        Message msg = new Message("LEAVE_ROOM",data);
        c.sendMessage(msg);
    }
}
