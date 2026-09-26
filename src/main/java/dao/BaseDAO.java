package dao;

import java.util.List;

/**
 * Interface DAO cơ sở, khai báo các thao tác CRUD dùng chung cho mọi Entity.
 * Các interface DAO cụ thể (UserDAO, ProductDAO, ...) kế thừa interface này
 * và khai báo thêm các phương thức tìm kiếm/truy vấn đặc thù cho từng Entity.
 *
 * @param <T>  Kiểu Entity (User, Product, ...)
 * @param <ID> Kiểu khóa chính của Entity (thường là String)
 */
public interface BaseDAO<T, ID> {

    /** Thêm mới một Entity vào database. */
    T save(T entity);

    /** Cập nhật một Entity đã tồn tại. */
    T update(T entity);

    /** Tìm một Entity theo khóa chính. Trả về null nếu không tìm thấy. */
    T findById(ID id);

    /** Lấy toàn bộ danh sách Entity. */
    List<T> findAll();

    /** Xóa một Entity (theo tham chiếu đối tượng). */
    void delete(T entity);

    /** Xóa một Entity theo khóa chính. Trả về true nếu xóa thành công. */
    boolean deleteById(ID id);
}
