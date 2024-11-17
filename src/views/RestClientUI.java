package views;

import entities.HttpRequest;
import services.HistoryManager;
import services.RestClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class RestClientUI extends JFrame {
    private JTextField urlField;
    private JComboBox<String> methodBox;
    private JTextArea headersArea;
    private JTextArea bodyArea;
    private JTextArea responseArea;
    private JTable historyTable;
    private DefaultTableModel historyTableModel;
    private JButton sendButton, deleteHistoryButton, loadRequestButton;
    private RestClient restClient;
    private HistoryManager historyManager;

    public RestClientUI() {
        super("Cliente REST");
        restClient = new RestClient("");
        historyManager = new HistoryManager();

        setLayout(new BorderLayout());
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel superior para la URL y método
        JPanel topPanel = new JPanel(new BorderLayout());
        urlField = new JTextField();
        methodBox = new JComboBox<>(new String[]{"GET", "POST", "PUT", "DELETE"});
        topPanel.add(new JLabel("URL:"), BorderLayout.WEST);
        topPanel.add(urlField, BorderLayout.CENTER);
        topPanel.add(methodBox, BorderLayout.EAST);

        // Panel para Headers
        headersArea = new JTextArea(5, 30);
        JScrollPane headersScroll = new JScrollPane(headersArea);
        headersArea.setBorder(BorderFactory.createTitledBorder("Headers (key:value)"));

        // Panel para Body
        bodyArea = new JTextArea(5, 30);
        JScrollPane bodyScroll = new JScrollPane(bodyArea);
        bodyArea.setBorder(BorderFactory.createTitledBorder("Cuerpo (Body)"));

        // Panel para la respuesta
        responseArea = new JTextArea(10, 30);
        JScrollPane responseScroll = new JScrollPane(responseArea);
        responseArea.setEditable(false);
        responseArea.setBorder(BorderFactory.createTitledBorder("Respuesta"));

        // Panel para historial
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyTableModel = new DefaultTableModel(new String[]{"ID", "Método", "URL", "Headers"}, 0);
        historyTable = new JTable(historyTableModel);
        JScrollPane historyScroll = new JScrollPane(historyTable);
        historyPanel.setBorder(BorderFactory.createTitledBorder("Historial de Solicitudes"));
        historyPanel.add(historyScroll, BorderLayout.CENTER);

        // Botones para historial
        JPanel historyButtons = new JPanel();
        deleteHistoryButton = new JButton("Eliminar Selección");
        deleteHistoryButton.addActionListener(this::deleteSelectedHistory);

        loadRequestButton = new JButton("Cargar Solicitud");
        loadRequestButton.addActionListener(this::loadSelectedRequest);

        historyButtons.add(deleteHistoryButton);
        historyButtons.add(loadRequestButton);
        historyPanel.add(historyButtons, BorderLayout.SOUTH);

        // Botón para enviar solicitud
        sendButton = new JButton("Enviar");
        sendButton.addActionListener(this::sendRequest);

        // Agregar componentes al layout principal
        JPanel centerPanel = new JPanel(new GridLayout(3, 1));
        centerPanel.add(headersScroll);
        centerPanel.add(bodyScroll);
        centerPanel.add(responseScroll);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(historyPanel, BorderLayout.EAST);
        add(sendButton, BorderLayout.SOUTH);

        setVisible(true);

        actualizarHistorial(); // Cargar historial al iniciar

        // Permitir doble clic para cargar solicitud desde la tabla
        historyTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    loadSelectedRequest(null);
                }
            }
        });
    }

    private void sendRequest(ActionEvent e) {
        String url = urlField.getText();
        String metodo = (String) methodBox.getSelectedItem();

        restClient = new RestClient(url);
        cargarHeaders();
        cargarQueryParams();
        restClient.definirCuerpo(bodyArea.getText());

        try {
            String respuesta = restClient.ejecutarRequest(metodo).getBody();
            responseArea.setText(respuesta);

            historyManager.saveRequest(restClient.getRequest());
            actualizarHistorial();
        } catch (IOException ex) {
            responseArea.setText("Error en la solicitud: " + ex.getMessage());
        }
    }

    private void deleteSelectedHistory(ActionEvent e) {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) historyTableModel.getValueAt(selectedRow, 0);
            historyManager.deleteRequest(id);
            actualizarHistorial();
        } else {
            JOptionPane.showMessageDialog(this, "Por favor seleccione una fila para eliminar.", "Eliminar Historial", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void loadSelectedRequest(ActionEvent e) {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow != -1) {
            HttpRequest request = historyManager.loadHistory().get(selectedRow);

            // Cargar los datos de la solicitud seleccionada
            urlField.setText(request.buildFullUrl());
            methodBox.setSelectedItem(request.getMethod());
            headersArea.setText(serializeHeaders(request.getHeaders()));
            bodyArea.setText(request.getBody());
        } else {
            JOptionPane.showMessageDialog(this, "Por favor seleccione una fila para cargar.", "Cargar Solicitud", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void actualizarHistorial() {
        historyTableModel.setRowCount(0);
        List<HttpRequest> history = historyManager.loadHistory();

        for (HttpRequest request : history) {
            historyTableModel.addRow(new Object[]{
                    request.getId(),
                    request.getMethod(),
                    request.buildFullUrl(),
                    serializeHeaders(request.getHeaders())
            });
        }
    }

    private void cargarHeaders() {
        String[] headers = headersArea.getText().split("\\n");
        System.out.println(headers[0]);
        for (String header : headers) {
            String[] parts = header.split(":", 2);
            if (parts.length == 2) {
                restClient.agregarHeader(parts[0].trim(), parts[1].trim());
            }
        }
    }

    private void cargarQueryParams() {
        // Lógica para cargar los parámetros desde la tabla de query
    }

    private String serializeHeaders(Map<String, String> headers) {
        StringBuilder sb = new StringBuilder();
        headers.forEach((key, value) -> sb.append(key).append(": ").append(value).append("\n"));
        return sb.toString().trim();
    }
}