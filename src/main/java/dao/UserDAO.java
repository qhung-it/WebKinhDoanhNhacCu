package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.Role;
import model.entity.User;

import java.util.List;

/**
 * DAO quản lý Entity User.
 * Vai trò chính: CRUD User, tìm theo ID/email/role.
 */
public class UserDAO extends BaseDAO<User, String> {

    public UserDAO() {
        super(User.class);
    }

    /** Tìm User theo email (email là duy nhất trong hệ thống). */
    public User findByEmail(String email) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.email = :email", User.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    /** Lấy danh sách User theo vai trò (USER/ADMIN). */
    public List<User> findByRole(Role role) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.role = :role", User.class);
            query.setParameter("role", role);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /** Kiểm tra email đã tồn tại trong hệ thống hay chưa. */
    public boolean existsByEmail(String email) {
        return findByEmail(email) != null;
    }

    /** Kiểm tra thông tin đăng nhập (email + mật khẩu). */
    public User login(String email, String password) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.email = :email AND u.password = :password", User.class);
            query.setParameter("email", email);
            query.setParameter("password", password);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
}
