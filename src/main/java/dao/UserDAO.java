package dao;

import model.Role;
import model.entity.User;

import java.util.List;

/**
 * DAO quản lý Entity User.
 * Vai trò chính: CRUD User, tìm theo ID/email/role.
 */
public interface UserDAO extends BaseDAO<User, String> {

    /** Tìm User theo email (email là duy nhất trong hệ thống). */
    User findByEmail(String email);

    /** Lấy danh sách User theo vai trò (USER/ADMIN). */
    List<User> findByRole(Role role);

    /** Kiểm tra email đã tồn tại trong hệ thống hay chưa. */
    boolean existsByEmail(String email);

    /** Kiểm tra thông tin đăng nhập (email + mật khẩu). */
    User login(String email, String password);
}
