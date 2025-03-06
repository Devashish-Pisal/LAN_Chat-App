package com.lanchatapp.lanchatapp.Controllers;

import com.lanchatapp.lanchatapp.Client.Client;
import com.lanchatapp.lanchatapp.Messages.Message;
import com.lanchatapp.lanchatapp.Messages.Objects.UserData;
import com.lanchatapp.lanchatapp.SceneManager;
import com.lanchatapp.lanchatapp.Server.HashingAlgo;
import com.lanchatapp.lanchatapp.Server.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginPageController {

    @FXML
    private Button loginButton;

    @FXML
    private Label orLabel;

    @FXML
    private Label passwordLabel;

    @FXML
    private TextField passwordTextField;

    @FXML
    private Button registerButton;

    @FXML
    private Label usernameLabel;

    @FXML
    public TextField usernameTextField;

    @FXML
    public Label warningLabel;

    @FXML
    private void onEnterButtonClick(){
        String name = usernameTextField.getText();
        if(name.isEmpty()){
            warningLabel.setText("Username must not be empty!");
            return;
        }else if (name.equals("SYSTEM")){
            warningLabel.setText("Username 'SYSTEM' is not allowed!");
            return;
        }
        if(passwordTextField.getText().isEmpty()){
            warningLabel.setText("Password must not be empty!");
            return;
        }
        String pass = HashingAlgo.hashSHA256(passwordTextField.getText());
        String regex = "^[A-Za-z0-9]{2,7}$";
        if(name.matches(regex)){
            UserData data = new UserData(name, pass);
            Message msg = new Message("CREDENTIALS_CHECK",data);
            Client client = Client.getClientInstance();
            client.connectToServer("localhost",5000,"SYSTEM");
            Client.getClientInstance().sendMessage(msg);
        }else{
            warningLabel.setText("In username only alphabets & nums are allowed (length 2-7)!");
            return;
        }
    }

    @FXML
    private void onRegisterButtonClicked(){
        SceneManager.getInstance().switchScene("register-page.fxml","Register");
    }


    @FXML
    private void clearWarningLabel(){
        warningLabel.setText("");
    }

}
