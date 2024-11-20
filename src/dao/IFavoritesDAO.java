package dao;

import entities.HttpRequest;

import java.util.List;

public interface IFavoritesDAO {
    void save(String name, HttpRequest request);
    List<HttpRequest> findAll();
    void delete(int id);
}