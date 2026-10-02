package model.dao.intf;

import model.Role;
import model.entity.User;

import java.util.List;

public interface UserDAO {

    boolean save(User user);

    boolean update(User user);

    boolean delete(String userId);

    User findById(String userId);

    User findByEmail(String email);

    List<User> findAll();

    List<User> findByRole(Role role);

    List<User> findByCustomerRankId(String rankId);

    List<User> findByOrderHistory(String userId);
}