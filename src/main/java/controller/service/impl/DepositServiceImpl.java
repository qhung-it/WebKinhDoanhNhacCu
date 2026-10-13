package controller.service.impl;

import controller.service.intf.DepositService;
import model.DepositStatus;
import model.dao.impl.DepositDAOImpl;
import model.dao.intf.DepositDAO;
import model.entity.Deposit;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DepositServiceImpl implements DepositService {

    private final DepositDAO depositDAO;

    public DepositServiceImpl() {
        this.depositDAO = new DepositDAOImpl();
    }

    @Override
    public void taoDatCoc(Deposit deposit) {
        if (deposit == null || deposit.getDepositId() == null || deposit.getDepositId().isBlank()) {
            throw new IllegalArgumentException("Thiếu thông tin đặt cọc");
        }
        if (deposit.getOrder() == null) {
            throw new IllegalArgumentException("Đặt cọc chưa có đơn hàng");
        }
        if (deposit.getAmount() == null || deposit.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền đặt cọc không hợp lệ");
        }

        if (deposit.getDepositDate() == null) {
            deposit.setDepositDate(LocalDateTime.now());
        }

        if (deposit.getDepositStatus() == null) {
            deposit.setDepositStatus(DepositStatus.PENDING);
        }

        if (!depositDAO.save(deposit)) {
            throw new IllegalStateException("Không lưu được tiền đặt cọc");
        }
    }

    @Override
    public Deposit getDatCocTheoDonHang(String orderId) {

        if (orderId == null ||
                orderId.trim().isEmpty()) {
            return null;
        }

        return depositDAO.findByOrderId(orderId);
    }
}
