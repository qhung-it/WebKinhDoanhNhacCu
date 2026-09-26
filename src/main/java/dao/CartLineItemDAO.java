package dao;

import model.entity.CartLineItem;

import java.util.List;

/**
 * DAO quản lý Entity CartLineItem.
 * Vai trò chính: CRUD dòng sản phẩm trong Cart.
 */
public interface CartLineItemDAO extends BaseDAO<CartLineItem, String> {

    /** Lấy danh sách các dòng sản phẩm trong một giỏ hàng. */
    List<CartLineItem> findByCart(String cartId);

    /** Tìm dòng sản phẩm cụ thể trong giỏ hàng theo cartId và productId. */
    CartLineItem findByCartAndProduct(String cartId, String productId);

    /** Cập nhật số lượng của một dòng sản phẩm trong giỏ hàng. */
    boolean updateQuantity(String cartLineItemId, int quantity);

    /** Xóa toàn bộ dòng sản phẩm thuộc một giỏ hàng (dùng khi checkout hoặc xóa giỏ hàng). */
    void deleteAllByCart(String cartId);
}
