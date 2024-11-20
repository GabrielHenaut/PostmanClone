package views;

import entities.HttpRequest;
import entities.History;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class HistoryPanel extends JPanel {
    private final DefaultTableModel historyTableModel;
    private final JTable historyTable;
    private final History history;
    private final RequestPanel requestPanel;

    public HistoryPanel(RequestPanel requestPanel) {
        this.requestPanel = requestPanel;
        this.history = new History();
        setLayout(new BorderLayout());

        historyTableModel = new DefaultTableModel(new String[]{"ID", "Método", "URL"}, 0);
        historyTable = new JTable(historyTableModel);
        JScrollPane historyScroll = new JScrollPane(historyTable);
        setBorder(BorderFactory.createTitledBorder("Historial"));

        JButton loadButton = new JButton("Cargar");
        JButton deleteButton = new JButton("Eliminar");

        loadButton.addActionListener(this::loadRequest);
        deleteButton.addActionListener(this::deleteRequest);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(loadButton);
        buttonsPanel.add(deleteButton);

        add(historyScroll, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);

        actualizarHistorial();
    }

    private void loadRequest(ActionEvent e) {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow != -1) {
            HttpRequest request = history.loadHistory().get(selectedRow);
            requestPanel.setUrl(request.buildFullUrl());
            requestPanel.setMethod(request.getMethod());
            requestPanel.setParams(request.getQueryParams());
            requestPanel.setHeaders(request.getHeaders());
            requestPanel.setBody(request.getBody());
        }
    }

    private void deleteRequest(ActionEvent e) {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) historyTableModel.getValueAt(selectedRow, 0);
            history.deleteRequest(id);
            actualizarHistorial();
        }
    }

    public void actualizarHistorial() {
        historyTableModel.setRowCount(0);
        List<HttpRequest> history = this.history.loadHistory();

        for (HttpRequest request : history) {
            historyTableModel.addRow(new Object[]{
                    request.getId(),
                    request.getMethod(),
                    request.buildFullUrl()
            });
        }
    }
}
