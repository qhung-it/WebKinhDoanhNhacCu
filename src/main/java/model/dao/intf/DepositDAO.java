package model.dao.intf;

import model.entity.Deposit;

public interface DepositDAO {

    boolean save(Deposit deposit);

    Deposit findByOrderId(String orderId);
}