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

    @Override
    public void luuGioHang(Cart cart) {
        if (cart == null || cart.getUser() == null) throw new IllegalArgumentException("Thiếu thông tin giỏ hàng");
        Cart current = cartDAO.findByUserId(cart.getUser().getUserId());
        if (current == null) {
            if (cart.getCartId() == null || cart.getCartId().isBlank()) throw new IllegalArgumentException("Thiếu mã giỏ hàng");
            if (!cartDAO.save(cart)) throw new IllegalStateException("Không lưu được giỏ hàng");
        } else {
            cart.setCartId(current.getCartId());
            for (model.entity.CartLineItem item : cart.getCartLineItems()) item.setCart(cart);
            if (!cartDAO.update(cart)) throw new IllegalStateException("Không cập nhật được giỏ hàng");
        }
    }
}