package controller.service.impl;

import controller.service.intf.UserService;
import model.Role;
import model.dao.impl.CustomerRankDAOImpl;
import model.dao.impl.UserDAOImpl;
import model.dao.intf.CustomerRankDAO;
import model.dao.intf.UserDAO;
import model.entity.CustomerRank;
import model.entity.User;

import java.util.List;

public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;
    private final CustomerRankDAO customerRankDAO;

    public UserServiceImpl() {
        this.userDAO = new UserDAOImpl();
        this.customerRankDAO = new CustomerRankDAOImpl();
    }

    @Override
    public void dangKy(User user) {
        userDAO.save(user);
    }

    @Override
    public User dangNhap(String email, String password) {
        User user = userDAO.findByEmail(email);

        if (user == null) {
            return null;
        }

        if (!user.getPassword().equals(password)) {
            return null;
        }

        return user;
    }

    @Override
    public void dangXuat() {
        // Xử lý đăng xuất sẽ được thực hiện ở Controller/Session
    }

    @Override
    public void suaThongTin(User user) {
        userDAO.update(user);
    }

    @Override
    public void doiMatKhau(String userId, String matKhauMoi) {
        User user = userDAO.findById(userId);

        if (user == null) {
            return;
        }

        user.setPassword(matKhauMoi);
        userDAO.update(user);
    }

    @Override
    public void xoaUser(String userId) {
        userDAO.delete(userId);
    }

    @Override
    public User timUser(String userId) {
        return userDAO.findById(userId);
    }

    @Override
    public List<User> getDanhSachUser() {
        return userDAO.findAll();
    }

    @Override
    public List<User> getUserTheoRole(Role role) {
        return userDAO.findByRole(role);
    }

    @Override
    public void capNhatHangKhachHang(String userId, String rankId) {

        if (userId == null || userId.trim().isEmpty()) {
            return;
        }

        if (rankId == null || rankId.trim().isEmpty()) {
            return;
        }

        User user = userDAO.findById(userId);

        if (user == null) {
            return;
        }

        CustomerRank customerRank = customerRankDAO.findById(rankId);

        if (customerRank == null) {
            return;
        }

        user.setCustomerRank(customerRank);

        userDAO.update(user);
    }

    @Override
    public List<User> getLichSuMuaHang(String userId) {
        return userDAO.findByOrderHistory(userId);
    }
}