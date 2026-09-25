package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import model.entity.Product;

import java.math.BigDecimal;
import java.util.List;

/**
 * DAO quản lý Entity Product.
 * Vai trò chính: CRUD sản phẩm, tìm kiếm/lọc, tồn kho.
 */
public class ProductDAO extends BaseDAO<Product, String> {

    public ProductDAO() {
        super(Product.class);
    }

    /** Tìm kiếm sản phẩm theo tên (gần đúng, không phân biệt hoa thường). */
    public List<Product> searchByName(String keyword) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                    "SELECT p FROM Product p WHERE LOWER(p.productName) LIKE LOWER(:keyword)", Product.class);
            query.setParameter("keyword", "%" + keyword + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /** Lọc sản phẩm theo danh mục. */
    public List<Product> findByCategory(String categoryId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                    "SELECT p FROM Product p WHERE p.category.categoryId = :categoryId", Product.class);
            query.setParameter("categoryId", categoryId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /** Lọc sản phẩm theo thương hiệu. */
    public List<Product> findByBrand(String brandId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                    "SELECT p FROM Product p WHERE p.brand.brandId = :brandId", Product.class);
            query.setParameter("brandId", brandId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /** Lọc sản phẩm theo khoảng giá [minPrice, maxPrice]. */
    public List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                    "SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice", Product.class);
            query.setParameter("minPrice", minPrice);
            query.setParameter("maxPrice", maxPrice);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /** Lọc sản phẩm theo trạng thái (ví dụ: "ACTIVE", "HIDDEN", ...). */
    public List<Product> findByStatus(String status) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                    "SELECT p FROM Product p WHERE p.status = :status", Product.class);
            query.setParameter("status", status);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /** Lấy danh sách sản phẩm còn hàng (quantity > 0). */
    public List<Product> findInStock() {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                    "SELECT p FROM Product p WHERE p.quantity > 0", Product.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /** Lấy danh sách sản phẩm hết hàng (quantity <= 0). */
    public List<Product> findOutOfStock() {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery(
                    "SELECT p FROM Product p WHERE p.quantity <= 0", Product.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /** Kiểm tra số lượng tồn kho hiện tại của một sản phẩm. */
    public int getStockQuantity(String productId) {
        Product product = findById(productId);
        return product != null ? product.getQuantity() : 0;
    }

    /**
     * Cập nhật (tăng/giảm) số lượng tồn kho của sản phẩm.
     * delta âm để giảm (khi bán hàng), dương để tăng (khi nhập hàng/hủy đơn).
     * Trả về true nếu cập nhật thành công, false nếu không đủ tồn kho hoặc không tìm thấy sản phẩm.
     */
    public boolean adjustStock(String productId, int delta) {
        EntityManager em = getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            Product product = em.find(Product.class, productId);
            if (product == null || product.getQuantity() + delta < 0) {
                rollback(em);
                return false;
            }
            product.setQuantity(product.getQuantity() + delta);
            em.getTransaction().commit();
            return true;
        } catch (RuntimeException e) {
            rollback(em);
            throw e;
        } finally {
            em.close();
        }
    }
}
