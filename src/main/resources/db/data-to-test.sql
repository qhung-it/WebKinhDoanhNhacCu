-- MySQL conversion of test-all.sql; run after Hibernate creates the schema.
-- Safe to rerun fixed sample-* records. Does not drop tables or real user data.
CREATE DATABASE IF NOT EXISTS db_webnhac CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE db_webnhac;
SET NAMES utf8mb4;
START TRANSACTION;

ALTER TABLE orders
    MODIFY COLUMN order_status VARCHAR(20) NOT NULL DEFAULT 'PROCESSING';

-- Catalog seed from the source SQL Server script.
INSERT INTO category (category_id, category_name, status) VALUES ('guitar', 'Guitar', 1) ON DUPLICATE KEY UPDATE category_name=VALUES(category_name), status=VALUES(status);
INSERT INTO category (category_id, category_name, status) VALUES ('piano', 'Piano', 1) ON DUPLICATE KEY UPDATE category_name=VALUES(category_name), status=VALUES(status);
INSERT INTO category (category_id, category_name, status) VALUES ('trong', 'Trống', 1) ON DUPLICATE KEY UPDATE category_name=VALUES(category_name), status=VALUES(status);
INSERT INTO category (category_id, category_name, status) VALUES ('organ', 'Organ', 1) ON DUPLICATE KEY UPDATE category_name=VALUES(category_name), status=VALUES(status);
INSERT INTO category (category_id, category_name, status) VALUES ('ukulele', 'Ukulele', 1) ON DUPLICATE KEY UPDATE category_name=VALUES(category_name), status=VALUES(status);
INSERT INTO category (category_id, category_name, status) VALUES ('violin', 'Violin', 1) ON DUPLICATE KEY UPDATE category_name=VALUES(category_name), status=VALUES(status);
INSERT INTO category (category_id, category_name, status) VALUES ('ken-sao', 'Kèn & Sáo', 1) ON DUPLICATE KEY UPDATE category_name=VALUES(category_name), status=VALUES(status);
INSERT INTO category (category_id, category_name, status) VALUES ('phu-kien', 'Phụ kiện', 1) ON DUPLICATE KEY UPDATE category_name=VALUES(category_name), status=VALUES(status);
INSERT INTO brand (brand_id, brand_name, description) VALUES ('yamaha', 'Yamaha', 'Thương hiệu nhạc cụ hàng đầu thế giới, nổi tiếng với piano, guitar và organ.') ON DUPLICATE KEY UPDATE brand_name=VALUES(brand_name), description=VALUES(description);
INSERT INTO brand (brand_id, brand_name, description) VALUES ('fender', 'Fender', 'Cái nôi của Stratocaster và Telecaster huyền thoại.') ON DUPLICATE KEY UPDATE brand_name=VALUES(brand_name), description=VALUES(description);
INSERT INTO brand (brand_id, brand_name, description) VALUES ('ibanez', 'Ibanez', 'Chuyên guitar điện, bass với cần đàn mỏng, tốc độ cao.') ON DUPLICATE KEY UPDATE brand_name=VALUES(brand_name), description=VALUES(description);
INSERT INTO brand (brand_id, brand_name, description) VALUES ('taylor', 'Taylor', 'Guitar acoustic cao cấp với âm thanh trong trẻo, tinh tế.') ON DUPLICATE KEY UPDATE brand_name=VALUES(brand_name), description=VALUES(description);
INSERT INTO brand (brand_id, brand_name, description) VALUES ('roland', 'Roland', 'Dẫn đầu về piano điện, trống điện và synthesizer.') ON DUPLICATE KEY UPDATE brand_name=VALUES(brand_name), description=VALUES(description);
INSERT INTO brand (brand_id, brand_name, description) VALUES ('cort', 'Cort', 'Guitar chất lượng tốt, giá hợp lý cho người mới bắt đầu.') ON DUPLICATE KEY UPDATE brand_name=VALUES(brand_name), description=VALUES(description);
INSERT INTO brand (brand_id, brand_name, description) VALUES ('casio', 'Casio', 'Organ và piano điện gọn nhẹ, dễ sử dụng, giá dễ tiếp cận.') ON DUPLICATE KEY UPDATE brand_name=VALUES(brand_name), description=VALUES(description);
INSERT INTO brand (brand_id, brand_name, description) VALUES ('gibson', 'Gibson', 'Les Paul, SG, ES - những cây đàn đã làm nên lịch sử rock.') ON DUPLICATE KEY UPDATE brand_name=VALUES(brand_name), description=VALUES(description);
INSERT INTO brand (brand_id, brand_name, description) VALUES ('marshall', 'Marshall', 'Ampli và loa mang âm thanh rock đặc trưng.') ON DUPLICATE KEY UPDATE brand_name=VALUES(brand_name), description=VALUES(description);
INSERT INTO brand (brand_id, brand_name, description) VALUES ('boss', 'Boss', 'Pedal hiệu ứng và thiết bị guitar bền bỉ, chuyên nghiệp.') ON DUPLICATE KEY UPDATE brand_name=VALUES(brand_name), description=VALUES(description);
INSERT INTO brand (brand_id, brand_name, description) VALUES ('pearl', 'Pearl', 'Bộ trống cơ chất lượng cao cho cả học viên lẫn nghệ sĩ.') ON DUPLICATE KEY UPDATE brand_name=VALUES(brand_name), description=VALUES(description);
INSERT INTO brand (brand_id, brand_name, description) VALUES ('kala', 'Kala', 'Ukulele thiết kế đẹp, âm vang ấm áp.') ON DUPLICATE KEY UPDATE brand_name=VALUES(brand_name), description=VALUES(description);
INSERT INTO brand (brand_id, brand_name, description) VALUES ('stentor', 'Stentor', 'Violin học sinh và sinh viên được nhiều trường nhạc tin dùng.') ON DUPLICATE KEY UPDATE brand_name=VALUES(brand_name), description=VALUES(description);
INSERT INTO brand (brand_id, brand_name, description) VALUES ('dunlop', 'Dunlop', 'Phím gảy, capo và phụ kiện guitar nổi tiếng.') ON DUPLICATE KEY UPDATE brand_name=VALUES(brand_name), description=VALUES(description);
INSERT INTO brand (brand_id, brand_name, description) VALUES ('d-addario', 'D''Addario', 'Dây đàn chất lượng số 1 cho guitar, bass, violin.') ON DUPLICATE KEY UPDATE brand_name=VALUES(brand_name), description=VALUES(description);
INSERT INTO brand (brand_id, brand_name, description) VALUES ('zildjian', 'Zildjian', 'Thương hiệu cymbal lâu đời hơn 400 năm.') ON DUPLICATE KEY UPDATE brand_name=VALUES(brand_name), description=VALUES(description);
INSERT INTO products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES ('1', 'Yamaha F310 - Guitar Acoustic', 'Yamaha F310 - Guitar Acoustic là lựa chọn nổi bật của Yamaha trong nhóm guitar. Mặt đàn gỗ vân sam cho âm sáng, ngân vang chuẩn xác, bán chạy số 1 cho người mới. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 3490000, 12, 'images/Yamaha F310 - Guitar Acoustic.jpg', 'yamaha', 'guitar') ON DUPLICATE KEY UPDATE product_name=VALUES(product_name), description=VALUES(description), price=VALUES(price), quantity=VALUES(quantity), image=VALUES(image), brand_id=VALUES(brand_id), category_id=VALUES(category_id);
INSERT INTO products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES ('2', 'Fender Player Stratocaster', 'Fender Player Stratocaster là lựa chọn nổi bật của Fender trong nhóm guitar. Huyền thoại Stratocaster với 3 single-coil cho âm thanh trong và cá tính. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 17900000, 19, 'images/Fender Player Stratocaster.webp', 'fender', 'guitar') ON DUPLICATE KEY UPDATE product_name=VALUES(product_name), description=VALUES(description), price=VALUES(price), quantity=VALUES(quantity), image=VALUES(image), brand_id=VALUES(brand_id), category_id=VALUES(category_id);
INSERT INTO products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES ('3', 'Ibanez GRG121DX - Guitar Điện', 'Ibanez GRG121DX - Guitar Điện là lựa chọn nổi bật của Ibanez trong nhóm guitar. Cần đàn mỏng, chơi solo nhanh, phù hợp rock và metal. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 6290000, 26, 'images/Ibanez GRG121DX - Guitar Điện.jpg', 'ibanez', 'guitar') ON DUPLICATE KEY UPDATE product_name=VALUES(product_name), description=VALUES(description), price=VALUES(price), quantity=VALUES(quantity), image=VALUES(image), brand_id=VALUES(brand_id), category_id=VALUES(category_id);
INSERT INTO products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES ('4', 'Taylor GS Mini Mahogany', 'Taylor GS Mini Mahogany là lựa chọn nổi bật của Taylor trong nhóm guitar. Dáng đàn nhỏ gọn nhưng âm lượng lớn, thích hợp du lịch và thu âm. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 19500000, 33, 'images/Taylor GS Mini Mahogany.webp', 'taylor', 'guitar') ON DUPLICATE KEY UPDATE product_name=VALUES(product_name), description=VALUES(description), price=VALUES(price), quantity=VALUES(quantity), image=VALUES(image), brand_id=VALUES(brand_id), category_id=VALUES(category_id);
INSERT INTO products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES ('5', 'Cort AD810 - Guitar Acoustic', 'Cort AD810 - Guitar Acoustic là lựa chọn nổi bật của Cort trong nhóm guitar. Mức giá dễ tiếp cận nhưng chất lượng ổn định, âm thanh ấm áp. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 2390000, 10, 'images/Cort AD810 - Guitar Acoustic.jpg', 'cort', 'guitar') ON DUPLICATE KEY UPDATE product_name=VALUES(product_name), description=VALUES(description), price=VALUES(price), quantity=VALUES(quantity), image=VALUES(image), brand_id=VALUES(brand_id), category_id=VALUES(category_id);
INSERT INTO products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES ('6', 'Yamaha P-125 - Piano Điện', 'Yamaha P-125 - Piano Điện là lựa chọn nổi bật của Yamaha trong nhóm piano. Phím GHS cảm ứng lực, âm thanh sampling từ đàn grand piano CFIIIS. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 16900000, 17, 'images/Yamaha P-125 - Piano Điện.webp', 'yamaha', 'piano') ON DUPLICATE KEY UPDATE product_name=VALUES(product_name), description=VALUES(description), price=VALUES(price), quantity=VALUES(quantity), image=VALUES(image), brand_id=VALUES(brand_id), category_id=VALUES(category_id);
INSERT INTO products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES ('7', 'Roland FP-30X - Piano Điện', 'Roland FP-30X - Piano Điện là lựa chọn nổi bật của Roland trong nhóm piano. Động cơ âm thanh SuperNATURAL, kết nối Bluetooth MIDI. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 18500000, 24, 'images/Roland FP-30X - Piano Điện.jpg', 'roland', 'piano') ON DUPLICATE KEY UPDATE product_name=VALUES(product_name), description=VALUES(description), price=VALUES(price), quantity=VALUES(quantity), image=VALUES(image), brand_id=VALUES(brand_id), category_id=VALUES(category_id);
INSERT INTO products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES ('8', 'Casio CT-S300 - Organ', 'Casio CT-S300 - Organ là lựa chọn nổi bật của Casio trong nhóm organ. Gọn nhẹ, nhiều điệu nhạc, phù hợp tập luyện tại nhà. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 3590000, 31, 'images/Casio CT-S300 - Organ.jpg', 'casio', 'organ') ON DUPLICATE KEY UPDATE product_name=VALUES(product_name), description=VALUES(description), price=VALUES(price), quantity=VALUES(quantity), image=VALUES(image), brand_id=VALUES(brand_id), category_id=VALUES(category_id);
INSERT INTO products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES ('9', 'Casio CDP-S160 - Piano Điện Mỏng', 'Casio CDP-S160 - Piano Điện Mỏng là lựa chọn nổi bật của Casio trong nhóm piano. Thiết kế siêu mỏng, bàn phím phản hồi lực Smart Scaled. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 9890000, 8, 'images/Casio CDP-S160 - Piano Điện Mỏng.webp', 'casio', 'piano') ON DUPLICATE KEY UPDATE product_name=VALUES(product_name), description=VALUES(description), price=VALUES(price), quantity=VALUES(quantity), image=VALUES(image), brand_id=VALUES(brand_id), category_id=VALUES(category_id);
INSERT INTO products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES ('10', 'Yamaha PSR-E373 - Organ', 'Yamaha PSR-E373 - Organ là lựa chọn nổi bật của Yamaha trong nhóm organ. 622 âm sắc, 205 điệu nhạc, có chế độ học nhạc thông minh. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 5990000, 15, 'images/Yamaha PSR-E373 - Organ.jpg', 'yamaha', 'organ') ON DUPLICATE KEY UPDATE product_name=VALUES(product_name), description=VALUES(description), price=VALUES(price), quantity=VALUES(quantity), image=VALUES(image), brand_id=VALUES(brand_id), category_id=VALUES(category_id);
INSERT INTO products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES ('11', 'Roland TD-1DMK - Trống Điện', 'Roland TD-1DMK - Trống Điện là lựa chọn nổi bật của Roland trong nhóm trống. Tập trống yên tĩnh trong căn hộ, mặt mesh cho cảm giác thật. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 15900000, 22, 'images/Roland TD-1DMK - Trống Điện.jpg', 'roland', 'trong') ON DUPLICATE KEY UPDATE product_name=VALUES(product_name), description=VALUES(description), price=VALUES(price), quantity=VALUES(quantity), image=VALUES(image), brand_id=VALUES(brand_id), category_id=VALUES(category_id);
INSERT INTO products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES ('12', 'Pearl Roadshow 5 Piece - Trống Cơ', 'Pearl Roadshow 5 Piece - Trống Cơ là lựa chọn nổi bật của Pearl trong nhóm trống. Bộ trống 5 món đầy đủ cymbal, hardware và ghế ngồi. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 12900000, 29, 'images/Pearl Roadshow 5 Piece - Trống Cơ.gif', 'pearl', 'trong') ON DUPLICATE KEY UPDATE product_name=VALUES(product_name), description=VALUES(description), price=VALUES(price), quantity=VALUES(quantity), image=VALUES(image), brand_id=VALUES(brand_id), category_id=VALUES(category_id);
INSERT INTO products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES ('13', 'Zildjian S Family Cymbal Pack', 'Zildjian S Family Cymbal Pack là lựa chọn nổi bật của Zildjian trong nhóm trống. Bộ cymbal 3 món cho âm thanh sáng, rõ và bền. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 7900000, 6, 'images/Zildjian S Family Cymbal Pack.webp', 'zildjian', 'trong') ON DUPLICATE KEY UPDATE product_name=VALUES(product_name), description=VALUES(description), price=VALUES(price), quantity=VALUES(quantity), image=VALUES(image), brand_id=VALUES(brand_id), category_id=VALUES(category_id);
INSERT INTO products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES ('14', 'Kala KA-15S - Ukulele Soprano', 'Kala KA-15S - Ukulele Soprano là lựa chọn nổi bật của Kala trong nhóm ukulele. Gỗ Mahogany, âm ấm, đẹp mắt, bán chạy nhất dòng ukulele. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 1290000, 13, 'images/Kala KA-15S - Ukulele Soprano.jpg', 'kala', 'ukulele') ON DUPLICATE KEY UPDATE product_name=VALUES(product_name), description=VALUES(description), price=VALUES(price), quantity=VALUES(quantity), image=VALUES(image), brand_id=VALUES(brand_id), category_id=VALUES(category_id);
INSERT INTO products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES ('15', 'Cort UKE Concert', 'Cort UKE Concert là lựa chọn nổi bật của Cort trong nhóm ukulele. Size Concert dễ bấm hợp tay người lớn, giá tốt. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 1090000, 20, 'images/Cort UKE Concert.jpg', 'cort', 'ukulele') ON DUPLICATE KEY UPDATE product_name=VALUES(product_name), description=VALUES(description), price=VALUES(price), quantity=VALUES(quantity), image=VALUES(image), brand_id=VALUES(brand_id), category_id=VALUES(category_id);
INSERT INTO products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES ('16', 'Yamaha V3SKA - Violin 4/4', 'Yamaha V3SKA - Violin 4/4 là lựa chọn nổi bật của Yamaha trong nhóm violin. Violin học viên cao cấp, thùng đàn gỗ thích cho âm sáng. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 6890000, 27, 'images/Yamaha V3SKA - Violin 4-4.jpg', 'yamaha', 'violin') ON DUPLICATE KEY UPDATE product_name=VALUES(product_name), description=VALUES(description), price=VALUES(price), quantity=VALUES(quantity), image=VALUES(image), brand_id=VALUES(brand_id), category_id=VALUES(category_id);
INSERT INTO products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES ('17', 'Stentor Student I - Violin 4/4', 'Stentor Student I - Violin 4/4 là lựa chọn nổi bật của Stentor trong nhóm violin. Chất lượng ổn định, được nhiều trường nhạc lựa chọn. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 3290000, 34, 'images/Stentor Student I - Violin 4-4.jpg', 'stentor', 'violin') ON DUPLICATE KEY UPDATE product_name=VALUES(product_name), description=VALUES(description), price=VALUES(price), quantity=VALUES(quantity), image=VALUES(image), brand_id=VALUES(brand_id), category_id=VALUES(category_id);
INSERT INTO products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES ('18', 'Yamaha YRS-24B - Sáo Recorder', 'Yamaha YRS-24B - Sáo Recorder là lựa chọn nổi bật của Yamaha trong nhóm kèn & sáo. Sáo recorder chuẩn trường học, âm chuẩn, dễ thổi. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 190000, 11, 'images/Yamaha YRS-24B - Sáo Recorder.jpg', 'yamaha', 'ken-sao') ON DUPLICATE KEY UPDATE product_name=VALUES(product_name), description=VALUES(description), price=VALUES(price), quantity=VALUES(quantity), image=VALUES(image), brand_id=VALUES(brand_id), category_id=VALUES(category_id);
INSERT INTO products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES ('19', 'Yamaha YAS-280 - Saxophone Alto', 'Yamaha YAS-280 - Saxophone Alto là lựa chọn nổi bật của Yamaha trong nhóm kèn & sáo. Alto Sax cho người mới, phím nhẹ, dễ ra âm. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 24900000, 18, 'images/Yamaha YAS-280 - Saxophone Alto.webp', 'yamaha', 'ken-sao') ON DUPLICATE KEY UPDATE product_name=VALUES(product_name), description=VALUES(description), price=VALUES(price), quantity=VALUES(quantity), image=VALUES(image), brand_id=VALUES(brand_id), category_id=VALUES(category_id);
INSERT INTO products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES ('20', 'Yamaha YTR-2330 - Trumpet', 'Yamaha YTR-2330 - Trumpet là lựa chọn nổi bật của Yamaha trong nhóm kèn & sáo. Trumpet Bb cho học viên, thân đồng thau mạ vàng. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 14500000, 25, 'images/Yamaha YTR-2330 - Trumpet.webp', 'yamaha', 'ken-sao') ON DUPLICATE KEY UPDATE product_name=VALUES(product_name), description=VALUES(description), price=VALUES(price), quantity=VALUES(quantity), image=VALUES(image), brand_id=VALUES(brand_id), category_id=VALUES(category_id);
INSERT INTO products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES ('21', 'Marshall MG15 - Ampli Guitar', 'Marshall MG15 - Ampli Guitar là lựa chọn nổi bật của Marshall trong nhóm phụ kiện. Ampli 15W, 2 kênh, âm thanh rock kinh điển. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 3190000, 32, 'images/Marshall MG15 - Ampli Guitar.jpg', 'marshall', 'phu-kien') ON DUPLICATE KEY UPDATE product_name=VALUES(product_name), description=VALUES(description), price=VALUES(price), quantity=VALUES(quantity), image=VALUES(image), brand_id=VALUES(brand_id), category_id=VALUES(category_id);
INSERT INTO products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES ('22', 'Boss TU-3 - Pedal Chỉnh Dây', 'Boss TU-3 - Pedal Chỉnh Dây là lựa chọn nổi bật của Boss trong nhóm phụ kiện. Tuner dạng pedal chính xác, màn hình sáng rõ trên sân khấu. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 2390000, 9, 'images/Boss TU-3 - Pedal Chỉnh Dây.jpg', 'boss', 'phu-kien') ON DUPLICATE KEY UPDATE product_name=VALUES(product_name), description=VALUES(description), price=VALUES(price), quantity=VALUES(quantity), image=VALUES(image), brand_id=VALUES(brand_id), category_id=VALUES(category_id);
INSERT INTO products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES ('23', 'D''Addario EXL110 - Dây Guitar Điện', 'D''Addario EXL110 - Dây Guitar Điện là lựa chọn nổi bật của D''Addario trong nhóm phụ kiện. Bộ dây điện 10-46 chuẩn quốc tế, bền và sáng tiếng. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 190000, 16, 'images/D''Addario EXL110 - Dây Guitar Điện.jpg', 'd-addario', 'phu-kien') ON DUPLICATE KEY UPDATE product_name=VALUES(product_name), description=VALUES(description), price=VALUES(price), quantity=VALUES(quantity), image=VALUES(image), brand_id=VALUES(brand_id), category_id=VALUES(category_id);
INSERT INTO products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES ('24', 'Dunlop Tortex - Phím Gảy (12 cái)', 'Dunlop Tortex - Phím Gảy (12 cái) là lựa chọn nổi bật của Dunlop trong nhóm phụ kiện. Chất liệu Tortex bền, bám tay, nhiều độ dày. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 69000, 23, 'images/Dunlop Tortex - Phím Gảy (12 cái).jpg', 'dunlop', 'phu-kien') ON DUPLICATE KEY UPDATE product_name=VALUES(product_name), description=VALUES(description), price=VALUES(price), quantity=VALUES(quantity), image=VALUES(image), brand_id=VALUES(brand_id), category_id=VALUES(category_id);
INSERT INTO products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES ('25', 'Gibson Les Paul Studio', 'Gibson Les Paul Studio là lựa chọn nổi bật của Gibson trong nhóm guitar. Biểu tượng rock với humbucker mạnh mẽ, sustain dài. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 52900000, 30, 'images/Gibson Les Paul Studio.webp', 'gibson', 'guitar') ON DUPLICATE KEY UPDATE product_name=VALUES(product_name), description=VALUES(description), price=VALUES(price), quantity=VALUES(quantity), image=VALUES(image), brand_id=VALUES(brand_id), category_id=VALUES(category_id);
INSERT INTO products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES ('26', 'Marshall Stanmore II - Loa Bluetooth', 'Marshall Stanmore II - Loa Bluetooth là lựa chọn nổi bật của Marshall trong nhóm phụ kiện. Loa Bluetooth thiết kế ampli cổ điển, âm thanh đa chiều. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 9990000, 7, 'images/Marshall Stanmore II - Loa Bluetooth.webp', 'marshall', 'phu-kien') ON DUPLICATE KEY UPDATE product_name=VALUES(product_name), description=VALUES(description), price=VALUES(price), quantity=VALUES(quantity), image=VALUES(image), brand_id=VALUES(brand_id), category_id=VALUES(category_id);
INSERT INTO products (product_id, product_name, description, price, quantity, image, brand_id, category_id) VALUES ('27', 'Boss Katana-50 MkII - Ampli', 'Boss Katana-50 MkII - Ampli là lựa chọn nổi bật của Boss trong nhóm phụ kiện. Ampli 50W với nhiều hiệu ứng tích hợp, kết nối USB thu âm. Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.', 7990000, 14, 'images/Boss Katana-50 MkII - Ampli.jpg', 'boss', 'phu-kien') ON DUPLICATE KEY UPDATE product_name=VALUES(product_name), description=VALUES(description), price=VALUES(price), quantity=VALUES(quantity), image=VALUES(image), brand_id=VALUES(brand_id), category_id=VALUES(category_id);

-- Default promotions.
INSERT INTO promotion (promotion_id,promotion_name,coupon_code,discount_type,discount_value,minimum_order_amount,max_discount,description,free_shipping,active) VALUES ('SOUNDORA10', 'Soundora giảm 10%', 'SOUNDORA10', 'PERCENTAGE', 10, 0, 500000, 'Giảm 10% (tối đa 500.000đ)', 0, 1) ON DUPLICATE KEY UPDATE promotion_name=VALUES(promotion_name), coupon_code=VALUES(coupon_code), discount_type=VALUES(discount_type), discount_value=VALUES(discount_value), minimum_order_amount=VALUES(minimum_order_amount), max_discount=VALUES(max_discount), description=VALUES(description), free_shipping=VALUES(free_shipping), active=VALUES(active);
INSERT INTO promotion (promotion_id,promotion_name,coupon_code,discount_type,discount_value,minimum_order_amount,max_discount,description,free_shipping,active) VALUES ('FREESHIP', 'Miễn phí vận chuyển', 'FREESHIP', 'FIXED_AMOUNT', 0, 0, NULL, 'Miễn phí vận chuyển', 1, 1) ON DUPLICATE KEY UPDATE promotion_name=VALUES(promotion_name), coupon_code=VALUES(coupon_code), discount_type=VALUES(discount_type), discount_value=VALUES(discount_value), minimum_order_amount=VALUES(minimum_order_amount), max_discount=VALUES(max_discount), description=VALUES(description), free_shipping=VALUES(free_shipping), active=VALUES(active);
INSERT INTO promotion (promotion_id,promotion_name,coupon_code,discount_type,discount_value,minimum_order_amount,max_discount,description,free_shipping,active) VALUES ('WELCOME50', 'Chào mừng giảm 50.000đ', 'WELCOME50', 'FIXED_AMOUNT', 50000, 1000000, NULL, 'Giảm 50.000đ cho đơn từ 1.000.000đ', 0, 1) ON DUPLICATE KEY UPDATE promotion_name=VALUES(promotion_name), coupon_code=VALUES(coupon_code), discount_type=VALUES(discount_type), discount_value=VALUES(discount_value), minimum_order_amount=VALUES(minimum_order_amount), max_discount=VALUES(max_discount), description=VALUES(description), free_shipping=VALUES(free_shipping), active=VALUES(active);

-- Remove only previous sample-* rows; keep real rows.
DELETE FROM promotion_product WHERE promotion_id IN ('sample-promo-001', 'sample-promo-002', 'sample-promo-003', 'sample-promo-004', 'sample-promo-005');
DELETE FROM promotion_product WHERE product_id LIKE 'sample-product-%';
DELETE FROM promotion_customer_rank WHERE promotion_id IN ('sample-promo-001', 'sample-promo-002', 'sample-promo-003', 'sample-promo-004', 'sample-promo-005');
DELETE FROM review WHERE review_id LIKE 'sample-review-%' OR product_id LIKE 'sample-product-%';
DELETE FROM inventory_history WHERE inventory_history_id LIKE 'sample-inventory-%' OR product_id LIKE 'sample-product-%';
DELETE FROM deposit WHERE order_id LIKE 'sample-order-%';
DELETE FROM payment WHERE order_id LIKE 'sample-order-%';
DELETE FROM order_history WHERE order_id LIKE 'sample-order-%';
DELETE FROM order_line_item WHERE order_id LIKE 'sample-order-%';
DELETE FROM orders WHERE order_id LIKE 'sample-order-%';
DELETE FROM cart_line_item WHERE cart_id LIKE 'sample-cart-%' OR cart_line_item_id LIKE 'sample-cart-line-%' OR product_id LIKE 'sample-product-%';
DELETE FROM cart WHERE cart_id LIKE 'sample-cart-%';
DELETE FROM wishlist_item WHERE product_id LIKE 'sample-product-%';
DELETE FROM contact_message WHERE contact_id LIKE 'sample-contact-%';
DELETE FROM news_article WHERE article_id LIKE 'sample-news-%';
DELETE FROM admin_audit_log WHERE audit_id LIKE 'sample-audit-%';
DELETE FROM site_setting WHERE setting_id LIKE 'sample-setting-%';
DELETE FROM promotion WHERE promotion_id LIKE 'sample-promo-%';
DELETE FROM products WHERE product_id LIKE 'sample-product-%';
DELETE FROM brand WHERE brand_id IN ('brand-sony', 'brand-jbl', 'brand-bose', 'brand-audio-technica', 'brand-sennheiser');
DELETE FROM category WHERE category_id IN ('cat-headphones', 'cat-speakers', 'cat-microphones', 'cat-amplifiers', 'cat-accessories');

-- Customer ranks. Keep one row per rank name; the old rank-vip-demo duplicated VIP.
INSERT INTO customer_rank (rank_id,rank_name,min_spending) VALUES ('rank-bronze','BRONZE',100000000),('rank-silver','SILVER',150000000),('rank-gold','GOLD',250000000),('rank-vip','VIP',500000000) ON DUPLICATE KEY UPDATE rank_name=VALUES(rank_name),min_spending=VALUES(min_spending);

-- Insert demo admin only if its ID and email are both unused.
INSERT INTO users (user_id,full_name,email,password,phone,role,rank_id) SELECT 'admin-001','Quản trị viên','admin@soundora.vn','admin123','0900000000','ADMIN','rank-vip' WHERE NOT EXISTS (SELECT 1 FROM users WHERE user_id='admin-001' OR email='admin@soundora.vn');

-- Demo customer accounts.
INSERT INTO users (user_id,full_name,email,password,phone,role,rank_id) VALUES ('sample-user-002','Nguyễn Minh Anh','minhanh@example.com','user123','0900000002','USER','rank-bronze'),('sample-user-003','Trần Hoàng Nam','hoangnam@example.com','user123','0900000003','USER','rank-silver'),('sample-user-004','Lê Thu Hà','thuha@example.com','user123','0900000004','USER','rank-gold'),('sample-user-005','Phạm Quốc Bảo','quocbao@example.com','user123','0900000005','USER','rank-vip') ON DUPLICATE KEY UPDATE full_name=VALUES(full_name),email=VALUES(email),password=VALUES(password),phone=VALUES(phone),role=VALUES(role),rank_id=VALUES(rank_id);

INSERT INTO promotion (promotion_id, promotion_name, coupon_code, discount_type, discount_value, start_date_time, end_date_time) VALUES
('sample-promo-001', 'Ưu đãi khách hàng mới', 'SAMPLE10', 'PERCENTAGE', 10, DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY)),
('sample-promo-002', 'Tuần lễ âm thanh', 'JBL10', 'PERCENTAGE', 10, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 20 DAY)),
('sample-promo-003', 'Ưu đãi đàn kèn', 'WELCOME200K', 'FIXED_AMOUNT', 200000, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_ADD(NOW(), INTERVAL 15 DAY)),
('sample-promo-004', 'Ưu đãi thành viên vàng', 'GOLD12', 'PERCENTAGE', 12, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 25 DAY)),
('sample-promo-005', 'Ưu đãi cuối tháng', 'ENDMONTH150', 'FIXED_AMOUNT', 150000, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 35 DAY));

INSERT INTO site_setting (setting_id, free_shipping_from, shipping_fee, updated_at) VALUES
('sample-setting-2', 600000, 25000, NOW()),
('sample-setting-3', 700000, 20000, NOW()),
('sample-setting-4', 800000, 15000, NOW()),
('sample-setting-5', 900000, 10000, NOW());

INSERT INTO contact_message (contact_id, full_name, email, phone, subject, message, status, internal_note, created_at) VALUES
('sample-contact-001', 'Nguyễn Minh Anh', 'minhanh@example.com', '0900000002', 'Tư vấn guitar', 'Yamaha F310 có phù hợp cho người mới học guitar không?', 'Mới', NULL, DATE_SUB(NOW(), INTERVAL 4 DAY)),
('sample-contact-002', 'Trần Hoàng Nam', 'hoangnam@example.com', '0900000003', 'Tình trạng đơn hàng', 'Tôi muốn kiểm tra tiến độ giao cây guitar đã đặt.', 'Đang xử lý', 'Đã tiếp nhận yêu cầu.', DATE_SUB(NOW(), INTERVAL 3 DAY)),
('sample-contact-003', 'Lê Thu Hà', 'thuha@example.com', '0900000004', 'Đổi trả', 'Chính sách đổi trả đàn piano điện áp dụng trong bao lâu?', 'Mới', NULL, DATE_SUB(NOW(), INTERVAL 2 DAY)),
('sample-contact-004', 'Phạm Quốc Bảo', 'quocbao@example.com', '0900000005', 'Tư vấn violin', 'Nhờ tư vấn violin phù hợp cho học sinh mới bắt đầu.', 'Đã phản hồi', 'Đã gửi thông tin các mẫu violin học viên.', DATE_SUB(NOW(), INTERVAL 1 DAY)),
('sample-contact-005', 'Hoàng Yến', 'yen@example.com', '0900000006', 'Bảo hành', 'Thời gian bảo hành của ampli guitar là bao lâu?', 'Mới', NULL, NOW());

INSERT INTO news_article (article_id, title, category, excerpt, content, emoji, author, color, color2, published, published_at, created_at) VALUES
('sample-news-001', 'Cách chọn guitar acoustic cho người mới', 'Tư vấn', 'Những điểm cần xem trước khi mua guitar đầu tiên.', 'Chọn kích thước đàn phù hợp, kiểm tra độ cao dây và thử âm thanh trước khi mua.', '🎸', 'Soundora', '#273469', '#4f6dff', 1, DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY)),
('sample-news-002', 'Bảo quản đàn guitar đúng cách', 'Hướng dẫn', 'Giữ đàn ổn định trong điều kiện khí hậu nóng ẩm.', 'Cất đàn trong bao, tránh nắng trực tiếp và lau dây sau khi chơi để hạn chế gỉ.', '🎸', 'Soundora', '#164e63', '#22a6b3', 1, DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
('sample-news-003', 'Làm quen với piano điện', 'Công nghệ', 'Một số tính năng hữu ích khi tập đàn tại nhà.', 'Bàn phím có lực, metronome và cổng tai nghe giúp việc luyện tập thuận tiện hơn.', '🎹', 'Soundora', '#4c1d95', '#8b5cf6', 1, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
('sample-news-004', 'Chọn violin cho học sinh', 'Hướng dẫn', 'Chọn kích thước violin theo vóc dáng người chơi.', 'Đo chiều dài cánh tay và nhờ người có kinh nghiệm kiểm tra đàn trước khi mua.', '🎻', 'Soundora', '#7c2d12', '#f97316', 1, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
('sample-news-005', 'Bắt đầu tập trống tại nhà', 'Tư vấn', 'Chuẩn bị không gian và dụng cụ tập trống cơ bản.', 'Chọn vị trí phù hợp, dùng pad tập và bảo vệ tai khi luyện tập lâu.', '🥁', 'Soundora', '#14532d', '#22c55e', 0, NULL, NOW());

INSERT INTO admin_audit_log (audit_id, actor_email, action, detail, created_at) VALUES
('sample-audit-001', 'admin@soundora.vn', 'Tạo sản phẩm', 'Tạo sản phẩm nhạc cụ mẫu product_id 1', DATE_SUB(NOW(), INTERVAL 4 DAY)),
('sample-audit-002', 'admin@soundora.vn', 'Cập nhật danh mục', 'Cập nhật danh mục guitar', DATE_SUB(NOW(), INTERVAL 3 DAY)),
('sample-audit-003', 'admin@soundora.vn', 'Tạo khuyến mãi', 'Tạo chương trình SAMPLE10', DATE_SUB(NOW(), INTERVAL 2 DAY)),
('sample-audit-004', 'admin@soundora.vn', 'Phản hồi liên hệ', 'Đã phản hồi sample-contact-004', DATE_SUB(NOW(), INTERVAL 1 DAY)),
('sample-audit-005', 'admin@soundora.vn', 'Cập nhật cài đặt', 'Cập nhật phí giao hàng', NOW());

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
('sample-order-001', DATE_SUB(NOW(), INTERVAL 10 DAY), 3490000, 0, 3520000, '12 Nguyễn Huệ, Quận 1, TP.HCM', 'COMPLETED', 'Giao giờ hành chính', 30000, 'sample-user-002'),
('sample-order-002', DATE_SUB(NOW(), INTERVAL 7 DAY), 17900000, 0, 17930000, '20 Lê Lợi, Quận 1, TP.HCM', 'SHIPPING', NULL, 30000, 'sample-user-003'),
('sample-order-003', DATE_SUB(NOW(), INTERVAL 5 DAY), 124500000, 0, 124530000, '5 Trần Hưng Đạo, Quận 5, TP.HCM', 'CONFIRMED', NULL, 30000, 'sample-user-004'),
('sample-order-004', DATE_SUB(NOW(), INTERVAL 3 DAY), 19500000, 0, 19530000, '88 Điện Biên Phủ, Bình Thạnh, TP.HCM', 'PROCESSING', NULL, 30000, 'sample-user-005'),
('sample-order-005', DATE_SUB(NOW(), INTERVAL 1 DAY), 4780000, 0, 4810000, '7 Phan Chu Trinh, Hải Châu, Đà Nẵng', 'CANCELLED', 'Khách yêu cầu hủy', 30000, 'sample-user-002');

INSERT INTO order_line_item (order_line_item_id, product_id, unit_price, quantity, order_id) VALUES
('sample-order-line-001', '1', 3490000, 1, 'sample-order-001'),
('sample-order-line-002', '2', 17900000, 1, 'sample-order-002'),
('sample-order-line-003', '19', 24900000, 5, 'sample-order-003'),
('sample-order-line-004', '4', 19500000, 1, 'sample-order-004'),
('sample-order-line-005', '5', 2390000, 2, 'sample-order-005');

INSERT INTO order_history (history_id, order_id, status, changed_at, changed_by) VALUES
('sample-order-history-001', 'sample-order-001', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 8 DAY), 'admin@soundora.vn'),
('sample-order-history-002', 'sample-order-002', 'SHIPPING', DATE_SUB(NOW(), INTERVAL 5 DAY), 'admin@soundora.vn'),
('sample-order-history-003', 'sample-order-003', 'CONFIRMED', DATE_SUB(NOW(), INTERVAL 4 DAY), 'admin@soundora.vn'),
('sample-order-history-004', 'sample-order-004', 'PROCESSING', DATE_SUB(NOW(), INTERVAL 3 DAY), 'admin@soundora.vn'),
('sample-order-history-005', 'sample-order-005', 'CANCELLED', DATE_SUB(NOW(), INTERVAL 1 DAY), 'admin@soundora.vn');

INSERT INTO payment (payment_id, payment_method, amount, payment_date, payment_status, order_id) VALUES
('sample-payment-001', 'CASH', 3520000, DATE_SUB(NOW(), INTERVAL 8 DAY), 'COMPLETED', 'sample-order-001'),
('sample-payment-002', 'E_WALLET', 17930000, NULL, 'PENDING', 'sample-order-002'),
('sample-payment-003', 'E_WALLET', 124530000, NULL, 'PENDING', 'sample-order-003'),
('sample-payment-004', 'CASH', 19530000, NULL, 'PENDING', 'sample-order-004'),
('sample-payment-005', 'CASH', 4810000, NULL, 'REFUNDED', 'sample-order-005');

INSERT INTO deposit (deposit_id, amount, deposit_date, deposit_status, order_id) VALUES
('sample-deposit-003', 37359000, NULL, 'PENDING', 'sample-order-003');

INSERT INTO review (review_id, rating, comment, approved, user_id, product_id, order_id) VALUES
('sample-review-001', 5, 'Âm thanh rõ, dễ chơi cho người mới.', 1, 'sample-user-002', '1', 'sample-order-001'),
('sample-review-002', 4, 'Đàn chắc chắn, tiếng sáng và dễ chỉnh dây.', 1, 'sample-user-003', '2', 'sample-order-002'),
('sample-review-003', 5, 'Âm thanh tốt, hoàn thiện đẹp.', 0, 'sample-user-004', '19', 'sample-order-003'),
('sample-review-004', 4, 'Đàn nhỏ gọn, phù hợp luyện tập tại nhà.', 1, 'sample-user-005', '4', 'sample-order-004'),
('sample-review-005', 5, 'Giá hợp lý, âm thanh ấm.', 0, 'sample-user-002', '5', 'sample-order-005');

INSERT INTO inventory_history (inventory_history_id, product_id, operation, quantity_change, stock_before, stock_after, note, changed_by, changed_at) VALUES
('sample-inventory-001', '1', 'STOCK_IN', 12, 0, 12, 'Nhập tồn kho ban đầu', 'admin@soundora.vn', DATE_SUB(NOW(), INTERVAL 8 DAY)),
('sample-inventory-002', '2', 'STOCK_IN', 19, 0, 19, 'Nhập tồn kho ban đầu', 'admin@soundora.vn', DATE_SUB(NOW(), INTERVAL 7 DAY)),
('sample-inventory-003', '19', 'ORDER', -5, 18, 13, 'Xuất kho theo đơn sample-order-003', 'admin@soundora.vn', DATE_SUB(NOW(), INTERVAL 5 DAY)),
('sample-inventory-004', '4', 'STOCK_IN', 33, 0, 33, 'Nhập tồn kho ban đầu', 'admin@soundora.vn', DATE_SUB(NOW(), INTERVAL 3 DAY)),
('sample-inventory-005', '5', 'STOCK_IN', 10, 0, 10, 'Nhập tồn kho ban đầu', 'admin@soundora.vn', DATE_SUB(NOW(), INTERVAL 1 DAY));

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
('sample-promo-004', 'rank-vip'),
('sample-promo-005', 'rank-vip');

INSERT INTO site_setting (setting_id,free_shipping_from,shipping_fee,updated_at) VALUES ('default',500000,30000,NOW()) ON DUPLICATE KEY UPDATE free_shipping_from=VALUES(free_shipping_from),shipping_fee=VALUES(shipping_fee),updated_at=VALUES(updated_at);

INSERT IGNORE INTO site_content (content_key,json_data) VALUES ('soundora','{"site":{"name":"Soundora","freeShipFrom":500000,"shipFee":30000,"perPage":12},"categories":[],"brands":[],"products":[],"coupons":{},"news":[],"tiers":[],"infoPages":{}}');

COMMIT;

USE db_webnhac;

SET NAMES utf8mb4;

START TRANSACTION;

UPDATE products SET image = 'images/Yamaha F310 - Guitar Acoustic.jpg' WHERE product_id = '1';
UPDATE products SET image = 'images/Fender Player Stratocaster.webp' WHERE product_id = '2';
UPDATE products SET image = 'images/Ibanez GRG121DX - Guitar Điện.jpg' WHERE product_id = '3';
UPDATE products SET image = 'images/Taylor GS Mini Mahogany.webp' WHERE product_id = '4';
UPDATE products SET image = 'images/Cort AD810 - Guitar Acoustic.jpg' WHERE product_id = '5';
UPDATE products SET image = 'images/Yamaha P-125 - Piano Điện.webp' WHERE product_id = '6';
UPDATE products SET image = 'images/Roland FP-30X - Piano Điện.jpg' WHERE product_id = '7';
UPDATE products SET image = 'images/Casio CT-S300 - Organ.jpg' WHERE product_id = '8';
UPDATE products SET image = 'images/Casio CDP-S160 - Piano Điện Mỏng.webp' WHERE product_id = '9';
UPDATE products SET image = 'images/Yamaha PSR-E373 - Organ.jpg' WHERE product_id = '10';
UPDATE products SET image = 'images/Roland TD-1DMK - Trống Điện.jpg' WHERE product_id = '11';
UPDATE products SET image = 'images/Pearl Roadshow 5 Piece - Trống Cơ.gif' WHERE product_id = '12';
UPDATE products SET image = 'images/Zildjian S Family Cymbal Pack.webp' WHERE product_id = '13';
UPDATE products SET image = 'images/Kala KA-15S - Ukulele Soprano.jpg' WHERE product_id = '14';
UPDATE products SET image = 'images/Cort UKE Concert.jpg' WHERE product_id = '15';
UPDATE products SET image = 'images/Yamaha V3SKA - Violin 4-4.jpg' WHERE product_id = '16';
UPDATE products SET image = 'images/Stentor Student I - Violin 4-4.jpg' WHERE product_id = '17';
UPDATE products SET image = 'images/Yamaha YRS-24B - Sáo Recorder.jpg' WHERE product_id = '18';
UPDATE products SET image = 'images/Yamaha YAS-280 - Saxophone Alto.webp' WHERE product_id = '19';
UPDATE products SET image = 'images/Yamaha YTR-2330 - Trumpet.webp' WHERE product_id = '20';
UPDATE products SET image = 'images/Marshall MG15 - Ampli Guitar.jpg' WHERE product_id = '21';
UPDATE products SET image = 'images/Boss TU-3 - Pedal Chỉnh Dây.jpg' WHERE product_id = '22';
UPDATE products SET image = 'images/D''Addario EXL110 - Dây Guitar Điện.jpg' WHERE product_id = '23';
UPDATE products SET image = 'images/Dunlop Tortex - Phím Gảy (12 cái).jpg' WHERE product_id = '24';
UPDATE products SET image = 'images/Gibson Les Paul Studio.webp' WHERE product_id = '25';
UPDATE products SET image = 'images/Marshall Stanmore II - Loa Bluetooth.webp' WHERE product_id = '26';
UPDATE products SET image = 'images/Boss Katana-50 MkII - Ampli.jpg' WHERE product_id = '27';

SELECT product_id, product_name, image
FROM products
ORDER BY CAST(product_id AS UNSIGNED), product_id;

COMMIT;

CREATE INDEX idx_orders_user_date
    ON orders (user_id, order_date);

CREATE INDEX idx_order_line_order_product
    ON order_line_item (order_id, product_id);

CREATE INDEX idx_cart_line_cart_product
    ON cart_line_item (cart_id, product_id);

CREATE INDEX idx_inventory_history_changed_at
    ON inventory_history (changed_at);

CREATE INDEX idx_orders_status
    ON orders (order_status);

CREATE INDEX idx_products_category
	ON products (category_id);

CREATE INDEX idx_products_brand
	ON products (brand_id);