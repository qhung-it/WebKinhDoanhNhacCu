package controller.service.intf;

import model.PaymentStatus;
import model.entity.Payment;

public interface PaymentService {

    boolean taoThanhToan(Payment payment);

    void xuLyThanhToan(String paymentId);

    boolean kiemTraTrangThaiThanhToan(String paymentId);

    void capNhatTrangThaiThanhToan(String paymentId, PaymentStatus status);

    Payment getThanhToanTheoDonHang(String orderId);

    void hoanTien(String paymentId);

    Payment confirmCollectionStage(String orderId, boolean remainingBalance, String changedBy);
}
