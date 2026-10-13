/*
  Du lieu mau cho SQL Server / DB_WebNhac.
  Chay sau khi ung dung da tao schema (hibernate.hbm2ddl.auto=update).
  Script giu lai tai khoan admin-001 neu da ton tai; moi bang duoc seed 5 dong.
  Có thể chạy lại sau bản seed cũ để cập nhật mã khuyến mãi. Khóa sample-* là cố định.
*/
SET XACT_ABORT ON;
BEGIN TRANSACTION;

-- Thêm cột nếu schema cũ được tạo trước khi có mã khuyến mãi.
IF COL_LENGTH('dbo.promotion', 'coupon_code') IS NULL
    ALTER TABLE dbo.promotion ADD coupon_code VARCHAR(50) NULL;
IF COL_LENGTH('dbo.orders', 'coupon_code') IS NULL
    ALTER TABLE dbo.orders ADD coupon_code VARCHAR(50) NULL;

-- Nếu đã chạy bản seed cũ, cập nhật mã cho các khuyến mãi hiện có.
UPDATE promotion SET coupon_code = CASE promotion_id
    WHEN 'sample-promo-001' THEN 'SOUNDORA10'
    WHEN 'sample-promo-002' THEN 'JBL10'
    WHEN 'sample-promo-003' THEN 'WELCOME200K'
    WHEN 'sample-promo-004' THEN 'GOLD12'
    WHEN 'sample-promo-005' THEN 'ENDMONTH150'
    ELSE coupon_code END
WHERE promotion_id IN ('sample-promo-001', 'sample-promo-002', 'sample-promo-003', 'sample-promo-004', 'sample-promo-005');

-- Bỏ qua insert mẫu nếu bộ sample đã được nạp trước đó.
IF NOT EXISTS (SELECT 1 FROM products WHERE product_id = 'sample-product-001')
BEGIN

-- Bảng độc lập / bảng cha
INSERT INTO customer_rank (rank_id, rank_name, min_spending) VALUES
('rank-bronze', 'BRONZE', 100000000),
('rank-silver', 'SILVER', 150000000),
('rank-gold', 'GOLD', 250000000),
('rank-vip', 'VIP', 500000000),
('rank-vip-demo', 'VIP', 750000000);

IF NOT EXISTS (SELECT 1 FROM users WHERE user_id = 'admin-001' OR email = 'admin@soundora.vn')
    INSERT INTO users (user_id, full_name, email, password, phone, role, rank_id)
    VALUES ('admin-001', N'Quản trị viên', 'admin@soundora.vn', 'admin123', '0900000000', 'ADMIN', 'rank-vip');

INSERT INTO users (user_id, full_name, email, password, phone, role, rank_id) VALUES
('sample-user-002', N'Nguyễn Minh Anh', 'minhanh@example.com', 'user123', '0900000002', 'USER', 'rank-bronze'),
('sample-user-003', N'Trần Hoàng Nam', 'hoangnam@example.com', 'user123', '0900000003', 'USER', 'rank-silver'),
('sample-user-004', N'Lê Thu Hà', 'thuha@example.com', 'user123', '0900000004', 'USER', 'rank-gold'),
('sample-user-005', N'Phạm Quốc Bảo', 'quocbao@example.com', 'user123', '0900000005', 'USER', 'rank-vip-demo');

INSERT INTO category (category_id, category_name, status) VALUES
('cat-headphones', N'Tai nghe', 1),
('cat-speakers', N'Loa', 1),
('cat-microphones', N'Micro', 1),
('cat-amplifiers', N'Ampli', 1),
('cat-accessories', N'Phụ kiện', 1);

INSERT INTO brand (brand_id, brand_name, origin, description) VALUES
('brand-sony', N'Sony', N'Nhật Bản', N'Thiết bị âm thanh Sony'),
('brand-jbl', N'JBL', N'Hoa Kỳ', N'Loa và tai nghe JBL'),
('brand-bose', N'Bose', N'Hoa Kỳ', N'Thiết bị âm thanh Bose'),
('brand-audio-technica', N'Audio-Technica', N'Nhật Bản', N'Tai nghe và micro'),
('brand-sennheiser', N'Sennheiser', N'Đức', N'Thiết bị âm thanh Sennheiser');

