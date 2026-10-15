package model;

public enum OrderStatus {
    PROCESSING,   // Đang xử lý
    CONFIRMED,    // Đã xác nhận
    SHIPPING,     // Đang giao
    DELIVERED,    // Đã giao - chờ thu
    COMPLETED,    // Hoàn thành
    CANCELLED     // Đã hủy
}
