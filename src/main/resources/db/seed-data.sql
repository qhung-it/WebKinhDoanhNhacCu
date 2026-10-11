-- Seed data derived from src/main/webapp/data.js.
-- Run against DB_WebNhac. Safe to run repeatedly; existing IDs are left unchanged.
-- News, coupons, and informational pages have no corresponding mapped tables/entities.
USE [DB_WebNhac];
SET NOCOUNT ON;
SET XACT_ABORT ON;

BEGIN TRY
    BEGIN TRANSACTION;

    -- Categories
    IF NOT EXISTS (SELECT 1 FROM dbo.category WHERE category_id = N'CAT-GUITAR')
        INSERT INTO dbo.category (category_id, category_name, [status]) VALUES (N'CAT-GUITAR', N'Guitar', 1);
    IF NOT EXISTS (SELECT 1 FROM dbo.category WHERE category_id = N'CAT-PIANO')
        INSERT INTO dbo.category (category_id, category_name, [status]) VALUES (N'CAT-PIANO', N'Piano', 1);
    IF NOT EXISTS (SELECT 1 FROM dbo.category WHERE category_id = N'CAT-TRONG')
        INSERT INTO dbo.category (category_id, category_name, [status]) VALUES (N'CAT-TRONG', N'Trống', 1);
    IF NOT EXISTS (SELECT 1 FROM dbo.category WHERE category_id = N'CAT-ORGAN')
        INSERT INTO dbo.category (category_id, category_name, [status]) VALUES (N'CAT-ORGAN', N'Organ', 1);
    IF NOT EXISTS (SELECT 1 FROM dbo.category WHERE category_id = N'CAT-UKULELE')
        INSERT INTO dbo.category (category_id, category_name, [status]) VALUES (N'CAT-UKULELE', N'Ukulele', 1);
    IF NOT EXISTS (SELECT 1 FROM dbo.category WHERE category_id = N'CAT-VIOLIN')
        INSERT INTO dbo.category (category_id, category_name, [status]) VALUES (N'CAT-VIOLIN', N'Violin', 1);
    IF NOT EXISTS (SELECT 1 FROM dbo.category WHERE category_id = N'CAT-KEN-SAO')
        INSERT INTO dbo.category (category_id, category_name, [status]) VALUES (N'CAT-KEN-SAO', N'Kèn & Sáo', 1);
    IF NOT EXISTS (SELECT 1 FROM dbo.category WHERE category_id = N'CAT-PHU-KIEN')
        INSERT INTO dbo.category (category_id, category_name, [status]) VALUES (N'CAT-PHU-KIEN', N'Phụ kiện', 1);

    -- Brands
    IF NOT EXISTS (SELECT 1 FROM dbo.brand WHERE brand_id = N'BR-YAMAHA')
        INSERT INTO dbo.brand (brand_id, brand_name, description) VALUES (N'BR-YAMAHA', N'Yamaha', N'Thương hiệu nhạc cụ hàng đầu thế giới, nổi tiếng với piano, guitar và organ.');
    IF NOT EXISTS (SELECT 1 FROM dbo.brand WHERE brand_id = N'BR-FENDER')
        INSERT INTO dbo.brand (brand_id, brand_name, description) VALUES (N'BR-FENDER', N'Fender', N'Cái nôi của Stratocaster và Telecaster huyền thoại.');
    IF NOT EXISTS (SELECT 1 FROM dbo.brand WHERE brand_id = N'BR-IBANEZ')
        INSERT INTO dbo.brand (brand_id, brand_name, description) VALUES (N'BR-IBANEZ', N'Ibanez', N'Chuyên guitar điện, bass với cần đàn mỏng, tốc độ cao.');
    IF NOT EXISTS (SELECT 1 FROM dbo.brand WHERE brand_id = N'BR-TAYLOR')
        INSERT INTO dbo.brand (brand_id, brand_name, description) VALUES (N'BR-TAYLOR', N'Taylor', N'Guitar acoustic cao cấp với âm thanh trong trẻo, tinh tế.');
    IF NOT EXISTS (SELECT 1 FROM dbo.brand WHERE brand_id = N'BR-ROLAND')
        INSERT INTO dbo.brand (brand_id, brand_name, description) VALUES (N'BR-ROLAND', N'Roland', N'Dẫn đầu về piano điện, trống điện và synthesizer.');
    IF NOT EXISTS (SELECT 1 FROM dbo.brand WHERE brand_id = N'BR-CORT')
        INSERT INTO dbo.brand (brand_id, brand_name, description) VALUES (N'BR-CORT', N'Cort', N'Guitar chất lượng tốt, giá hợp lý cho người mới bắt đầu.');
    IF NOT EXISTS (SELECT 1 FROM dbo.brand WHERE brand_id = N'BR-CASIO')
        INSERT INTO dbo.brand (brand_id, brand_name, description) VALUES (N'BR-CASIO', N'Casio', N'Organ và piano điện gọn nhẹ, dễ sử dụng, giá dễ tiếp cận.');
    IF NOT EXISTS (SELECT 1 FROM dbo.brand WHERE brand_id = N'BR-GIBSON')
        INSERT INTO dbo.brand (brand_id, brand_name, description) VALUES (N'BR-GIBSON', N'Gibson', N'Les Paul, SG, ES - những cây đàn đã làm nên lịch sử rock.');
    IF NOT EXISTS (SELECT 1 FROM dbo.brand WHERE brand_id = N'BR-MARSHALL')
        INSERT INTO dbo.brand (brand_id, brand_name, description) VALUES (N'BR-MARSHALL', N'Marshall', N'Ampli và loa mang âm thanh rock đặc trưng.');
    IF NOT EXISTS (SELECT 1 FROM dbo.brand WHERE brand_id = N'BR-BOSS')
        INSERT INTO dbo.brand (brand_id, brand_name, description) VALUES (N'BR-BOSS', N'Boss', N'Pedal hiệu ứng và thiết bị guitar bền bỉ, chuyên nghiệp.');
    IF NOT EXISTS (SELECT 1 FROM dbo.brand WHERE brand_id = N'BR-PEARL')
        INSERT INTO dbo.brand (brand_id, brand_name, description) VALUES (N'BR-PEARL', N'Pearl', N'Bộ trống cơ chất lượng cao cho cả học viên lẫn nghệ sĩ.');
    IF NOT EXISTS (SELECT 1 FROM dbo.brand WHERE brand_id = N'BR-KALA')
        INSERT INTO dbo.brand (brand_id, brand_name, description) VALUES (N'BR-KALA', N'Kala', N'Ukulele thiết kế đẹp, âm vang ấm áp.');
    IF NOT EXISTS (SELECT 1 FROM dbo.brand WHERE brand_id = N'BR-STENTOR')
        INSERT INTO dbo.brand (brand_id, brand_name, description) VALUES (N'BR-STENTOR', N'Stentor', N'Violin học sinh và sinh viên được nhiều trường nhạc tin dùng.');
    IF NOT EXISTS (SELECT 1 FROM dbo.brand WHERE brand_id = N'BR-DUNLOP')
        INSERT INTO dbo.brand (brand_id, brand_name, description) VALUES (N'BR-DUNLOP', N'Dunlop', N'Phím gảy, capo và phụ kiện guitar nổi tiếng.');
    IF NOT EXISTS (SELECT 1 FROM dbo.brand WHERE brand_id = N'BR-DADDARIO')
        INSERT INTO dbo.brand (brand_id, brand_name, description) VALUES (N'BR-DADDARIO', N'D''Addario', N'Dây đàn chất lượng số 1 cho guitar, bass, violin.');
    IF NOT EXISTS (SELECT 1 FROM dbo.brand WHERE brand_id = N'BR-ZILDJIAN')
        INSERT INTO dbo.brand (brand_id, brand_name, description) VALUES (N'BR-ZILDJIAN', N'Zildjian', N'Thương hiệu cymbal lâu đời hơn 400 năm.');

    -- Products
    IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'PRD-0001')
        INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id)
        VALUES (N'PRD-0001', N'Yamaha F310 - Guitar Acoustic', N'Mặt đàn gỗ vân sam cho âm sáng, ngân vang chuẩn xác, bán chạy số 1 cho người mới.', 3490000, 12, NULL, N'BR-YAMAHA', N'CAT-GUITAR');
    IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'PRD-0002')
        INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id)
        VALUES (N'PRD-0002', N'Fender Player Stratocaster', N'Huyền thoại Stratocaster với 3 single-coil cho âm thanh trong và cá tính.', 17900000, 19, NULL, N'BR-FENDER', N'CAT-GUITAR');
    IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'PRD-0003')
        INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id)
        VALUES (N'PRD-0003', N'Ibanez GRG121DX - Guitar Điện', N'Cần đàn mỏng, chơi solo nhanh, phù hợp rock và metal.', 6290000, 26, NULL, N'BR-IBANEZ', N'CAT-GUITAR');
    IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'PRD-0004')
        INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id)
        VALUES (N'PRD-0004', N'Taylor GS Mini Mahogany', N'Dáng đàn nhỏ gọn nhưng âm lượng lớn, thích hợp du lịch và thu âm.', 19500000, 33, NULL, N'BR-TAYLOR', N'CAT-GUITAR');
    IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'PRD-0005')
        INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id)
        VALUES (N'PRD-0005', N'Cort AD810 - Guitar Acoustic', N'Mức giá dễ tiếp cận nhưng chất lượng ổn định, âm thanh ấm áp.', 2390000, 10, NULL, N'BR-CORT', N'CAT-GUITAR');
    IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'PRD-0006')
        INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id)
        VALUES (N'PRD-0006', N'Yamaha P-125 - Piano Điện', N'Phím GHS cảm ứng lực, âm thanh sampling từ đàn grand piano CFIIIS.', 16900000, 17, NULL, N'BR-YAMAHA', N'CAT-PIANO');
    IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'PRD-0007')
        INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id)
        VALUES (N'PRD-0007', N'Roland FP-30X - Piano Điện', N'Động cơ âm thanh SuperNATURAL, kết nối Bluetooth MIDI.', 18500000, 24, NULL, N'BR-ROLAND', N'CAT-PIANO');
    IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'PRD-0008')
        INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id)
        VALUES (N'PRD-0008', N'Casio CT-S300 - Organ', N'Gọn nhẹ, nhiều điệu nhạc, phù hợp tập luyện tại nhà.', 3590000, 31, NULL, N'BR-CASIO', N'CAT-ORGAN');
    IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'PRD-0009')
        INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id)
        VALUES (N'PRD-0009', N'Casio CDP-S160 - Piano Điện Mỏng', N'Thiết kế siêu mỏng, bàn phím phản hồi lực Smart Scaled.', 9890000, 8, NULL, N'BR-CASIO', N'CAT-PIANO');
    IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'PRD-0010')
        INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id)
        VALUES (N'PRD-0010', N'Yamaha PSR-E373 - Organ', N'622 âm sắc, 205 điệu nhạc, có chế độ học nhạc thông minh.', 5990000, 15, NULL, N'BR-YAMAHA', N'CAT-ORGAN');
    IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'PRD-0011')
        INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id)
        VALUES (N'PRD-0011', N'Roland TD-1DMK - Trống Điện', N'Tập trống yên tĩnh trong căn hộ, mặt mesh cho cảm giác thật.', 15900000, 22, NULL, N'BR-ROLAND', N'CAT-TRONG');
    IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'PRD-0012')
        INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id)
        VALUES (N'PRD-0012', N'Pearl Roadshow 5 Piece - Trống Cơ', N'Bộ trống 5 món đầy đủ cymbal, hardware và ghế ngồi.', 12900000, 29, NULL, N'BR-PEARL', N'CAT-TRONG');
    IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'PRD-0013')
        INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id)
        VALUES (N'PRD-0013', N'Zildjian S Family Cymbal Pack', N'Bộ cymbal 3 món cho âm thanh sáng, rõ và bền.', 7900000, 6, NULL, N'BR-ZILDJIAN', N'CAT-TRONG');
    IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'PRD-0014')
        INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id)
        VALUES (N'PRD-0014', N'Kala KA-15S - Ukulele Soprano', N'Gỗ Mahogany, âm ấm, đẹp mắt, bán chạy nhất dòng ukulele.', 1290000, 13, NULL, N'BR-KALA', N'CAT-UKULELE');
    IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'PRD-0015')
        INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id)
        VALUES (N'PRD-0015', N'Cort UKE Concert', N'Size Concert dễ bấm hợp tay người lớn, giá tốt.', 1090000, 20, NULL, N'BR-CORT', N'CAT-UKULELE');
    IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'PRD-0016')
        INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id)
        VALUES (N'PRD-0016', N'Yamaha V3SKA - Violin 4/4', N'Violin học viên cao cấp, thùng đàn gỗ thích cho âm sáng.', 6890000, 27, NULL, N'BR-YAMAHA', N'CAT-VIOLIN');
    IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'PRD-0017')
        INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id)
        VALUES (N'PRD-0017', N'Stentor Student I - Violin 4/4', N'Chất lượng ổn định, được nhiều trường nhạc lựa chọn.', 3290000, 34, NULL, N'BR-STENTOR', N'CAT-VIOLIN');
    IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'PRD-0018')
        INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id)
        VALUES (N'PRD-0018', N'Yamaha YRS-24B - Sáo Recorder', N'Sáo recorder chuẩn trường học, âm chuẩn, dễ thổi.', 190000, 11, NULL, N'BR-YAMAHA', N'CAT-KEN-SAO');
    IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'PRD-0019')
        INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id)
        VALUES (N'PRD-0019', N'Yamaha YAS-280 - Saxophone Alto', N'Alto Sax cho người mới, phím nhẹ, dễ ra âm.', 24900000, 18, NULL, N'BR-YAMAHA', N'CAT-KEN-SAO');
    IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'PRD-0020')
        INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id)
        VALUES (N'PRD-0020', N'Yamaha YTR-2330 - Trumpet', N'Trumpet Bb cho học viên, thân đồng thau mạ vàng.', 14500000, 25, NULL, N'BR-YAMAHA', N'CAT-KEN-SAO');
    IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'PRD-0021')
        INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id)
        VALUES (N'PRD-0021', N'Marshall MG15 - Ampli Guitar', N'Ampli 15W, 2 kênh, âm thanh rock kinh điển.', 3190000, 32, NULL, N'BR-MARSHALL', N'CAT-PHU-KIEN');
    IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'PRD-0022')
        INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id)
        VALUES (N'PRD-0022', N'Boss TU-3 - Pedal Chỉnh Dây', N'Tuner dạng pedal chính xác, màn hình sáng rõ trên sân khấu.', 2390000, 9, NULL, N'BR-BOSS', N'CAT-PHU-KIEN');
    IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'PRD-0023')
        INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id)
        VALUES (N'PRD-0023', N'D''Addario EXL110 - Dây Guitar Điện', N'Bộ dây điện 10-46 chuẩn quốc tế, bền và sáng tiếng.', 190000, 16, NULL, N'BR-DADDARIO', N'CAT-PHU-KIEN');
    IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'PRD-0024')
        INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id)
        VALUES (N'PRD-0024', N'Dunlop Tortex - Phím Gảy (12 cái)', N'Chất liệu Tortex bền, bám tay, nhiều độ dày.', 69000, 23, NULL, N'BR-DUNLOP', N'CAT-PHU-KIEN');
    IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'PRD-0025')
        INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id)
        VALUES (N'PRD-0025', N'Gibson Les Paul Studio', N'Biểu tượng rock với humbucker mạnh mẽ, sustain dài.', 52900000, 30, NULL, N'BR-GIBSON', N'CAT-GUITAR');
    IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'PRD-0026')
        INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id)
        VALUES (N'PRD-0026', N'Marshall Stanmore II - Loa Bluetooth', N'Loa Bluetooth thiết kế ampli cổ điển, âm thanh đa chiều.', 9990000, 7, NULL, N'BR-MARSHALL', N'CAT-PHU-KIEN');
    IF NOT EXISTS (SELECT 1 FROM dbo.products WHERE product_id = N'PRD-0027')
        INSERT INTO dbo.products (product_id, product_name, description, price, quantity, image, brand_id, category_id)
        VALUES (N'PRD-0027', N'Boss Katana-50 MkII - Ampli', N'Ampli 50W với nhiều hiệu ứng tích hợp, kết nối USB thu âm.', 7990000, 14, NULL, N'BR-BOSS', N'CAT-PHU-KIEN');

    -- Promotions from old-price fields in data.js.
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion WHERE promotion_id = N'PROMO-0001')
        INSERT INTO dbo.promotion (promotion_id, promotion_name, discount_type, discount_value, start_date_time, end_date_time)
        VALUES (N'PROMO-0001', N'Sale - Yamaha F310 - Guitar Acoustic', N'FIXED_AMOUNT', 500000, CONVERT(datetime2, '2020-01-01T00:00:00'), CONVERT(datetime2, '2099-12-31T23:59:59'));
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion_product WHERE promotion_id = N'PROMO-0001' AND product_id = N'PRD-0001')
        INSERT INTO dbo.promotion_product (promotion_id, product_id) VALUES (N'PROMO-0001', N'PRD-0001');
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion WHERE promotion_id = N'PROMO-0003')
        INSERT INTO dbo.promotion (promotion_id, promotion_name, discount_type, discount_value, start_date_time, end_date_time)
        VALUES (N'PROMO-0003', N'Sale - Ibanez GRG121DX - Guitar Điện', N'FIXED_AMOUNT', 700000, CONVERT(datetime2, '2020-01-01T00:00:00'), CONVERT(datetime2, '2099-12-31T23:59:59'));
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion_product WHERE promotion_id = N'PROMO-0003' AND product_id = N'PRD-0003')
        INSERT INTO dbo.promotion_product (promotion_id, product_id) VALUES (N'PROMO-0003', N'PRD-0003');
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion WHERE promotion_id = N'PROMO-0005')
        INSERT INTO dbo.promotion (promotion_id, promotion_name, discount_type, discount_value, start_date_time, end_date_time)
        VALUES (N'PROMO-0005', N'Sale - Cort AD810 - Guitar Acoustic', N'FIXED_AMOUNT', 400000, CONVERT(datetime2, '2020-01-01T00:00:00'), CONVERT(datetime2, '2099-12-31T23:59:59'));
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion_product WHERE promotion_id = N'PROMO-0005' AND product_id = N'PRD-0005')
        INSERT INTO dbo.promotion_product (promotion_id, product_id) VALUES (N'PROMO-0005', N'PRD-0005');
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion WHERE promotion_id = N'PROMO-0006')
        INSERT INTO dbo.promotion (promotion_id, promotion_name, discount_type, discount_value, start_date_time, end_date_time)
        VALUES (N'PROMO-0006', N'Sale - Yamaha P-125 - Piano Điện', N'FIXED_AMOUNT', 3000000, CONVERT(datetime2, '2020-01-01T00:00:00'), CONVERT(datetime2, '2099-12-31T23:59:59'));
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion_product WHERE promotion_id = N'PROMO-0006' AND product_id = N'PRD-0006')
        INSERT INTO dbo.promotion_product (promotion_id, product_id) VALUES (N'PROMO-0006', N'PRD-0006');
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion WHERE promotion_id = N'PROMO-0008')
        INSERT INTO dbo.promotion (promotion_id, promotion_name, discount_type, discount_value, start_date_time, end_date_time)
        VALUES (N'PROMO-0008', N'Sale - Casio CT-S300 - Organ', N'FIXED_AMOUNT', 400000, CONVERT(datetime2, '2020-01-01T00:00:00'), CONVERT(datetime2, '2099-12-31T23:59:59'));
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion_product WHERE promotion_id = N'PROMO-0008' AND product_id = N'PRD-0008')
        INSERT INTO dbo.promotion_product (promotion_id, product_id) VALUES (N'PROMO-0008', N'PRD-0008');
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion WHERE promotion_id = N'PROMO-0009')
        INSERT INTO dbo.promotion (promotion_id, promotion_name, discount_type, discount_value, start_date_time, end_date_time)
        VALUES (N'PROMO-0009', N'Sale - Casio CDP-S160 - Piano Điện Mỏng', N'FIXED_AMOUNT', 1100000, CONVERT(datetime2, '2020-01-01T00:00:00'), CONVERT(datetime2, '2099-12-31T23:59:59'));
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion_product WHERE promotion_id = N'PROMO-0009' AND product_id = N'PRD-0009')
        INSERT INTO dbo.promotion_product (promotion_id, product_id) VALUES (N'PROMO-0009', N'PRD-0009');
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion WHERE promotion_id = N'PROMO-0010')
        INSERT INTO dbo.promotion (promotion_id, promotion_name, discount_type, discount_value, start_date_time, end_date_time)
        VALUES (N'PROMO-0010', N'Sale - Yamaha PSR-E373 - Organ', N'FIXED_AMOUNT', 800000, CONVERT(datetime2, '2020-01-01T00:00:00'), CONVERT(datetime2, '2099-12-31T23:59:59'));
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion_product WHERE promotion_id = N'PROMO-0010' AND product_id = N'PRD-0010')
        INSERT INTO dbo.promotion_product (promotion_id, product_id) VALUES (N'PROMO-0010', N'PRD-0010');
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion WHERE promotion_id = N'PROMO-0011')
        INSERT INTO dbo.promotion (promotion_id, promotion_name, discount_type, discount_value, start_date_time, end_date_time)
        VALUES (N'PROMO-0011', N'Sale - Roland TD-1DMK - Trống Điện', N'FIXED_AMOUNT', 2000000, CONVERT(datetime2, '2020-01-01T00:00:00'), CONVERT(datetime2, '2099-12-31T23:59:59'));
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion_product WHERE promotion_id = N'PROMO-0011' AND product_id = N'PRD-0011')
        INSERT INTO dbo.promotion_product (promotion_id, product_id) VALUES (N'PROMO-0011', N'PRD-0011');
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion WHERE promotion_id = N'PROMO-0012')
        INSERT INTO dbo.promotion (promotion_id, promotion_name, discount_type, discount_value, start_date_time, end_date_time)
        VALUES (N'PROMO-0012', N'Sale - Pearl Roadshow 5 Piece - Trống Cơ', N'FIXED_AMOUNT', 1600000, CONVERT(datetime2, '2020-01-01T00:00:00'), CONVERT(datetime2, '2099-12-31T23:59:59'));
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion_product WHERE promotion_id = N'PROMO-0012' AND product_id = N'PRD-0012')
        INSERT INTO dbo.promotion_product (promotion_id, product_id) VALUES (N'PROMO-0012', N'PRD-0012');
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion WHERE promotion_id = N'PROMO-0013')
        INSERT INTO dbo.promotion (promotion_id, promotion_name, discount_type, discount_value, start_date_time, end_date_time)
        VALUES (N'PROMO-0013', N'Sale - Zildjian S Family Cymbal Pack', N'FIXED_AMOUNT', 1000000, CONVERT(datetime2, '2020-01-01T00:00:00'), CONVERT(datetime2, '2099-12-31T23:59:59'));
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion_product WHERE promotion_id = N'PROMO-0013' AND product_id = N'PRD-0013')
        INSERT INTO dbo.promotion_product (promotion_id, product_id) VALUES (N'PROMO-0013', N'PRD-0013');
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion WHERE promotion_id = N'PROMO-0014')
        INSERT INTO dbo.promotion (promotion_id, promotion_name, discount_type, discount_value, start_date_time, end_date_time)
        VALUES (N'PROMO-0014', N'Sale - Kala KA-15S - Ukulele Soprano', N'FIXED_AMOUNT', 300000, CONVERT(datetime2, '2020-01-01T00:00:00'), CONVERT(datetime2, '2099-12-31T23:59:59'));
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion_product WHERE promotion_id = N'PROMO-0014' AND product_id = N'PRD-0014')
        INSERT INTO dbo.promotion_product (promotion_id, product_id) VALUES (N'PROMO-0014', N'PRD-0014');
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion WHERE promotion_id = N'PROMO-0016')
        INSERT INTO dbo.promotion (promotion_id, promotion_name, discount_type, discount_value, start_date_time, end_date_time)
        VALUES (N'PROMO-0016', N'Sale - Yamaha V3SKA - Violin 4/4', N'FIXED_AMOUNT', 700000, CONVERT(datetime2, '2020-01-01T00:00:00'), CONVERT(datetime2, '2099-12-31T23:59:59'));
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion_product WHERE promotion_id = N'PROMO-0016' AND product_id = N'PRD-0016')
        INSERT INTO dbo.promotion_product (promotion_id, product_id) VALUES (N'PROMO-0016', N'PRD-0016');
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion WHERE promotion_id = N'PROMO-0017')
        INSERT INTO dbo.promotion (promotion_id, promotion_name, discount_type, discount_value, start_date_time, end_date_time)
        VALUES (N'PROMO-0017', N'Sale - Stentor Student I - Violin 4/4', N'FIXED_AMOUNT', 500000, CONVERT(datetime2, '2020-01-01T00:00:00'), CONVERT(datetime2, '2099-12-31T23:59:59'));
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion_product WHERE promotion_id = N'PROMO-0017' AND product_id = N'PRD-0017')
        INSERT INTO dbo.promotion_product (promotion_id, product_id) VALUES (N'PROMO-0017', N'PRD-0017');
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion WHERE promotion_id = N'PROMO-0018')
        INSERT INTO dbo.promotion (promotion_id, promotion_name, discount_type, discount_value, start_date_time, end_date_time)
        VALUES (N'PROMO-0018', N'Sale - Yamaha YRS-24B - Sáo Recorder', N'FIXED_AMOUNT', 60000, CONVERT(datetime2, '2020-01-01T00:00:00'), CONVERT(datetime2, '2099-12-31T23:59:59'));
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion_product WHERE promotion_id = N'PROMO-0018' AND product_id = N'PRD-0018')
        INSERT INTO dbo.promotion_product (promotion_id, product_id) VALUES (N'PROMO-0018', N'PRD-0018');
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion WHERE promotion_id = N'PROMO-0019')
        INSERT INTO dbo.promotion (promotion_id, promotion_name, discount_type, discount_value, start_date_time, end_date_time)
        VALUES (N'PROMO-0019', N'Sale - Yamaha YAS-280 - Saxophone Alto', N'FIXED_AMOUNT', 3000000, CONVERT(datetime2, '2020-01-01T00:00:00'), CONVERT(datetime2, '2099-12-31T23:59:59'));
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion_product WHERE promotion_id = N'PROMO-0019' AND product_id = N'PRD-0019')
        INSERT INTO dbo.promotion_product (promotion_id, product_id) VALUES (N'PROMO-0019', N'PRD-0019');
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion WHERE promotion_id = N'PROMO-0021')
        INSERT INTO dbo.promotion (promotion_id, promotion_name, discount_type, discount_value, start_date_time, end_date_time)
        VALUES (N'PROMO-0021', N'Sale - Marshall MG15 - Ampli Guitar', N'FIXED_AMOUNT', 400000, CONVERT(datetime2, '2020-01-01T00:00:00'), CONVERT(datetime2, '2099-12-31T23:59:59'));
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion_product WHERE promotion_id = N'PROMO-0021' AND product_id = N'PRD-0021')
        INSERT INTO dbo.promotion_product (promotion_id, product_id) VALUES (N'PROMO-0021', N'PRD-0021');
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion WHERE promotion_id = N'PROMO-0022')
        INSERT INTO dbo.promotion (promotion_id, promotion_name, discount_type, discount_value, start_date_time, end_date_time)
        VALUES (N'PROMO-0022', N'Sale - Boss TU-3 - Pedal Chỉnh Dây', N'FIXED_AMOUNT', 300000, CONVERT(datetime2, '2020-01-01T00:00:00'), CONVERT(datetime2, '2099-12-31T23:59:59'));
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion_product WHERE promotion_id = N'PROMO-0022' AND product_id = N'PRD-0022')
        INSERT INTO dbo.promotion_product (promotion_id, product_id) VALUES (N'PROMO-0022', N'PRD-0022');
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion WHERE promotion_id = N'PROMO-0023')
        INSERT INTO dbo.promotion (promotion_id, promotion_name, discount_type, discount_value, start_date_time, end_date_time)
        VALUES (N'PROMO-0023', N'Sale - D''Addario EXL110 - Dây Guitar Điện', N'FIXED_AMOUNT', 60000, CONVERT(datetime2, '2020-01-01T00:00:00'), CONVERT(datetime2, '2099-12-31T23:59:59'));
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion_product WHERE promotion_id = N'PROMO-0023' AND product_id = N'PRD-0023')
        INSERT INTO dbo.promotion_product (promotion_id, product_id) VALUES (N'PROMO-0023', N'PRD-0023');
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion WHERE promotion_id = N'PROMO-0024')
        INSERT INTO dbo.promotion (promotion_id, promotion_name, discount_type, discount_value, start_date_time, end_date_time)
        VALUES (N'PROMO-0024', N'Sale - Dunlop Tortex - Phím Gảy (12 cái)', N'FIXED_AMOUNT', 21000, CONVERT(datetime2, '2020-01-01T00:00:00'), CONVERT(datetime2, '2099-12-31T23:59:59'));
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion_product WHERE promotion_id = N'PROMO-0024' AND product_id = N'PRD-0024')
        INSERT INTO dbo.promotion_product (promotion_id, product_id) VALUES (N'PROMO-0024', N'PRD-0024');
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion WHERE promotion_id = N'PROMO-0026')
        INSERT INTO dbo.promotion (promotion_id, promotion_name, discount_type, discount_value, start_date_time, end_date_time)
        VALUES (N'PROMO-0026', N'Sale - Marshall Stanmore II - Loa Bluetooth', N'FIXED_AMOUNT', 1000000, CONVERT(datetime2, '2020-01-01T00:00:00'), CONVERT(datetime2, '2099-12-31T23:59:59'));
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion_product WHERE promotion_id = N'PROMO-0026' AND product_id = N'PRD-0026')
        INSERT INTO dbo.promotion_product (promotion_id, product_id) VALUES (N'PROMO-0026', N'PRD-0026');
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion WHERE promotion_id = N'PROMO-0027')
        INSERT INTO dbo.promotion (promotion_id, promotion_name, discount_type, discount_value, start_date_time, end_date_time)
        VALUES (N'PROMO-0027', N'Sale - Boss Katana-50 MkII - Ampli', N'FIXED_AMOUNT', 1000000, CONVERT(datetime2, '2020-01-01T00:00:00'), CONVERT(datetime2, '2099-12-31T23:59:59'));
    IF NOT EXISTS (SELECT 1 FROM dbo.promotion_product WHERE promotion_id = N'PROMO-0027' AND product_id = N'PRD-0027')
        INSERT INTO dbo.promotion_product (promotion_id, product_id) VALUES (N'PROMO-0027', N'PRD-0027');

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;
