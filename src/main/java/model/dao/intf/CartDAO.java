package model.dao.intf;

import model.entity.Cart;

public interface CartDAO {
    boolean save(Cart cart);
    boolean update(Cart cart);
    Cart findByUserId(String userId);
    boolean addItem(String userId, String productId, int quantity);
    boolean updateItem(String userId, String productId, int quantity);
    boolean removeItem(String userId, String productId);
    boolean clear(String userId);
}
