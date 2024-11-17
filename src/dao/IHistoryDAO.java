package dao;

import entities.HttpRequest;

import java.util.List;

public interface IHistoryDAO {
    void save(HttpRequest request);
    List<HttpRequest> findAll();
    void delete(int id);
}