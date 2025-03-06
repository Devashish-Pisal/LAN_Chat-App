package com.lanchatapp.lanchatapp.Controllers;

import com.lanchatapp.lanchatapp.Client.Client;
import com.lanchatapp.lanchatapp.Messages.Message;
import com.lanchatapp.lanchatapp.Messages.Objects.UserData;
import com.lanchatapp.lanchatapp.Server.HashingAlgo;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterPageController {

    @FXML
    private Label enterUsernameLabel;

    @FXML
    private Label enterpasswordLabel;

    @FXML
    private Button okButton;

    @FXML
    private PasswordField password1TextField;

    @FXML
    private PasswordField password2TextField;

    @FXML
    private Label repeatPasswordLabel;

    @FXML
    private TextField usernameTextField;

    @FXML
    public Label warningLabel;

    @FXML
    public void onRegisterButtonClick(){
        String name = usernameTextField.getText();
        String pass1 = password1TextField.getText();
        String pass2 = password2TextField.getText();
        if(name.isEmpty()){
            warningLabel.setText("Username must not be empty!");
            return;
        }else if(pass1.isEmpty() || pass2.isEmpty()){
            warningLabel.setText("Password must not be empty!");
            return;
        }
        String regex = "^[A-Za-z0-9]{2,7}$";
        if(!name.matches(regex)){
            warningLabel.setText("In username only alphabets & nums are allowed (length 2-7)!");
            return;
        }else if(!pass1.equals(pass2)){
            warningLabel.setText("Password and repeated password are not same!");
            return;
        }else if(pass1.length() < 2 || pass1.length() > 7){
            warningLabel.setText("Allowed password length is 2-7!");
            return;
        }
        Client c = Client.getClientInstance();
        c.connectToServer("localhost", 5000, "SYSTEM");
        UserData data = new UserData(name, HashingAlgo.hashSHA256(pass1));
        Message msg = new Message("CHECK_DUPLICATE_USERNAME", data);
        c.sendMessage(msg);
    }

    @FXML
    public void clearWarningLabel(){
        warningLabel.setText("");
    }

}

