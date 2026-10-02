package controller.service.intf;

import model.entity.CustomerRank;

import java.util.List;

public interface CustomerRankService {

    void themHang(CustomerRank customerRank);

    void suaHang(CustomerRank customerRank);

    void xoaHang(String rankId);

    CustomerRank timHang(String rankId);

    List<CustomerRank> getDanhSachHang();

    void tinhHangKhachHang(String userId);
}