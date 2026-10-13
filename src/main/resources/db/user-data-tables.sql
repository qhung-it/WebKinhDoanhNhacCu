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
        message NVARCHAR(4000) NOT NULL,
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
