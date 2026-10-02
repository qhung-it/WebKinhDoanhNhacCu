package model.dao.intf;

import model.entity.OrderStatus;

import java.util.List;

public interface OrderStatusDAO {

    boolean save(OrderStatus orderStatus);

    boolean update(OrderStatus orderStatus);

    boolean delete(String statusId);

    OrderStatus findById(String statusId);

    List<OrderStatus> findAll();
}