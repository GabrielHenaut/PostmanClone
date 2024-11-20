package views;

import javax.swing.*;
import java.awt.*;

import entities.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

public class ResponsePanel extends JPanel {
    private final JTextArea statusResponseArea;
    private final JTextArea rawResponseArea;
    private final JTextArea formattedResponseArea;
    private final JLabel imageLabel;
    private final JTabbedPane tabbedPane;

    public ResponsePanel() {
        setLayout(new BorderLayout());

        statusResponseArea = new JTextArea();
        statusResponseArea.setEditable(false);

        rawResponseArea = new JTextArea();
        rawResponseArea.setEditable(false);
        JScrollPane rawScroll = new JScrollPane(rawResponseArea);

        formattedResponseArea = new JTextArea();
        formattedResponseArea.setEditable(false);
        JScrollPane formattedScroll = new JScrollPane(formattedResponseArea);

        imageLabel = new JLabel("", SwingConstants.CENTER);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Crudo", rawScroll);
        tabbedPane.addTab("Formateado", formattedScroll);
        tabbedPane.addTab("Imagen", imageLabel);

        add(statusResponseArea, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    public void fillResponsePane(HttpResponse response) {
        this.setStatusResponseArea(response.getStatusCode());

        this.setRawResponse(response.getBody());

        String contentType = response.getContentType();
        this.setFormattedResponse(response.getBody(), contentType);
    }

    public void setStatusResponseArea(Integer statusCode) {
        statusResponseArea.setText("Response Status Code: " + statusCode.toString());
    }

    public void setRawResponse(String response) {
        rawResponseArea.setText(response);
    }

    public void setFormattedResponse(String response, String contentType) {
        if (contentType.contains("json")) {
            String formatted = (response.charAt(0) == '[' ? new JSONArray(response).toString(4) : new JSONObject(response).toString(4));
            formattedResponseArea.setText(formatted);
        } else if (contentType.contains("xml")) {
            formattedResponseArea.setText(XML.toString(new JSONObject(response)));
        } else {
            formattedResponseArea.setText("Formato desconocido.");
        }
    }
}
