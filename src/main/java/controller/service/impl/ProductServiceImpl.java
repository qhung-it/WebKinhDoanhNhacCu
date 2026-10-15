package controller.service.impl;

import controller.service.intf.ProductService;
import model.dao.impl.ProductDAOImpl;
import model.dao.intf.ProductDAO;
import model.entity.Product;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

public class ProductServiceImpl implements ProductService {

    private final ProductDAO productDAO;

    public ProductServiceImpl() {
        this.productDAO = new ProductDAOImpl();
    }

    private static boolean blank(String s) {
        return s == null || s.trim().isEmpty();
    }

    /** Ném IllegalArgumentException nếu dữ liệu sai; servlet bắt và trả mã 400. */
    private void kiemTra(Product p) {
        if (p == null) throw new IllegalArgumentException("Dữ liệu sản phẩm trống");
        if (blank(p.getProductName()) || p.getProductName().trim().length() < 3)
            throw new IllegalArgumentException("Tên sản phẩm tối thiểu 3 ký tự");
        if (p.getPrice() == null || p.getPrice().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Giá bán phải lớn hơn 0");
        if (p.getOldPrice() != null && p.getOldPrice().compareTo(p.getPrice()) <= 0)
            throw new IllegalArgumentException("Giá gốc phải lớn hơn giá bán (hoặc để trống)");
        if (p.getQuantity() < 0)
            throw new IllegalArgumentException("Số lượng tồn không được âm");
        if (p.getCategory() == null) throw new IllegalArgumentException("Chưa chọn danh mục");
        if (p.getBrand() == null) throw new IllegalArgumentException("Chưa chọn thương hiệu");
        if (blank(p.getShortDescription()) || p.getShortDescription().trim().length() < 5)
            throw new IllegalArgumentException("Vui lòng nhập mô tả ngắn (tối thiểu 5 ký tự)");
        String b = p.getBadge();
        if (b != null && !b.equals("hot") && !b.equals("new") && !b.equals("sale"))
            throw new IllegalArgumentException("Nhãn sản phẩm không hợp lệ");
    }

    private boolean skuDaTonTai(String sku, String exceptId) {
        for (Product x : productDAO.findAll()) {
            if (x.getSku() != null && x.getSku().equalsIgnoreCase(sku)
                    && !x.getProductId().equals(exceptId)) return true;
        }
        return false;
    }

    private String taoSku(Product p) {
        String prefix = "SP";
        if (p.getBrand() != null && p.getBrand().getBrandName() != null) {
            String n = Normalizer.normalize(p.getBrand().getBrandName(), Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", "").replaceAll("[^A-Za-z0-9]", "").toUpperCase();
            if (!n.isEmpty()) prefix = n.length() >= 3 ? n.substring(0, 3) : n;
        }
        Set<String> used = productDAO.findAll().stream()
                .map(Product::getSku).filter(Objects::nonNull).collect(Collectors.toSet());
        int i = 1001;
        while (used.contains(prefix + "-" + i)) i++;
        return prefix + "-" + i;
    }

    @Override
    public void themSanPham(Product product) {
        kiemTra(product);
        if (blank(product.getProductId())) product.setProductId(UUID.randomUUID().toString());
        if (product.getActive() == null) product.setActive(true);
        if (product.getIsNew() == null) product.setIsNew(false);
        if (blank(product.getSku())) {
            product.setSku(taoSku(product));
        } else if (skuDaTonTai(product.getSku(), null)) {
            throw new IllegalArgumentException("Mã SKU đã tồn tại");
        }
        productDAO.save(product);
    }

    @Override
    public void suaSanPham(Product product) {
        kiemTra(product);
        if (blank(product.getProductId())) throw new IllegalArgumentException("Thiếu mã sản phẩm");
        if (!blank(product.getSku()) && skuDaTonTai(product.getSku(), product.getProductId()))
            throw new IllegalArgumentException("Mã SKU đã tồn tại");
        productDAO.update(product);
    }

    @Override
    public boolean xoaSanPham(String productId) {
        if (blank(productId)) throw new IllegalArgumentException("Thiếu mã sản phẩm");
        return productDAO.delete(productId);
    }

    @Override
    public Product timSanPham(String productId) {
        return productDAO.findById(productId);
    }

    @Override
    public List<Product> getTatCaSanPham() {
        return productDAO.findAll();
    }

    @Override
    public List<Product> locSanPham(String keyword) {
        return productDAO.filter(keyword);
    }

    @Override
    public List<Product> getSanPhamTheoDanhMuc(String categoryId) {
        return productDAO.findByCategoryId(categoryId);
    }

    @Override
    public List<Product> getSanPhamTheoThuongHieu(String brandId) {
        return productDAO.findByBrandId(brandId);
    }

    @Override
    public List<Product> getDanhSachSanPham() {
        return productDAO.findAll();
    }

    /** Lọc kết hợp từ khóa (tên, thương hiệu, SKU) và danh mục. Tham số nào rỗng thì bỏ qua. */
    @Override
    public List<Product> timKiemSanPham(String keyword, String categoryId) {
        String k = blank(keyword) ? null : keyword.trim().toLowerCase();
        List<Product> out = new ArrayList<>();
        for (Product p : productDAO.findAll()) {
            if (!blank(categoryId)
                    && (p.getCategory() == null || !categoryId.equals(p.getCategory().getCategoryId()))) continue;
            if (k != null) {
                String text = (p.getProductName() + " "
                        + (p.getBrand() == null ? "" : p.getBrand().getBrandName()) + " "
                        + (p.getSku() == null ? "" : p.getSku())).toLowerCase();
                if (!text.contains(k)) continue;
            }
            out.add(p);
        }
        return out;
    }
}
