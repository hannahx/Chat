/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Client connects to a server, and sends messages to it.
 */
public class Client implements Observer {
    
    private Socket clientSocket;
    private String in;
    private PrintWriter out = null;
    private BufferedReader input = null;
    private Profile profile;
    
    private DefaultListModel listModel;
    private ArrayList<String> onlineArr = new ArrayList<String>();
    
    JTextPane t;
    
    public Client(Profile profile) {
        
        this.profile = profile;
        
        listModel = new DefaultListModel();
        
//        if(profile.getView() != null)
//            t = profile.getView().getChatArea();
        
        //Connect to server
        try {
            if(profile.getIP().equals("local")) {
                profile.setIP(Inet4Address.getLocalHost().getHostAddress()); //TODO: Funkar det annars? Kolla!
            }      
            System.out.println("C: Attempting to connect to " + profile.getIP() + ":" + profile.getPort());
            clientSocket = new Socket(profile.getIP(), profile.getPort());
            
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); //TODO: input from server???
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
        
        System.out.println("C: connected");
        
        
        // Listen for message from Server (in new thread)
        ReadFromServer rs = new ReadFromServer();
        rs.start();
        
        //Send "connected" message
        Message m = new Message(profile, 1); //TODO: this message gets lost somewhere :(
        out.println(m.getMessage());
        

    }

    @Override
    public void update(Observable o, Object arg) {
        Message m = (Message)o;
        in = m.getMessage();
        out.println(in); //Send message to Server
    }
    
    public void parseXML(String s) throws Exception {
        if(profile.getChatView() != null)
                if(t==null)
                    t = profile.getChatView().getChatArea();
        
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(s));

        Document doc = db.parse(is);
        NodeList nodes = doc.getElementsByTagName("message");
        
        //System.out.println("client reads: " + s);

        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            String name = element.getAttribute("sender");

            Element line;
            String finalText = name;
            Color col = Color.black;

            if(element.getElementsByTagName("disconnect").item(0) != null) {
                NodeList disconnect = element.getElementsByTagName("disconnect");
                finalText += " logged out!\n";
            }
            else if (element.getElementsByTagName("online").item(0) != null){
                NodeList online = element.getElementsByTagName("online");
                //System.out.println("C: ONLINE");
                //TODO: hoppar över detta så länge
//                if(profile.getView().getInfoArea() != null) {
//                    JTextPane jt = profile.getView().getInfoArea();
//                    jt.setText(null);
//
//                    StyledDocument sdoc = jt.getStyledDocument();
//                    Style style = jt.addStyle("Style", null);
//                    StyleConstants.setForeground(style, col);
//
//                    for(int j=0; j<online.getLength(); j++) {
//                        //TODO: behövs id...??? isf färg hellre?!!!!!
//                        line = (Element) online.item(j);
//                        String n = line.getAttribute("name");
//
//                        try { sdoc.insertString(sdoc.getLength(), n + "\n", style); 
//                        } catch (Exception e) {
//                            System.out.println("info area null");
//                        }
//                    }
//                }
                
                JTextPane pane = profile.getClientView().getOnlineArea();
                pane.setText("");
                for(int j=0; j<online.getLength(); j++) {
                    line = (Element) online.item(j);
                    String n = line.getAttribute("name");
                    int id = Integer.parseInt(line.getAttribute("ID"));
                    if(id == profile.ID)
                        pane.setText(pane.getText() + n + " (you)\n");
                    else
                        pane.setText(pane.getText() + n + "\n");
                }  
            }
            else {
                finalText += ": ";
                NodeList text = element.getElementsByTagName("text");
                for(int j=0; j< text.getLength(); j++) {
                    line = (Element) text.item(j);
                    String Text = getCharacterDataFromElement(line);
                    finalText += Text + "\n";
                    String colorStr = line.getAttribute("color");
                    col =  Color.decode(colorStr);
                }
            }
            if(element.getElementsByTagName("connect").item(0) == null) {
                //Add to "chat area"
                StyledDocument sdoc = t.getStyledDocument();
                Style style = t.addStyle("Style", null);
                StyleConstants.setForeground(style, col);
                try { sdoc.insertString(sdoc.getLength(), finalText, style); 
                } catch (Exception e) {}
            }
            //TODO: how close socket?
//            if(Disconnect) {
//                clientSocket.close();
//                Disconnect = false;
//            }
        }
    }
        
    public String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
          CharacterData cd = (CharacterData) child;
          return cd.getData();
        }
        return "";
    }
    
    class ReadFromServer extends Thread {
        public void run() {            
            while(true) {
                try {
                    String IN = input.readLine();
                    if(IN==null) {
                        System.out.println("Server disconnect!");
                        System.exit(1);
                    }
                    try {
                        //System.out.println("C reads: " + IN);
                        parseXML(IN);
                    } catch(Exception e) {
                        try {
                            StyledDocument sdoc = profile.getChatView().getChatArea().getStyledDocument();
                            Style style = profile.getChatView().getChatArea().addStyle("Style", null);
                            StyleConstants.setForeground(style, Color.black);
                            sdoc.insertString(sdoc.getLength(), "Broken message... \n" ,style); 
                        } catch (Exception E) {}
                    }
                } catch (IOException ex) {
                    System.out.println("C: Error: Unable to read server response\n\t" + ex);
                }
            }

        }
    }
    
}
