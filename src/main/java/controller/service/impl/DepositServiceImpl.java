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

        if (deposit == null) {
            return;
        }

        if (deposit.getDepositId() == null ||
                deposit.getDepositId().trim().isEmpty()) {
            return;
        }

        if (deposit.getOrder() == null) {
            return;
        }

        if (deposit.getAmount() == null ||
                deposit.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        if (deposit.getDepositDate() == null) {
            deposit.setDepositDate(LocalDateTime.now());
        }

        if (deposit.getDepositStatus() == null) {
            deposit.setDepositStatus(DepositStatus.PENDING);
        }

        depositDAO.save(deposit);
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