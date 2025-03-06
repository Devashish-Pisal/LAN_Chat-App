package com.lanchatapp.lanchatapp.Server;

import java.sql.*;

public class UserAuthServer {
    private static UserAuthServer instance;
    private UserAuthServer(){}

    public static UserAuthServer getInstance(){
        if(instance == null){
            instance = new UserAuthServer();
        }
        return instance;
    }

     static Connection connect(){
        String url = "jdbc:sqlite:src/main/resources/UserCredentials.db";
        try{
            return DriverManager.getConnection(url);
        }catch (SQLException e){
            e.printStackTrace();
            System.out.println("Connection with UserCredentials.db failed!");
            return null;
        }
    }


    void createTable(){
        String query = "CREATE TABLE IF NOT EXISTS users (" +
                "username TEXT NOT NULL UNIQUE PRIMARY KEY," +
                "password TEXT NOT NULL," +
                "status TEXT NOT NULL DEFAULT 'LOGGED_IN')";

        try(Connection conn = connect()){
            assert conn != null;
            try(Statement stm = conn.createStatement()){
                stm.execute(query);
                conn.close();
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }


     void insertSystemUser(){
        String query = "INSERT INTO users(username,password,status) VALUES('SYSTEM','SYSTEM','NO_STATUS')";
        try(Connection conn = connect()){
            assert conn != null;
            try(Statement stm = conn.createStatement()){
                stm.execute(query);
                conn.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



     void deleteUsersTableData(){
        String query = "DELETE FROM users WHERE username IS NOT 'SYSTEM'";
        try(Connection conn = connect()){
            assert conn != null;
            try(Statement stm = conn.createStatement()){
                stm.execute(query);
                conn.close();
                System.out.println("User Credentials deleted successfully!");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


     void insertUser(String username, String password){
        String query = "INSERT INTO users(username, password) VALUES(?,?)";
        try(Connection conn = connect()){
            assert conn != null;
            try(PreparedStatement pstm = conn.prepareStatement(query)){
                pstm.setString(1,username);
                pstm.setString(2,password);
                pstm.executeUpdate();
                System.out.println(username + " registered successfully!");
                conn.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


     boolean checkExistingUser(String username){
        String query = "SELECT COUNT(*) FROM users WHERE username='" + username + "'";
        createTable();
        try(Connection conn = connect()){
            assert conn != null;
            try(Statement stm = conn.createStatement()){
                ResultSet rs = stm.executeQuery(query);
                rs.next();
                if(rs.getInt(1) > 0){
                    conn.close();
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

     boolean verifyLoggedOutStatus(String username){
        String query = "SELECT COUNT(*) FROM users WHERE username=? AND status=?";
        try(Connection conn = connect()){
            PreparedStatement pstm = conn.prepareStatement(query);
            pstm.setString(1,username);
            pstm.setString(2,"LOGGED_OUT");
            ResultSet rs = pstm.executeQuery();
            rs.next();
            if(rs.getInt(1) == 0 || rs.getInt(1) > 1){
                return false;
            }
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    void changeStatusTo(String username, String status){
        if(!status.equals("LOGGED_OUT") && !status.equals("LOGGED_IN")){
            throw new IllegalArgumentException("Provided Status is invalid! Allowed values are LOGGED_IN/LOGGED_OUT.");
        }
        if(checkExistingUser(username)) {
            String query = "UPDATE users SET status=? WHERE username=?";
            try (Connection conn = connect()) {
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setString(1, status);
                pst.setString(2, username);
                pst.executeUpdate();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            throw new NullPointerException("User with username: " + username + " does not exists!");
        }
    }

    boolean verifyPassword(String username, String password){
        String query = "SELECT COUNT(*) FROM users WHERE username=? AND password=?";
        try(Connection conn = connect()){
            assert conn != null;
            try(PreparedStatement pstm = conn.prepareStatement(query)){
                pstm.setString(1,username);
                pstm.setString(2,password);
                ResultSet rs = pstm.executeQuery();
                rs.next();
                if(rs.getInt(1) == 1){
                    conn.close();
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
