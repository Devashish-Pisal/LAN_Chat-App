package com.lanchatapp.lanchatapp.Server;

import com.lanchatapp.lanchatapp.Client.Client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SessionManager {
    private static SessionManager instance;
    private HashMap<String, List<ClientHandler>> rooms;
    private HashMap<String,Integer> roomCapacity;

    private SessionManager(){
        rooms = new HashMap<>();
        roomCapacity = new HashMap<>();
        rooms.put("LOBBY", new ArrayList<>());
    }

    public static SessionManager getInstance(){
        if(instance == null){
            instance = new SessionManager();
        }
        return instance;
    }

    void addClientToSession(String sessionName, ClientHandler ch){
        if(rooms.containsKey(sessionName)){
            List<ClientHandler> l = rooms.get(sessionName);
            l.add(ch);
            rooms.replace(sessionName,l);
        }else{
            List<ClientHandler> l = new ArrayList<>();
            l.add(ch);
            rooms.put(sessionName,l);
        }
    }

    List<ClientHandler> getClientHandlerList(String roomName){
        return rooms.get(roomName);
    }

    boolean checkAvailableRoom(String name){
        return rooms.containsKey(name);
    }

    ClientHandler removeClientHandlerFromRoom(String room, String username){
        List<ClientHandler> ch = getClientHandlerList(room);
        List<ClientHandler> newList = new ArrayList<>();
        ClientHandler result = null;
        for(ClientHandler c : ch){
            if(!c.getUsername().equals(username)){
                newList.add(c);
            }
            if(c.getUsername().equals(username)){
                result = c;
            }
        }
        if(!newList.isEmpty()) {
            rooms.replace(room, newList);
        }else{
            rooms.remove(room);
        }
        return result;
    }

    void createNewRoom(String room, ClientHandler ch){
        if(!checkAvailableRoom(room)) {
            List<ClientHandler> list = new ArrayList<>();
            list.add(ch);
            rooms.put(room, list);
        }
    }

    boolean isRoomFull(String name){
        int capacity = roomCapacity.get(name);
        int currentSize = rooms.get(name).size();
        if(currentSize < capacity){
            return false;
        }
        return true;
    }

    void addRoomCapacity(String name, int capacity){
        roomCapacity.put(name,capacity);
    }

    HashMap<String, String> createOnlineRoomList(){
        HashMap<String,String> result = new HashMap<>();
            for(String key : rooms.keySet()){
                if(!key.equals("LOBBY")){
                    String playersInRoom = String.valueOf(rooms.get(key).size()) + "/" + roomCapacity.get(key);
                    result.put(key,playersInRoom);
                }
            }
            return result;
    }

    List<String> createMembersList(String roomName){
        List<String> list = new ArrayList<>();
        List<ClientHandler> ch = getClientHandlerList(roomName);
        if(ch != null) {
            for (ClientHandler c : ch) {
                list.add(c.getUsername());
            }
            return list;
        }
        return null;
    }

}
