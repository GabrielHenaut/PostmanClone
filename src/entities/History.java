package entities;

import dao.HistoryDAO;

import java.util.List;

public class History {
    private final HistoryDAO historyDAO;

    public History() {
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