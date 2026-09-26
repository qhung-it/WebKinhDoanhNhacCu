package dao;

import model.entity.Cart;

/**
 * DAO quản lý Entity Cart.
 * Vai trò chính: Lấy/xóa giỏ hàng theo User.
 */
public interface CartDAO extends BaseDAO<Cart, String> {

    /** Lấy giỏ hàng của một User (mỗi User chỉ có 1 Cart - quan hệ 1-1). */
    Cart findByUser(String userId);

    /** Xóa giỏ hàng của một User (đồng thời xóa các CartLineItem nhờ cascade REMOVE). */
    boolean deleteByUser(String userId);
}
