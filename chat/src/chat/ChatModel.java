/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.util.ArrayList;

/**
 * ChatModel keeps track of all online profiles.
 * And removes profiles if thy logs out.
 */
public class ChatModel {
//TODO: ska man kunna ha fler servers?
    int ID = 0;
    ArrayList<String> names;
    ArrayList<Integer> IDs;
    
    ArrayList<String> groupChats;
    ArrayList<Integer> groupChatIDs;
    
    public ChatModel() {
        names = new ArrayList<String>();
        IDs = new ArrayList<Integer>();
        
        groupChats = new ArrayList<String>();
        groupChatIDs = new ArrayList<Integer>();
    }
    
    public int generateID() {
        return ID++;
    }
    public void addName(String name) {
        names.add(name);
    }
    public void addID(int id) {
        IDs.add(id);
    }
    public ArrayList<String> getNames() {
        return names;
    }
    public ArrayList<Integer> getIDs() {
        return IDs;
    }
    public void remove(int id) {
        int index = IDs.indexOf(id);
        IDs.remove(index);
        names.remove(index);
    }
    public void printModel() {
        for(int i=0; i<IDs.size(); i++) {
            System.out.println(IDs.get(i) + " " + names.get(i));
        }
    }
}
