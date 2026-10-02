package controller.service.intf;

import model.entity.OrderStatus;

import java.util.List;

public interface OrderStatusService {

    void themTrangThai(OrderStatus orderStatus);

    void suaTrangThai(OrderStatus orderStatus);

    void xoaTrangThai(String statusId);

    List<OrderStatus> getDanhSachTrangThai();
}