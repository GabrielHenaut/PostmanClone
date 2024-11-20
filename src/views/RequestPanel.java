package views;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

public class RequestPanel extends JPanel {
    private final JTextField urlField;
    private final JComboBox<String> methodBox;
    private final JButton sendButton;
    private final JTable paramsTable;
    private final JTable headersTable;
    private final DefaultTableModel paramsTableModel;
    private final DefaultTableModel headersTableModel;
    private final JTextArea bodyArea;
    private final JTabbedPane topTabbedPane;
    private final JComboBox<String> urlSuggestions;
    private boolean isUserEditing = false;

    public RequestPanel() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        urlField = new JTextField();
        urlSuggestions = new JComboBox<>();
        urlSuggestions.setEditable(true);
        urlSuggestions.getEditor().setItem(urlField.getText());

        methodBox = new JComboBox<>(new String[]{"GET", "POST", "PUT", "DELETE"});
        sendButton = new JButton("Enviar Solicitud");


        JPanel methodPanel = new JPanel(new BorderLayout());
        methodPanel.add(methodBox, BorderLayout.CENTER);
        methodPanel.add(sendButton, BorderLayout.EAST);


        topPanel.add(new JLabel("URL:"), BorderLayout.WEST);
        topPanel.add(urlField, BorderLayout.CENTER);
        topPanel.add(methodPanel, BorderLayout.EAST);


        paramsTableModel = new DefaultTableModel(new String[]{"Clave", "Valor"}, 0);
        paramsTable = new JTable(paramsTableModel);
        JScrollPane paramsScroll = new JScrollPane(paramsTable);
        JPanel paramsPanel = new JPanel(new BorderLayout());
        paramsPanel.add(paramsScroll, BorderLayout.CENTER);

        JPanel paramsButtons = new JPanel();
        JButton addParamButton = new JButton("Agregar");
        JButton removeParamButton = new JButton("Eliminar");
        paramsButtons.add(addParamButton);
        paramsButtons.add(removeParamButton);
        paramsPanel.add(paramsButtons, BorderLayout.SOUTH);

        addParamButton.addActionListener(e -> paramsTableModel.addRow(new Object[]{"", ""}));
        removeParamButton.addActionListener(e -> {
            int selectedRow = paramsTable.getSelectedRow();
            if (selectedRow != -1) paramsTableModel.removeRow(selectedRow);
        });

        headersTableModel = new DefaultTableModel(new String[]{"Clave", "Valor"}, 0);
        headersTable = new JTable(headersTableModel);
        JScrollPane headersScroll = new JScrollPane(headersTable);
        JPanel headersPanel = new JPanel(new BorderLayout());
        headersPanel.add(headersScroll, BorderLayout.CENTER);

        JPanel headersButtons = new JPanel();
        JButton addHeaderButton = new JButton("Agregar");
        JButton removeHeaderButton = new JButton("Eliminar");
        headersButtons.add(addHeaderButton);
        headersButtons.add(removeHeaderButton);
        headersPanel.add(headersButtons, BorderLayout.SOUTH);

        addHeaderButton.addActionListener(e -> headersTableModel.addRow(new Object[]{"", ""}));
        removeHeaderButton.addActionListener(e -> {
            int selectedRow = headersTable.getSelectedRow();
            if (selectedRow != -1) headersTableModel.removeRow(selectedRow);
        });

        bodyArea = new JTextArea(15, 30);
        JScrollPane bodyScroll = new JScrollPane(bodyArea);
        bodyArea.setBorder(BorderFactory.createTitledBorder("Cuerpo (Body)"));

        topTabbedPane = new JTabbedPane();
        topTabbedPane.addTab("Params", paramsPanel);
        topTabbedPane.addTab("Headers", headersPanel);
        topTabbedPane.addTab("Body", bodyScroll);

        configureUrlAndQueryParamSync();

