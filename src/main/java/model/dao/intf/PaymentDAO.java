package model.dao.intf;

import model.entity.Payment;

public interface PaymentDAO {

    boolean save(Payment payment);

    boolean update(Payment payment);

    Payment findById(String paymentId);

    Payment findByOrderId(String orderId);
}