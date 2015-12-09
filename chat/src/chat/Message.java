/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.util.Observable;

/**
 *
 * @author Hannah
 */
public class Message extends Observable  {

    private Profile p;
    private String mess;
    private boolean disconnect = false;
    private boolean connect = false;
    
    /* Message constructor only used when sending messages. */
    public Message(String mess, Profile p) {
        this.p = p;
        this.mess = mess;
    }
    
    /* Use this to connect or disconnect 
       i=1: connect
       i=0: disconnect */
    public Message(Profile p, int i) {
        this.p = p;
        mess = "";
        if(i==1)
            connect = true;
        else if(i==0)
            disconnect = true;
    }

    public String getMessage() {
        String ret = convertToXML(mess);
        System.out.println("M: Message sent: " + ret);
        return ret;
    }
    
    // Try to use when connecting
    public Profile getProfile() {
        return p;
    }
    
    public String convertToXML(String m) {
        
        String name = p.getName().replace("<", "&lt;").replace(">", "&gt;");// replace signs in text to avoid errors    
        String message = "";

        if(connect) {
            message += "<connect />";
            connect = false;
        }
        else if(disconnect) {
            message += "<disconnect />";
            disconnect = false;
        }
        else {
            m = m.replace("<", "&lt;").replace(">", "&gt;");
            String[] arr = m.split("\n");
            for(int i=0; i<arr.length; i++) {
                message += "<text color = " + '"' + p.getColor() + '"' + ">" + arr[i] + "</text>";
            }
        }
        message = "<message sender = " + '"' + name + '"' + " ID = " + '"' + p.ID + '"' + ">" + message + "</message>";

        return message; //TODO: can't handle åäö (jo det funkar i vanlige fall, men inte till annan dator...)
    }  
}
