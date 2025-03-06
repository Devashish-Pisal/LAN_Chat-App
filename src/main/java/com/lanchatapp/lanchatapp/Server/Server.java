package com.lanchatapp.lanchatapp.Server;

import com.lanchatapp.lanchatapp.Client.Client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Server {
    private static Server serverInstance;
    private static final int PORT = 5000;
    private ServerSocket serverSocket;



    private Server(){
        startServer();
    }


    private void startServer(){
        try{
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on PORT : " + PORT);

            while(true){
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static synchronized Server getServerInstance(){
        if(serverInstance == null){
            serverInstance = new Server();
        }
        return serverInstance;
    }

    public static void main(String[] args){
        //UserAuthServer.getInstance().deleteUsersTableData();
        //UserAuthServer.getInstance().insertUser("dev", HashingAlgo.hashSHA256("dev"));
        UserAuthServer.getInstance().changeStatusTo("dev", "LOGGED_OUT");
        UserAuthServer.getInstance().changeStatusTo("deva", "LOGGED_OUT");
        UserAuthServer.getInstance().changeStatusTo("dp", "LOGGED_OUT");
        Server.getServerInstance().startServer();
    }


}
