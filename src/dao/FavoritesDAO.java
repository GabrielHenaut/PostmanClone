package dao;

import entities.HttpRequest;
import services.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoritesDAO implements IFavoritesDAO {
    @Override
    public void save(String name, HttpRequest request) {
        String insertSQL = """
                INSERT INTO favorites (name, url, method, headers, query_params, body)
                VALUES (?, ?, ?, ?, ?, ?);
                """;

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, request.buildFullUrl());
            preparedStatement.setString(3, request.getMethod());
            preparedStatement.setString(4, serializeMap(request.getHeaders()));
            preparedStatement.setString(5, serializeMap(request.getQueryParams()));
            preparedStatement.setString(6, request.getBody());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el favorito en la base de datos", e);
        }
    }

    @Override
    public List<HttpRequest> findAll() {
        List<HttpRequest> favorites = new ArrayList<>();
        String selectSQL = "SELECT id, name, url, method, headers, query_params, body FROM favorites ORDER BY id DESC;";

        try (Connection connection = DatabaseConfig.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSQL)) {

            while (resultSet.next()) {
                HttpRequest request = new HttpRequest(resultSet.getString("url"), resultSet.getString("method"));
                request.setBody(resultSet.getString("body"));
                deserializeMap(resultSet.getString("headers")).forEach(request::addHeader);
                deserializeMap(resultSet.getString("query_params")).forEach(request::addQueryParam);
                request.setId(resultSet.getInt("id"));
                request.setName(resultSet.getString("name"));
                favorites.add(request);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al cargar los favoritos desde la base de datos", e);
        }

        return favorites;
    }

    @Override
    public void delete(int id) {
        String deleteSQL = "DELETE FROM favorites WHERE id = ?;";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el favorito", e);
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