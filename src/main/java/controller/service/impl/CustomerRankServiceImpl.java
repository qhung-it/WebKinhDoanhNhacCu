package controller.service.impl;

import controller.service.intf.CustomerRankService;
import model.dao.impl.CustomerRankDAOImpl;
import model.dao.impl.OrderDAOImpl;
import model.dao.impl.UserDAOImpl;
import model.dao.intf.CustomerRankDAO;
import model.dao.intf.OrderDAO;
import model.dao.intf.UserDAO;
import model.entity.CustomerRank;
import model.entity.Order;
import model.entity.User;

import java.math.BigDecimal;
import java.util.List;

public class CustomerRankServiceImpl implements CustomerRankService {

    private final CustomerRankDAO customerRankDAO;
    private final UserDAO userDAO;
    private final OrderDAO orderDAO;

    public CustomerRankServiceImpl() {
        this.customerRankDAO = new CustomerRankDAOImpl();
        this.userDAO = new UserDAOImpl();
        this.orderDAO = new OrderDAOImpl();
    }

    @Override
    public void themHang(CustomerRank customerRank) {
        customerRankDAO.save(customerRank);
    }

    @Override
    public void suaHang(CustomerRank customerRank) {
        customerRankDAO.update(customerRank);
    }

    @Override
    public void xoaHang(String rankId) {
        customerRankDAO.delete(rankId);
    }

    @Override
    public CustomerRank timHang(String rankId) {
        return customerRankDAO.findById(rankId);
    }

    @Override
    public List<CustomerRank> getDanhSachHang() {
        return customerRankDAO.findAll();
    }

    @Override
    public void tinhHangKhachHang(String userId) {

        User user = userDAO.findById(userId);

        if (user == null) {
            return;
        }

        List<Order> orders = orderDAO.findByUserId(userId);

        BigDecimal tongChiTieu = BigDecimal.ZERO;

        for (Order order : orders) {

            if (order.getTotalAmount() != null) {
                tongChiTieu = tongChiTieu.add(order.getTotalAmount());
            }
        }

        List<CustomerRank> danhSachHang = customerRankDAO.findAll();

        CustomerRank hangPhuHop = null;

        for (CustomerRank rank : danhSachHang) {

            if (rank.getMinSpending() == null) {
                continue;
            }

            if (rank.getMinSpending().compareTo(tongChiTieu) <= 0) {

                if (hangPhuHop == null ||
                        rank.getMinSpending()
                                .compareTo(hangPhuHop.getMinSpending()) > 0) {

                    hangPhuHop = rank;
                }
            }
        }

        user.setCustomerRank(hangPhuHop);

        userDAO.update(user);
    }
}