package controller.service.intf;

import model.Role;
import model.entity.User;

import java.util.List;

public interface UserService {

    void dangKy(User user);

    User dangNhap(String email, String password);

    void dangXuat();

    void suaThongTin(User user);

    void doiMatKhau(String userId, String matKhauMoi);

    void xoaUser(String userId);

    User timUser(String userId);

    List<User> getDanhSachUser();

    List<User> getUserTheoRole(Role role);

    void capNhatHangKhachHang(String userId, String rankId);

    List<User> getLichSuMuaHang(String userId);
}