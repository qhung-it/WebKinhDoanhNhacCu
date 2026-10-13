-- Script tong hop de chuan bi du lieu test admin va user.
-- Yeu cau: DB_WebNhac da co schema ung dung; khoi dong app mot lan truoc (Hibernate hbm2ddl.auto=update).
-- Chay file nay trong SQL Server Management Studio; se nap catalog, bang user, du lieu mau va chuan hoa Unicode.
-- Ma SOUNDORA10 trong sample-data duoc doi thanh SAMPLE10 de tranh trung ma catalog.

-- ===== datajs-catalog-seed.sql =====
-- Soundora catalog seed parsed from data.js; SQL Server / DB_WebNhac.
USE DB_WebNhac;
SET XACT_ABORT ON;

-- Tạo schema catalog tối thiểu nếu DB mới chưa được Hibernate khởi tạo.
IF OBJECT_ID(N'dbo.category', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.category (
        category_id NVARCHAR(255) NOT NULL PRIMARY KEY,
        category_name NVARCHAR(255) NULL,
        status BIT NOT NULL CONSTRAINT df_category_status DEFAULT (1)
    );
END;
IF OBJECT_ID(N'dbo.brand', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.brand (
        brand_id NVARCHAR(255) NOT NULL PRIMARY KEY,
        brand_name NVARCHAR(255) NULL,
        description NVARCHAR(MAX) NULL
    );
END;
IF OBJECT_ID(N'dbo.products', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.products (
        product_id NVARCHAR(255) NOT NULL PRIMARY KEY,
        product_name NVARCHAR(255) NOT NULL,
        description NVARCHAR(MAX) NULL,
        price DECIMAL(19,2) NULL,
        quantity INT NOT NULL CONSTRAINT df_products_quantity DEFAULT (0),
        image NVARCHAR(MAX) NULL,
        brand_id NVARCHAR(255) NULL,
        category_id NVARCHAR(255) NULL
    );
END;

BEGIN TRANSACTION;
IF OBJECT_ID(N'dbo.promotion', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.promotion (
        promotion_id NVARCHAR(255) NOT NULL PRIMARY KEY,
        promotion_name NVARCHAR(255) NOT NULL,
        coupon_code NVARCHAR(20) NULL,
        discount_type NVARCHAR(32) NOT NULL,
        discount_value DECIMAL(19,2) NOT NULL,
        start_date_time DATETIME2 NULL,
        end_date_time DATETIME2 NULL,
        minimum_order_amount DECIMAL(19,2) NOT NULL CONSTRAINT df_promotion_minimum DEFAULT (0),
        max_discount DECIMAL(19,2) NULL,
        description NVARCHAR(500) NULL,
        free_shipping BIT NOT NULL CONSTRAINT df_promotion_free_shipping DEFAULT (0),
        active BIT NOT NULL CONSTRAINT df_promotion_active DEFAULT (1)
    );
END;
IF COL_LENGTH(N'dbo.promotion', N'minimum_order_amount') IS NULL
    ALTER TABLE dbo.promotion ADD minimum_order_amount DECIMAL(19,2) NOT NULL CONSTRAINT df_promotion_minimum_migration DEFAULT (0);
IF COL_LENGTH(N'dbo.promotion', N'max_discount') IS NULL
    ALTER TABLE dbo.promotion ADD max_discount DECIMAL(19,2) NULL;
IF COL_LENGTH(N'dbo.promotion', N'description') IS NULL
    ALTER TABLE dbo.promotion ADD description NVARCHAR(500) NULL;
IF COL_LENGTH(N'dbo.promotion', N'free_shipping') IS NULL
    ALTER TABLE dbo.promotion ADD free_shipping BIT NOT NULL CONSTRAINT df_promotion_free_shipping_migration DEFAULT (0);
IF COL_LENGTH(N'dbo.promotion', N'active') IS NULL
    ALTER TABLE dbo.promotion ADD active BIT NOT NULL CONSTRAINT df_promotion_active_migration DEFAULT (1);
IF COL_LENGTH(N'dbo.promotion', N'coupon_code') IS NULL
    ALTER TABLE dbo.promotion ADD coupon_code NVARCHAR(20) NULL;
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'ux_promotion_coupon_code' AND object_id = OBJECT_ID(N'dbo.promotion'))
    CREATE UNIQUE INDEX ux_promotion_coupon_code ON dbo.promotion(coupon_code) WHERE coupon_code IS NOT NULL;

IF OBJECT_ID(N'dbo.orders', N'U') IS NOT NULL AND COL_LENGTH(N'dbo.orders', N'shipping_fee') IS NULL
    ALTER TABLE dbo.orders ADD shipping_fee DECIMAL(19,2) NOT NULL CONSTRAINT df_orders_shipping_fee DEFAULT (0);
IF OBJECT_ID(N'dbo.orders', N'U') IS NOT NULL AND COL_LENGTH(N'dbo.orders', N'promotion_code') IS NULL
    ALTER TABLE dbo.orders ADD promotion_code NVARCHAR(255) NULL;

UPDATE dbo.promotion SET promotion_name = N'Soundora giảm 10%', coupon_code = N'SOUNDORA10', discount_type = N'PERCENTAGE', discount_value = 10, minimum_order_amount = 0, max_discount = 500000, description = N'Giảm 10% (tối đa 500.000đ)', free_shipping = 0, active = 1 WHERE promotion_id = N'SOUNDORA10';
IF NOT EXISTS (SELECT 1 FROM dbo.promotion WHERE promotion_id = N'SOUNDORA10')
    INSERT INTO dbo.promotion (promotion_id, promotion_name, coupon_code, discount_type, discount_value, minimum_order_amount, max_discount, description, free_shipping, active)
    VALUES (N'SOUNDORA10', N'Soundora giảm 10%', N'SOUNDORA10', N'PERCENTAGE', 10, 0, 500000, N'Giảm 10% (tối đa 500.000đ)', 0, 1);
UPDATE dbo.promotion SET promotion_name = N'Miễn phí vận chuyển', coupon_code = N'FREESHIP', discount_type = N'FIXED_AMOUNT', discount_value = 0, minimum_order_amount = 0, description = N'Miễn phí vận chuyển', free_shipping = 1, active = 1 WHERE promotion_id = N'FREESHIP';
IF NOT EXISTS (SELECT 1 FROM dbo.promotion WHERE promotion_id = N'FREESHIP')
    INSERT INTO dbo.promotion (promotion_id, promotion_name, coupon_code, discount_type, discount_value, minimum_order_amount, description, free_shipping, active)
    VALUES (N'FREESHIP', N'Miễn phí vận chuyển', N'FREESHIP', N'FIXED_AMOUNT', 0, 0, N'Miễn phí vận chuyển', 1, 1);
UPDATE dbo.promotion SET promotion_name = N'Chào mừng giảm 50.000đ', coupon_code = N'WELCOME50', discount_type = N'FIXED_AMOUNT', discount_value = 50000, minimum_order_amount = 1000000, max_discount = NULL, description = N'Giảm 50.000đ cho đơn từ 1.000.000đ', free_shipping = 0, active = 1 WHERE promotion_id = N'WELCOME50';
IF NOT EXISTS (SELECT 1 FROM dbo.promotion WHERE promotion_id = N'WELCOME50')
    INSERT INTO dbo.promotion (promotion_id, promotion_name, coupon_code, discount_type, discount_value, minimum_order_amount, description, free_shipping, active)
    VALUES (N'WELCOME50', N'Chào mừng giảm 50.000đ', N'WELCOME50', N'FIXED_AMOUNT', 50000, 1000000, N'Giảm 50.000đ cho đơn từ 1.000.000đ', 0, 1);

IF COL_LENGTH(N'dbo.products', N'description') IS NULL
    ALTER TABLE dbo.products ADD description NVARCHAR(MAX) NULL;
ELSE
    ALTER TABLE dbo.products ALTER COLUMN description NVARCHAR(MAX) NULL;
UPDATE dbo.category SET category_name = N'Guitar' WHERE category_id = N'guitar';
IF NOT EXISTS (SELECT 1 FROM dbo.category WHERE category_id = N'guitar')
    INSERT INTO dbo.category (category_id, category_name, status) VALUES (N'guitar', N'Guitar', 1);
UPDATE dbo.category SET category_name = N'Piano' WHERE category_id = N'piano';
IF NOT EXISTS (SELECT 1 FROM dbo.category WHERE category_id = N'piano')
    INSERT INTO dbo.category (category_id, category_name, status) VALUES (N'piano', N'Piano', 1);
UPDATE dbo.category SET category_name = N'Trống' WHERE category_id = N'trong';
IF NOT EXISTS (SELECT 1 FROM dbo.category WHERE category_id = N'trong')
    INSERT INTO dbo.category (category_id, category_name, status) VALUES (N'trong', N'Trống', 1);
UPDATE dbo.category SET category_name = N'Organ' WHERE category_id = N'organ';
IF NOT EXISTS (SELECT 1 FROM dbo.category WHERE category_id = N'organ')
    INSERT INTO dbo.category (category_id, category_name, status) VALUES (N'organ', N'Organ', 1);
UPDATE dbo.category SET category_name = N'Ukulele' WHERE category_id = N'ukulele';
IF NOT EXISTS (SELECT 1 FROM dbo.category WHERE category_id = N'ukulele')
    INSERT INTO dbo.category (category_id, category_name, status) VALUES (N'ukulele', N'Ukulele', 1);
UPDATE dbo.category SET category_name = N'Violin' WHERE category_id = N'violin';
IF NOT EXISTS (SELECT 1 FROM dbo.category WHERE category_id = N'violin')
    INSERT INTO dbo.category (category_id, category_name, status) VALUES (N'violin', N'Violin', 1);
UPDATE dbo.category SET category_name = N'Kèn & Sáo' WHERE category_id = N'ken-sao';
IF NOT EXISTS (SELECT 1 FROM dbo.category WHERE category_id = N'ken-sao')
    INSERT INTO dbo.category (category_id, category_name, status) VALUES (N'ken-sao', N'Kèn & Sáo', 1);
UPDATE dbo.category SET category_name = N'Phụ kiện' WHERE category_id = N'phu-kien';
IF NOT EXISTS (SELECT 1 FROM dbo.category WHERE category_id = N'phu-kien')
    INSERT INTO dbo.category (category_id, category_name, status) VALUES (N'phu-kien', N'Phụ kiện', 1);
UPDATE dbo.brand SET brand_name = N'Yamaha', description = N'Thương hiệu nhạc cụ hàng đầu thế giới, nổi tiếng với piano, guitar và organ.' WHERE brand_id = N'yamaha';
IF NOT EXISTS (SELECT 1 FROM dbo.brand WHERE brand_id = N'yamaha')
    INSERT INTO dbo.brand (brand_id, brand_name, description) VALUES (N'yamaha', N'Yamaha', N'Thương hiệu nhạc cụ hàng đầu thế giới, nổi tiếng với piano, guitar và organ.');
UPDATE dbo.brand SET brand_name = N'Fender', description = N'Cái nôi của Stratocaster và Telecaster huyền thoại.' WHERE brand_id = N'fender';
IF NOT EXISTS (SELECT 1 FROM dbo.brand WHERE brand_id = N'fender')
    INSERT INTO dbo.brand (brand_id, brand_name, description) VALUES (N'fender', N'Fender', N'Cái nôi của Stratocaster và Telecaster huyền thoại.');
UPDATE dbo.brand SET brand_name = N'Ibanez', description = N'Chuyên guitar điện, bass với cần đàn mỏng, tốc độ cao.' WHERE brand_id = N'ibanez';
IF NOT EXISTS (SELECT 1 FROM dbo.brand WHERE brand_id = N'ibanez')
    INSERT INTO dbo.brand (brand_id, brand_name, description) VALUES (N'ibanez', N'Ibanez', N'Chuyên guitar điện, bass với cần đàn mỏng, tốc độ cao.');
UPDATE dbo.brand SET brand_name = N'Taylor', description = N'Guitar acoustic cao cấp với âm thanh trong trẻo, tinh tế.' WHERE brand_id = N'taylor';
IF NOT EXISTS (SELECT 1 FROM dbo.brand WHERE brand_id = N'taylor')
    INSERT INTO dbo.brand (brand_id, brand_name, description) VALUES (N'taylor', N'Taylor', N'Guitar acoustic cao cấp với âm thanh trong trẻo, tinh tế.');
UPDATE dbo.brand SET brand_name = N'Roland', description = N'Dẫn đầu về piano điện, trống điện và synthesizer.' WHERE brand_id = N'roland';
IF NOT EXISTS (SELECT 1 FROM dbo.brand WHERE brand_id = N'roland')
    INSERT INTO dbo.brand (brand_id, brand_name, description) VALUES (N'roland', N'Roland', N'Dẫn đầu về piano điện, trống điện và synthesizer.');
UPDATE dbo.brand SET brand_name = N'Cort', description = N'Guitar chất lượng tốt, giá hợp lý cho người mới bắt đầu.' WHERE brand_id = N'cort';
IF NOT EXISTS (SELECT 1 FROM dbo.brand WHERE brand_id = N'cort')
    INSERT INTO dbo.brand (brand_id, brand_name, description) VALUES (N'cort', N'Cort', N'Guitar chất lượng tốt, giá hợp lý cho người mới bắt đầu.');
UPDATE dbo.brand SET brand_name = N'Casio', description = N'Organ và piano điện gọn nhẹ, dễ sử dụng, giá dễ tiếp cận.' WHERE brand_id = N'casio';
IF NOT EXISTS (SELECT 1 FROM dbo.brand WHERE brand_id = N'casio')
    INSERT INTO dbo.brand (brand_id, brand_name, description) VALUES (N'casio', N'Casio', N'Organ và piano điện gọn nhẹ, dễ sử dụng, giá dễ tiếp cận.');
UPDATE dbo.brand SET brand_name = N'Gibson', description = N'Les Paul, SG, ES - những cây đàn đã làm nên lịch sử rock.' WHERE brand_id = N'gibson';
IF NOT EXISTS (SELECT 1 FROM dbo.brand WHERE brand_id = N'gibson')
    INSERT INTO dbo.brand (brand_id, brand_name, description) VALUES (N'gibson', N'Gibson', N'Les Paul, SG, ES - những cây đàn đã làm nên lịch sử rock.');
UPDATE dbo.brand SET brand_name = N'Marshall', description = N'Ampli và loa mang âm thanh rock đặc trưng.' WHERE brand_id = N'marshall';
IF NOT EXISTS (SELECT 1 FROM dbo.brand WHERE brand_id = N'marshall')
    INSERT INTO dbo.brand (brand_id, brand_name, description) VALUES (N'marshall', N'Marshall', N'Ampli và loa mang âm thanh rock đặc trưng.');
UPDATE dbo.brand SET brand_name = N'Boss', description = N'Pedal hiệu ứng và thiết bị guitar bền bỉ, chuyên nghiệp.' WHERE brand_id = N'boss';
IF NOT EXISTS (SELECT 1 FROM dbo.brand WHERE brand_id = N'boss')
    INSERT INTO dbo.brand (brand_id, brand_name, description) VALUES (N'boss', N'Boss', N'Pedal hiệu ứng và thiết bị guitar bền bỉ, chuyên nghiệp.');
UPDATE dbo.brand SET brand_name = N'Pearl', description = N'Bộ trống cơ chất lượng cao cho cả học viên lẫn nghệ sĩ.' WHERE brand_id = N'pearl';
IF NOT EXISTS (SELECT 1 FROM dbo.brand WHERE brand_id = N'pearl')
    INSERT INTO dbo.brand (brand_id, brand_name, description) VALUES (N'pearl', N'Pearl', N'Bộ trống cơ chất lượng cao cho cả học viên lẫn nghệ sĩ.');
UPDATE dbo.brand SET brand_name = N'Kala', description = N'Ukulele thiết kế đẹp, âm vang ấm áp.' WHERE brand_id = N'kala';
IF NOT EXISTS (SELECT 1 FROM dbo.brand WHERE brand_id = N'kala')
    INSERT INTO dbo.brand (brand_id, brand_name, description) VALUES (N'kala', N'Kala', N'Ukulele thiết kế đẹp, âm vang ấm áp.');
UPDATE dbo.brand SET brand_name = N'Stentor', description = N'Violin học sinh và sinh viên được nhiều trường nhạc tin dùng.' WHERE brand_id = N'stentor';
IF NOT EXISTS (SELECT 1 FROM dbo.brand WHERE brand_id = N'stentor')
    INSERT INTO dbo.brand (brand_id, brand_name, description) VALUES (N'stentor', N'Stentor', N'Violin học sinh và sinh viên được nhiều trường nhạc tin dùng.');
UPDATE dbo.brand SET brand_name = N'Dunlop', description = N'Phím gảy, capo và phụ kiện guitar nổi tiếng.' WHERE brand_id = N'dunlop';
IF NOT EXISTS (SELECT 1 FROM dbo.brand WHERE brand_id = N'dunlop')
    INSERT INTO dbo.brand (brand_id, brand_name, description) VALUES (N'dunlop', N'Dunlop', N'Phím gảy, capo và phụ kiện guitar nổi tiếng.');
UPDATE dbo.brand SET brand_name = N'D''Addario', description = N'Dây đàn chất lượng số 1 cho guitar, bass, violin.' WHERE brand_id = N'd-addario';
IF NOT EXISTS (SELECT 1 FROM dbo.brand WHERE brand_id = N'd-addario')
    INSERT INTO dbo.brand (brand_id, brand_name, description) VALUES (N'd-addario', N'D''Addario', N'Dây đàn chất lượng số 1 cho guitar, bass, violin.');
UPDATE dbo.brand SET brand_name = N'Zildjian', description = N'Thương hiệu cymbal lâu đời hơn 400 năm.' WHERE brand_id = N'zildjian';
IF NOT EXISTS (SELECT 1 FROM dbo.brand WHERE brand_id = N'zildjian')
    INSERT INTO dbo.brand (brand_id, brand_name, description) VALUES (N'zildjian', N'Zildjian', N'Thương hiệu cymbal lâu đời hơn 400 năm.');
UPDATE dbo.products SET product_name = N'Yamaha F310 - Guitar Acoustic', description = N'Yamaha F310 - Guitar Acoustic là lựa chọn nổi bật của Yamaha trong nhóm guitar. Mặt đàn gỗ vân sam cho âm sáng, ngân vang chuẩn xác, bán chạy số 1 cho người mới. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.' WHERE product_id = N'1';
IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'1')
    INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES (N'1', N'Yamaha F310 - Guitar Acoustic', N'Yamaha F310 - Guitar Acoustic là lựa chọn nổi bật của Yamaha trong nhóm guitar. Mặt đàn gỗ vân sam cho âm sáng, ngân vang chuẩn xác, bán chạy số 1 cho người mới. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 3490000, 12, NULL, N'yamaha', N'guitar');
UPDATE dbo.products SET product_name = N'Fender Player Stratocaster', description = N'Fender Player Stratocaster là lựa chọn nổi bật của Fender trong nhóm guitar. Huyền thoại Stratocaster với 3 single-coil cho âm thanh trong và cá tính. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.' WHERE product_id = N'2';
IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'2')
    INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES (N'2', N'Fender Player Stratocaster', N'Fender Player Stratocaster là lựa chọn nổi bật của Fender trong nhóm guitar. Huyền thoại Stratocaster với 3 single-coil cho âm thanh trong và cá tính. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 17900000, 19, NULL, N'fender', N'guitar');
UPDATE dbo.products SET product_name = N'Ibanez GRG121DX - Guitar Điện', description = N'Ibanez GRG121DX - Guitar Điện là lựa chọn nổi bật của Ibanez trong nhóm guitar. Cần đàn mỏng, chơi solo nhanh, phù hợp rock và metal. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.' WHERE product_id = N'3';
IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'3')
    INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES (N'3', N'Ibanez GRG121DX - Guitar Điện', N'Ibanez GRG121DX - Guitar Điện là lựa chọn nổi bật của Ibanez trong nhóm guitar. Cần đàn mỏng, chơi solo nhanh, phù hợp rock và metal. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 6290000, 26, NULL, N'ibanez', N'guitar');
UPDATE dbo.products SET product_name = N'Taylor GS Mini Mahogany', description = N'Taylor GS Mini Mahogany là lựa chọn nổi bật của Taylor trong nhóm guitar. Dáng đàn nhỏ gọn nhưng âm lượng lớn, thích hợp du lịch và thu âm. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.' WHERE product_id = N'4';
IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'4')
    INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES (N'4', N'Taylor GS Mini Mahogany', N'Taylor GS Mini Mahogany là lựa chọn nổi bật của Taylor trong nhóm guitar. Dáng đàn nhỏ gọn nhưng âm lượng lớn, thích hợp du lịch và thu âm. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 19500000, 33, NULL, N'taylor', N'guitar');
UPDATE dbo.products SET product_name = N'Cort AD810 - Guitar Acoustic', description = N'Cort AD810 - Guitar Acoustic là lựa chọn nổi bật của Cort trong nhóm guitar. Mức giá dễ tiếp cận nhưng chất lượng ổn định, âm thanh ấm áp. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.' WHERE product_id = N'5';
IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'5')
    INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES (N'5', N'Cort AD810 - Guitar Acoustic', N'Cort AD810 - Guitar Acoustic là lựa chọn nổi bật của Cort trong nhóm guitar. Mức giá dễ tiếp cận nhưng chất lượng ổn định, âm thanh ấm áp. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 2390000, 10, NULL, N'cort', N'guitar');
UPDATE dbo.products SET product_name = N'Yamaha P-125 - Piano Điện', description = N'Yamaha P-125 - Piano Điện là lựa chọn nổi bật của Yamaha trong nhóm piano. Phím GHS cảm ứng lực, âm thanh sampling từ đàn grand piano CFIIIS. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.' WHERE product_id = N'6';
IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'6')
    INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES (N'6', N'Yamaha P-125 - Piano Điện', N'Yamaha P-125 - Piano Điện là lựa chọn nổi bật của Yamaha trong nhóm piano. Phím GHS cảm ứng lực, âm thanh sampling từ đàn grand piano CFIIIS. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 16900000, 17, NULL, N'yamaha', N'piano');
UPDATE dbo.products SET product_name = N'Roland FP-30X - Piano Điện', description = N'Roland FP-30X - Piano Điện là lựa chọn nổi bật của Roland trong nhóm piano. Động cơ âm thanh SuperNATURAL, kết nối Bluetooth MIDI. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.' WHERE product_id = N'7';
IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'7')
    INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES (N'7', N'Roland FP-30X - Piano Điện', N'Roland FP-30X - Piano Điện là lựa chọn nổi bật của Roland trong nhóm piano. Động cơ âm thanh SuperNATURAL, kết nối Bluetooth MIDI. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 18500000, 24, NULL, N'roland', N'piano');
UPDATE dbo.products SET product_name = N'Casio CT-S300 - Organ', description = N'Casio CT-S300 - Organ là lựa chọn nổi bật của Casio trong nhóm organ. Gọn nhẹ, nhiều điệu nhạc, phù hợp tập luyện tại nhà. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.' WHERE product_id = N'8';
IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'8')
    INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES (N'8', N'Casio CT-S300 - Organ', N'Casio CT-S300 - Organ là lựa chọn nổi bật của Casio trong nhóm organ. Gọn nhẹ, nhiều điệu nhạc, phù hợp tập luyện tại nhà. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 3590000, 31, NULL, N'casio', N'organ');
UPDATE dbo.products SET product_name = N'Casio CDP-S160 - Piano Điện Mỏng', description = N'Casio CDP-S160 - Piano Điện Mỏng là lựa chọn nổi bật của Casio trong nhóm piano. Thiết kế siêu mỏng, bàn phím phản hồi lực Smart Scaled. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.' WHERE product_id = N'9';
IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'9')
    INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES (N'9', N'Casio CDP-S160 - Piano Điện Mỏng', N'Casio CDP-S160 - Piano Điện Mỏng là lựa chọn nổi bật của Casio trong nhóm piano. Thiết kế siêu mỏng, bàn phím phản hồi lực Smart Scaled. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 9890000, 8, NULL, N'casio', N'piano');
UPDATE dbo.products SET product_name = N'Yamaha PSR-E373 - Organ', description = N'Yamaha PSR-E373 - Organ là lựa chọn nổi bật của Yamaha trong nhóm organ. 622 âm sắc, 205 điệu nhạc, có chế độ học nhạc thông minh. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.' WHERE product_id = N'10';
IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'10')
    INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES (N'10', N'Yamaha PSR-E373 - Organ', N'Yamaha PSR-E373 - Organ là lựa chọn nổi bật của Yamaha trong nhóm organ. 622 âm sắc, 205 điệu nhạc, có chế độ học nhạc thông minh. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 5990000, 15, NULL, N'yamaha', N'organ');
UPDATE dbo.products SET product_name = N'Roland TD-1DMK - Trống Điện', description = N'Roland TD-1DMK - Trống Điện là lựa chọn nổi bật của Roland trong nhóm trống. Tập trống yên tĩnh trong căn hộ, mặt mesh cho cảm giác thật. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.' WHERE product_id = N'11';
IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'11')
    INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES (N'11', N'Roland TD-1DMK - Trống Điện', N'Roland TD-1DMK - Trống Điện là lựa chọn nổi bật của Roland trong nhóm trống. Tập trống yên tĩnh trong căn hộ, mặt mesh cho cảm giác thật. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 15900000, 22, NULL, N'roland', N'trong');
UPDATE dbo.products SET product_name = N'Pearl Roadshow 5 Piece - Trống Cơ', description = N'Pearl Roadshow 5 Piece - Trống Cơ là lựa chọn nổi bật của Pearl trong nhóm trống. Bộ trống 5 món đầy đủ cymbal, hardware và ghế ngồi. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.' WHERE product_id = N'12';
IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'12')
    INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES (N'12', N'Pearl Roadshow 5 Piece - Trống Cơ', N'Pearl Roadshow 5 Piece - Trống Cơ là lựa chọn nổi bật của Pearl trong nhóm trống. Bộ trống 5 món đầy đủ cymbal, hardware và ghế ngồi. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 12900000, 29, NULL, N'pearl', N'trong');
UPDATE dbo.products SET product_name = N'Zildjian S Family Cymbal Pack', description = N'Zildjian S Family Cymbal Pack là lựa chọn nổi bật của Zildjian trong nhóm trống. Bộ cymbal 3 món cho âm thanh sáng, rõ và bền. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.' WHERE product_id = N'13';
IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'13')
    INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES (N'13', N'Zildjian S Family Cymbal Pack', N'Zildjian S Family Cymbal Pack là lựa chọn nổi bật của Zildjian trong nhóm trống. Bộ cymbal 3 món cho âm thanh sáng, rõ và bền. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 7900000, 6, NULL, N'zildjian', N'trong');
UPDATE dbo.products SET product_name = N'Kala KA-15S - Ukulele Soprano', description = N'Kala KA-15S - Ukulele Soprano là lựa chọn nổi bật của Kala trong nhóm ukulele. Gỗ Mahogany, âm ấm, đẹp mắt, bán chạy nhất dòng ukulele. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.' WHERE product_id = N'14';
IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'14')
    INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES (N'14', N'Kala KA-15S - Ukulele Soprano', N'Kala KA-15S - Ukulele Soprano là lựa chọn nổi bật của Kala trong nhóm ukulele. Gỗ Mahogany, âm ấm, đẹp mắt, bán chạy nhất dòng ukulele. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 1290000, 13, NULL, N'kala', N'ukulele');
UPDATE dbo.products SET product_name = N'Cort UKE Concert', description = N'Cort UKE Concert là lựa chọn nổi bật của Cort trong nhóm ukulele. Size Concert dễ bấm hợp tay người lớn, giá tốt. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.' WHERE product_id = N'15';
IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'15')
    INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES (N'15', N'Cort UKE Concert', N'Cort UKE Concert là lựa chọn nổi bật của Cort trong nhóm ukulele. Size Concert dễ bấm hợp tay người lớn, giá tốt. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 1090000, 20, NULL, N'cort', N'ukulele');
UPDATE dbo.products SET product_name = N'Yamaha V3SKA - Violin 4/4', description = N'Yamaha V3SKA - Violin 4/4 là lựa chọn nổi bật của Yamaha trong nhóm violin. Violin học viên cao cấp, thùng đàn gỗ thích cho âm sáng. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.' WHERE product_id = N'16';
IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'16')
    INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES (N'16', N'Yamaha V3SKA - Violin 4/4', N'Yamaha V3SKA - Violin 4/4 là lựa chọn nổi bật của Yamaha trong nhóm violin. Violin học viên cao cấp, thùng đàn gỗ thích cho âm sáng. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 6890000, 27, NULL, N'yamaha', N'violin');
UPDATE dbo.products SET product_name = N'Stentor Student I - Violin 4/4', description = N'Stentor Student I - Violin 4/4 là lựa chọn nổi bật của Stentor trong nhóm violin. Chất lượng ổn định, được nhiều trường nhạc lựa chọn. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.' WHERE product_id = N'17';
IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'17')
    INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES (N'17', N'Stentor Student I - Violin 4/4', N'Stentor Student I - Violin 4/4 là lựa chọn nổi bật của Stentor trong nhóm violin. Chất lượng ổn định, được nhiều trường nhạc lựa chọn. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 3290000, 34, NULL, N'stentor', N'violin');
UPDATE dbo.products SET product_name = N'Yamaha YRS-24B - Sáo Recorder', description = N'Yamaha YRS-24B - Sáo Recorder là lựa chọn nổi bật của Yamaha trong nhóm kèn & sáo. Sáo recorder chuẩn trường học, âm chuẩn, dễ thổi. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.' WHERE product_id = N'18';
IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'18')
    INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES (N'18', N'Yamaha YRS-24B - Sáo Recorder', N'Yamaha YRS-24B - Sáo Recorder là lựa chọn nổi bật của Yamaha trong nhóm kèn & sáo. Sáo recorder chuẩn trường học, âm chuẩn, dễ thổi. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 190000, 11, NULL, N'yamaha', N'ken-sao');
UPDATE dbo.products SET product_name = N'Yamaha YAS-280 - Saxophone Alto', description = N'Yamaha YAS-280 - Saxophone Alto là lựa chọn nổi bật của Yamaha trong nhóm kèn & sáo. Alto Sax cho người mới, phím nhẹ, dễ ra âm. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.' WHERE product_id = N'19';
IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'19')
    INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES (N'19', N'Yamaha YAS-280 - Saxophone Alto', N'Yamaha YAS-280 - Saxophone Alto là lựa chọn nổi bật của Yamaha trong nhóm kèn & sáo. Alto Sax cho người mới, phím nhẹ, dễ ra âm. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 24900000, 18, NULL, N'yamaha', N'ken-sao');
UPDATE dbo.products SET product_name = N'Yamaha YTR-2330 - Trumpet', description = N'Yamaha YTR-2330 - Trumpet là lựa chọn nổi bật của Yamaha trong nhóm kèn & sáo. Trumpet Bb cho học viên, thân đồng thau mạ vàng. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.' WHERE product_id = N'20';
IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'20')
    INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES (N'20', N'Yamaha YTR-2330 - Trumpet', N'Yamaha YTR-2330 - Trumpet là lựa chọn nổi bật của Yamaha trong nhóm kèn & sáo. Trumpet Bb cho học viên, thân đồng thau mạ vàng. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 14500000, 25, NULL, N'yamaha', N'ken-sao');
UPDATE dbo.products SET product_name = N'Marshall MG15 - Ampli Guitar', description = N'Marshall MG15 - Ampli Guitar là lựa chọn nổi bật của Marshall trong nhóm phụ kiện. Ampli 15W, 2 kênh, âm thanh rock kinh điển. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.' WHERE product_id = N'21';
IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'21')
    INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES (N'21', N'Marshall MG15 - Ampli Guitar', N'Marshall MG15 - Ampli Guitar là lựa chọn nổi bật của Marshall trong nhóm phụ kiện. Ampli 15W, 2 kênh, âm thanh rock kinh điển. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 3190000, 32, NULL, N'marshall', N'phu-kien');
UPDATE dbo.products SET product_name = N'Boss TU-3 - Pedal Chỉnh Dây', description = N'Boss TU-3 - Pedal Chỉnh Dây là lựa chọn nổi bật của Boss trong nhóm phụ kiện. Tuner dạng pedal chính xác, màn hình sáng rõ trên sân khấu. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.' WHERE product_id = N'22';
IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'22')
    INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES (N'22', N'Boss TU-3 - Pedal Chỉnh Dây', N'Boss TU-3 - Pedal Chỉnh Dây là lựa chọn nổi bật của Boss trong nhóm phụ kiện. Tuner dạng pedal chính xác, màn hình sáng rõ trên sân khấu. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 2390000, 9, NULL, N'boss', N'phu-kien');
UPDATE dbo.products SET product_name = N'D''Addario EXL110 - Dây Guitar Điện', description = N'D''Addario EXL110 - Dây Guitar Điện là lựa chọn nổi bật của D''Addario trong nhóm phụ kiện. Bộ dây điện 10-46 chuẩn quốc tế, bền và sáng tiếng. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.' WHERE product_id = N'23';
IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'23')
    INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES (N'23', N'D''Addario EXL110 - Dây Guitar Điện', N'D''Addario EXL110 - Dây Guitar Điện là lựa chọn nổi bật của D''Addario trong nhóm phụ kiện. Bộ dây điện 10-46 chuẩn quốc tế, bền và sáng tiếng. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 190000, 16, NULL, N'd-addario', N'phu-kien');
UPDATE dbo.products SET product_name = N'Dunlop Tortex - Phím Gảy (12 cái)', description = N'Dunlop Tortex - Phím Gảy (12 cái) là lựa chọn nổi bật của Dunlop trong nhóm phụ kiện. Chất liệu Tortex bền, bám tay, nhiều độ dày. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.' WHERE product_id = N'24';
IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'24')
    INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES (N'24', N'Dunlop Tortex - Phím Gảy (12 cái)', N'Dunlop Tortex - Phím Gảy (12 cái) là lựa chọn nổi bật của Dunlop trong nhóm phụ kiện. Chất liệu Tortex bền, bám tay, nhiều độ dày. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 69000, 23, NULL, N'dunlop', N'phu-kien');
UPDATE dbo.products SET product_name = N'Gibson Les Paul Studio', description = N'Gibson Les Paul Studio là lựa chọn nổi bật của Gibson trong nhóm guitar. Biểu tượng rock với humbucker mạnh mẽ, sustain dài. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.' WHERE product_id = N'25';
IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'25')
    INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES (N'25', N'Gibson Les Paul Studio', N'Gibson Les Paul Studio là lựa chọn nổi bật của Gibson trong nhóm guitar. Biểu tượng rock với humbucker mạnh mẽ, sustain dài. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 52900000, 30, NULL, N'gibson', N'guitar');
UPDATE dbo.products SET product_name = N'Marshall Stanmore II - Loa Bluetooth', description = N'Marshall Stanmore II - Loa Bluetooth là lựa chọn nổi bật của Marshall trong nhóm phụ kiện. Loa Bluetooth thiết kế ampli cổ điển, âm thanh đa chiều. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.' WHERE product_id = N'26';
IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'26')
    INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES (N'26', N'Marshall Stanmore II - Loa Bluetooth', N'Marshall Stanmore II - Loa Bluetooth là lựa chọn nổi bật của Marshall trong nhóm phụ kiện. Loa Bluetooth thiết kế ampli cổ điển, âm thanh đa chiều. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 9990000, 7, NULL, N'marshall', N'phu-kien');
UPDATE dbo.products SET product_name = N'Boss Katana-50 MkII - Ampli', description = N'Boss Katana-50 MkII - Ampli là lựa chọn nổi bật của Boss trong nhóm phụ kiện. Ampli 50W với nhiều hiệu ứng tích hợp, kết nối USB thu âm. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.' WHERE product_id = N'27';
IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'27')
    INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES (N'27', N'Boss Katana-50 MkII - Ampli', N'Boss Katana-50 MkII - Ampli là lựa chọn nổi bật của Boss trong nhóm phụ kiện. Ampli 50W với nhiều hiệu ứng tích hợp, kết nối USB thu âm. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 7990000, 14, NULL, N'boss', N'phu-kien');
IF OBJECT_ID(N'dbo.site_content', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.site_content (content_key VARCHAR(100) NOT NULL PRIMARY KEY, json_data NVARCHAR(MAX) NOT NULL);
END;
IF EXISTS (SELECT 1 FROM dbo.site_content WHERE content_key = 'soundora')
    UPDATE dbo.site_content SET json_data = CAST(N'' AS NVARCHAR(MAX)) + N'{"site":{"name":"Soundora","freeShipFrom":500000,"shipFee":30000,"perPage":12},"categories":[{"key":"guitar","name":"Guitar","icon":"🎸"},{"key":"piano","name":"Piano","icon":"🎹"},{"key":"trong","name":"Trống","icon":"🥁"},{"key":"organ","name":"Organ","icon":"🎼"},{"key":"ukulele","name":"Ukulele","icon":"🪕"},{"key":"violin","name":"Violin","icon":"🎻"},{"key":"ken-sao","name":"Kèn & Sáo","icon":"🎷"},{"key":"phu-kien","name":"Phụ kiện","icon":"🎚️"}],"brands":[{"name":"Yamaha","origin":"Nhật Bản","desc":"Thương hiệu nhạc cụ hàng đầu thế giới, nổi tiếng với piano, guitar và organ."},{"name":"Fender","origin":"Mỹ","desc":"Cái nôi của Stratocaster và Telecaster huyền thoại."},{"name":"Ibanez","origin":"Nhật Bản","desc":"Chuyên guitar điện, bass với cần đàn mỏng, tốc độ cao."},{"name":"Taylor","origin":"Mỹ","desc":"Guitar acoustic cao cấp với âm thanh trong trẻo, tinh tế."},{"name":"Roland","origin":"Nhật Bản","desc":"Dẫn đầu về piano điện, trống điện và synthesizer."},{"name":"Cort","origin":"Hàn Quốc","desc":"Guitar chất lượng tốt, giá hợp lý cho người mới bắt đầu."},{"name":"Casio","origin":"Nhật Bản","desc":"Organ và piano điện gọn nhẹ, dễ sử dụng, giá dễ tiếp cận."},{"name":"Gibson","origin":"Mỹ","desc":"Les Paul, SG, ES - những cây đàn đã làm nên lịch sử rock."},{"name":"Marshall","origin":"Anh","desc":"Ampli và loa mang âm thanh rock đặc trưng."},{"name":"Boss","origin":"Nhật Bản","desc":"Pedal hiệu ứng và thiết bị guitar bền bỉ, chuyên nghiệp."},{"name":"Pearl","origin":"Nhật Bản","desc":"Bộ trống cơ chất lượng cao cho cả học viên lẫn nghệ sĩ."},{"name":"Kala","origin":"Mỹ","desc":"Ukulele thiết kế đẹp, âm vang ấm áp."},{"name":"Stentor","origin":"Anh","desc":"Violin học sinh và sinh viên được nhiều trường nhạc tin dùng."},{"name":"Dunlop","origin":"Mỹ","desc":"Phím gảy, capo và phụ kiện guitar nổi tiếng."},{"name":"D''Addario","origin":"Mỹ","desc":"Dây đàn chất lượng số 1 cho guitar, bass, violin."},{"name":"Zildjian","origin":"Mỹ","desc":"Thương hiệu cymbal lâu đời hơn 400 năm."}],"coupons":{"SOUNDORA10":{"type":"percent","value":10,"max":500000,"min":0,"desc":"Giảm 10% (tối đa 500.000đ)"},"FREESHIP":{"type":"ship","value":0,"max":0,"min":0,"desc":"Miễn phí vận chuyển"},"WELCOME50":{"type":"amount","value":50000,"max":0,"min":1000000,"desc":"Giảm 50.000đ cho đơn từ 1.000.000đ"}},"news":[{"id":1,"cat":"Hướng dẫn","emoji":"🎸","color":"#6C5CE7","color2":"#A29BFE","date":"22/09/2026","author":"Minh Hoàng","title":"Hướng dẫn chọn Guitar Acoustic cho người mới bắt đầu","excerpt":"Tìm hiểu các yếu tố quan trọng khi chọn mua cây đàn guitar acoustic đầu tiên: dáng đàn, loại gỗ mặt top, độ cao action và ngân sách phù hợp.","content":["Chọn cây guitar đầu tiên là bước quan trọng quyết định bạn có giữ được niềm hứng thú khi tập hay không. Một cây đàn quá cứng tay hoặc phô ra âm thanh khó nghe sẽ khiến người mới nhanh nản.","Hãy ưu tiên dáng đàn Dreadnought hoặc Concert tùy kích thước cơ thể, mặt top gỗ Spruce cho âm sáng, và action thấp (khoảng 2-3mm tại ph' +
        N'ím 12) để bấm dễ dàng. Ngân sách hợp lý cho người mới thường từ 2 đến 4 triệu đồng.","Cuối cùng, đừng quên kiểm tra phụ kiện đi kèm như bao đàn, dây dự phòng, phím gảy và cờ lê chỉnh cần. Tại Soundora, đội ngũ tư vấn luôn sẵn sàng giúp bạn thử đàn trực tiếp trước khi quyết định."]},{"id":2,"cat":"Review","emoji":"🎹","color":"#0984E3","color2":"#74B9FF","date":"15/09/2026","author":"Thanh Tâm","title":"Review Piano điện Yamaha P-125: Đáng tiền trong tầm giá?","excerpt":"Đánh giá chi tiết về cảm giác phím, chất lượng âm thanh, tính năng và độ bền của Yamaha P-125 sau hai tháng sử dụng thực tế.","content":["Yamaha P-125 là một trong những cây piano điện bán chạy nhất thế giới nhờ sự cân bằng giữa chất lượng và giá thành. Thân máy gọn, loa tích hợp đủ lớn để tập luyện ở phòng nhỏ.","Bàn phím GHS có độ nặng giảm dần từ trầm lên cao giống piano cơ, cho cảm giác chơi tự nhiên. Âm thanh được lấy mẫu từ grand piano concert nên khá đầy đặn, nhất là khi dùng tai nghe.","Điểm trừ nhỏ là số lượng âm sắc không nhiều và không có Bluetooth. Tuy vậy, nếu bạn cần một cây đàn học nghiêm túc và bền bỉ thì P-125 vẫn là lựa chọn rất đáng cân nhắc."]},{"id":3,"cat":"Tin tức","emoji":"🎵","color":"#00B894","color2":"#55EFC4","date":"10/09/2026","author":"Quốc Bảo","title":"Soundora khai trương cửa hàng mới tại Quận 7 với nhiều ưu đãi","excerpt":"Chào mừng cửa hàng thứ 5 trong hệ thống với không gian trải nghiệm nhạc cụ rộng hơn 300m2 và hàng loạt quà tặng cho khách hàng.","content":["Nhằm phục vụ khách hàng khu vực phía Nam thành phố, Soundora chính thức khai trương cửa hàng mới tại Quận 7 với không gian rộng hơn 300m2, chia thành các khu trải nghiệm guitar, piano, trống và phụ kiện.","Trong tuần khai trương, khách hàng sẽ được giảm đến 20% cho các dòng sản phẩm chọn lọc, tặng kèm bao đàn và khóa học thử miễn phí với giảng viên.","Cửa hàng mở cửa từ 8:00 đến 21:00 tất cả các ngày trong tuần. Hãy ghé thử đàn và cảm nhận âm thanh trực tiếp bạn nhé."]},{"id":4,"cat":"Mẹo hay","emoji":"🎼","color":"#FDCB6E","color2":"#FFEAA7","date":"05/09/2026","author":"Minh Hoàng","title":"7 mẹo bảo quản đàn guitar trong mùa mưa ẩm","excerpt":"Khí hậu nồm ẩm có thể làm cong cần đàn, nở mặt gỗ và gỉ dây. Cùng xem những cách đơn giản để giữ đàn luôn ở trạng thái tốt nhất.","content":["Độ ẩm cao là kẻ thù số một của nhạc cụ gỗ. Gỗ hút ẩm sẽ nở ra, làm thay đổi độ cong cần đàn, khiến action cao và gây ra tiếng rè.","Hãy cất đàn trong bao hoặc hộp cứng kèm gói hút ẩm silica gel, tránh để sát tường hoặc gần cửa sổ. Thường xuyên lau dây bằng khăn mềm sau khi chơi và thay dây định kỳ 2-3 tháng.","Nếu có điều kiện, hãy dùng máy hút ẩm hoặc điều hòa duy trì độ ẩm 40-55% trong phòng đặt đàn. Đây là cách bảo vệ cây đàn của bạn hiệu quả nhất."]},{"id":5,"cat":"Sự kiện","emoji":"🥁","color":"#FF6B6B","color2":"#FFA8A8","date":"28/08/2026","author":"Thanh Tâm","title":"Soundora Music Day 2026: Đêm nhạc cùng học viên và nghệ sĩ khách mời","excerpt":"Sự kiện thường niên quy tụ hơn 500 người yêu nhạc với các' +
        N' tiết mục biểu diễn, workshop và nhiều phần quà hấp dẫn.","content":["Soundora Music Day trở lại với chủ đề \"Play Your Passion\", nơi học viên, giảng viên và các nghệ sĩ khách mời cùng tham gia biểu diễn trên sân khấu ngoài trời.","Bên cạnh phần biểu diễn, chương trình còn có các workshop ngắn về kỹ thuật fingerstyle, nhập môn trống điện và cách thu âm tại nhà với chi phí thấp.","Vé tham dự miễn phí cho khách hàng đã mua sắm tại Soundora trong năm 2026. Đừng quên mang theo hóa đơn hoặc số điện thoại đã đăng ký khi tới sự kiện."]},{"id":6,"cat":"Hướng dẫn","emoji":"🎻","color":"#6C5CE7","color2":"#A29BFE","date":"20/08/2026","author":"Quốc Bảo","title":"Chọn Violin cho người mới: kích thước, chất liệu và phụ kiện","excerpt":"Violin có nhiều kích cỡ từ 1/16 đến 4/4. Chọn đúng kích thước giúp bạn tránh đau vai, đau cổ khi tập và tiến bộ nhanh hơn.","content":["Kích thước violin phụ thuộc vào chiều dài cánh tay của người chơi. Người lớn thường dùng size 4/4, trẻ em cần chọn size nhỏ hơn và thay đổi khi lớn lên.","Mặt đàn nên bằng gỗ Spruce, lưng và cần đàn bằng gỗ Maple để có âm sáng. Đừng quên bộ phụ kiện như vĩ, nhựa thông, kê vai và hộp đàn.","Bạn nên thử cầm đàn trước khi mua để cảm nhận độ vừa tay. Đội ngũ Soundora sẽ hỗ trợ đo kích thước và chỉnh dây miễn phí."]},{"id":7,"cat":"Review","emoji":"🔊","color":"#0984E3","color2":"#74B9FF","date":"12/08/2026","author":"Thanh Tâm","title":"Marshall MG15: Ampli nhỏ gọn cho người mới tập guitar điện","excerpt":"Với công suất 15W và 2 kênh âm thanh, MG15 là lựa chọn đáng giá để tập luyện tại nhà mà vẫn giữ được chất rock.","content":["Marshall MG15 nổi bật với thiết kế cổ điển và âm thanh đặc trưng của Marshall. Công suất 15W đủ để chơi trong phòng và tập cùng ban nhạc nhỏ.","Kênh Clean cho âm thanh sạch và sáng, kênh Overdrive đủ gắt để chơi rock/blues. Núm chỉnh EQ 3 band dễ dùng, cổng tai nghe thuận tiện khi tập đêm.","Tóm lại, đây là một chiếc ampli có giá hợp lý, bền bỉ và là bước khởi đầu tuyệt vời cho người mới chơi guitar điện."]},{"id":8,"cat":"Mẹo hay","emoji":"🎚️","color":"#FDCB6E","color2":"#FFEAA7","date":"02/08/2026","author":"Minh Hoàng","title":"Cách lên dây guitar chính xác chỉ trong 1 phút","excerpt":"Chỉ cần một chiếc tuner kẹp hoặc ứng dụng điện thoại, bạn đã có thể lên dây guitar chuẩn mà không cần tai nghe nhạc.","content":["Thứ tự dây guitar chuẩn từ dây 6 đến dây 1 là E - A - D - G - B - E. Bạn nên bắt đầu từ dây dày nhất và chỉnh từng dây một.","Với tuner kẹp, hãy gảy từng dây nhẹ nhàng và vặn khóa cho đến khi kim hoặc màn hình báo xanh. Nếu dây bị thấp, hãy vặn lên từ từ để tránh đứt.","Sau khi lên đủ 6 dây, hãy kiểm tra lại một lượt vì dây mới sẽ giãn và bị lệch. Thói quen này sẽ giúp tai nghe của bạn nhạy hơn qua thời gian."]}],"infoPages":{"about":{"title":"Giới thiệu về Soundora","icon":"fa-music","body":["Soundora là hệ thống cửa hàng nhạc cụ chính hãng với hơn 10 năm kinh nghiệm, phục vụ hàng trăm nghìn khách hàng trên toàn quốc.","Chúng tôi phân phối nhạc cụ và phụ kiện t' +
        N'ừ các thương hiệu hàng đầu thế giới như Yamaha, Fender, Roland, Taylor, Casio... với cam kết 100% chính hãng.","Sứ mệnh của Soundora là đưa âm nhạc đến gần hơn với mọi người, từ người mới bắt đầu cho đến nghệ sĩ chuyên nghiệp, thông qua sản phẩm chất lượng và dịch vụ tận tâm."]},"store":{"title":"Hệ thống cửa hàng","icon":"fa-map-marker-alt","body":["Trụ sở chính: 123 Nguyễn Huệ, Quận 1, TP. Hồ Chí Minh.","Chi nhánh Quận 7: 45 Nguyễn Thị Thập, Quận 7, TP. Hồ Chí Minh.","Chi nhánh Hà Nội: 88 Tây Sơn, Đống Đa, Hà Nội.","Giờ mở cửa: 8:00 - 21:00 tất cả các ngày trong tuần. Hotline: 1900 9999."]},"careers":{"title":"Tuyển dụng","icon":"fa-briefcase","body":["Soundora luôn chào đón những người yêu âm nhạc và có tinh thần phục vụ khách hàng.","Các vị trí đang tuyển: Nhân viên tư vấn bán hàng, Kỹ thuật viên nhạc cụ, Chuyên viên marketing, Nhân viên kho vận.","Vui lòng gửi CV về email tuyendung@soundora.vn hoặc liên hệ hotline 1900 9999 để biết thêm chi tiết."]},"warranty":{"title":"Chính sách bảo hành","icon":"fa-shield-alt","body":["Tất cả sản phẩm tại Soundora đều được bảo hành chính hãng từ 6 đến 12 tháng tùy loại.","Bảo hành áp dụng cho lỗi do nhà sản xuất. Không bảo hành đối với hư hỏng do va đập, vào nước, tự ý sửa chữa hoặc sử dụng sai cách.","Để được bảo hành, vui lòng mang sản phẩm và hóa đơn đến cửa hàng hoặc liên hệ hotline để được hướng dẫn gửi sản phẩm."]},"return":{"title":"Chính sách đổi trả","icon":"fa-undo-alt","body":["Đổi trả miễn phí trong vòng 30 ngày nếu sản phẩm lỗi do nhà sản xuất.","Sản phẩm đổi trả cần còn nguyên tem, hộp và đầy đủ phụ kiện đi kèm.","Hoàn tiền trong vòng 3-7 ngày làm việc sau khi chúng tôi nhận và kiểm tra sản phẩm."]},"guide":{"title":"Hướng dẫn mua hàng","icon":"fa-shopping-cart","body":["Bước 1: Tìm sản phẩm bằng thanh tìm kiếm hoặc duyệt theo danh mục.","Bước 2: Nhấn \"Thêm vào giỏ\" và kiểm tra lại giỏ hàng của bạn.","Bước 3: Nhập mã giảm giá (nếu có), chọn \"Thanh toán\" và điền thông tin nhận hàng.","Bước 4: Chọn phương thức thanh toán và xác nhận đơn hàng. Chúng tôi sẽ liên hệ xác nhận trong thời gian sớm nhất."]},"faq":{"title":"Câu hỏi thường gặp","icon":"fa-question-circle","body":["Hỏi: Sản phẩm có chính hãng không? Đáp: 100% sản phẩm chính hãng, có đầy đủ tem và giấy tờ.","Hỏi: Thời gian giao hàng bao lâu? Đáp: Nội thành 1-2 ngày, toàn quốc 2-5 ngày.","Hỏi: Tôi có thể thử đàn trước khi mua không? Đáp: Có, bạn có thể đến cửa hàng để thử trực tiếp.","Hỏi: Có hỗ trợ trả góp không? Đáp: Có, hỗ trợ trả góp 0% qua thẻ tín dụng cho đơn từ 3.000.000đ."]}},"categoryInfo":{"guitar":{"features":["Thiết kế chuẩn form, dễ chơi cho cả người mới lẫn người đã có kinh nghiệm","Cần đàn được làm phẳng, action thấp, hạn chế đau ngón tay"],"specs":[["Loại nhạc cụ","Guitar"],["Số phím","20 - 22 phím"],["Dây đàn","6 dây tiêu chuẩn"],["Cần đàn","Gỗ tuyển chọn, xử lý chống cong vênh"],["Phụ kiện kèm theo","Phím gảy, dây dự phòng, cờ lê chỉnh cần"]]},"piano":{"features":["Phím bấm cảm ứng lực mô phỏng đàn piano cơ","Âm than' +
        N'h sampling chân thực, hỗ trợ cổng tai nghe và kết nối USB"],"specs":[["Loại nhạc cụ","Piano điện"],["Số phím","88 phím"],["Độ đa âm","64 - 256 notes"],["Kết nối","USB, tai nghe, Pedal"],["Phụ kiện kèm theo","Adapter nguồn, pedal, giá nhạc"]]},"trong":{"features":["Cấu hình đầy đủ, sẵn sàng chơi ngay sau khi lắp đặt","Chất liệu bền bỉ, âm thanh vang và rõ"],"specs":[["Loại nhạc cụ","Trống / Cymbal"],["Chất liệu","Gỗ Poplar / Birch / Mesh"],["Cấu hình","Theo bộ tiêu chuẩn của hãng"],["Màu sắc","Theo từng phiên bản"],["Phụ kiện kèm theo","Dùi trống, ghế, chân pedal"]]},"organ":{"features":["Hàng trăm âm sắc và điệu nhạc có sẵn","Phù hợp học tập, luyện tập tại nhà và biểu diễn nhỏ"],"specs":[["Loại nhạc cụ","Organ điện tử"],["Số phím","61 phím"],["Âm sắc","400+ âm sắc"],["Điệu nhạc","100+ điệu nhạc"],["Phụ kiện kèm theo","Adapter, giá nhạc"]]},"ukulele":{"features":["Kích thước nhỏ gọn, dễ mang theo khi đi dã ngoại","Âm thanh vui tươi, dễ học cho người mới"],"specs":[["Loại nhạc cụ","Ukulele"],["Kích thước","Soprano / Concert"],["Dây đàn","Nylon 4 dây"],["Mặt đàn","Gỗ tuyển chọn"],["Phụ kiện kèm theo","Bao đàn, dây dự phòng"]]},"violin":{"features":["Gỗ được sấy kỹ, âm thanh sáng và đầy đặn","Bộ phụ kiện đầy đủ, sẵn sàng tập ngay"],"specs":[["Loại nhạc cụ","Violin"],["Kích thước","4/4"],["Mặt đàn","Gỗ vân sam (Spruce)"],["Lưng và cần","Gỗ thích (Maple)"],["Phụ kiện kèm theo","Hộp đàn, vĩ, nhựa thông"]]},"ken-sao":{"features":["Chất liệu bền, dễ vệ sinh và bảo quản","Phù hợp cho học sinh, sinh viên và người chơi nghiệp dư"],"specs":[["Loại nhạc cụ","Kèn / Sáo"],["Chất liệu","Nhựa ABS / Đồng thau"],["Âm vực","Theo chuẩn của hãng"],["Bảo hành","12 tháng"],["Phụ kiện kèm theo","Hộp đựng, dầu bôi trơn, khăn lau"]]},"phu-kien":{"features":["Phụ kiện chính hãng, độ bền cao","Tương thích với đa số nhạc cụ cùng loại"],"specs":[["Loại sản phẩm","Phụ kiện / Thiết bị âm thanh"],["Xuất xứ","Theo hãng sản xuất"],["Tình trạng","Mới 100%, nguyên seal"],["Bảo hành","6 - 12 tháng"],["Đóng gói","Hộp chính hãng"]]}},"tiers":[{"key":"thuong","name":"Thường","min":0,"discount":0,"color":"#8E8E93"},{"key":"thanh-vien","name":"Thành viên","min":5000000,"discount":2,"color":"#0984E3"},{"key":"vip","name":"VIP","min":20000000,"discount":5,"color":"#E1A200"}],"productPresentation":[{"id":1,"sku":"YAM-1001","emoji":"🎸","old":3990000,"rating":4.9,"reviews":128,"badge":"hot","sold":980,"isNew":false,"short":"Mặt đàn gỗ vân sam cho âm sáng, ngân vang chuẩn xác, bán chạy số 1 cho người mới."},{"id":2,"sku":"FEN-1002","emoji":"🎸","old":null,"rating":4.8,"reviews":64,"badge":null,"sold":210,"isNew":false,"short":"Huyền thoại Stratocaster với 3 single-coil cho âm thanh trong và cá tính."},{"id":3,"sku":"IBA-1003","emoji":"🎸","old":6990000,"rating":4.7,"reviews":52,"badge":"sale","sold":340,"isNew":false,"short":"Cần đàn mỏng, chơi solo nhanh, phù hợp rock và metal."},{"id":4,"sku":"TAY-1004","emoji":"🎸","old":null,"rating":4.9,"reviews":31,"badge":null,"sold":95,"isNew":false,"short":' +
        N'"Dáng đàn nhỏ gọn nhưng âm lượng lớn, thích hợp du lịch và thu âm."},{"id":5,"sku":"COR-1005","emoji":"🎸","old":2790000,"rating":4.6,"reviews":210,"badge":"hot","sold":1250,"isNew":false,"short":"Mức giá dễ tiếp cận nhưng chất lượng ổn định, âm thanh ấm áp."},{"id":6,"sku":"YAM-1006","emoji":"🎹","old":19900000,"rating":4.9,"reviews":96,"badge":"sale","sold":410,"isNew":false,"short":"Phím GHS cảm ứng lực, âm thanh sampling từ đàn grand piano CFIIIS."},{"id":7,"sku":"ROL-1007","emoji":"🎹","old":null,"rating":4.8,"reviews":47,"badge":null,"sold":180,"isNew":false,"short":"Động cơ âm thanh SuperNATURAL, kết nối Bluetooth MIDI."},{"id":8,"sku":"CAS-1008","emoji":"🎹","old":3990000,"rating":4.5,"reviews":143,"badge":null,"sold":760,"isNew":false,"short":"Gọn nhẹ, nhiều điệu nhạc, phù hợp tập luyện tại nhà."},{"id":9,"sku":"CAS-1009","emoji":"🎹","old":10990000,"rating":4.7,"reviews":58,"badge":"new","sold":120,"isNew":true,"short":"Thiết kế siêu mỏng, bàn phím phản hồi lực Smart Scaled."},{"id":10,"sku":"YAM-1010","emoji":"🎹","old":6790000,"rating":4.8,"reviews":175,"badge":"hot","sold":890,"isNew":false,"short":"622 âm sắc, 205 điệu nhạc, có chế độ học nhạc thông minh."},{"id":11,"sku":"ROL-1011","emoji":"🥁","old":17900000,"rating":4.7,"reviews":29,"badge":null,"sold":75,"isNew":false,"short":"Tập trống yên tĩnh trong căn hộ, mặt mesh cho cảm giác thật."},{"id":12,"sku":"PEA-1012","emoji":"🥁","old":14500000,"rating":4.6,"reviews":22,"badge":"sale","sold":60,"isNew":false,"short":"Bộ trống 5 món đầy đủ cymbal, hardware và ghế ngồi."},{"id":13,"sku":"ZIL-1013","emoji":"🥁","old":8900000,"rating":4.7,"reviews":25,"badge":null,"sold":48,"isNew":false,"short":"Bộ cymbal 3 món cho âm thanh sáng, rõ và bền."},{"id":14,"sku":"KAL-1014","emoji":"🪕","old":1590000,"rating":4.8,"reviews":301,"badge":"hot","sold":1600,"isNew":false,"short":"Gỗ Mahogany, âm ấm, đẹp mắt, bán chạy nhất dòng ukulele."},{"id":15,"sku":"COR-1015","emoji":"🪕","old":null,"rating":4.5,"reviews":89,"badge":null,"sold":430,"isNew":false,"short":"Size Concert dễ bấm hợp tay người lớn, giá tốt."},{"id":16,"sku":"YAM-1016","emoji":"🎻","old":7590000,"rating":4.8,"reviews":37,"badge":"new","sold":70,"isNew":true,"short":"Violin học viên cao cấp, thùng đàn gỗ thích cho âm sáng."},{"id":17,"sku":"STE-1017","emoji":"🎻","old":3790000,"rating":4.6,"reviews":66,"badge":null,"sold":210,"isNew":false,"short":"Chất lượng ổn định, được nhiều trường nhạc lựa chọn."},{"id":18,"sku":"YAM-1018","emoji":"🪈","old":250000,"rating":4.9,"reviews":520,"badge":"hot","sold":3800,"isNew":false,"short":"Sáo recorder chuẩn trường học, âm chuẩn, dễ thổi."},{"id":19,"sku":"YAM-1019","emoji":"🎷","old":27900000,"rating":4.8,"reviews":14,"badge":null,"sold":22,"isNew":false,"short":"Alto Sax cho người mới, phím nhẹ, dễ ra âm."},{"id":20,"sku":"YAM-1020","emoji":"🎺","old":null,"rating":4.7,"reviews":12,"badge":"new","sold":18,"isNew":true,"short":"Trumpet Bb cho học viên, thân đồng thau mạ vàng."},{"id":21,"sku":"MAR-1021","emoj' +
        N'i":"🔊","old":3590000,"rating":4.7,"reviews":88,"badge":"sale","sold":520,"isNew":false,"short":"Ampli 15W, 2 kênh, âm thanh rock kinh điển."},{"id":22,"sku":"BOS-1022","emoji":"🎚️","old":2690000,"rating":4.8,"reviews":120,"badge":null,"sold":640,"isNew":false,"short":"Tuner dạng pedal chính xác, màn hình sáng rõ trên sân khấu."},{"id":23,"sku":"D''A-1023","emoji":"🎼","old":250000,"rating":4.9,"reviews":860,"badge":"hot","sold":5400,"isNew":false,"short":"Bộ dây điện 10-46 chuẩn quốc tế, bền và sáng tiếng."},{"id":24,"sku":"DUN-1024","emoji":"🎵","old":90000,"rating":4.9,"reviews":1020,"badge":null,"sold":6100,"isNew":false,"short":"Chất liệu Tortex bền, bám tay, nhiều độ dày."},{"id":25,"sku":"GIB-1025","emoji":"🎸","old":null,"rating":4.9,"reviews":9,"badge":"new","sold":6,"isNew":true,"short":"Biểu tượng rock với humbucker mạnh mẽ, sustain dài."},{"id":26,"sku":"MAR-1026","emoji":"🔊","old":10990000,"rating":4.7,"reviews":41,"badge":null,"sold":130,"isNew":false,"short":"Loa Bluetooth thiết kế ampli cổ điển, âm thanh đa chiều."},{"id":27,"sku":"BOS-1027","emoji":"🔊","old":8990000,"rating":4.8,"reviews":55,"badge":"sale","sold":160,"isNew":false,"short":"Ampli 50W với nhiều hiệu ứng tích hợp, kết nối USB thu âm."}]}' WHERE content_key = 'soundora';
ELSE
    INSERT INTO dbo.site_content (content_key, json_data) VALUES ('soundora', CAST(N'' AS NVARCHAR(MAX)) + N'{"site":{"name":"Soundora","freeShipFrom":500000,"shipFee":30000,"perPage":12},"categories":[{"key":"guitar","name":"Guitar","icon":"🎸"},{"key":"piano","name":"Piano","icon":"🎹"},{"key":"trong","name":"Trống","icon":"🥁"},{"key":"organ","name":"Organ","icon":"🎼"},{"key":"ukulele","name":"Ukulele","icon":"🪕"},{"key":"violin","name":"Violin","icon":"🎻"},{"key":"ken-sao","name":"Kèn & Sáo","icon":"🎷"},{"key":"phu-kien","name":"Phụ kiện","icon":"🎚️"}],"brands":[{"name":"Yamaha","origin":"Nhật Bản","desc":"Thương hiệu nhạc cụ hàng đầu thế giới, nổi tiếng với piano, guitar và organ."},{"name":"Fender","origin":"Mỹ","desc":"Cái nôi của Stratocaster và Telecaster huyền thoại."},{"name":"Ibanez","origin":"Nhật Bản","desc":"Chuyên guitar điện, bass với cần đàn mỏng, tốc độ cao."},{"name":"Taylor","origin":"Mỹ","desc":"Guitar acoustic cao cấp với âm thanh trong trẻo, tinh tế."},{"name":"Roland","origin":"Nhật Bản","desc":"Dẫn đầu về piano điện, trống điện và synthesizer."},{"name":"Cort","origin":"Hàn Quốc","desc":"Guitar chất lượng tốt, giá hợp lý cho người mới bắt đầu."},{"name":"Casio","origin":"Nhật Bản","desc":"Organ và piano điện gọn nhẹ, dễ sử dụng, giá dễ tiếp cận."},{"name":"Gibson","origin":"Mỹ","desc":"Les Paul, SG, ES - những cây đàn đã làm nên lịch sử rock."},{"name":"Marshall","origin":"Anh","desc":"Ampli và loa mang âm thanh rock đặc trưng."},{"name":"Boss","origin":"Nhật Bản","desc":"Pedal hiệu ứng và thiết bị guitar bền bỉ, chuyên nghiệp."},{"name":"Pearl","origin":"Nhật Bản","desc":"Bộ trống cơ chất lượng cao cho cả học viên lẫn nghệ sĩ."},{"name":"Kala","origin":"Mỹ","desc":"Ukulele thiết kế đẹp, âm vang ấm áp."},{"name":"Stentor","origin":"Anh","desc":"Violin học sinh và sinh viên được nhiều trường nhạc tin dùng."},{"name":"Dunlop","origin":"Mỹ","desc":"Phím gảy, capo và phụ kiện guitar nổi tiếng."},{"name":"D''Addario","origin":"Mỹ","desc":"Dây đàn chất lượng số 1 cho guitar, bass, violin."},{"name":"Zildjian","origin":"Mỹ","desc":"Thương hiệu cymbal lâu đời hơn 400 năm."}],"coupons":{"SOUNDORA10":{"type":"percent","value":10,"max":500000,"min":0,"desc":"Giảm 10% (tối đa 500.000đ)"},"FREESHIP":{"type":"ship","value":0,"max":0,"min":0,"desc":"Miễn phí vận chuyển"},"WELCOME50":{"type":"amount","value":50000,"max":0,"min":1000000,"desc":"Giảm 50.000đ cho đơn từ 1.000.000đ"}},"news":[{"id":1,"cat":"Hướng dẫn","emoji":"🎸","color":"#6C5CE7","color2":"#A29BFE","date":"22/09/2026","author":"Minh Hoàng","title":"Hướng dẫn chọn Guitar Acoustic cho người mới bắt đầu","excerpt":"Tìm hiểu các yếu tố quan trọng khi chọn mua cây đàn guitar acoustic đầu tiên: dáng đàn, loại gỗ mặt top, độ cao action và ngân sách phù hợp.","content":["Chọn cây guitar đầu tiên là bước quan trọng quyết định bạn có giữ được niềm hứng thú khi tập hay không. Một cây đàn quá cứng tay hoặc phô ra âm thanh khó nghe sẽ khiến người mới nhanh nản.","Hãy ưu tiên dáng đàn Dreadnought hoặc Concert tùy kích thước cơ thể, mặt top gỗ Spruce cho âm sáng, và action thấp (khoảng 2-3mm tại ph' +
        N'ím 12) để bấm dễ dàng. Ngân sách hợp lý cho người mới thường từ 2 đến 4 triệu đồng.","Cuối cùng, đừng quên kiểm tra phụ kiện đi kèm như bao đàn, dây dự phòng, phím gảy và cờ lê chỉnh cần. Tại Soundora, đội ngũ tư vấn luôn sẵn sàng giúp bạn thử đàn trực tiếp trước khi quyết định."]},{"id":2,"cat":"Review","emoji":"🎹","color":"#0984E3","color2":"#74B9FF","date":"15/09/2026","author":"Thanh Tâm","title":"Review Piano điện Yamaha P-125: Đáng tiền trong tầm giá?","excerpt":"Đánh giá chi tiết về cảm giác phím, chất lượng âm thanh, tính năng và độ bền của Yamaha P-125 sau hai tháng sử dụng thực tế.","content":["Yamaha P-125 là một trong những cây piano điện bán chạy nhất thế giới nhờ sự cân bằng giữa chất lượng và giá thành. Thân máy gọn, loa tích hợp đủ lớn để tập luyện ở phòng nhỏ.","Bàn phím GHS có độ nặng giảm dần từ trầm lên cao giống piano cơ, cho cảm giác chơi tự nhiên. Âm thanh được lấy mẫu từ grand piano concert nên khá đầy đặn, nhất là khi dùng tai nghe.","Điểm trừ nhỏ là số lượng âm sắc không nhiều và không có Bluetooth. Tuy vậy, nếu bạn cần một cây đàn học nghiêm túc và bền bỉ thì P-125 vẫn là lựa chọn rất đáng cân nhắc."]},{"id":3,"cat":"Tin tức","emoji":"🎵","color":"#00B894","color2":"#55EFC4","date":"10/09/2026","author":"Quốc Bảo","title":"Soundora khai trương cửa hàng mới tại Quận 7 với nhiều ưu đãi","excerpt":"Chào mừng cửa hàng thứ 5 trong hệ thống với không gian trải nghiệm nhạc cụ rộng hơn 300m2 và hàng loạt quà tặng cho khách hàng.","content":["Nhằm phục vụ khách hàng khu vực phía Nam thành phố, Soundora chính thức khai trương cửa hàng mới tại Quận 7 với không gian rộng hơn 300m2, chia thành các khu trải nghiệm guitar, piano, trống và phụ kiện.","Trong tuần khai trương, khách hàng sẽ được giảm đến 20% cho các dòng sản phẩm chọn lọc, tặng kèm bao đàn và khóa học thử miễn phí với giảng viên.","Cửa hàng mở cửa từ 8:00 đến 21:00 tất cả các ngày trong tuần. Hãy ghé thử đàn và cảm nhận âm thanh trực tiếp bạn nhé."]},{"id":4,"cat":"Mẹo hay","emoji":"🎼","color":"#FDCB6E","color2":"#FFEAA7","date":"05/09/2026","author":"Minh Hoàng","title":"7 mẹo bảo quản đàn guitar trong mùa mưa ẩm","excerpt":"Khí hậu nồm ẩm có thể làm cong cần đàn, nở mặt gỗ và gỉ dây. Cùng xem những cách đơn giản để giữ đàn luôn ở trạng thái tốt nhất.","content":["Độ ẩm cao là kẻ thù số một của nhạc cụ gỗ. Gỗ hút ẩm sẽ nở ra, làm thay đổi độ cong cần đàn, khiến action cao và gây ra tiếng rè.","Hãy cất đàn trong bao hoặc hộp cứng kèm gói hút ẩm silica gel, tránh để sát tường hoặc gần cửa sổ. Thường xuyên lau dây bằng khăn mềm sau khi chơi và thay dây định kỳ 2-3 tháng.","Nếu có điều kiện, hãy dùng máy hút ẩm hoặc điều hòa duy trì độ ẩm 40-55% trong phòng đặt đàn. Đây là cách bảo vệ cây đàn của bạn hiệu quả nhất."]},{"id":5,"cat":"Sự kiện","emoji":"🥁","color":"#FF6B6B","color2":"#FFA8A8","date":"28/08/2026","author":"Thanh Tâm","title":"Soundora Music Day 2026: Đêm nhạc cùng học viên và nghệ sĩ khách mời","excerpt":"Sự kiện thường niên quy tụ hơn 500 người yêu nhạc với các' +
        N' tiết mục biểu diễn, workshop và nhiều phần quà hấp dẫn.","content":["Soundora Music Day trở lại với chủ đề \"Play Your Passion\", nơi học viên, giảng viên và các nghệ sĩ khách mời cùng tham gia biểu diễn trên sân khấu ngoài trời.","Bên cạnh phần biểu diễn, chương trình còn có các workshop ngắn về kỹ thuật fingerstyle, nhập môn trống điện và cách thu âm tại nhà với chi phí thấp.","Vé tham dự miễn phí cho khách hàng đã mua sắm tại Soundora trong năm 2026. Đừng quên mang theo hóa đơn hoặc số điện thoại đã đăng ký khi tới sự kiện."]},{"id":6,"cat":"Hướng dẫn","emoji":"🎻","color":"#6C5CE7","color2":"#A29BFE","date":"20/08/2026","author":"Quốc Bảo","title":"Chọn Violin cho người mới: kích thước, chất liệu và phụ kiện","excerpt":"Violin có nhiều kích cỡ từ 1/16 đến 4/4. Chọn đúng kích thước giúp bạn tránh đau vai, đau cổ khi tập và tiến bộ nhanh hơn.","content":["Kích thước violin phụ thuộc vào chiều dài cánh tay của người chơi. Người lớn thường dùng size 4/4, trẻ em cần chọn size nhỏ hơn và thay đổi khi lớn lên.","Mặt đàn nên bằng gỗ Spruce, lưng và cần đàn bằng gỗ Maple để có âm sáng. Đừng quên bộ phụ kiện như vĩ, nhựa thông, kê vai và hộp đàn.","Bạn nên thử cầm đàn trước khi mua để cảm nhận độ vừa tay. Đội ngũ Soundora sẽ hỗ trợ đo kích thước và chỉnh dây miễn phí."]},{"id":7,"cat":"Review","emoji":"🔊","color":"#0984E3","color2":"#74B9FF","date":"12/08/2026","author":"Thanh Tâm","title":"Marshall MG15: Ampli nhỏ gọn cho người mới tập guitar điện","excerpt":"Với công suất 15W và 2 kênh âm thanh, MG15 là lựa chọn đáng giá để tập luyện tại nhà mà vẫn giữ được chất rock.","content":["Marshall MG15 nổi bật với thiết kế cổ điển và âm thanh đặc trưng của Marshall. Công suất 15W đủ để chơi trong phòng và tập cùng ban nhạc nhỏ.","Kênh Clean cho âm thanh sạch và sáng, kênh Overdrive đủ gắt để chơi rock/blues. Núm chỉnh EQ 3 band dễ dùng, cổng tai nghe thuận tiện khi tập đêm.","Tóm lại, đây là một chiếc ampli có giá hợp lý, bền bỉ và là bước khởi đầu tuyệt vời cho người mới chơi guitar điện."]},{"id":8,"cat":"Mẹo hay","emoji":"🎚️","color":"#FDCB6E","color2":"#FFEAA7","date":"02/08/2026","author":"Minh Hoàng","title":"Cách lên dây guitar chính xác chỉ trong 1 phút","excerpt":"Chỉ cần một chiếc tuner kẹp hoặc ứng dụng điện thoại, bạn đã có thể lên dây guitar chuẩn mà không cần tai nghe nhạc.","content":["Thứ tự dây guitar chuẩn từ dây 6 đến dây 1 là E - A - D - G - B - E. Bạn nên bắt đầu từ dây dày nhất và chỉnh từng dây một.","Với tuner kẹp, hãy gảy từng dây nhẹ nhàng và vặn khóa cho đến khi kim hoặc màn hình báo xanh. Nếu dây bị thấp, hãy vặn lên từ từ để tránh đứt.","Sau khi lên đủ 6 dây, hãy kiểm tra lại một lượt vì dây mới sẽ giãn và bị lệch. Thói quen này sẽ giúp tai nghe của bạn nhạy hơn qua thời gian."]}],"infoPages":{"about":{"title":"Giới thiệu về Soundora","icon":"fa-music","body":["Soundora là hệ thống cửa hàng nhạc cụ chính hãng với hơn 10 năm kinh nghiệm, phục vụ hàng trăm nghìn khách hàng trên toàn quốc.","Chúng tôi phân phối nhạc cụ và phụ kiện t' +
        N'ừ các thương hiệu hàng đầu thế giới như Yamaha, Fender, Roland, Taylor, Casio... với cam kết 100% chính hãng.","Sứ mệnh của Soundora là đưa âm nhạc đến gần hơn với mọi người, từ người mới bắt đầu cho đến nghệ sĩ chuyên nghiệp, thông qua sản phẩm chất lượng và dịch vụ tận tâm."]},"store":{"title":"Hệ thống cửa hàng","icon":"fa-map-marker-alt","body":["Trụ sở chính: 123 Nguyễn Huệ, Quận 1, TP. Hồ Chí Minh.","Chi nhánh Quận 7: 45 Nguyễn Thị Thập, Quận 7, TP. Hồ Chí Minh.","Chi nhánh Hà Nội: 88 Tây Sơn, Đống Đa, Hà Nội.","Giờ mở cửa: 8:00 - 21:00 tất cả các ngày trong tuần. Hotline: 1900 9999."]},"careers":{"title":"Tuyển dụng","icon":"fa-briefcase","body":["Soundora luôn chào đón những người yêu âm nhạc và có tinh thần phục vụ khách hàng.","Các vị trí đang tuyển: Nhân viên tư vấn bán hàng, Kỹ thuật viên nhạc cụ, Chuyên viên marketing, Nhân viên kho vận.","Vui lòng gửi CV về email tuyendung@soundora.vn hoặc liên hệ hotline 1900 9999 để biết thêm chi tiết."]},"warranty":{"title":"Chính sách bảo hành","icon":"fa-shield-alt","body":["Tất cả sản phẩm tại Soundora đều được bảo hành chính hãng từ 6 đến 12 tháng tùy loại.","Bảo hành áp dụng cho lỗi do nhà sản xuất. Không bảo hành đối với hư hỏng do va đập, vào nước, tự ý sửa chữa hoặc sử dụng sai cách.","Để được bảo hành, vui lòng mang sản phẩm và hóa đơn đến cửa hàng hoặc liên hệ hotline để được hướng dẫn gửi sản phẩm."]},"return":{"title":"Chính sách đổi trả","icon":"fa-undo-alt","body":["Đổi trả miễn phí trong vòng 30 ngày nếu sản phẩm lỗi do nhà sản xuất.","Sản phẩm đổi trả cần còn nguyên tem, hộp và đầy đủ phụ kiện đi kèm.","Hoàn tiền trong vòng 3-7 ngày làm việc sau khi chúng tôi nhận và kiểm tra sản phẩm."]},"guide":{"title":"Hướng dẫn mua hàng","icon":"fa-shopping-cart","body":["Bước 1: Tìm sản phẩm bằng thanh tìm kiếm hoặc duyệt theo danh mục.","Bước 2: Nhấn \"Thêm vào giỏ\" và kiểm tra lại giỏ hàng của bạn.","Bước 3: Nhập mã giảm giá (nếu có), chọn \"Thanh toán\" và điền thông tin nhận hàng.","Bước 4: Chọn phương thức thanh toán và xác nhận đơn hàng. Chúng tôi sẽ liên hệ xác nhận trong thời gian sớm nhất."]},"faq":{"title":"Câu hỏi thường gặp","icon":"fa-question-circle","body":["Hỏi: Sản phẩm có chính hãng không? Đáp: 100% sản phẩm chính hãng, có đầy đủ tem và giấy tờ.","Hỏi: Thời gian giao hàng bao lâu? Đáp: Nội thành 1-2 ngày, toàn quốc 2-5 ngày.","Hỏi: Tôi có thể thử đàn trước khi mua không? Đáp: Có, bạn có thể đến cửa hàng để thử trực tiếp.","Hỏi: Có hỗ trợ trả góp không? Đáp: Có, hỗ trợ trả góp 0% qua thẻ tín dụng cho đơn từ 3.000.000đ."]}},"categoryInfo":{"guitar":{"features":["Thiết kế chuẩn form, dễ chơi cho cả người mới lẫn người đã có kinh nghiệm","Cần đàn được làm phẳng, action thấp, hạn chế đau ngón tay"],"specs":[["Loại nhạc cụ","Guitar"],["Số phím","20 - 22 phím"],["Dây đàn","6 dây tiêu chuẩn"],["Cần đàn","Gỗ tuyển chọn, xử lý chống cong vênh"],["Phụ kiện kèm theo","Phím gảy, dây dự phòng, cờ lê chỉnh cần"]]},"piano":{"features":["Phím bấm cảm ứng lực mô phỏng đàn piano cơ","Âm than' +
        N'h sampling chân thực, hỗ trợ cổng tai nghe và kết nối USB"],"specs":[["Loại nhạc cụ","Piano điện"],["Số phím","88 phím"],["Độ đa âm","64 - 256 notes"],["Kết nối","USB, tai nghe, Pedal"],["Phụ kiện kèm theo","Adapter nguồn, pedal, giá nhạc"]]},"trong":{"features":["Cấu hình đầy đủ, sẵn sàng chơi ngay sau khi lắp đặt","Chất liệu bền bỉ, âm thanh vang và rõ"],"specs":[["Loại nhạc cụ","Trống / Cymbal"],["Chất liệu","Gỗ Poplar / Birch / Mesh"],["Cấu hình","Theo bộ tiêu chuẩn của hãng"],["Màu sắc","Theo từng phiên bản"],["Phụ kiện kèm theo","Dùi trống, ghế, chân pedal"]]},"organ":{"features":["Hàng trăm âm sắc và điệu nhạc có sẵn","Phù hợp học tập, luyện tập tại nhà và biểu diễn nhỏ"],"specs":[["Loại nhạc cụ","Organ điện tử"],["Số phím","61 phím"],["Âm sắc","400+ âm sắc"],["Điệu nhạc","100+ điệu nhạc"],["Phụ kiện kèm theo","Adapter, giá nhạc"]]},"ukulele":{"features":["Kích thước nhỏ gọn, dễ mang theo khi đi dã ngoại","Âm thanh vui tươi, dễ học cho người mới"],"specs":[["Loại nhạc cụ","Ukulele"],["Kích thước","Soprano / Concert"],["Dây đàn","Nylon 4 dây"],["Mặt đàn","Gỗ tuyển chọn"],["Phụ kiện kèm theo","Bao đàn, dây dự phòng"]]},"violin":{"features":["Gỗ được sấy kỹ, âm thanh sáng và đầy đặn","Bộ phụ kiện đầy đủ, sẵn sàng tập ngay"],"specs":[["Loại nhạc cụ","Violin"],["Kích thước","4/4"],["Mặt đàn","Gỗ vân sam (Spruce)"],["Lưng và cần","Gỗ thích (Maple)"],["Phụ kiện kèm theo","Hộp đàn, vĩ, nhựa thông"]]},"ken-sao":{"features":["Chất liệu bền, dễ vệ sinh và bảo quản","Phù hợp cho học sinh, sinh viên và người chơi nghiệp dư"],"specs":[["Loại nhạc cụ","Kèn / Sáo"],["Chất liệu","Nhựa ABS / Đồng thau"],["Âm vực","Theo chuẩn của hãng"],["Bảo hành","12 tháng"],["Phụ kiện kèm theo","Hộp đựng, dầu bôi trơn, khăn lau"]]},"phu-kien":{"features":["Phụ kiện chính hãng, độ bền cao","Tương thích với đa số nhạc cụ cùng loại"],"specs":[["Loại sản phẩm","Phụ kiện / Thiết bị âm thanh"],["Xuất xứ","Theo hãng sản xuất"],["Tình trạng","Mới 100%, nguyên seal"],["Bảo hành","6 - 12 tháng"],["Đóng gói","Hộp chính hãng"]]}},"tiers":[{"key":"thuong","name":"Thường","min":0,"discount":0,"color":"#8E8E93"},{"key":"thanh-vien","name":"Thành viên","min":5000000,"discount":2,"color":"#0984E3"},{"key":"vip","name":"VIP","min":20000000,"discount":5,"color":"#E1A200"}],"productPresentation":[{"id":1,"sku":"YAM-1001","emoji":"🎸","old":3990000,"rating":4.9,"reviews":128,"badge":"hot","sold":980,"isNew":false,"short":"Mặt đàn gỗ vân sam cho âm sáng, ngân vang chuẩn xác, bán chạy số 1 cho người mới."},{"id":2,"sku":"FEN-1002","emoji":"🎸","old":null,"rating":4.8,"reviews":64,"badge":null,"sold":210,"isNew":false,"short":"Huyền thoại Stratocaster với 3 single-coil cho âm thanh trong và cá tính."},{"id":3,"sku":"IBA-1003","emoji":"🎸","old":6990000,"rating":4.7,"reviews":52,"badge":"sale","sold":340,"isNew":false,"short":"Cần đàn mỏng, chơi solo nhanh, phù hợp rock và metal."},{"id":4,"sku":"TAY-1004","emoji":"🎸","old":null,"rating":4.9,"reviews":31,"badge":null,"sold":95,"isNew":false,"short":' +
        N'"Dáng đàn nhỏ gọn nhưng âm lượng lớn, thích hợp du lịch và thu âm."},{"id":5,"sku":"COR-1005","emoji":"🎸","old":2790000,"rating":4.6,"reviews":210,"badge":"hot","sold":1250,"isNew":false,"short":"Mức giá dễ tiếp cận nhưng chất lượng ổn định, âm thanh ấm áp."},{"id":6,"sku":"YAM-1006","emoji":"🎹","old":19900000,"rating":4.9,"reviews":96,"badge":"sale","sold":410,"isNew":false,"short":"Phím GHS cảm ứng lực, âm thanh sampling từ đàn grand piano CFIIIS."},{"id":7,"sku":"ROL-1007","emoji":"🎹","old":null,"rating":4.8,"reviews":47,"badge":null,"sold":180,"isNew":false,"short":"Động cơ âm thanh SuperNATURAL, kết nối Bluetooth MIDI."},{"id":8,"sku":"CAS-1008","emoji":"🎹","old":3990000,"rating":4.5,"reviews":143,"badge":null,"sold":760,"isNew":false,"short":"Gọn nhẹ, nhiều điệu nhạc, phù hợp tập luyện tại nhà."},{"id":9,"sku":"CAS-1009","emoji":"🎹","old":10990000,"rating":4.7,"reviews":58,"badge":"new","sold":120,"isNew":true,"short":"Thiết kế siêu mỏng, bàn phím phản hồi lực Smart Scaled."},{"id":10,"sku":"YAM-1010","emoji":"🎹","old":6790000,"rating":4.8,"reviews":175,"badge":"hot","sold":890,"isNew":false,"short":"622 âm sắc, 205 điệu nhạc, có chế độ học nhạc thông minh."},{"id":11,"sku":"ROL-1011","emoji":"🥁","old":17900000,"rating":4.7,"reviews":29,"badge":null,"sold":75,"isNew":false,"short":"Tập trống yên tĩnh trong căn hộ, mặt mesh cho cảm giác thật."},{"id":12,"sku":"PEA-1012","emoji":"🥁","old":14500000,"rating":4.6,"reviews":22,"badge":"sale","sold":60,"isNew":false,"short":"Bộ trống 5 món đầy đủ cymbal, hardware và ghế ngồi."},{"id":13,"sku":"ZIL-1013","emoji":"🥁","old":8900000,"rating":4.7,"reviews":25,"badge":null,"sold":48,"isNew":false,"short":"Bộ cymbal 3 món cho âm thanh sáng, rõ và bền."},{"id":14,"sku":"KAL-1014","emoji":"🪕","old":1590000,"rating":4.8,"reviews":301,"badge":"hot","sold":1600,"isNew":false,"short":"Gỗ Mahogany, âm ấm, đẹp mắt, bán chạy nhất dòng ukulele."},{"id":15,"sku":"COR-1015","emoji":"🪕","old":null,"rating":4.5,"reviews":89,"badge":null,"sold":430,"isNew":false,"short":"Size Concert dễ bấm hợp tay người lớn, giá tốt."},{"id":16,"sku":"YAM-1016","emoji":"🎻","old":7590000,"rating":4.8,"reviews":37,"badge":"new","sold":70,"isNew":true,"short":"Violin học viên cao cấp, thùng đàn gỗ thích cho âm sáng."},{"id":17,"sku":"STE-1017","emoji":"🎻","old":3790000,"rating":4.6,"reviews":66,"badge":null,"sold":210,"isNew":false,"short":"Chất lượng ổn định, được nhiều trường nhạc lựa chọn."},{"id":18,"sku":"YAM-1018","emoji":"🪈","old":250000,"rating":4.9,"reviews":520,"badge":"hot","sold":3800,"isNew":false,"short":"Sáo recorder chuẩn trường học, âm chuẩn, dễ thổi."},{"id":19,"sku":"YAM-1019","emoji":"🎷","old":27900000,"rating":4.8,"reviews":14,"badge":null,"sold":22,"isNew":false,"short":"Alto Sax cho người mới, phím nhẹ, dễ ra âm."},{"id":20,"sku":"YAM-1020","emoji":"🎺","old":null,"rating":4.7,"reviews":12,"badge":"new","sold":18,"isNew":true,"short":"Trumpet Bb cho học viên, thân đồng thau mạ vàng."},{"id":21,"sku":"MAR-1021","emoj' +
        N'i":"🔊","old":3590000,"rating":4.7,"reviews":88,"badge":"sale","sold":520,"isNew":false,"short":"Ampli 15W, 2 kênh, âm thanh rock kinh điển."},{"id":22,"sku":"BOS-1022","emoji":"🎚️","old":2690000,"rating":4.8,"reviews":120,"badge":null,"sold":640,"isNew":false,"short":"Tuner dạng pedal chính xác, màn hình sáng rõ trên sân khấu."},{"id":23,"sku":"D''A-1023","emoji":"🎼","old":250000,"rating":4.9,"reviews":860,"badge":"hot","sold":5400,"isNew":false,"short":"Bộ dây điện 10-46 chuẩn quốc tế, bền và sáng tiếng."},{"id":24,"sku":"DUN-1024","emoji":"🎵","old":90000,"rating":4.9,"reviews":1020,"badge":null,"sold":6100,"isNew":false,"short":"Chất liệu Tortex bền, bám tay, nhiều độ dày."},{"id":25,"sku":"GIB-1025","emoji":"🎸","old":null,"rating":4.9,"reviews":9,"badge":"new","sold":6,"isNew":true,"short":"Biểu tượng rock với humbucker mạnh mẽ, sustain dài."},{"id":26,"sku":"MAR-1026","emoji":"🔊","old":10990000,"rating":4.7,"reviews":41,"badge":null,"sold":130,"isNew":false,"short":"Loa Bluetooth thiết kế ampli cổ điển, âm thanh đa chiều."},{"id":27,"sku":"BOS-1027","emoji":"🔊","old":8990000,"rating":4.8,"reviews":55,"badge":"sale","sold":160,"isNew":false,"short":"Ampli 50W với nhiều hiệu ứng tích hợp, kết nối USB thu âm."}]}');
COMMIT TRANSACTION;

-- ===== user-data-tables.sql =====
IF OBJECT_ID(N'dbo.wishlist_item', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.wishlist_item (
        wishlist_item_id NVARCHAR(255) NOT NULL PRIMARY KEY,
        user_id VARCHAR(255) NOT NULL,
        product_id VARCHAR(255) NOT NULL,
        CONSTRAINT uq_wishlist_user_product UNIQUE (user_id, product_id),
        CONSTRAINT fk_wishlist_user FOREIGN KEY (user_id) REFERENCES dbo.users(user_id),
        CONSTRAINT fk_wishlist_product FOREIGN KEY (product_id) REFERENCES dbo.products(product_id)
    );
END;

IF OBJECT_ID(N'dbo.contact_message', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.contact_message (
        contact_id NVARCHAR(255) NOT NULL PRIMARY KEY,
        full_name NVARCHAR(255) NOT NULL,
        email NVARCHAR(255) NOT NULL,
        phone NVARCHAR(64) NULL,
        subject NVARCHAR(255) NOT NULL,
        message NVARCHAR(MAX) NOT NULL,
        created_at DATETIME2 NOT NULL
    );
END;

IF OBJECT_ID(N'dbo.newsletter_subscription', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.newsletter_subscription (
        subscription_id NVARCHAR(255) NOT NULL PRIMARY KEY,
        email NVARCHAR(255) NOT NULL UNIQUE,
        created_at DATETIME2 NOT NULL
    );
END;

-- Bổ sung các cột thiếu trên schema DB cũ trước khi nạp sample-data.
IF COL_LENGTH(N'dbo.brand', N'origin') IS NULL
    ALTER TABLE dbo.brand ADD origin NVARCHAR(100) NULL;
IF COL_LENGTH(N'dbo.contact_message', N'status') IS NULL
    ALTER TABLE dbo.contact_message ADD status NVARCHAR(30) NOT NULL CONSTRAINT df_contact_message_status_seed DEFAULT (N'Mới');
IF COL_LENGTH(N'dbo.contact_message', N'internal_note') IS NULL
    ALTER TABLE dbo.contact_message ADD internal_note NVARCHAR(MAX) NULL;
IF COL_LENGTH(N'dbo.orders', N'note') IS NULL
    ALTER TABLE dbo.orders ADD note NVARCHAR(500) NULL;
IF COL_LENGTH(N'dbo.products', N'sku') IS NULL
    ALTER TABLE dbo.products ADD sku NVARCHAR(50) NULL;
IF COL_LENGTH(N'dbo.products', N'short_description') IS NULL
    ALTER TABLE dbo.products ADD short_description NVARCHAR(500) NULL;
IF COL_LENGTH(N'dbo.products', N'old_price') IS NULL
    ALTER TABLE dbo.products ADD old_price DECIMAL(19,2) NULL;
IF COL_LENGTH(N'dbo.products', N'badge') IS NULL
    ALTER TABLE dbo.products ADD badge NVARCHAR(10) NULL;
IF COL_LENGTH(N'dbo.products', N'emoji') IS NULL
    ALTER TABLE dbo.products ADD emoji NVARCHAR(8) NULL;
IF COL_LENGTH(N'dbo.products', N'is_new') IS NULL
    ALTER TABLE dbo.products ADD is_new BIT NULL;
IF COL_LENGTH(N'dbo.products', N'active') IS NULL
    ALTER TABLE dbo.products ADD active BIT NULL;
IF COL_LENGTH(N'dbo.order_line_item', N'unit_price') IS NULL
    ALTER TABLE dbo.order_line_item ADD unit_price DECIMAL(19,2) NULL;
IF COL_LENGTH(N'dbo.products', N'images') IS NULL
    ALTER TABLE dbo.products ADD images NVARCHAR(2000) NULL;
GO

-- ===== sample-data.sql =====
/*
  Du lieu mau cho SQL Server / DB_WebNhac.
  Chay sau khi ung dung da tao schema (hibernate.hbm2ddl.auto=update).
  Script giữ tài khoản admin-001 và nạp lại các dòng test có khóa sample-*.
  Sản phẩm mẫu dùng catalog nhạc cụ có product_id từ 1 đến 24 ở phần trên.
*/
-- Đồng bộ CHECK constraint cũ của orders với các trạng thái OrderStatus trong ứng dụng.
DECLARE @orderStatusConstraint SYSNAME;
SELECT TOP (1) @orderStatusConstraint = name
FROM sys.check_constraints
WHERE parent_object_id = OBJECT_ID(N'dbo.orders')
  AND definition LIKE N'%order_status%';
IF @orderStatusConstraint IS NOT NULL
    EXEC(N'ALTER TABLE dbo.orders DROP CONSTRAINT ' + QUOTENAME(@orderStatusConstraint));
IF NOT EXISTS (
    SELECT 1 FROM sys.check_constraints
    WHERE parent_object_id = OBJECT_ID(N'dbo.orders')
      AND name = N'CK_orders_order_status'
)
    ALTER TABLE dbo.orders WITH CHECK ADD CONSTRAINT CK_orders_order_status
        CHECK (order_status IN (N'PROCESSING', N'CONFIRMED', N'SHIPPING', N'COMPLETED', N'CANCELLED', N'PENDING', N'FAILED'));

SET XACT_ABORT ON;
BEGIN TRANSACTION;

-- Thêm cột nếu schema cũ được tạo trước khi có mã khuyến mãi.
IF COL_LENGTH('dbo.promotion', 'coupon_code') IS NULL
    ALTER TABLE dbo.promotion ADD coupon_code VARCHAR(50) NULL;
IF COL_LENGTH('dbo.orders', 'coupon_code') IS NULL
    ALTER TABLE dbo.orders ADD coupon_code VARCHAR(50) NULL;

-- Xóa riêng các dòng test cũ trước khi nạp lại để script chạy được nhiều lần.
-- Giữ nguyên dữ liệu người dùng thật, catalog nhạc cụ và tài khoản quản trị.
DELETE FROM dbo.promotion_product WHERE promotion_id IN ('sample-promo-001', 'sample-promo-002', 'sample-promo-003', 'sample-promo-004', 'sample-promo-005');
DELETE FROM dbo.promotion_product WHERE product_id LIKE 'sample-product-%';
DELETE FROM dbo.promotion_customer_rank WHERE promotion_id IN ('sample-promo-001', 'sample-promo-002', 'sample-promo-003', 'sample-promo-004', 'sample-promo-005');
DELETE FROM dbo.review WHERE review_id LIKE 'sample-review-%' OR product_id LIKE 'sample-product-%';
DELETE FROM dbo.inventory_history WHERE inventory_history_id LIKE 'sample-inventory-%' OR product_id LIKE 'sample-product-%';
DELETE FROM dbo.deposit WHERE order_id LIKE 'sample-order-%';
DELETE FROM dbo.payment WHERE order_id LIKE 'sample-order-%';
DELETE FROM dbo.order_history WHERE order_id LIKE 'sample-order-%';
DELETE FROM dbo.order_line_item WHERE order_id LIKE 'sample-order-%';
DELETE FROM dbo.orders WHERE order_id LIKE 'sample-order-%';
DELETE FROM dbo.cart_line_item WHERE cart_id LIKE 'sample-cart-%' OR cart_line_item_id LIKE 'sample-cart-line-%' OR product_id LIKE 'sample-product-%';
DELETE FROM dbo.cart WHERE cart_id LIKE 'sample-cart-%';
DELETE FROM dbo.wishlist_item WHERE product_id LIKE 'sample-product-%';
DELETE FROM dbo.contact_message WHERE contact_id LIKE 'sample-contact-%';
DELETE FROM dbo.news_article WHERE article_id LIKE 'sample-news-%';
DELETE FROM dbo.admin_audit_log WHERE audit_id LIKE 'sample-audit-%';
DELETE FROM dbo.site_setting WHERE setting_id LIKE 'sample-setting-%';
DELETE FROM dbo.promotion WHERE promotion_id LIKE 'sample-promo-%';
DELETE FROM dbo.products WHERE product_id LIKE 'sample-product-%';
DELETE FROM dbo.brand WHERE brand_id IN ('brand-sony', 'brand-jbl', 'brand-bose', 'brand-audio-technica', 'brand-sennheiser');
DELETE FROM dbo.category WHERE category_id IN ('cat-headphones', 'cat-speakers', 'cat-microphones', 'cat-amplifiers', 'cat-accessories');

-- Bảng độc lập / bảng cha
MERGE dbo.customer_rank AS target
USING (VALUES
    ('rank-bronze', 'BRONZE', 100000000),
    ('rank-silver', 'SILVER', 150000000),
    ('rank-gold', 'GOLD', 250000000),
    ('rank-vip', 'VIP', 500000000),
    ('rank-vip-demo', 'VIP', 750000000)
) AS source(rank_id, rank_name, min_spending)
ON target.rank_id = source.rank_id
WHEN MATCHED THEN UPDATE SET rank_name = source.rank_name, min_spending = source.min_spending
WHEN NOT MATCHED THEN INSERT (rank_id, rank_name, min_spending)
    VALUES (source.rank_id, source.rank_name, source.min_spending);

IF NOT EXISTS (SELECT 1 FROM users WHERE user_id = 'admin-001' OR email = 'admin@soundora.vn')
    INSERT INTO users (user_id, full_name, email, password, phone, role, rank_id)
    VALUES ('admin-001', N'Quản trị viên', 'admin@soundora.vn', 'admin123', '0900000000', 'ADMIN', 'rank-vip');

MERGE dbo.users AS target
USING (VALUES
    ('sample-user-002', N'Nguyễn Minh Anh', 'minhanh@example.com', 'user123', '0900000002', 'USER', 'rank-bronze'),
    ('sample-user-003', N'Trần Hoàng Nam', 'hoangnam@example.com', 'user123', '0900000003', 'USER', 'rank-silver'),
    ('sample-user-004', N'Lê Thu Hà', 'thuha@example.com', 'user123', '0900000004', 'USER', 'rank-gold'),
    ('sample-user-005', N'Phạm Quốc Bảo', 'quocbao@example.com', 'user123', '0900000005', 'USER', 'rank-vip-demo')
) AS source(user_id, full_name, email, password, phone, role, rank_id)
ON target.user_id = source.user_id
WHEN MATCHED THEN UPDATE SET full_name = source.full_name, email = source.email,
    password = source.password, phone = source.phone, role = source.role, rank_id = source.rank_id
WHEN NOT MATCHED THEN INSERT (user_id, full_name, email, password, phone, role, rank_id)
    VALUES (source.user_id, source.full_name, source.email, source.password, source.phone, source.role, source.rank_id);

-- Dùng catalog nhạc cụ product_id 1..24 đã được seed phía trên, không tạo catalog tai nghe riêng.

INSERT INTO promotion (promotion_id, promotion_name, coupon_code, discount_type, discount_value, start_date_time, end_date_time) VALUES
('sample-promo-001', N'Ưu đãi khách hàng mới', 'SAMPLE10', 'PERCENTAGE', 10, DATEADD(day,-10,GETDATE()), DATEADD(day,30,GETDATE())),
('sample-promo-002', N'Tuần lễ âm thanh', 'JBL10', 'PERCENTAGE', 10, DATEADD(day,-5,GETDATE()), DATEADD(day,20,GETDATE())),
('sample-promo-003', N'Ưu đãi đàn kèn', 'WELCOME200K', 'FIXED_AMOUNT', 200000, DATEADD(day,-2,GETDATE()), DATEADD(day,15,GETDATE())),
('sample-promo-004', N'Ưu đãi thành viên vàng', 'GOLD12', 'PERCENTAGE', 12, DATEADD(day,-1,GETDATE()), DATEADD(day,25,GETDATE())),
('sample-promo-005', N'Ưu đãi cuối tháng', 'ENDMONTH150', 'FIXED_AMOUNT', 150000, DATEADD(day,-1,GETDATE()), DATEADD(day,35,GETDATE()));

IF EXISTS (SELECT 1 FROM dbo.site_setting WHERE setting_id = 'default')
    UPDATE dbo.site_setting SET free_shipping_from = 500000, shipping_fee = 30000, updated_at = GETDATE() WHERE setting_id = 'default';
ELSE
    INSERT INTO dbo.site_setting (setting_id, free_shipping_from, shipping_fee, updated_at) VALUES ('default', 500000, 30000, GETDATE());

INSERT INTO site_setting (setting_id, free_shipping_from, shipping_fee, updated_at) VALUES
('sample-setting-2', 600000, 25000, GETDATE()),
('sample-setting-3', 700000, 20000, GETDATE()),
('sample-setting-4', 800000, 15000, GETDATE()),
('sample-setting-5', 900000, 10000, GETDATE());

-- Dữ liệu nội dung không phụ thuộc đơn hàng
INSERT INTO contact_message (contact_id, full_name, email, phone, subject, message, status, internal_note, created_at) VALUES
('sample-contact-001', N'Nguyễn Minh Anh', 'minhanh@example.com', '0900000002', N'Tư vấn guitar', N'Yamaha F310 có phù hợp cho người mới học guitar không?', N'Mới', NULL, DATEADD(day,-4,GETDATE())),
('sample-contact-002', N'Trần Hoàng Nam', 'hoangnam@example.com', '0900000003', N'Tình trạng đơn hàng', N'Tôi muốn kiểm tra tiến độ giao cây guitar đã đặt.', N'Đang xử lý', N'Đã tiếp nhận yêu cầu.', DATEADD(day,-3,GETDATE())),
('sample-contact-003', N'Lê Thu Hà', 'thuha@example.com', '0900000004', N'Đổi trả', N'Chính sách đổi trả đàn piano điện áp dụng trong bao lâu?', N'Mới', NULL, DATEADD(day,-2,GETDATE())),
('sample-contact-004', N'Phạm Quốc Bảo', 'quocbao@example.com', '0900000005', N'Tư vấn violin', N'Nhờ tư vấn violin phù hợp cho học sinh mới bắt đầu.', N'Đã phản hồi', N'Đã gửi thông tin các mẫu violin học viên.', DATEADD(day,-1,GETDATE())),
('sample-contact-005', N'Hoàng Yến', 'yen@example.com', '0900000006', N'Bảo hành', N'Thời gian bảo hành của ampli guitar là bao lâu?', N'Mới', NULL, GETDATE());

INSERT INTO news_article (article_id, title, category, excerpt, content, emoji, author, color, color2, published, published_at, created_at) VALUES
('sample-news-001', N'Cách chọn guitar acoustic cho người mới', N'Tư vấn', N'Những điểm cần xem trước khi mua guitar đầu tiên.', N'Chọn kích thước đàn phù hợp, kiểm tra độ cao dây và thử âm thanh trước khi mua.', N'🎸', N'Soundora', '#273469', '#4f6dff', 1, DATEADD(day,-8,GETDATE()), DATEADD(day,-9,GETDATE())),
('sample-news-002', N'Bảo quản đàn guitar đúng cách', N'Hướng dẫn', N'Giữ đàn ổn định trong điều kiện khí hậu nóng ẩm.', N'Cất đàn trong bao, tránh nắng trực tiếp và lau dây sau khi chơi để hạn chế gỉ.', N'🎸', N'Soundora', '#164e63', '#22a6b3', 1, DATEADD(day,-6,GETDATE()), DATEADD(day,-7,GETDATE())),
('sample-news-003', N'Làm quen với piano điện', N'Công nghệ', N'Một số tính năng hữu ích khi tập đàn tại nhà.', N'Bàn phím có lực, metronome và cổng tai nghe giúp việc luyện tập thuận tiện hơn.', N'🎹', N'Soundora', '#4c1d95', '#8b5cf6', 1, DATEADD(day,-4,GETDATE()), DATEADD(day,-5,GETDATE())),
('sample-news-004', N'Chọn violin cho học sinh', N'Hướng dẫn', N'Chọn kích thước violin theo vóc dáng người chơi.', N'Đo chiều dài cánh tay và nhờ người có kinh nghiệm kiểm tra đàn trước khi mua.', N'🎻', N'Soundora', '#7c2d12', '#f97316', 1, DATEADD(day,-2,GETDATE()), DATEADD(day,-3,GETDATE())),
('sample-news-005', N'Bắt đầu tập trống tại nhà', N'Tư vấn', N'Chuẩn bị không gian và dụng cụ tập trống cơ bản.', N'Chọn vị trí phù hợp, dùng pad tập và bảo vệ tai khi luyện tập lâu.', N'🥁', N'Soundora', '#14532d', '#22c55e', 0, NULL, GETDATE());

INSERT INTO admin_audit_log (audit_id, actor_email, action, detail, created_at) VALUES
('sample-audit-001', 'admin@soundora.vn', N'Tạo sản phẩm', N'Tạo sản phẩm nhạc cụ mẫu product_id 1', DATEADD(day,-4,GETDATE())),
('sample-audit-002', 'admin@soundora.vn', N'Cập nhật danh mục', N'Cập nhật danh mục guitar', DATEADD(day,-3,GETDATE())),
('sample-audit-003', 'admin@soundora.vn', N'Tạo khuyến mãi', N'Tạo chương trình SAMPLE10', DATEADD(day,-2,GETDATE())),
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
('sample-cart-line-001', 'sample-cart-001', '1', 1),
('sample-cart-line-002', 'sample-cart-002', '2', 1),
('sample-cart-line-003', 'sample-cart-003', '19', 1),
('sample-cart-line-004', 'sample-cart-004', '4', 1),
('sample-cart-line-005', 'sample-cart-005', '5', 2);

INSERT INTO orders (order_id, order_date, subtotal, discount, total_amount, shipping_address, order_status, note, shipping_fee, user_id) VALUES
('sample-order-001', DATEADD(day,-10,GETDATE()), 3490000, 0, 3520000, N'12 Nguyễn Huệ, Quận 1, TP.HCM', 'COMPLETED', N'Giao giờ hành chính', 30000, 'sample-user-002'),
('sample-order-002', DATEADD(day,-7,GETDATE()), 17900000, 0, 17930000, N'20 Lê Lợi, Quận 1, TP.HCM', 'SHIPPING', NULL, 30000, 'sample-user-003'),
('sample-order-003', DATEADD(day,-5,GETDATE()), 124500000, 0, 124530000, N'5 Trần Hưng Đạo, Quận 5, TP.HCM', 'CONFIRMED', NULL, 30000, 'sample-user-004'),
('sample-order-004', DATEADD(day,-3,GETDATE()), 19500000, 0, 19530000, N'88 Điện Biên Phủ, Bình Thạnh, TP.HCM', 'PROCESSING', NULL, 30000, 'sample-user-005'),
('sample-order-005', DATEADD(day,-1,GETDATE()), 4780000, 0, 4810000, N'7 Phan Chu Trinh, Hải Châu, Đà Nẵng', 'CANCELLED', N'Khách yêu cầu hủy', 30000, 'sample-user-002');

INSERT INTO order_line_item (order_line_item_id, product_id, unit_price, quantity, order_id) VALUES
('sample-order-line-001', '1', 3490000, 1, 'sample-order-001'),
('sample-order-line-002', '2', 17900000, 1, 'sample-order-002'),
('sample-order-line-003', '19', 24900000, 5, 'sample-order-003'),
('sample-order-line-004', '4', 19500000, 1, 'sample-order-004'),
('sample-order-line-005', '5', 2390000, 2, 'sample-order-005');

INSERT INTO order_history (history_id, order_id, status, changed_at, changed_by) VALUES
('sample-order-history-001', 'sample-order-001', 'COMPLETED', DATEADD(day,-8,GETDATE()), N'admin@soundora.vn'),
('sample-order-history-002', 'sample-order-002', 'SHIPPING', DATEADD(day,-5,GETDATE()), N'admin@soundora.vn'),
('sample-order-history-003', 'sample-order-003', 'CONFIRMED', DATEADD(day,-4,GETDATE()), N'admin@soundora.vn'),
('sample-order-history-004', 'sample-order-004', 'PROCESSING', DATEADD(day,-3,GETDATE()), N'admin@soundora.vn'),
('sample-order-history-005', 'sample-order-005', 'CANCELLED', DATEADD(day,-1,GETDATE()), N'admin@soundora.vn');

INSERT INTO payment (payment_id, payment_method, amount, payment_date, payment_status, order_id) VALUES
('sample-payment-001', 'CASH', 3520000, DATEADD(day,-8,GETDATE()), 'COMPLETED', 'sample-order-001'),
('sample-payment-002', 'E_WALLET', 17930000, NULL, 'PENDING', 'sample-order-002'),
('sample-payment-003', 'E_WALLET', 124530000, NULL, 'PENDING', 'sample-order-003'),
('sample-payment-004', 'CASH', 19530000, NULL, 'PENDING', 'sample-order-004'),
('sample-payment-005', 'CASH', 4810000, NULL, 'REFUNDED', 'sample-order-005');

INSERT INTO deposit (deposit_id, amount, deposit_date, deposit_status, order_id) VALUES
('sample-deposit-003', 37359000, NULL, 'PENDING', 'sample-order-003');

INSERT INTO review (review_id, rating, comment, approved, user_id, product_id, order_id) VALUES
('sample-review-001', 5, N'Âm thanh rõ, dễ chơi cho người mới.', 1, 'sample-user-002', '1', 'sample-order-001'),
('sample-review-002', 4, N'Đàn chắc chắn, tiếng sáng và dễ chỉnh dây.', 1, 'sample-user-003', '2', 'sample-order-002'),
('sample-review-003', 5, N'Âm thanh tốt, hoàn thiện đẹp.', 0, 'sample-user-004', '19', 'sample-order-003'),
('sample-review-004', 4, N'Đàn nhỏ gọn, phù hợp luyện tập tại nhà.', 1, 'sample-user-005', '4', 'sample-order-004'),
('sample-review-005', 5, N'Giá hợp lý, âm thanh ấm.', 0, 'sample-user-002', '5', 'sample-order-005');

INSERT INTO inventory_history (inventory_history_id, product_id, operation, quantity_change, stock_before, stock_after, note, changed_by, changed_at) VALUES
('sample-inventory-001', '1', 'STOCK_IN', 12, 0, 12, N'Nhập tồn kho ban đầu', 'admin@soundora.vn', DATEADD(day,-8,GETDATE())),
('sample-inventory-002', '2', 'STOCK_IN', 19, 0, 19, N'Nhập tồn kho ban đầu', 'admin@soundora.vn', DATEADD(day,-7,GETDATE())),
('sample-inventory-003', '19', 'ORDER', -5, 18, 13, N'Xuất kho theo đơn sample-order-003', 'admin@soundora.vn', DATEADD(day,-5,GETDATE())),
('sample-inventory-004', '4', 'STOCK_IN', 33, 0, 33, N'Nhập tồn kho ban đầu', 'admin@soundora.vn', DATEADD(day,-3,GETDATE())),
('sample-inventory-005', '5', 'STOCK_IN', 10, 0, 10, N'Nhập tồn kho ban đầu', 'admin@soundora.vn', DATEADD(day,-1,GETDATE()));

-- Bảng liên kết many-to-many
INSERT INTO promotion_product (promotion_id, product_id) VALUES
('sample-promo-001', '1'),
('sample-promo-002', '2'),
('sample-promo-003', '19'),
('sample-promo-004', '4'),
('sample-promo-005', '5');

INSERT INTO promotion_customer_rank (promotion_id, rank_id) VALUES
('sample-promo-001', 'rank-bronze'),
('sample-promo-002', 'rank-silver'),
('sample-promo-003', 'rank-gold'),
('sample-promo-004', 'rank-vip-demo'),
('sample-promo-005', 'rank-vip-demo');

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

-- ===== unicode-text-columns.sql =====
-- Chuyển các cột chứa nội dung/ngôn ngữ người dùng sang NVARCHAR trên SQL Server.
-- Chạy trong DB_WebNhac. Việc đổi kiểu không thể khôi phục ký tự đã bị lưu thành '?';
-- với dữ liệu danh mục, chạy lại datajs-catalog-seed.sql sau script này.
USE DB_WebNhac;
SET XACT_ABORT ON;
BEGIN TRANSACTION;

IF OBJECT_ID(N'dbo.products', N'U') IS NOT NULL
BEGIN
    IF COL_LENGTH(N'dbo.products', N'product_name') IS NOT NULL
        ALTER TABLE dbo.products ALTER COLUMN product_name NVARCHAR(255) NOT NULL;
    IF COL_LENGTH(N'dbo.products', N'description') IS NOT NULL
        ALTER TABLE dbo.products ALTER COLUMN description NVARCHAR(MAX) NULL;
END;

IF OBJECT_ID(N'dbo.brand', N'U') IS NOT NULL
BEGIN
    IF COL_LENGTH(N'dbo.brand', N'brand_name') IS NOT NULL
        ALTER TABLE dbo.brand ALTER COLUMN brand_name NVARCHAR(255) NULL;
    IF COL_LENGTH(N'dbo.brand', N'description') IS NOT NULL
        ALTER TABLE dbo.brand ALTER COLUMN description NVARCHAR(MAX) NULL;
END;

IF OBJECT_ID(N'dbo.category', N'U') IS NOT NULL AND COL_LENGTH(N'dbo.category', N'category_name') IS NOT NULL
    ALTER TABLE dbo.category ALTER COLUMN category_name NVARCHAR(255) NULL;

IF OBJECT_ID(N'dbo.users', N'U') IS NOT NULL
BEGIN
    IF COL_LENGTH(N'dbo.users', N'full_name') IS NOT NULL
        ALTER TABLE dbo.users ALTER COLUMN full_name NVARCHAR(255) NOT NULL;
    IF COL_LENGTH(N'dbo.users', N'address') IS NOT NULL
        ALTER TABLE dbo.users ALTER COLUMN address NVARCHAR(1000) NULL;
END;

IF OBJECT_ID(N'dbo.orders', N'U') IS NOT NULL AND COL_LENGTH(N'dbo.orders', N'shipping_address') IS NOT NULL
    ALTER TABLE dbo.orders ALTER COLUMN shipping_address NVARCHAR(1000) NULL;

IF OBJECT_ID(N'dbo.review', N'U') IS NOT NULL AND COL_LENGTH(N'dbo.review', N'comment') IS NOT NULL
    ALTER TABLE dbo.review ALTER COLUMN comment NVARCHAR(MAX) NULL;

IF OBJECT_ID(N'dbo.promotion', N'U') IS NOT NULL
BEGIN
    IF COL_LENGTH(N'dbo.promotion', N'promotion_name') IS NOT NULL
        ALTER TABLE dbo.promotion ALTER COLUMN promotion_name NVARCHAR(255) NULL;
    IF COL_LENGTH(N'dbo.promotion', N'description') IS NOT NULL
        ALTER TABLE dbo.promotion ALTER COLUMN description NVARCHAR(500) NULL;
END;

-- Các bảng này vốn tạo đúng Unicode; giữ các ALTER ở đây để sửa DB cũ nếu cột bị tạo sai kiểu.
IF OBJECT_ID(N'dbo.contact_message', N'U') IS NOT NULL
BEGIN
    ALTER TABLE dbo.contact_message ALTER COLUMN full_name NVARCHAR(255) NOT NULL;
    ALTER TABLE dbo.contact_message ALTER COLUMN subject NVARCHAR(255) NOT NULL;
    ALTER TABLE dbo.contact_message ALTER COLUMN message NVARCHAR(MAX) NOT NULL;
END;

COMMIT TRANSACTION;
