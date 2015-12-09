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
public class Chat {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //ChatModel model = new ChatModel();
        JFrame frame = new JFrame("Chat set up");
        InitView window1 = new InitView();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //TODO: nej - förstör för servern om nån annan stänger av... (eller är det verkligen därför? tror inte det..)
        frame.add(window1);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
}
