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
    ALTER TABLE dbo.contact_message ALTER COLUMN message NVARCHAR(4000) NOT NULL;
END;

COMMIT TRANSACTION;