INSERT INTO products (product_id, product_name, sku, short_description, description, price, old_price, quantity, badge, emoji, is_new, active, image, images, brand_id, category_id) VALUES
('sample-product-001', N'Tai nghe Sony WH-1000XM5', 'SON-1001', N'Tai nghe chống ồn cao cấp', N'Tai nghe không dây chống ồn chủ động.', 8490000, 9990000, 20, 'hot', N'🎧', 1, 1, NULL, NULL, 'brand-sony', 'cat-headphones'),
('sample-product-002', N'Loa JBL Flip 6', 'JBL-1002', N'Loa Bluetooth chống nước', N'Loa di động nhỏ gọn, âm thanh mạnh mẽ.', 2490000, 2990000, 35, 'sale', N'🔊', 0, 1, NULL, NULL, 'brand-jbl', 'cat-speakers'),
('sample-product-003', N'Tai nghe Bose QC Ultra', 'BOS-1003', N'Tai nghe chống ồn êm ái', N'Tai nghe Bluetooth với chống ồn cao cấp.', 9990000, 10990000, 12, 'new', N'🎧', 1, 1, NULL, NULL, 'brand-bose', 'cat-headphones'),
('sample-product-004', N'Micro Audio-Technica AT2020', 'AUD-1004', N'Micro thu âm condenser', N'Micro dành cho phòng thu và sáng tạo nội dung.', 2890000, 3290000, 18, NULL, N'🎙️', 0, 1, NULL, NULL, 'brand-audio-technica', 'cat-microphones'),
('sample-product-005', N'Tai nghe Sennheiser HD 560S', 'SEN-1005', N'Tai nghe kiểm âm mở', N'Tai nghe có dây cho trải nghiệm nghe chi tiết.', 4590000, 4990000, 9, NULL, N'🎧', 0, 1, NULL, NULL, 'brand-sennheiser', 'cat-headphones');

INSERT INTO promotion (promotion_id, promotion_name, coupon_code, discount_type, discount_value, start_date_time, end_date_time) VALUES
('sample-promo-001', N'Ưu đãi khách hàng mới', 'SOUNDORA10', 'PERCENTAGE', 10, DATEADD(day,-10,GETDATE()), DATEADD(day,30,GETDATE())),
('sample-promo-002', N'Tuần lễ âm thanh', 'JBL10', 'PERCENTAGE', 10, DATEADD(day,-5,GETDATE()), DATEADD(day,20,GETDATE())),
('sample-promo-003', N'Giảm giá tai nghe', 'WELCOME200K', 'FIXED_AMOUNT', 200000, DATEADD(day,-2,GETDATE()), DATEADD(day,15,GETDATE())),
('sample-promo-004', N'Ưu đãi thành viên vàng', 'GOLD12', 'PERCENTAGE', 12, DATEADD(day,-1,GETDATE()), DATEADD(day,25,GETDATE())),
('sample-promo-005', N'Ưu đãi cuối tháng', 'ENDMONTH150', 'FIXED_AMOUNT', 150000, DATEADD(day,-1,GETDATE()), DATEADD(day,35,GETDATE()));

INSERT INTO site_setting (setting_id, free_shipping_from, shipping_fee, updated_at) VALUES
('default', 500000, 30000, GETDATE()),
('sample-setting-2', 600000, 25000, GETDATE()),
('sample-setting-3', 700000, 20000, GETDATE()),
('sample-setting-4', 800000, 15000, GETDATE()),
('sample-setting-5', 900000, 10000, GETDATE());

-- Dữ liệu nội dung không phụ thuộc đơn hàng
INSERT INTO contact_message (contact_id, sender_name, email, phone, subject, message, status, internal_note, created_at) VALUES
('sample-contact-001', N'Nguyễn Minh Anh', 'minhanh@example.com', '0900000002', N'Hỏi về sản phẩm', N'Tai nghe này có hỗ trợ kết nối Bluetooth nhiều thiết bị không?', N'Mới', NULL, DATEADD(day,-4,GETDATE())),
('sample-contact-002', N'Trần Hoàng Nam', 'hoangnam@example.com', '0900000003', N'Tình trạng đơn hàng', N'Tôi muốn kiểm tra tiến độ giao đơn hàng.', N'Đang xử lý', N'Đã tiếp nhận yêu cầu.', DATEADD(day,-3,GETDATE())),
('sample-contact-003', N'Lê Thu Hà', 'thuha@example.com', '0900000004', N'Đổi trả', N'Sản phẩm còn trong thời hạn đổi trả không?', N'Mới', NULL, DATEADD(day,-2,GETDATE())),
('sample-contact-004', N'Phạm Quốc Bảo', 'quocbao@example.com', '0900000005', N'Tư vấn loa', N'Nhờ tư vấn loa phù hợp cho phòng khách.', N'Đã phản hồi', N'Đã gửi thông tin sản phẩm.', DATEADD(day,-1,GETDATE())),
('sample-contact-005', N'Hoàng Yến', 'yen@example.com', '0900000006', N'Bảo hành', N'Thời gian bảo hành của micro là bao lâu?', N'Mới', NULL, GETDATE());

