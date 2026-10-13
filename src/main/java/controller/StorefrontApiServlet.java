package controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import controller.service.impl.BrandServiceImpl;
import controller.service.impl.CartServiceImpl;
import controller.service.impl.CategoryServiceImpl;
import controller.service.impl.ContactMessageServiceImpl;
import controller.service.impl.CustomerRankServiceImpl;
import controller.service.impl.DepositServiceImpl;
import controller.service.impl.NewsArticleServiceImpl;
import controller.service.impl.OrderServiceImpl;
import controller.service.impl.PaymentServiceImpl;
import controller.service.impl.ProductServiceImpl;
import controller.service.impl.PromotionServiceImpl;
import controller.service.impl.ReviewServiceImpl;
import controller.service.impl.SiteSettingServiceImpl;
import controller.service.impl.UserServiceImpl;
import controller.service.intf.BrandService;
import controller.service.intf.CartService;
import controller.service.intf.CategoryService;
import controller.service.intf.ContactMessageService;
import controller.service.intf.CustomerRankService;
import controller.service.intf.DepositService;
import controller.service.intf.NewsArticleService;
import controller.service.intf.OrderService;
import controller.service.intf.PaymentService;
import controller.service.intf.ProductService;
import controller.service.intf.PromotionService;
import controller.service.intf.ReviewService;
import controller.service.intf.SiteSettingService;
import controller.service.intf.UserService;
import controller.util.ApiUtil;
import model.PaymentMethod;
import model.Role;
import model.entity.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/api/store/*")
public class StorefrontApiServlet extends HttpServlet {

    private final ProductService productService = new ProductServiceImpl();
    private final CategoryService categoryService = new CategoryServiceImpl();
    private final CustomerRankService rankService = new CustomerRankServiceImpl();
    private final BrandService brandService = new BrandServiceImpl();
    private final PromotionService promotionService = new PromotionServiceImpl();
    private final ReviewService reviewService = new ReviewServiceImpl();
    private final UserService userService = new UserServiceImpl();
    private final CartService cartService = new CartServiceImpl();
    private final OrderService orderService = new OrderServiceImpl();
    private final PaymentService paymentService = new PaymentServiceImpl();
    private final DepositService depositService = new DepositServiceImpl();
    private final ContactMessageService contactService = new ContactMessageServiceImpl();
    private final NewsArticleService newsService = new NewsArticleServiceImpl();
    private final SiteSettingService settingService = new SiteSettingServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String p = path(req);

        if (p.equals("/products")) {
            ApiUtil.ok(resp, productService.getDanhSachSanPham().stream()
                    .filter(x -> !Boolean.FALSE.equals(x.getActive()) && (x.getCategory() == null || x.getCategory().isStatus()))
                    .map(this::productMap)
                    .collect(Collectors.toList()));
        } else if (p.startsWith("/products/")) {
            Product product = productService.timSanPham(idFromPath(req));
            if (product == null || Boolean.FALSE.equals(product.getActive())) {
                ApiUtil.error(resp, 404, "Không tìm thấy sản phẩm");
                return;
            }
            ApiUtil.ok(resp, productMap(product));
        } else if (p.equals("/categories")) {
            ApiUtil.ok(resp, categoryService.getDanhSachDanhMuc().stream()
                    .filter(Category::isStatus)
                    .map(c -> Map.of(
                            "id", c.getCategoryId(),
                            "key", slug(c.getCategoryName()),
                            "name", c.getCategoryName()
                    ))
                    .collect(Collectors.toList()));
        } else if (p.equals("/brands")) {
            ApiUtil.ok(resp, brandService.getDanhSachThuongHieu().stream()
                    .map(b -> Map.of(
                            "id", b.getBrandId(),
                            "name", b.getBrandName(),
                            "origin", b.getOrigin() == null ? "" : b.getOrigin(),
                            "desc", b.getDescription() == null ? "" : b.getDescription()
                    ))
                    .collect(Collectors.toList()));
        } else if (p.equals("/promotions")) {
            ApiUtil.ok(resp, promotionService.getDanhSachKhuyenMai().stream()
                    .filter(Promotion::isActive)
                    .map(x -> {
                        Map<String, Object> item = new LinkedHashMap<>();
                        item.put("id", x.getPromotionId());
                        item.put("name", x.getPromotionName());
                        item.put("couponCode", x.getCouponCode());
                        item.put("discountType", x.getDiscountType() == null ? "PERCENTAGE" : x.getDiscountType().name());
                        item.put("discountValue", x.getDiscountValue());
                        item.put("startAt", x.getStartDateTime());
                        item.put("endAt", x.getEndDateTime());
                        return item;
                    })
                    .collect(Collectors.toList()));
        } else if (p.equals("/news")) {
            ApiUtil.ok(resp, newsService.published().stream()
                    .map(this::newsMap)
                    .collect(Collectors.toList()));
        } else if (p.startsWith("/news/")) {
            String articleId = idFromPath(req);
            NewsArticle article = newsService.find(articleId);
            if (article == null || !article.isPublished()) {
                ApiUtil.error(resp, 404, "Không tìm thấy bài viết");
                return;
            }
            ApiUtil.ok(resp, newsMap(article));
        } else if (p.equals("/settings")) {
            SiteSetting setting = settingService.get();
            ApiUtil.ok(resp, Map.of(
                    "freeShipFrom", setting.getFreeShippingFrom(),
                    "shipFee", setting.getShippingFee()
            ));
        } else if (p.equals("/ranks")) {
            ApiUtil.ok(resp, rankService.getDanhSachHang().stream()
                    .map(rank -> {
                        Map<String, Object> item = new LinkedHashMap<>();
                        item.put("id", rank.getRankId());
                        item.put("name", rank.getRankName() == null ? "Thành viên" : rank.getRankName().name());
                        item.put("minSpending", rank.getMinSpending());
                        return item;
                    })
                    .collect(Collectors.toList()));
        } else if (p.equals("/reviews")) {
            String productId = req.getParameter("productId");
            if (productId == null) {
                ApiUtil.error(resp, 400, "Thiếu mã sản phẩm");
                return;
            }
            ApiUtil.ok(resp, reviewService.getDanhGiaTheoSanPham(productId).stream()
                    .filter(Review::isApproved)
                    .map(r -> {
                        Map<String, Object> map = new LinkedHashMap<>();
                        map.put("id", r.getReviewId());
                        map.put("rating", r.getRating());
                        map.put("comment", r.getComment());
                        map.put("text", r.getComment());
                        map.put("date", "");
                        map.put("name", r.getUser() == null ? "Khách" : r.getUser().getFullName());
                        map.put("verified", r.getOrder() != null);
                        return map;
                    })
                    .collect(Collectors.toList()));
        } else if (p.equals("/me")) {
            User user = sessionUser(req);
            if (user == null) {
                ApiUtil.error(resp, 401, "Chưa đăng nhập");
                return;
            }
            ApiUtil.ok(resp, userMap(user));
        } else if (p.equals("/orders")) {
            User user = sessionUser(req);
            if (user == null) {
                ApiUtil.error(resp, 401, "Chưa đăng nhập");
                return;
            }
            ApiUtil.ok(resp, orderService.getDonHangTheoUser(user.getUserId()).stream()
                    .map(this::orderMap)
                    .collect(Collectors.toList()));
        } else if (p.equals("/cart")) {
            User user = sessionUser(req);
            if (user == null) {
                ApiUtil.error(resp, 401, "Chưa đăng nhập");
                return;
            }
            Cart cart = cartService.getGioHangTheoUser(user.getUserId());
            ApiUtil.ok(resp, cart == null ? List.of() : cart.getCartLineItems().stream()
                    .map(x -> Map.of(
                            "productId", x.getProduct().getProductId(),
                            "quantity", x.getQuantity()
                    ))
                    .collect(Collectors.toList()));
        } else {
            ApiUtil.error(resp, 404, "Không tìm thấy API");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String p = path(req);
        JsonObject body = ApiUtil.body(req);

        if (p.equals("/auth/login")) {
            User user = userService.dangNhap(ApiUtil.str(body, "email"), ApiUtil.str(body, "password"));
            if (user == null || user.getRole() != Role.USER) {
                ApiUtil.error(resp, 401, "Email hoặc mật khẩu không đúng");
                return;
            }
            req.getSession(true).setAttribute("storeUserId", user.getUserId());
            ApiUtil.ok(resp, userMap(user));
        } else if (p.equals("/auth/register")) {
            String name = ApiUtil.str(body, "name");
            String email = ApiUtil.str(body, "email");
            String phone = ApiUtil.str(body, "phone");
            String password = ApiUtil.str(body, "password");

            if (name == null || name.length() < 2 || email == null || phone == null || password == null || password.length() < 6) {
                ApiUtil.error(resp, 400, "Thông tin đăng ký không hợp lệ");
                return;
            }

            if (userService.getDanhSachUser().stream().anyMatch(u -> email.equalsIgnoreCase(u.getEmail()))) {
                ApiUtil.error(resp, 409, "Email đã được sử dụng");
                return;
            }

            User user = new User();
            user.setUserId(UUID.randomUUID().toString());
            user.setFullName(name);
            user.setEmail(email.toLowerCase(Locale.ROOT));
            user.setPhone(phone);
            user.setPassword(password);
            user.setRole(Role.USER);

            userService.dangKy(user);
            req.getSession(true).setAttribute("storeUserId", user.getUserId());
            ApiUtil.json(resp, 201, userMap(user));
        } else if (p.equals("/auth/logout")) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            ApiUtil.ok(resp, Map.of("ok", true));
        } else if (p.equals("/cart")) {
            User user = sessionUser(req);
            if (user == null) {
                ApiUtil.error(resp, 401, "Đăng nhập để lưu giỏ hàng");
                return;
            }

            JsonArray rows = body.has("items") && body.get("items").isJsonArray() ? body.getAsJsonArray("items") : new JsonArray();
            Cart cart = cartService.getGioHangTheoUser(user.getUserId());
            if (cart == null) {
                cart = new Cart();
                cart.setCartId(UUID.randomUUID().toString());
                cart.setUser(user);
            }

            List<CartLineItem> items = new ArrayList<>();
            for (JsonElement element : rows) {
                JsonObject row = element.getAsJsonObject();
                String productId = ApiUtil.str(row, "productId");
                int quantity = row.has("quantity") ? row.get("quantity").getAsInt() : 0;
                Product product = productService.timSanPham(productId);

                if (product == null || !product.checkStock(quantity)) {
                    ApiUtil.error(resp, 400, "Sản phẩm hoặc số lượng trong giỏ không hợp lệ");
                    return;
                }

                CartLineItem item = new CartLineItem();
                item.setCartLineItemId(UUID.randomUUID().toString());
                item.setCart(cart);
                item.setProduct(product);
                item.setQuantity(quantity);
                items.add(item);
            }

            cart.setCartLineItems(items);
            cartService.luuGioHang(cart);
            ApiUtil.ok(resp, Map.of("ok", true));
        } else if (p.equals("/reviews")) {
            User user = sessionUser(req);
            if (user == null) {
                ApiUtil.error(resp, 401, "Đăng nhập để gửi đánh giá");
                return;
            }

            String productId = ApiUtil.str(body, "productId");
            Product product = productService.timSanPham(productId);
            int rating;
            try {
                rating = body.get("rating").getAsInt();
            } catch (Exception e) {
                rating = 0;
            }

            if (product == null || rating < 1 || rating > 5) {
                ApiUtil.error(resp, 400, "Sản phẩm hoặc số sao không hợp lệ");
                return;
            }

            Review review = new Review();
            review.setReviewId(UUID.randomUUID().toString());
            review.setProduct(product);
            review.setUser(user);
            review.setRating(rating);
            review.setComment(ApiUtil.str(body, "comment"));
            review.setApproved(false);

            reviewService.guiDanhGia(review);
            ApiUtil.json(resp, 201, Map.of("ok", true));
        } else if (p.equals("/contact")) {
            String name = ApiUtil.str(body, "name");
            String email = ApiUtil.str(body, "email");
            String phone = ApiUtil.str(body, "phone");
            String subject = ApiUtil.str(body, "subject");
            String message = ApiUtil.str(body, "message");

            if (name == null || name.length() < 2 || email == null || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
                    || subject == null || message == null || message.length() < 10) {
                ApiUtil.error(resp, 400, "Thông tin liên hệ không hợp lệ");
                return;
            }

            ContactMessage contact = new ContactMessage();
            contact.setName(name);
            contact.setEmail(email);
            contact.setPhone(phone);
            contact.setSubject(subject);
            contact.setMessage(message);

            contactService.create(contact);
            ApiUtil.json(resp, 201, Map.of("ok", true));
        } else if (p.equals("/coupons/validate")) {
            User user = sessionUser(req);
            if (user == null) {
                ApiUtil.error(resp, 401, "Đăng nhập để kiểm tra mã giảm giá");
                return;
            }

            try {
                JsonArray rows = body.has("items") && body.get("items").isJsonArray() ? body.getAsJsonArray("items") : new JsonArray();
                List<OrderLineItem> items = couponItems(rows);
                CouponResult result = calculateCoupon(user, ApiUtil.str(body, "code"), items);
                BigDecimal subtotal = items.stream().map(OrderLineItem::calculateSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);

                Map<String, Object> response = new LinkedHashMap<>();
                response.put("code", result.promotion.getCouponCode());
                response.put("name", result.promotion.getPromotionName());
                response.put("discount", result.discount);
                response.put("subtotal", subtotal);

                ApiUtil.ok(resp, response);
            } catch (IllegalArgumentException e) {
                ApiUtil.error(resp, 400, e.getMessage());
            }
        } else if (p.equals("/orders")) {
            User user = sessionUser(req);
            if (user == null) {
                ApiUtil.error(resp, 401, "Đăng nhập để đặt hàng");
                return;
            }

            try {
                Order order = new Order();
                order.setOrderId(UUID.randomUUID().toString());
                order.setUser(user);

                String address = ApiUtil.str(body, "address");
                if (address == null || address.isBlank()) {
                    throw new IllegalArgumentException("Nhập địa chỉ giao hàng");
                }
                order.setShippingAddress(address);
                order.setNote(ApiUtil.str(body, "note"));

                JsonArray rows = body.has("items") && body.get("items").isJsonArray() ? body.getAsJsonArray("items") : new JsonArray();
                if (rows.isEmpty()) {
                    throw new IllegalArgumentException("Giỏ hàng đang trống");
                }

                for (JsonElement element : rows) {
                    JsonObject row = element.getAsJsonObject();
                    String productId = ApiUtil.str(row, "productId");
                    int quantity = row.get("quantity").getAsInt();
                    Product product = productService.timSanPham(productId);

                    if (product == null || !product.checkStock(quantity)) {
                        throw new IllegalArgumentException("Sản phẩm hết hàng hoặc không đủ số lượng");
                    }

                    OrderLineItem item = new OrderLineItem();
                    item.setOrderLineItemId(UUID.randomUUID().toString());
                    item.setOrder(order);
                    item.setProduct(product);
                    item.setQuantity(quantity);
                    item.setUnitPrice(product.getPrice());
                    order.addLineItem(item);
                }

                BigDecimal subtotal = order.getOrderLineItems().stream()
                        .map(OrderLineItem::calculateSubtotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                SiteSetting setting = settingService.get();
                order.setShippingFee(subtotal.compareTo(setting.getFreeShippingFrom()) >= 0 ? BigDecimal.ZERO : setting.getShippingFee());

                String couponCode = ApiUtil.str(body, "couponCode");
                if (couponCode != null && !couponCode.isBlank()) {
                    CouponResult coupon = calculateCoupon(user, couponCode, order.getOrderLineItems());
                    order.setCouponCode(coupon.promotion.getCouponCode());
                    order.setDiscount(coupon.discount);
                } else {
                    order.setCouponCode(null);
                    order.setDiscount(BigDecimal.ZERO);
                }

                orderService.taoDonHang(order);

                String rawMethod = ApiUtil.str(body, "paymentMethod");
                Payment payment = new Payment();
                payment.setPaymentId(UUID.randomUUID().toString());
                payment.setOrder(order);
                payment.setAmount(order.getTotalAmount());
                payment.setPaymentMethod("E_WALLET".equalsIgnoreCase(rawMethod) ? PaymentMethod.E_WALLET : PaymentMethod.CASH);
                paymentService.taoThanhToan(payment);

                Order savedOrder = orderService.timDonHang(order.getOrderId());
                if (savedOrder.getTotalAmount().compareTo(new BigDecimal("100000000")) >= 0) {
                    Deposit deposit = new Deposit();
                    deposit.setDepositId(UUID.randomUUID().toString());
                    deposit.setOrder(savedOrder);
                    deposit.setAmount(deposit.calculateDeposit());
                    depositService.taoDatCoc(deposit);
                }

                ApiUtil.json(resp, 201, orderMap(savedOrder));
            } catch (IllegalArgumentException | IllegalStateException e) {
                ApiUtil.error(resp, 400, e.getMessage());
            }
        } else {
            ApiUtil.error(resp, 404, "Không tìm thấy API");
        }
    }

    private String path(HttpServletRequest req) {
        return req.getPathInfo() == null ? "/" : req.getPathInfo();
    }

    private String idFromPath(HttpServletRequest req) {
        String[] parts = path(req).split("/");
        return parts.length > 2 ? parts[2] : null;
    }

    private User sessionUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        Object id = (session == null) ? null : session.getAttribute("storeUserId");
        return (id == null) ? null : userService.timUser(String.valueOf(id));
    }

    private Map<String, Object> userMap(User user) {
        if (user == null) {
            return null;
        }
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", user.getUserId());
        map.put("name", user.getFullName());
        map.put("email", user.getEmail());
        map.put("phone", user.getPhone());
        map.put("address", user.getAddress());
        return map;
    }

    private String slug(String name) {
        return java.text.Normalizer.normalize(name == null ? "" : name, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").replace('đ', 'd').replace('Đ', 'D')
                .toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    }

    private Map<String, Object> productMap(Product product) {
        Category category = product.getCategory();
        Brand brand = product.getBrand();

        List<Review> reviews = reviewService.getDanhGiaTheoSanPham(product.getProductId()).stream()
                .filter(Review::isApproved)
                .collect(Collectors.toList());

        double rating = reviews.isEmpty() ? 0 : reviews.stream().mapToInt(Review::getRating).average().orElse(0);

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", product.getProductId());
        map.put("name", product.getProductName());
        map.put("cat", category == null ? "" : slug(category.getCategoryName()));
        map.put("catName", category == null ? "" : category.getCategoryName());
        map.put("categoryId", category == null ? null : category.getCategoryId());
        map.put("brand", brand == null ? "" : brand.getBrandName());
        map.put("brandId", brand == null ? null : brand.getBrandId());
        map.put("brandName", brand == null ? "" : brand.getBrandName());
        map.put("price", product.getPrice());
        map.put("old", product.getOldPrice());
        map.put("sku", product.getSku());
        map.put("stock", product.getQuantity());
        map.put("quantity", product.getQuantity());
        map.put("short", product.getShortDescription());
        map.put("desc", product.getDescription());
        map.put("image", product.getImage());
        map.put("images", product.getImages() == null ? List.of() : Arrays.asList(product.getImages().split("\\n")));
        map.put("emoji", product.getEmoji());
        map.put("badge", product.getBadge());
        map.put("isNew", Boolean.TRUE.equals(product.getIsNew()));
        map.put("active", !Boolean.FALSE.equals(product.getActive()));
        map.put("rating", rating);
        map.put("reviews", reviews.size());
        map.put("sold", 0);
        map.put("features", product.getShortDescription() == null ? List.of() : List.of(product.getShortDescription()));
        map.put("specs", List.of());
        return map;
    }

    private Map<String, Object> orderMap(Order order) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", order.getOrderId());
        map.put("createdAt", order.getOrderDate());
        map.put("status", order.getOrderStatus() == null ? null : order.getOrderStatus().name());
        map.put("address", order.getShippingAddress());
        map.put("note", order.getNote());
        map.put("subtotal", order.getSubtotal());
        map.put("discount", order.getDiscount());
        map.put("couponCode", order.getCouponCode());
        map.put("shippingFee", order.getShippingFee());
        map.put("total", order.getTotalAmount());
        map.put("items", order.getOrderLineItems().stream().map(item -> {
            Map<String, Object> line = new LinkedHashMap<>();
            line.put("productId", item.getProduct() == null ? null : item.getProduct().getProductId());
            line.put("name", item.getProduct() == null ? "" : item.getProduct().getProductName());
            line.put("price", item.getUnitPrice());
            line.put("qty", item.getQuantity());
            return line;
        }).collect(Collectors.toList()));

        Payment payment = paymentService.getThanhToanTheoDonHang(order.getOrderId());
        map.put("paymentStatus", (payment == null || payment.getPaymentStatus() == null) ? "PENDING" : payment.getPaymentStatus().name());
        map.put("paymentMethod", (payment == null || payment.getPaymentMethod() == null) ? null : payment.getPaymentMethod().name());

        Deposit deposit = depositService.getDatCocTheoDonHang(order.getOrderId());
        map.put("deposit", deposit == null ? null : Map.of("amount", deposit.getAmount(), "status", deposit.getDepositStatus().name()));
        return map;
    }

    private Map<String, Object> newsMap(NewsArticle article) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", article.getArticleId());
        map.put("title", article.getTitle());
        map.put("cat", article.getCategory());
        map.put("excerpt", article.getExcerpt());
        map.put("content", (article.getContent() == null || article.getContent().isBlank())
                ? List.of()
                : Arrays.asList(article.getContent().split("\\R\\s*\\R")));
        map.put("emoji", article.getEmoji());
        map.put("author", article.getAuthor());
        map.put("color", article.getColor());
        map.put("color2", article.getColor2());
        map.put("date", article.getPublishedAt() == null ? article.getCreatedAt() : article.getPublishedAt());
        return map;
    }

    private static class CouponResult {
        final Promotion promotion;
        final BigDecimal discount;

        CouponResult(Promotion promotion, BigDecimal discount) {
            this.promotion = promotion;
            this.discount = discount;
        }
    }

    private CouponResult calculateCoupon(User user, String rawCode, List<OrderLineItem> items) {
        String code = (rawCode == null) ? "" : rawCode.trim();
        if (code.isEmpty()) {
            throw new IllegalArgumentException("Nhập mã giảm giá");
        }

        Promotion promotion = promotionService.getDanhSachKhuyenMai().stream()
                .filter(x -> x.getCouponCode() != null && x.getCouponCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Mã giảm giá không hợp lệ"));

        if (!promotion.isActive()) {
            throw new IllegalArgumentException("Mã giảm giá đã hết hạn hoặc chưa bắt đầu");
        }

        CustomerRank userRank = user.getCustomerRank();
        boolean rankAllowed = userRank != null && promotion.getCustomerRanks().stream()
                .anyMatch(rank -> Objects.equals(rank.getRankId(), userRank.getRankId()));

        if (!rankAllowed) {
            throw new IllegalArgumentException("Hạng thành viên của bạn không áp dụng được mã này");
        }

        Set<String> eligibleProductIds = promotion.getProducts().stream()
                .map(Product::getProductId)
                .collect(Collectors.toSet());

        BigDecimal eligibleSubtotal = items.stream()
                .filter(item -> item.getProduct() != null && eligibleProductIds.contains(item.getProduct().getProductId()))
                .map(OrderLineItem::calculateSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (eligibleSubtotal.signum() <= 0) {
            throw new IllegalArgumentException("Mã này không áp dụng cho sản phẩm trong giỏ hàng");
        }

        return new CouponResult(promotion, promotion.calculateDiscount(eligibleSubtotal));
    }

    private List<OrderLineItem> couponItems(JsonArray rows) {
        if (rows == null || rows.isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng đang trống");
        }

        List<OrderLineItem> items = new ArrayList<>();
        for (JsonElement element : rows) {
            if (!element.isJsonObject()) {
                throw new IllegalArgumentException("Sản phẩm trong giỏ không hợp lệ");
            }

            JsonObject row = element.getAsJsonObject();
            String productId = ApiUtil.str(row, "productId");
            int quantity;

            try {
                quantity = row.get("quantity").getAsInt();
            } catch (Exception e) {
                throw new IllegalArgumentException("Số lượng sản phẩm không hợp lệ");
            }

            Product product = productService.timSanPham(productId);
            if (product == null || quantity <= 0) {
                throw new IllegalArgumentException("Sản phẩm hoặc số lượng trong giỏ không hợp lệ");
            }

            OrderLineItem item = new OrderLineItem();
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setUnitPrice(product.getPrice());
            items.add(item);
        }
        return items;
    }
}