-- Gán đường dẫn ảnh theo tên file hiện có trong src/main/webapp/images.
-- 4/4 trong tên sản phẩm được ghi là 4-4 vì dấu / không hợp lệ trong tên file.

BEGIN TRANSACTION;
UPDATE dbo.products SET image = N'images/Yamaha F310 - Guitar Acoustic.jpg' WHERE product_id = N'1';
UPDATE dbo.products SET image = N'images/Fender Player Stratocaster.webp' WHERE product_id = N'2';
UPDATE dbo.products SET image = N'images/Ibanez GRG121DX - Guitar Điện.jpg' WHERE product_id = N'3';
UPDATE dbo.products SET image = N'images/Taylor GS Mini Mahogany.webp' WHERE product_id = N'4';
UPDATE dbo.products SET image = N'images/Cort AD810 - Guitar Acoustic.jpg' WHERE product_id = N'5';
UPDATE dbo.products SET image = N'images/Yamaha P-125 - Piano Điện.webp' WHERE product_id = N'6';
UPDATE dbo.products SET image = N'images/Roland FP-30X - Piano Điện.jpg' WHERE product_id = N'7';
UPDATE dbo.products SET image = N'images/Casio CT-S300 - Organ.jpg' WHERE product_id = N'8';
UPDATE dbo.products SET image = N'images/Casio CDP-S160 - Piano Điện Mỏng.webp' WHERE product_id = N'9';
UPDATE dbo.products SET image = N'images/Yamaha PSR-E373 - Organ.jpg' WHERE product_id = N'10';
UPDATE dbo.products SET image = N'images/Roland TD-1DMK - Trống Điện.jpg' WHERE product_id = N'11';
UPDATE dbo.products SET image = N'images/Pearl Roadshow 5 Piece - Trống Cơ.gif' WHERE product_id = N'12';
UPDATE dbo.products SET image = N'images/Zildjian S Family Cymbal Pack.webp' WHERE product_id = N'13';
UPDATE dbo.products SET image = N'images/Kala KA-15S - Ukulele Soprano.jpg' WHERE product_id = N'14';
UPDATE dbo.products SET image = N'images/Cort UKE Concert.jpg' WHERE product_id = N'15';
UPDATE dbo.products SET image = N'images/Yamaha V3SKA - Violin 4-4.jpg' WHERE product_id = N'16';
UPDATE dbo.products SET image = N'images/Stentor Student I - Violin 4-4.jpg' WHERE product_id = N'17';
UPDATE dbo.products SET image = N'images/Yamaha YRS-24B - Sáo Recorder.jpg' WHERE product_id = N'18';
UPDATE dbo.products SET image = N'images/Yamaha YAS-280 - Saxophone Alto.webp' WHERE product_id = N'19';
UPDATE dbo.products SET image = N'images/Yamaha YTR-2330 - Trumpet.webp' WHERE product_id = N'20';
UPDATE dbo.products SET image = N'images/Marshall MG15 - Ampli Guitar.jpg' WHERE product_id = N'21';
UPDATE dbo.products SET image = N'images/Boss TU-3 - Pedal Chỉnh Dây.jpg' WHERE product_id = N'22';
UPDATE dbo.products SET image = N'images/D''Addario EXL110 - Dây Guitar Điện.jpg' WHERE product_id = N'23';
UPDATE dbo.products SET image = N'images/Dunlop Tortex - Phím Gảy (12 cái).jpg' WHERE product_id = N'24';
UPDATE dbo.products SET image = N'images/Gibson Les Paul Studio.webp' WHERE product_id = N'25';
UPDATE dbo.products SET image = N'images/Marshall Stanmore II - Loa Bluetooth.webp' WHERE product_id = N'26';
UPDATE dbo.products SET image = N'images/Boss Katana-50 MkII - Ampli.jpg' WHERE product_id = N'27';
SELECT product_id, product_name, image
FROM dbo.products
ORDER BY TRY_CONVERT(int, product_id), product_id;

COMMIT TRANSACTION;
