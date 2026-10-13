package controller.service.intf;

import model.entity.Cart;

public interface CartService {
    Cart getGioHangTheoUser(String userId);
    boolean themSanPham(String userId, String productId, int quantity);
    boolean capNhatSanPham(String userId, String productId, int quantity);
    boolean xoaSanPham(String userId, String productId);
    boolean xoaTatCa(String userId);
    void luuGioHang(Cart cart);
}
