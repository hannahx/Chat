/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import javax.swing.JFrame;

/**
 *
 * @author Hannah
 */
public class Profile {
    
    private String name;
    private int port;
    private String IP;
    private String color;
    private JFrame frame;
    private ChatView cv;
    private ClientView clv;
    int ID;
    
    public Profile(String name, String color, int port, String IP, JFrame frame, ChatView cv, int ID) {
        
        this.name = name;
        this.color = color;
        this.port = port;
        this.IP = IP;
        this.frame = frame;
        this.cv = cv;
        this.ID = ID;
        
        System.out.println("Client profile crated for " + name);
        //TODO: vad behövs inte längre här?
    }
    
    public String getName() {
        return name;
    }
    
    public String getColor() {
        return color;
    } 
    
    public int getPort() {
        return port;
    }
    
    public String getIP() {
        return IP;
    }
    
    public void setIP(String ip) {
        IP = ip;
    }
    
    public JFrame getFrame() {
        return frame;
    }
    
    public ChatView getChatView() {
        return cv;
    }
    
    public void setChatView(ChatView c) {
        cv = c;
    }
    
    public ClientView getClientView() {
        return clv;
    }
    
    public void setClientView(ClientView c) {
        clv = c;
    }
    
    public void setID(int id) {
        ID = id;
    }
}
