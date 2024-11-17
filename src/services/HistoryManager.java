package services;

import dao.HistoryDAO;
import entities.HttpRequest;

import java.util.List;

public class HistoryManager {
    private final HistoryDAO historyDAO;

    public HistoryManager() {
        this.historyDAO = new HistoryDAO();
    }

    public void saveRequest(HttpRequest request) {
        historyDAO.save(request);
    }

    public List<HttpRequest> loadHistory() {
        return historyDAO.findAll();
    }

    public void deleteRequest(int id) {
        historyDAO.delete(id);
    }
}