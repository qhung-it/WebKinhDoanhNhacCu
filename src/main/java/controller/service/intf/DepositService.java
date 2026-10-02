package controller.service.intf;

import model.entity.Deposit;

public interface DepositService {

    void taoDatCoc(Deposit deposit);

    Deposit getDatCocTheoDonHang(String orderId);
}