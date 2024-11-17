package dao;

import entities.HttpRequest;
import services.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryDAO implements IHistoryDAO {
    @Override
    public void save(HttpRequest request) {
        String insertSQL = """
                INSERT INTO history (url, method, headers, query_params, body)
                VALUES (?, ?, ?, ?, ?);
                """;

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, request.buildFullUrl());
            preparedStatement.setString(2, request.getMethod());
            preparedStatement.setString(3, serializeMap(request.getHeaders()));
            preparedStatement.setString(4, serializeMap(request.getQueryParams()));
            preparedStatement.setString(5, request.getBody());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar la solicitud en la base de datos", e);
        }
    }

    @Override
    public List<HttpRequest> findAll() {
        List<HttpRequest> history = new ArrayList<>();
        String selectSQL = "SELECT id, url, method, headers, query_params, body FROM history ORDER BY id DESC;";

        try (Connection connection = DatabaseConfig.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSQL)) {

            while (resultSet.next()) {
                HttpRequest request = new HttpRequest(resultSet.getInt("id"), resultSet.getString("url"), resultSet.getString("method"));
                request.setBody(resultSet.getString("body"));
                deserializeMap(resultSet.getString("headers")).forEach(request::addHeader);
                deserializeMap(resultSet.getString("query_params")).forEach(request::addQueryParam);
                history.add(request);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al cargar el historial desde la base de datos", e);
        }

        return history;
    }

    @Override
    public void delete(int id) {
        String deleteSQL = "DELETE FROM history WHERE id = ?;";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar la solicitud del historial", e);
        }
    }

    private String serializeMap(Map<String, String> map) {
        if (map.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        map.forEach((key, value) -> sb.append(key).append(":").append(value).append("\n"));
        return sb.toString();
    }

    private Map<String, String> deserializeMap(String serialized) {
        Map<String, String> map = new HashMap<>();
        if (serialized == null || serialized.isEmpty()) return map;
        String[] lines = serialized.split("\\n");
        for (String line : lines) {
            String[] parts = line.split(":", 2);
            if (parts.length == 2) {
                map.put(parts[0].trim(), parts[1].trim());
            }
        }
        return map;
    }
}
