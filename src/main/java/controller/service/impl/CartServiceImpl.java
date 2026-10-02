package controller.service.impl;

import controller.service.intf.CartService;
import model.dao.impl.CartDAOImpl;
import model.dao.intf.CartDAO;
import model.entity.Cart;

public class CartServiceImpl implements CartService {

    private final CartDAO cartDAO;

    public CartServiceImpl() {
        this.cartDAO = new CartDAOImpl();
    }

    @Override
    public Cart getGioHangTheoUser(String userId) {
        return cartDAO.findByUserId(userId);
    }
}