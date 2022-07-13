package fr.univrouen.rss22.client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConfigPanel extends JPanel {
    // ATTRIBUTS
    private Client mainFrame;
    private JTextField domainTextField;
    private JTextField portTextField;
    private JButton updateConfigButton;

    private JLabel portLabel;

    private JLabel domainLabel;

    // CONSTRUCTEURS
    public ConfigPanel(Client mainFrame) {
        if(mainFrame != null) {
            createView(mainFrame);
            placeComponents();
            createController();
        }
    }
    //Commande

    // OUTILS
    private void createView(Client mainFrame) {

        this.mainFrame = mainFrame;

        domainLabel = new JLabel("Nom de domaine :");
        domainTextField = new JTextField();
        domainTextField.setEditable(true);
        domainTextField.setText("https://rss22-hoche-pires.cleverapps.io");
        domainTextField.setColumns(20);

        portLabel = new JLabel("port :");
        portTextField = new JTextField();
        portTextField.setEditable(true);
        portTextField.setColumns(20);

        updateConfigButton = new JButton("Update Configuration");
    }
    public void placeComponents() {
        JPanel q = new JPanel(); {
            q.add(domainLabel);
            q.add(domainTextField);
            q.add(portLabel);
            q.add(portTextField);
            q.add(updateConfigButton);
        }
        this.add(q);
    }
    public void createController() {
        updateConfigButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.setPort(portTextField.getText());
                mainFrame.setDomain(domainTextField.getText());
                JOptionPane.showConfirmDialog(null,
                        "Update successfully.",
                        "Confirmation",
                        JOptionPane.DEFAULT_OPTION);
            }
        });
    }
}

