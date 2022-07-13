package fr.univrouen.rss22.client;

import javax.swing.*;
import java.awt.*;

public class Client {
    // ATTRIBUTS
    private JFrame mainFrame;

    private String domain;
    private String port;

    // CONSTRUCTEURS
    public Client() {
        createView();
        placeComponents();
        createController();
    }
    // COMMANDES
    public void display() {
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    public void setPort(String port){
        this.port = port;
    }
    public void setDomain(String domain){
        this.domain = domain;
    }

    public String getPort(){
        return this.port;
    }
    public String getDomain(){
        return this.domain;
    }

    // OUTILS
    private void createView() {
        final int frameWidth = 1000;
        final int frameHeight = 800;
        mainFrame = new JFrame("Client RSS22");
        mainFrame.setPreferredSize(new Dimension(frameWidth, frameHeight));

        port = "";
        domain = "https://rss22-hoche-pires.cleverapps.io";

    }

    public void placeComponents() {
        JTabbedPane tabbedPane =new JTabbedPane();
        tabbedPane.setBounds(25,25, 900,700);
        tabbedPane.add("Resume", new ResumePanel(this));
        tabbedPane.add("Insert",  new InsertPanel(this));
        tabbedPane.add("Delete", new DeletePanel(this));
        tabbedPane.add("Configuration", new ConfigPanel(this));

        mainFrame.add(tabbedPane);
        mainFrame.setSize(mainFrame.getWidth(),mainFrame.getHeight());
        mainFrame.setLayout(null);
        mainFrame.setVisible(true);
    }

    public void createController() {
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    // MAIN FUNCTION

    public static void main (String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Client().display();
            }
        });
    }
}
