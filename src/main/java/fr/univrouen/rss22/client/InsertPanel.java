package fr.univrouen.rss22.client;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class InsertPanel extends JPanel{
    // ATTRIBUTS
    private JTextArea insertDatabaseField;
    private JButton insertDatabaseButton;
    private JTextPane result;

    private Client mainFrame;


    // CONSTRUCTEURS
    public InsertPanel(Client mainFrame) {
        createView(mainFrame);
        placeComponents();
        createController();
    }
    // COMMANDES

    // OUTILS
    private void createView(Client mainFrame) {
        this.mainFrame = mainFrame;
        insertDatabaseField = new JTextArea();
        insertDatabaseField.setPreferredSize(new Dimension(700, 500));
        insertDatabaseField.setEditable(true);
        insertDatabaseButton = new JButton("Insert to  Database");

        result = new JTextPane();
        result.setEditable(false);
        result.setPreferredSize(new Dimension(700, 100));


    }

    public void placeComponents() {
        JPanel q = new JPanel(); {
            JScrollPane jScrollPane = new JScrollPane(
                    insertDatabaseField,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
            );
            q.add(jScrollPane);
            q.add(insertDatabaseButton);
        }
        this.add(q);
        this.add(new JLabel("Request result :"));
        JScrollPane scrollPane = new JScrollPane(
                result,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.add(scrollPane);
    }

    public void createController() {
        insertDatabaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (insertDatabaseField != null) {
                    if (insertDatabaseField.getText().length() == 0) {
                        JOptionPane.showMessageDialog(getParent(),
                                "Data field is empty. Please enter XML before insert.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }else{
                        HttpURLConnection con;
                        try {
                            URL url = new URL(mainFrame.getDomain() + ":" + mainFrame.getPort() + "/insertTest");
                            con = (HttpURLConnection) url.openConnection();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        try {
                            con.setRequestMethod("POST");
                            con.setDoOutput(true);
                            con.setRequestProperty("Content-Type", "application/xml");
                            OutputStreamWriter out = new OutputStreamWriter(
                                    con.getOutputStream());
                            out.write(insertDatabaseField.getText());
                            out.close();
                            con.connect();
                        } catch (IOException ex) {

                        } finally {
                            BufferedReader br;
                            try {
                                if (100 <= con.getResponseCode() && con.getResponseCode() <= 399) {
                                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                                } else {
                                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                                }
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }

                            String response = "";
                            String line;
                            try {
                                while ((line = br.readLine()) != null) {
                                    response += line + "\n";
                                }
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            result.setContentType("application/xml");
                            result.setText(formatXMLStringWithIndent(response, 6));
                        }
                    }
                }
            }
        });
    }

    private String formatXMLStringWithIndent(String xmlString, int indent) {

        try {
            InputSource src = new InputSource(new StringReader(xmlString));
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", indent);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            Writer out = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(out));
            return out.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error occurs when pretty-printing xml:\n" + xmlString, e);
        }
    }

}