INSERT INTO news_article (article_id, title, category, excerpt, content, emoji, author, color, color2, published, published_at, created_at) VALUES
('sample-news-001', N'Cách chọn tai nghe phù hợp', N'Tư vấn', N'Một vài tiêu chí giúp bạn chọn tai nghe.', N'Xác định nhu cầu, kiểu đeo và ngân sách trước khi chọn tai nghe.', N'🎧', N'Soundora', '#273469', '#4f6dff', 1, DATEADD(day,-8,GETDATE()), DATEADD(day,-9,GETDATE())),
('sample-news-002', N'Bảo quản loa Bluetooth', N'Hướng dẫn', N'Mẹo giúp loa bền và hoạt động tốt.', N'Tránh nhiệt độ cao, vệ sinh đúng cách và sạc theo hướng dẫn.', N'🔊', N'Soundora', '#164e63', '#22a6b3', 1, DATEADD(day,-6,GETDATE()), DATEADD(day,-7,GETDATE())),
('sample-news-003', N'Tìm hiểu về chống ồn chủ động', N'Công nghệ', N'ANC hoạt động như thế nào?', N'Công nghệ ANC dùng micro và tín hiệu âm thanh để giảm tiếng ồn môi trường.', N'🎶', N'Soundora', '#4c1d95', '#8b5cf6', 1, DATEADD(day,-4,GETDATE()), DATEADD(day,-5,GETDATE())),
('sample-news-004', N'Thiết lập góc thu âm tại nhà', N'Hướng dẫn', N'Bắt đầu với thiết bị thu âm cơ bản.', N'Chọn micro phù hợp, đặt micro đúng vị trí và giảm âm vang trong phòng.', N'🎙️', N'Soundora', '#7c2d12', '#f97316', 1, DATEADD(day,-2,GETDATE()), DATEADD(day,-3,GETDATE())),
('sample-news-005', N'Chọn loa cho không gian nhỏ', N'Tư vấn', N'Ưu tiên kích thước và cách bố trí loa.', N'Đo diện tích phòng và xác định nhu cầu nghe trước khi chọn công suất loa.', N'🎵', N'Soundora', '#14532d', '#22c55e', 0, NULL, GETDATE());

INSERT INTO admin_audit_log (audit_id, actor_email, action, detail, created_at) VALUES
('sample-audit-001', 'admin@soundora.vn', N'Tạo sản phẩm', N'Tạo sản phẩm mẫu sample-product-001', DATEADD(day,-4,GETDATE())),
('sample-audit-002', 'admin@soundora.vn', N'Cập nhật danh mục', N'Cập nhật danh mục cat-headphones', DATEADD(day,-3,GETDATE())),
('sample-audit-003', 'admin@soundora.vn', N'Tạo khuyến mãi', N'Tạo chương trình sample-promo-001', DATEADD(day,-2,GETDATE())),
('sample-audit-004', 'admin@soundora.vn', N'Phản hồi liên hệ', N'Đã phản hồi sample-contact-004', DATEADD(day,-1,GETDATE())),
('sample-audit-005', 'admin@soundora.vn', N'Cập nhật cài đặt', N'Cập nhật phí giao hàng', GETDATE());

-- Bảng phụ thuộc người dùng / sản phẩm / đơn hàng
INSERT INTO cart (cart_id, user_id) VALUES
('sample-cart-001', 'admin-001'),
('sample-cart-002', 'sample-user-002'),
('sample-cart-003', 'sample-user-003'),
('sample-cart-004', 'sample-user-004'),
('sample-cart-005', 'sample-user-005');

