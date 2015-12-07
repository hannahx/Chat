/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.util.ArrayList;

/**
 * Server connects to clients, reads all client messages and sends them back to all clients.
 */
public class Server {
    
    private ServerSocket serverSocket;
    private Socket clientSocket = null;
    private ChatModel model;
    private ArrayList<PrintWriter> outputs; //outputs to all clients
    
    public Server(int port, ChatModel model) {
        
        this.model = model;
        outputs = new ArrayList<PrintWriter>();
        
        try {
            serverSocket = new ServerSocket(port); //Connect server socket
        } catch(IOException e) {
            System.out.println(e);
            System.exit(-1);
        }
        // Listen for clients, start new thread for each client
        while(true) {
            try {
                System.out.println("S: Waiting for clients...");
                clientSocket = serverSocket.accept();
                System.out.println("S: Connection established!");

                PrintWriter OUT = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader IN = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                
                outputs.add(OUT);
                ClientHandler ch = new ClientHandler(IN);
                ch.start();

            } catch(IOException e) {
                System.out.println(e);
                System.exit(-1);
            }
        }
    }
    
    /**
     * A ClientHandler is created for each Client.
     * It displays the client's messages to all "chat areas".
     */
    class ClientHandler extends Thread {
        
        BufferedReader in;
        String echo;
        String remove = "";
                
        public ClientHandler(BufferedReader in) {
            this.in = in;
        }
        
        public void run () 
        {            
            //Read Client message
            while(true) {
                try {
                    echo = in.readLine();
                    if(echo==null) {
                        System.out.println("Client disconnect!");
                        System.exit(1);
                    }
                    //System.out.println("S reads: " + echo);
                    
                    //1. Check if connect/disconnect
                    try {
                        connection(echo);
                    } catch (Exception e) {
                        System.out.println("add something here.....");
                    }
                   
                    //2. Send message to all clients
                    for(int i=0; i<outputs.size(); i++) {
                        outputs.get(i).println(echo);
                    }
                    
                } catch(IOException e) {
                    System.out.println("readLine failed: " + e);
                    System.exit(1); //TODO: fix this!!!
                }
            }
        }
        
        public void connection(String s) throws Exception {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(s));

            Document doc = db.parse(is);
            NodeList nodes = doc.getElementsByTagName("message");
            
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                String name = element.getAttribute("sender");
                int id = Integer.parseInt(element.getAttribute("ID"));
                
                if(element.getElementsByTagName("connect").item(0) != null) {
                    System.out.println(name + " connected");
                    model.addName(name);
                    //int newID = model.generateID();
                    model.addID(id);
                    model.printModel();
                    updateInfo();
                }
                else if(element.getElementsByTagName("disconnect").item(0) != null) {
                    System.out.println(name + " disconnected");
                    model.remove(id);
                    model.printModel();
                    updateInfo();
                }
                
            }
        }
        
        public void updateInfo() {
            String s = "";
            for(int i=0; i<model.getIDs().size(); i++) {
                //s += "<online name = " + '"' + model.getNames().get(i) + '"' + " ID = " + '"' + model.getIDs().get(i) + '"' + "></online>";
                s += "<online name = " + '"' + model.getNames().get(i) + '"' + "></online>";
            }
            s = "<message>" + s + "</message>";
            for(int i=0; i<outputs.size(); i++) {
                outputs.get(i).println(s);
            }
        }
    }
}
