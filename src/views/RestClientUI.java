package views;

import entities.HttpRequest;
import entities.HttpResponse;
import services.RestClient;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class RestClientUI extends JFrame {
    private RestClient restClient;
    private final RequestPanel requestPanel;
    private final ResponsePanel responsePanel;
    private final HistoryPanel historyPanel;
    private final FavoritesPanel favoritesPanel;
    private final JTabbedPane sideTabbedPane;
    private final JButton toggleButton;

    public RestClientUI(RestClient restClient) {
        super("Cliente REST");
        this.restClient = restClient;
        setLayout(new BorderLayout());
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        requestPanel = new RequestPanel();
        configureSendButton();
        responsePanel = new ResponsePanel();
        historyPanel = new HistoryPanel(requestPanel);
        favoritesPanel = new FavoritesPanel(requestPanel);

        sideTabbedPane = new JTabbedPane();
        sideTabbedPane.addTab("Historial", historyPanel);
        sideTabbedPane.addTab("Favoritos", favoritesPanel);

        toggleButton = new JButton("Ocultar Paneles");
        toggleButton.addActionListener(e -> toggleTabbedPaneVisibility());

        JPanel sidePanel = new JPanel(new BorderLayout());
        sidePanel.add(sideTabbedPane, BorderLayout.CENTER);
        sidePanel.add(toggleButton, BorderLayout.SOUTH);

        add(requestPanel, BorderLayout.NORTH);
        add(responsePanel, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);

        setVisible(true);
    }

    private void configureSendButton() {
        requestPanel.getSendButton().addActionListener(e -> sendRequest());
    }

    private void sendRequest() {
        HttpRequest request = setupRequest();

        try {
            HttpResponse response = restClient.ejecutarRequest(request);
            historyPanel.actualizarHistorial();
            responsePanel.fillResponsePane(response);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al ejecutar la solicitud: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private HttpRequest setupRequest() {
        String url = requestPanel.getUrl();
        String method = requestPanel.getMethod();
        String[][] headers = requestPanel.getHeaders();
        String[][] queryParams = requestPanel.getQueryParams();
        String body = requestPanel.getBody();

        return new HttpRequest(url, method, headers, queryParams, body);
    }

    private void toggleTabbedPaneVisibility() {
        boolean isVisible = sideTabbedPane.isVisible();
        sideTabbedPane.setVisible(!isVisible);
        toggleButton.setText(isVisible ? "Mostrar Paneles" : "Ocultar Paneles");
        revalidate();
        repaint();
    }
}