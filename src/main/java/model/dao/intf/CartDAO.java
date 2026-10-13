package model.dao.intf;

import model.entity.Cart;

public interface CartDAO {

    boolean save(Cart cart);

    boolean update(Cart cart);

    Cart findByUserId(String userId);
}