INSERT INTO cart_line_item (cart_line_item_id, cart_id, product_id, quantity) VALUES
('sample-cart-line-001', 'sample-cart-001', 'sample-product-001', 1),
('sample-cart-line-002', 'sample-cart-002', 'sample-product-002', 2),
('sample-cart-line-003', 'sample-cart-003', 'sample-product-003', 1),
('sample-cart-line-004', 'sample-cart-004', 'sample-product-004', 1),
('sample-cart-line-005', 'sample-cart-005', 'sample-product-005', 2);

INSERT INTO orders (order_id, order_date, subtotal, discount, total_amount, shipping_address, order_status, note, shipping_fee, user_id) VALUES
('sample-order-001', DATEADD(day,-10,GETDATE()), 8490000, 0, 8520000, N'12 Nguyễn Huệ, Quận 1, TP.HCM', 'COMPLETED', N'Giao giờ hành chính', 30000, 'sample-user-002'),
('sample-order-002', DATEADD(day,-7,GETDATE()), 4980000, 0, 5010000, N'20 Lê Lợi, Quận 1, TP.HCM', 'SHIPPING', NULL, 30000, 'sample-user-003'),
('sample-order-003', DATEADD(day,-5,GETDATE()), 9990000, 0, 10020000, N'5 Trần Hưng Đạo, Quận 5, TP.HCM', 'CONFIRMED', NULL, 30000, 'sample-user-004'),
('sample-order-004', DATEADD(day,-3,GETDATE()), 2890000, 0, 2920000, N'88 Điện Biên Phủ, Bình Thạnh, TP.HCM', 'PROCESSING', NULL, 30000, 'sample-user-005'),
('sample-order-005', DATEADD(day,-1,GETDATE()), 4590000, 0, 4620000, N'7 Phan Chu Trinh, Hải Châu, Đà Nẵng', 'CANCELLED', N'Khách yêu cầu hủy', 30000, 'sample-user-002');

INSERT INTO order_line_item (order_line_item_id, product_id, unit_price, quantity, order_id) VALUES
('sample-order-line-001', 'sample-product-001', 8490000, 1, 'sample-order-001'),
('sample-order-line-002', 'sample-product-002', 2490000, 2, 'sample-order-002'),
('sample-order-line-003', 'sample-product-003', 9990000, 1, 'sample-order-003'),
('sample-order-line-004', 'sample-product-004', 2890000, 1, 'sample-order-004'),
('sample-order-line-005', 'sample-product-005', 4590000, 1, 'sample-order-005');

INSERT INTO order_history (history_id, order_id, status, changed_at, changed_by) VALUES
('sample-order-history-001', 'sample-order-001', 'COMPLETED', DATEADD(day,-8,GETDATE()), N'admin@soundora.vn'),
('sample-order-history-002', 'sample-order-002', 'SHIPPING', DATEADD(day,-5,GETDATE()), N'admin@soundora.vn'),
('sample-order-history-003', 'sample-order-003', 'CONFIRMED', DATEADD(day,-4,GETDATE()), N'admin@soundora.vn'),
('sample-order-history-004', 'sample-order-004', 'PROCESSING', DATEADD(day,-3,GETDATE()), N'admin@soundora.vn'),
('sample-order-history-005', 'sample-order-005', 'CANCELLED', DATEADD(day,-1,GETDATE()), N'admin@soundora.vn');

INSERT INTO payment (payment_id, payment_method, amount, payment_date, payment_status, order_id) VALUES
('sample-payment-001', 'CASH', 8520000, DATEADD(day,-8,GETDATE()), 'COMPLETED', 'sample-order-001'),
('sample-payment-002', 'E_WALLET', 5010000, NULL, 'PENDING', 'sample-order-002'),
('sample-payment-003', 'E_WALLET', 10020000, NULL, 'PENDING', 'sample-order-003'),
('sample-payment-004', 'CASH', 2920000, NULL, 'PENDING', 'sample-order-004'),
('sample-payment-005', 'CASH', 4620000, NULL, 'REFUNDED', 'sample-order-005');

INSERT INTO deposit (deposit_id, amount, deposit_date, deposit_status, order_id) VALUES
('sample-deposit-001', 2556000, DATEADD(day,-8,GETDATE()), 'COMPLETED', 'sample-order-001'),
('sample-deposit-002', 1503000, NULL, 'PENDING', 'sample-order-002'),
('sample-deposit-003', 3006000, NULL, 'PENDING', 'sample-order-003'),
('sample-deposit-004', 876000, NULL, 'PENDING', 'sample-order-004'),
('sample-deposit-005', 0, DATEADD(day,-1,GETDATE()), 'REFUNDED', 'sample-order-005');