        add(topPanel, BorderLayout.NORTH);
        add(topTabbedPane, BorderLayout.CENTER);
    }

    private final DocumentListener urlSyncListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            handleUrlEdit();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            handleUrlEdit();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            handleUrlEdit();
        }
    };

    private void handleUrlEdit() {
        isUserEditing = true;
        try {
            updateQueryParamsFromUrl();
        } finally {
            isUserEditing = false;
        }
    }

    private void configureUrlAndQueryParamSync() {
        paramsTableModel.addTableModelListener(e -> {
            if (!isUserEditing) {
                updateUrlFromQueryParams();
            }
        });

        urlField.getDocument().addDocumentListener(urlSyncListener);
    }

    private void updateUrlFromQueryParams() {
        String baseUrl = urlField.getText().split("\\?")[0];
        StringBuilder queryParams = new StringBuilder();

        for (int i = 0; i < paramsTableModel.getRowCount(); i++) {
            String key = (String) paramsTableModel.getValueAt(i, 0);
            String value = (String) paramsTableModel.getValueAt(i, 1);

            if (key != null && !key.isBlank() && value != null && !value.isBlank()) {
                if (queryParams.length() > 0) {
                    queryParams.append("&");
                }
                queryParams.append(key).append("=").append(value);
            }
        }

        String updatedUrl = baseUrl + (queryParams.length() > 0 ? "?" + queryParams : "");
        urlField.getDocument().removeDocumentListener(urlSyncListener);
        try {
            urlField.setText(updatedUrl);
        } finally {
            urlField.getDocument().addDocumentListener(urlSyncListener);
        }
    }

    private void updateQueryParamsFromUrl() {
        String url = urlField.getText();
        if (url == null || url.isBlank()) {
            paramsTableModel.setRowCount(0);
            return;
        }

        try {
            String[] parts = url.split("\\?", 2);
            if (parts.length > 1) {
                String[] params = parts[1].split("&");
                paramsTableModel.setRowCount(0);

                for (String param : params) {
                    String[] keyValue = param.split("=", 2);
                    if (keyValue.length == 2 && !keyValue[0].isBlank() && !keyValue[1].isBlank()) {
                        paramsTableModel.addRow(new Object[]{keyValue[0], keyValue[1]});
                    }
                }
            } else {
                paramsTableModel.setRowCount(0);
            }
        } catch (Exception e) {
            paramsTableModel.setRowCount(0);
            System.err.println("Error al procesar parámetros de querystring: " + e.getMessage());
        }
    }

    public String getUrl() {
        return urlField.getText();
    }

    public String getMethod() {
        return (String) methodBox.getSelectedItem();
    }

    public JButton getSendButton() {
        return sendButton;
    }

    public String getBody() {
        return bodyArea.getText();
    }

    public void setBody(String body) {
        bodyArea.setText(body);
    }

    public String[][] getHeaders() {
        return getValues(headersTableModel);
    }

    public String[][] getQueryParams() {
        return getValues(paramsTableModel);
    }

    private String[][] getValues(DefaultTableModel table) {
        int rows = table.getRowCount();
        String[][] params = new String[rows][2];
        for (int i = 0; i < rows; i++) {
            // TODO take a look ata this bug where the value registers as 0 if the field is selected
            params[i][0] = (String) table.getValueAt(i, 0);
            params[i][1] = (String) table.getValueAt(i, 1);
        }
        return params;
    }

    public void setUrl(String url) {
        urlField.getDocument().removeDocumentListener(urlSyncListener);
        try {
            urlField.setText(url != null ? url : "");
        } finally {
            urlField.getDocument().addDocumentListener(urlSyncListener);
        }
    }

    public void setMethod(String method) {
        methodBox.setSelectedItem(method);
    }

    public void setHeaders(Map<String, String> headers) {
        setTables(headers, headersTableModel);
    }

    public void setParams(Map<String, String> params) {
        setTables(params, paramsTableModel);
    }

    public void setTables(Map<String, String> data, DefaultTableModel table) {
        table.setRowCount(0);
        data.forEach((key, value) -> {
            if (key != null && !key.isBlank() && value != null && !value.isBlank()) {
                table.addRow(new Object[]{key, value});
            }
        });
    }
}