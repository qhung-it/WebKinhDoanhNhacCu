package model.dao.intf;

import model.entity.Cart;

public interface CartDAO {

    Cart findByUserId(String userId);
}