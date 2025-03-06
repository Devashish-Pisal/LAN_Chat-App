package com.lanchatapp.lanchatapp.Controllers;

import com.lanchatapp.lanchatapp.Client.Client;
import com.lanchatapp.lanchatapp.Messages.Message;
import com.lanchatapp.lanchatapp.Messages.Objects.RoomData;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class RoomCreationController {

    @FXML
    private TextField maxParticipantsTextField;

    @FXML
    private Button okButton;

    @FXML
    private TextField roomNameTextField;

    @FXML
    public Label warningLabel;


    @FXML
    public void onOkButtonClick(){
        String name = roomNameTextField.getText();
        String maxParticipants = maxParticipantsTextField.getText();
        String nameRegex = "^[a-zA-Z]{2,7}$";
        String participantsRegex = "^[0-9]{1,2}$";
        if(!name.matches(nameRegex)){
            warningLabel.setText("Room Name can only contain alphabets (Length 2-7)!");
            return;
        }else if(name.equals("LOBBY")){
            warningLabel.setText("Room Name 'LOBBY' is not allowed!");
            return;
        }else if(!maxParticipants.matches(participantsRegex)){
            warningLabel.setText("Max Participants number is not valid!");
            return;
        }
        int num = Integer.parseInt(maxParticipants);
        if(num < 2 || num > 15){
            warningLabel.setText("Allowed range for Max Participants is 2-15!");
            return;
        }
        RoomData data = new RoomData(name,num);
        Message msg = new Message("CREATE_ROOM_REQUEST",data);
        Client.getClientInstance().sendMessage(msg);
    }

    @FXML
    public void clearWarningLabel(){
        warningLabel.setText("");
    }
}

