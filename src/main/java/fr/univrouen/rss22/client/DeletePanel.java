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

public class DeletePanel extends JPanel{
    // ATTRIBUTS
    private Client mainFrame;
    private JTextField requestGuidField;
    private JLabel guidLabel;
    private JButton sendDeleteRequest;

    private JTextPane result;

    // CONSTRUCTEURS
    public DeletePanel(Client mainFrame) {
        createView(mainFrame);
        placeComponents();
        createController();
    }

    // OUTILS
    private void createView(Client mainFrame) {
        this.mainFrame = mainFrame;


        requestGuidField = new JTextField();
        requestGuidField.setColumns(20);
        requestGuidField.setEditable(true);

        guidLabel = new JLabel("Guid :");

        sendDeleteRequest = new JButton("Delete item");

        result = new JTextPane();
        result.setEditable(false);
        result.setPreferredSize(new Dimension(700, 550));

    }

    public void placeComponents() {
        JPanel q = new JPanel(new GridLayout(1,3));{
                q.add(guidLabel);
                q.add(requestGuidField);
                q.add(sendDeleteRequest);
        }
        this.add(q);
        JScrollPane scrollPane = new JScrollPane(
                result,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.add(scrollPane);
    }

    public void createController() {
        sendDeleteRequest.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String txt;
                    String path = "/rss22/delete/";
                    String resultFormat = "application/xml";
                    if (requestGuidField != null && requestGuidField.getText().length() > 0) {
                        txt = requestGuidField.getText();
                        requestGuidField.setText("");
                    } else{
                        JOptionPane.showMessageDialog(getParent(),
                                "GUID field is empty. Please enter GUID.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    try {
                        System.out.println(mainFrame.getDomain() + ":" + mainFrame.getPort() + path + txt);
                        URL url = new URL(mainFrame.getDomain() + ":" + mainFrame.getPort() + path + txt);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("DELETE");
                        BufferedReader in;
                        if (100 <= con.getResponseCode() && con.getResponseCode() <= 399)
                            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        else
                            in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                        String inputLine;
                        StringBuilder content = new StringBuilder();
                        while ((inputLine = in.readLine()) != null) {
                            content.append(inputLine);
                        }
                        in.close();
                        con.disconnect();
                        result.setContentType(resultFormat);
                        if (resultFormat.equals("application/xml"))
                            result.setText(formatXMLStringWithIndent(content.toString(), 6, true));
                        else
                            result.setText(content.toString());

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        );
    }

    private String formatXMLStringWithIndent(String xmlString, int indent, boolean ignoreDeclaration) {

        try {
            InputSource src = new InputSource(new StringReader(xmlString));
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", indent);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, ignoreDeclaration ? "yes" : "no");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            Writer out = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(out));
            return out.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error occurs when pretty-printing xml:\n" + xmlString, e);
        }
    }

}
