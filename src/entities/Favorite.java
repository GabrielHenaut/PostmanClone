package entities;

import dao.FavoritesDAO;

import java.util.List;

public class Favorite {
    private final FavoritesDAO favoritesDAO;

    public Favorite() {
        this.favoritesDAO = new FavoritesDAO();
    }

    public void saveFavorite(String name, HttpRequest request) {
        favoritesDAO.save(name, request);
    }

    public List<HttpRequest> loadFavorites() {
        return favoritesDAO.findAll();
    }

    public void deleteFavorite(int id) {
        favoritesDAO.delete(id);
    }
}