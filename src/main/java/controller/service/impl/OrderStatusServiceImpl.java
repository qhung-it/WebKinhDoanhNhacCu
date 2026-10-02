package controller.service.impl;

import controller.service.intf.OrderStatusService;
import model.dao.impl.OrderStatusDAOImpl;
import model.dao.intf.OrderStatusDAO;
import model.entity.OrderStatus;

import java.util.List;

public class OrderStatusServiceImpl implements OrderStatusService {

    private final OrderStatusDAO orderStatusDAO;

    public OrderStatusServiceImpl() {
        this.orderStatusDAO = new OrderStatusDAOImpl();
    }

    @Override
    public void themTrangThai(OrderStatus orderStatus) {

        if (orderStatus == null) {
            return;
        }

        if (orderStatus.getStatusId() == null ||
                orderStatus.getStatusId().trim().isEmpty()) {
            return;
        }

        if (orderStatus.getStatusName() == null ||
                orderStatus.getStatusName().trim().isEmpty()) {
            return;
        }

        orderStatusDAO.save(orderStatus);
    }

    @Override
    public void suaTrangThai(OrderStatus orderStatus) {

        if (orderStatus == null ||
                orderStatus.getStatusId() == null) {
            return;
        }

        orderStatusDAO.update(orderStatus);
    }

    @Override
    public void xoaTrangThai(String statusId) {

        if (statusId == null ||
                statusId.trim().isEmpty()) {
            return;
        }

        OrderStatus orderStatus = orderStatusDAO.findById(statusId);

        if (orderStatus == null) {
            return;
        }

        /*
         * Không xóa trực tiếp nếu trạng thái đang được Order sử dụng.
         * Việc kiểm tra FK thực tế sẽ do database đảm bảo.
         */
        orderStatusDAO.delete(statusId);
    }

    @Override
    public List<OrderStatus> getDanhSachTrangThai() {
        return orderStatusDAO.findAll();
    }
}