INSERT INTO review (review_id, rating, comment, approved, user_id, product_id, order_id) VALUES
('sample-review-001', 5, N'Chống ồn tốt, đeo thoải mái.', 1, 'sample-user-002', 'sample-product-001', 'sample-order-001'),
('sample-review-002', 4, N'Loa gọn và âm thanh tốt.', 1, 'sample-user-003', 'sample-product-002', 'sample-order-002'),
('sample-review-003', 5, N'Âm thanh chi tiết, hoàn thiện đẹp.', 0, 'sample-user-004', 'sample-product-003', 'sample-order-003'),
('sample-review-004', 4, N'Thu âm rõ, dễ sử dụng.', 1, 'sample-user-005', 'sample-product-004', 'sample-order-004'),
('sample-review-005', 5, N'Nghe nhạc lâu không mỏi tai.', 0, 'sample-user-002', 'sample-product-005', 'sample-order-005');

INSERT INTO inventory_history (inventory_history_id, product_id, operation, quantity_change, stock_before, stock_after, note, changed_by, changed_at) VALUES
('sample-inventory-001', 'sample-product-001', 'STOCK_IN', 20, 0, 20, N'Nhập tồn kho ban đầu', 'admin@soundora.vn', DATEADD(day,-8,GETDATE())),
('sample-inventory-002', 'sample-product-002', 'STOCK_IN', 35, 0, 35, N'Nhập tồn kho ban đầu', 'admin@soundora.vn', DATEADD(day,-7,GETDATE())),
('sample-inventory-003', 'sample-product-003', 'ADJUSTMENT', -2, 14, 12, N'Điều chỉnh sau kiểm kê', 'admin@soundora.vn', DATEADD(day,-5,GETDATE())),
('sample-inventory-004', 'sample-product-004', 'STOCK_IN', 18, 0, 18, N'Nhập tồn kho ban đầu', 'admin@soundora.vn', DATEADD(day,-3,GETDATE())),
('sample-inventory-005', 'sample-product-005', 'ORDER', -1, 10, 9, N'Xuất kho theo đơn mẫu', 'admin@soundora.vn', DATEADD(day,-1,GETDATE()));

-- Bảng liên kết many-to-many
INSERT INTO promotion_product (promotion_id, product_id) VALUES
('sample-promo-001', 'sample-product-001'),
('sample-promo-002', 'sample-product-002'),
('sample-promo-003', 'sample-product-003'),
('sample-promo-004', 'sample-product-004'),
('sample-promo-005', 'sample-product-005');

INSERT INTO promotion_customer_rank (promotion_id, rank_id) VALUES
('sample-promo-001', 'rank-bronze'),
('sample-promo-002', 'rank-silver'),
('sample-promo-003', 'rank-gold'),
('sample-promo-004', 'rank-vip-demo'),
('sample-promo-005', 'rank-vip-demo');

END;

-- GOLD12 dùng được bởi tài khoản mẫu có rank-vip-demo; đồng bộ cả seed cũ.
DELETE FROM promotion_customer_rank WHERE promotion_id = 'sample-promo-004';
INSERT INTO promotion_customer_rank (promotion_id, rank_id)
VALUES ('sample-promo-004', 'rank-vip-demo');

COMMIT TRANSACTION;

-- Kiểm tra dữ liệu mẫu: một SELECT cho mỗi entity/table
SELECT * FROM admin_audit_log;
SELECT * FROM brand;
SELECT * FROM cart;
SELECT * FROM cart_line_item;
SELECT * FROM category;
SELECT * FROM contact_message;
SELECT * FROM customer_rank;
SELECT * FROM deposit;
SELECT * FROM inventory_history;
SELECT * FROM news_article;
SELECT * FROM orders;
SELECT * FROM order_history;
SELECT * FROM order_line_item;
SELECT * FROM payment;
SELECT * FROM products;
SELECT * FROM promotion;
SELECT * FROM review;
SELECT * FROM site_setting;
SELECT * FROM users;

-- Hai bảng liên kết many-to-many của Promotion
SELECT * FROM promotion_product;
SELECT * FROM promotion_customer_rank;
