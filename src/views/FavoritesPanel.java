package views;

import entities.HttpRequest;
import entities.Favorite;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class FavoritesPanel extends JPanel {
    private final DefaultTableModel favoritesTableModel;
    private final JTable favoritesTable;
    private final Favorite favorite;
    private final RequestPanel requestPanel;

    public FavoritesPanel(RequestPanel requestPanel) {
        this.requestPanel = requestPanel;
        this.favorite = new Favorite();
        setLayout(new BorderLayout());

        favoritesTableModel = new DefaultTableModel(new String[]{"ID", "Nombre", "Método", "URL"}, 0);
        favoritesTable = new JTable(favoritesTableModel);
        JScrollPane favoritesScroll = new JScrollPane(favoritesTable);
        setBorder(BorderFactory.createTitledBorder("Favoritos"));

        JButton loadButton = new JButton("Cargar");
        JButton deleteButton = new JButton("Eliminar");
        JButton saveButton = new JButton("Guardar Actual");

        loadButton.addActionListener(this::loadFavorite);
        deleteButton.addActionListener(this::deleteFavorite);
        saveButton.addActionListener(this::saveFavorite);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(loadButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(saveButton);

        add(favoritesScroll, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);

        actualizarFavoritos();
    }

    private void saveFavorite(ActionEvent e) {
        String name = JOptionPane.showInputDialog(this, "Ingrese un nombre para el favorito:");
        if (name != null && !name.isBlank()) {
            String url = requestPanel.getUrl();
            String method = requestPanel.getMethod();
            String[][] headers = requestPanel.getHeaders();
            String[][] queryParams = requestPanel.getQueryParams();
            String body = requestPanel.getBody();
            HttpRequest request = new HttpRequest(url, method, headers, queryParams, body);

            favorite.saveFavorite(name, request);
            actualizarFavoritos();
        }
    }

    private void loadFavorite(ActionEvent e) {
        int selectedRow = favoritesTable.getSelectedRow();
        if (selectedRow != -1) {
            HttpRequest favorite = this.favorite.loadFavorites().get(selectedRow);
            requestPanel.setUrl(favorite.buildFullUrl());
            requestPanel.setMethod(favorite.getMethod());
            requestPanel.setParams(favorite.getQueryParams());
            requestPanel.setHeaders(favorite.getHeaders());
            requestPanel.setBody(favorite.getBody());
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un favorito para cargar.", "Cargar Favorito", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteFavorite(ActionEvent e) {
        int selectedRow = favoritesTable.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) favoritesTableModel.getValueAt(selectedRow, 0);
            favorite.deleteFavorite(id);
            actualizarFavoritos();
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un favorito para eliminar.", "Eliminar Favorito", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void actualizarFavoritos() {
        favoritesTableModel.setRowCount(0);
        List<HttpRequest> favorites = favorite.loadFavorites();

        for (HttpRequest favorite : favorites) {
            favoritesTableModel.addRow(new Object[]{
                    favorite.getId(),
                    favorite.getName(),
                    favorite.getMethod(),
                    favorite.buildFullUrl()
            });
        }
    }
}