package controller.User;

import controller.service.impl.CartServiceImpl;
import controller.service.impl.OrderServiceImpl;
import controller.service.impl.PaymentServiceImpl;
import controller.service.impl.CustomerRankServiceImpl;
import controller.service.impl.UserServiceImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.OrderStatus;
import model.PaymentMethod;
import model.entity.Cart;
import model.entity.CartLineItem;
import model.entity.Order;
import model.entity.OrderLineItem;
import model.entity.Payment;
import model.entity.Promotion;
import model.entity.User;
import model.dao.impl.PromotionDAOImpl;
import model.util.JpaUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@WebServlet("/orders/*")
public class OrderServlet extends jakarta.servlet.http.HttpServlet {
    private static final com.google.gson.Gson JSON = new com.google.gson.Gson();

    protected void send(jakarta.servlet.http.HttpServletResponse response, int status, Object body)
            throws java.io.IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSON.toJson(body, response.getWriter());
    }

    protected model.entity.User currentUser(jakarta.servlet.http.HttpServletRequest request) {
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        Object value = session == null ? null : session.getAttribute("user");
        return value instanceof model.entity.User ? (model.entity.User) value : null;
    }

    protected String required(jakarta.servlet.http.HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    protected void unauthorized(jakarta.servlet.http.HttpServletResponse response)
            throws java.io.IOException {
        send(response, jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED,
                new ApiMessage("Please log in"));
    }

    protected static final class ApiMessage {
        public final String message;
        ApiMessage(String message) { this.message = message; }
    }

    private final OrderServiceImpl orders = new OrderServiceImpl();
    private final CartServiceImpl carts = new CartServiceImpl();
    private final PaymentServiceImpl payments = new PaymentServiceImpl();
    private final CustomerRankServiceImpl customerRanks = new CustomerRankServiceImpl();
    private final UserServiceImpl users = new UserServiceImpl();
    private final PromotionDAOImpl promotions = new PromotionDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = currentUser(request);
        if (user == null) { unauthorized(response); return; }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Order order : orders.getDonHangTheoUser(user.getUserId())) {
            result.add(orderSummary(order));
        }
        send(response, HttpServletResponse.SC_OK, result);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        User user = currentUser(request);
        if (user == null) { unauthorized(response); return; }
        String path = request.getPathInfo();
        if (path != null && path.startsWith("/cancel/")) {
            cancelOrder(user, path.substring("/cancel/".length()), response);
            return;
        }
        createOrder(user, request, response);
    }

    private void createOrder(User user, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String address = required(request, "address");
        if (address == null) {
            send(response, HttpServletResponse.SC_BAD_REQUEST, new ApiMessage("Thiếu địa chỉ giao hàng"));
            return;
        }
        user.setAddress(address);
        if (!users.suaThongTin(user)) {
            send(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    new ApiMessage("Không thể lưu địa chỉ giao hàng vào hồ sơ"));
            return;
        }
        request.getSession().setAttribute("user", user);
        Cart cart = carts.getGioHangTheoUser(user.getUserId());
        if (cart == null || cart.getCartLineItems().isEmpty()) {
            send(response, HttpServletResponse.SC_BAD_REQUEST, new ApiMessage("Giỏ hàng đang trống"));
            return;
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartLineItem item : cart.getCartLineItems()) {
            if (item.getProduct() != null && item.getQuantity() > 0) {
                subtotal = subtotal.add(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }
        String promotionCode = request.getParameter("couponCode");
        Promotion promotion = null;
        BigDecimal couponDiscount = BigDecimal.ZERO;
        boolean couponFreeShipping = false;
        if (promotionCode != null && !promotionCode.trim().isEmpty()) {
            promotionCode = promotionCode.trim().toUpperCase(java.util.Locale.ROOT);
            promotion = promotions.findByCode(promotionCode);
            if (promotion == null || !promotion.isActive()) {
                send(response, HttpServletResponse.SC_BAD_REQUEST, new ApiMessage("Mã khuyến mãi không hợp lệ hoặc đã hết hạn"));
                return;
            }
            BigDecimal minimum = promotion.getMinimumOrderAmount() == null
                    ? BigDecimal.ZERO : promotion.getMinimumOrderAmount();
            if (subtotal.compareTo(minimum) < 0) {
                send(response, HttpServletResponse.SC_BAD_REQUEST, new ApiMessage("Đơn hàng chưa đạt giá trị tối thiểu của mã khuyến mãi"));
                return;
            }
            couponDiscount = promotion.calculateDiscount(subtotal);
            couponFreeShipping = promotion.isFreeShipping();
        } else {
            promotionCode = null;
        }
        BigDecimal shippingFee = calculateShippingFee(subtotal, couponFreeShipping);
        BigDecimal tierDiscount = calculateTierDiscount(user, subtotal.subtract(couponDiscount));

        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(address);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setUser(user);
        order.setDiscount(couponDiscount.add(tierDiscount));
        order.setPromotionCode(promotionCode);
        order.setShippingFee(shippingFee);
        for (CartLineItem cartItem : cart.getCartLineItems()) {
            if (cartItem.getQuantity() <= 0 || cartItem.getProduct() == null
                    || cartItem.getProduct().getQuantity() < cartItem.getQuantity()) {
                send(response, HttpServletResponse.SC_CONFLICT,
                        new ApiMessage("Số lượng sản phẩm trong giỏ vượt tồn kho"));
                return;
            }
            OrderLineItem line = new OrderLineItem();
            line.setOrderLineItemId(UUID.randomUUID().toString());
            line.setProduct(cartItem.getProduct());
            line.setQuantity(cartItem.getQuantity());
            order.addLineItem(line);
        }

        if (!orders.taoDonHang(order)) {
            send(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    new ApiMessage("Không thể tạo đơn hàng"));
            return;
        }
        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID().toString());
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(resolvePaymentMethod(request.getParameter("paymentMethod")));
        if (!payments.taoThanhToan(payment)) {
            send(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    new ApiMessage("Không thể tạo giao dịch thanh toán cho đơn hàng"));
            return;
        }
        try {
            customerRanks.tinhHangKhachHang(user.getUserId());
        } catch (RuntimeException exception) {
            // Order and payment are already saved; a rank update must not turn checkout into a 500.
            exception.printStackTrace();
        }
        carts.xoaTatCa(user.getUserId());
        Map<String, Object> result = orderSummary(order);
        send(response, HttpServletResponse.SC_CREATED, result);
    }

    private BigDecimal calculateShippingFee(BigDecimal subtotal, boolean couponFreeShipping) {
        if (couponFreeShipping) return BigDecimal.ZERO;
        BigDecimal freeShippingFrom = new BigDecimal("500000");
        BigDecimal standardFee = new BigDecimal("30000");
        try (jakarta.persistence.EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager()) {
            String json = (String) em.createNativeQuery(
                    "SELECT json_data FROM dbo.site_content WHERE content_key = :contentKey")
                    .setParameter("contentKey", "soundora").getSingleResult();
            com.google.gson.JsonObject site = com.google.gson.JsonParser.parseString(json)
                    .getAsJsonObject().getAsJsonObject("site");
            if (site != null) {
                if (site.has("freeShipFrom")) freeShippingFrom = site.get("freeShipFrom").getAsBigDecimal();
                if (site.has("shipFee")) standardFee = site.get("shipFee").getAsBigDecimal();
            }
        } catch (RuntimeException ignored) {
            // Keep the store defaults if optional site content has not been seeded yet.
        }
        return subtotal.compareTo(freeShippingFrom) >= 0 ? BigDecimal.ZERO : standardFee;
    }

    private BigDecimal calculateTierDiscount(User user, BigDecimal eligibleAmount) {
        if (eligibleAmount.signum() <= 0) return BigDecimal.ZERO;
        try (jakarta.persistence.EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager()) {
            Object value = em.createQuery(
                    "SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o "
                            + "WHERE o.user.userId = :userId AND o.orderStatus <> :cancelled")
                    .setParameter("userId", user.getUserId())
                    .setParameter("cancelled", OrderStatus.CANCELLED)
                    .getSingleResult();
            java.math.BigDecimal spending = value instanceof BigDecimal
                    ? (BigDecimal) value : new BigDecimal(String.valueOf(value));
            String json = (String) em.createNativeQuery(
                    "SELECT json_data FROM dbo.site_content WHERE content_key = :contentKey")
                    .setParameter("contentKey", "soundora").getSingleResult();
            com.google.gson.JsonArray tiers = com.google.gson.JsonParser.parseString(json)
                    .getAsJsonObject().getAsJsonArray("tiers");
            BigDecimal selectedMin = BigDecimal.valueOf(-1);
            BigDecimal selectedDiscount = BigDecimal.ZERO;
            for (com.google.gson.JsonElement item : tiers) {
                com.google.gson.JsonObject tier = item.getAsJsonObject();
                BigDecimal min = tier.get("min").getAsBigDecimal();
                BigDecimal percent = tier.get("discount").getAsBigDecimal();
                if (spending.compareTo(min) >= 0 && min.compareTo(selectedMin) > 0) {
                    selectedMin = min;
                    selectedDiscount = percent;
                }
            }
            if (selectedDiscount.signum() <= 0) return BigDecimal.ZERO;
            return eligibleAmount.multiply(selectedDiscount).divide(BigDecimal.valueOf(100),
                    0, java.math.RoundingMode.HALF_UP);
        } catch (RuntimeException ignored) {
            return BigDecimal.ZERO;
        }
    }

    private void cancelOrder(User user, String orderId, HttpServletResponse response) throws IOException {
        Order order = orders.timDonHang(orderId);
        boolean owned = false;
        for (Order userOrder : orders.getDonHangTheoUser(user.getUserId())) {
            if (userOrder.getOrderId().equals(orderId)) { owned = true; break; }
        }
        if (order == null || !owned) {
            send(response, HttpServletResponse.SC_NOT_FOUND, new ApiMessage("Không tìm thấy đơn hàng"));
            return;
        }
        if (order.getOrderStatus() != OrderStatus.PENDING) {
            send(response, HttpServletResponse.SC_CONFLICT,
                    new ApiMessage("Chỉ có thể hủy đơn đang chờ xử lý"));
            return;
        }
        orders.huyDonHang(orderId);
        send(response, HttpServletResponse.SC_OK, new ApiMessage("Đã hủy đơn hàng"));
    }

    private PaymentMethod resolvePaymentMethod(String value) {
        if ("bank".equalsIgnoreCase(value) || "momo".equalsIgnoreCase(value)) {
            return PaymentMethod.E_WALLET;
        }
        return PaymentMethod.CASH;
    }

    private Map<String, Object> orderSummary(Order order) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("orderId", order.getOrderId());
        result.put("orderDate", order.getOrderDate() == null ? null : order.getOrderDate().toString());
        result.put("status", order.getOrderStatus());
        result.put("totalAmount", order.getTotalAmount());
        result.put("subtotal", order.getSubtotal());
        result.put("discount", order.getDiscount());
        result.put("shippingFee", order.getShippingFee());
        result.put("promotionCode", order.getPromotionCode());
        result.put("shippingAddress", order.getShippingAddress());
        return result;
    }
}
