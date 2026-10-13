package controller.service.impl;

import controller.service.intf.CartService;
import model.dao.impl.CartDAOImpl;
import model.dao.intf.CartDAO;
import model.entity.Cart;

public class CartServiceImpl implements CartService {
    private final CartDAO cartDAO = new CartDAOImpl();
    @Override public Cart getGioHangTheoUser(String userId) { return cartDAO.findByUserId(userId); }
    @Override public boolean themSanPham(String userId, String productId, int quantity) { return cartDAO.addItem(userId, productId, quantity); }
    @Override public boolean capNhatSanPham(String userId, String productId, int quantity) { return cartDAO.updateItem(userId, productId, quantity); }
    @Override public boolean xoaSanPham(String userId, String productId) { return cartDAO.removeItem(userId, productId); }
    @Override public boolean xoaTatCa(String userId) { return cartDAO.clear(userId); }
}
