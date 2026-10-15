package model.dao.impl;

import jakarta.persistence.EntityManager;
import model.dao.intf.PaymentDAO;
import model.entity.Payment;
import model.entity.Deposit;
import model.entity.Order;
import model.entity.OrderHistory;
import model.OrderStatus;
import model.DepositStatus;
import model.PaymentStatus;
import model.util.JpaUtil;

public class PaymentDAOImpl implements PaymentDAO {

    @Override
    public boolean save(Payment payment) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(payment);

            em.getTransaction().commit();
            return true;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            e.printStackTrace();
            return false;

        } finally {
            em.close();
        }
    }

    @Override
    public boolean update(Payment payment) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();

            Payment managed = em.merge(payment);
            if (managed.getOrder() != null && managed.getPaymentStatus() != null) {
                Deposit deposit = em.createQuery(
                                "SELECT d FROM Deposit d WHERE d.order.orderId = :orderId", Deposit.class)
                        .setParameter("orderId", managed.getOrder().getOrderId())
                        .getResultStream().findFirst().orElse(null);
                if (deposit != null && managed.getPaymentStatus() == PaymentStatus.COMPLETED) {
                    deposit.setDepositStatus(DepositStatus.COMPLETED);
                    if (deposit.getDepositDate() == null)
                        deposit.setDepositDate(java.time.LocalDateTime.now());
                } else if (deposit != null && managed.getPaymentStatus() == PaymentStatus.REFUNDED) {
                    deposit.setDepositStatus(DepositStatus.REFUNDED);
                }
            }
            em.getTransaction().commit();
            return true;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            e.printStackTrace();
            return false;

        } finally {
            em.close();
        }
    }

    @Override
    public Payment confirmCollectionStage(String orderId, boolean remainingBalance, String changedBy) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Payment payment = em.createQuery(
                    "SELECT p FROM Payment p JOIN FETCH p.order o LEFT JOIN FETCH o.deposit WHERE o.orderId = :orderId",
                    Payment.class).setParameter("orderId", orderId).getResultStream().findFirst().orElse(null);
            if (payment == null) throw new IllegalArgumentException("Payment not found");
            Order order = payment.getOrder();
            Deposit deposit = order.getDeposit();
            if (deposit == null) throw new IllegalStateException("Order has no deposit");
            order.getHistory().size();

            if (!remainingBalance) {
                if (order.getOrderStatus() != OrderStatus.CONFIRMED
                        || payment.getPaymentStatus() != PaymentStatus.PENDING
                        || deposit.getDepositStatus() != DepositStatus.PENDING) {
                    throw new IllegalStateException("Deposit can only be collected after order confirmation");
                }
                payment.setPaymentStatus(PaymentStatus.COMPLETED);
                payment.setPaymentDate(java.time.LocalDateTime.now());
                deposit.setDepositStatus(DepositStatus.COMPLETED);
                deposit.setDepositDate(java.time.LocalDateTime.now());
                order.setOrderStatus(OrderStatus.SHIPPING);
                order.getHistory().add(new OrderHistory(order, OrderStatus.SHIPPING, changedBy));
            } else {
                if (order.getOrderStatus() != OrderStatus.DELIVERED
                        || payment.getPaymentStatus() != PaymentStatus.COMPLETED
                        || deposit.getDepositStatus() != DepositStatus.COMPLETED) {
                    throw new IllegalStateException("Remaining balance can only be collected after delivery");
                }
                payment.setAmount(order.getTotalAmount());
                payment.setPaymentDate(java.time.LocalDateTime.now());
                order.setOrderStatus(OrderStatus.COMPLETED);
                order.getHistory().add(new OrderHistory(order, OrderStatus.COMPLETED, changedBy));
            }
            em.getTransaction().commit();
            return payment;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new IllegalStateException("Could not update collection stage", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Payment findById(String paymentId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.find(Payment.class, paymentId);

        } finally {
            em.close();
        }
    }

    @Override
    public Payment findByOrderId(String orderId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.createQuery(
                            "SELECT p FROM Payment p " +
                                    "WHERE p.order.orderId = :orderId",
                            Payment.class
                    )
                    .setParameter("orderId", orderId)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

        } finally {
            em.close();
        }
    }
}