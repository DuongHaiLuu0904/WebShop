-- Bảng người dùng
CREATE TABLE Customers (
   id INT AUTO_INCREMENT PRIMARY KEY,
   username VARCHAR(50) NOT NULL UNIQUE,
   password VARCHAR(50) NOT NULL,
   fullname VARCHAR(50) NOT NULL,
   email VARCHAR(50) NOT NULL,
   photo VARCHAR(500) NOT NULL DEFAULT 'https://res.cloudinary.com/djhidgxfo/image/upload/v1/images/user.png',
   token VARCHAR(50) NOT NULL
);

-- Bảng vai trò người dùng
CREATE TABLE Roles (
   id VARCHAR(10) NOT NULL,
   name VARCHAR(50) NOT NULL,
   CONSTRAINT PK_Roles PRIMARY KEY (id)
);

-- Bảng phân quyền người dùng
CREATE TABLE Authorities (
     id INT AUTO_INCREMENT NOT NULL,
     customerId INT NOT NULL,
     roleId VARCHAR(10) NOT NULL,
     CONSTRAINT PK_UserRoles PRIMARY KEY (id),
     CONSTRAINT FK_Authorities_Customers FOREIGN KEY (customerId) REFERENCES Customers (id) ON UPDATE CASCADE ON DELETE CASCADE,
     CONSTRAINT FK_Authorities_Roles FOREIGN KEY (roleId) REFERENCES Roles (id) ON UPDATE CASCADE ON DELETE CASCADE
);

-- Bảng danh mục sản phẩm
CREATE TABLE Categories (
    id CHAR(4) NOT NULL,
    name VARCHAR(50) NOT NULL,
    note VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    CONSTRAINT PK_Categories PRIMARY KEY (id)
);

-- Bảng sản phẩm
CREATE TABLE Products (
  id INT AUTO_INCREMENT NOT NULL,
  name VARCHAR(50) NOT NULL,
  image VARCHAR(500) NOT NULL DEFAULT 'https://res.cloudinary.com/djhidgxfo/image/upload/v1/images/cloud-upload.jpg',
  price FLOAT NOT NULL DEFAULT 0,
  quantity INT NOT NULL,
  createDate DATE NOT NULL DEFAULT (CURRENT_DATE),
  available BOOLEAN NOT NULL DEFAULT 1,
  categoryId CHAR(4) NOT NULL,
  CONSTRAINT PK_Products PRIMARY KEY (id),
  CONSTRAINT FK_Products_Categories FOREIGN KEY (categoryId) REFERENCES Categories (id) ON DELETE CASCADE
);

-- Bảng đơn hàng
CREATE TABLE Orders (
    id BIGINT AUTO_INCREMENT NOT NULL,
    customerId INT NOT NULL,
    createDate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    address VARCHAR(100) NOT NULL,
    paymentStatus VARCHAR(20) DEFAULT 'PENDING',
    vnpayTransactionId VARCHAR(100) NULL,
    totalAmount DOUBLE NULL,
    CONSTRAINT PK_Orders PRIMARY KEY (id),
    CONSTRAINT FK_Orders_Customers FOREIGN KEY (customerId) REFERENCES Customers (id) ON UPDATE CASCADE ON DELETE CASCADE
);

-- Bảng chi tiết đơn hàng
CREATE TABLE OrderDetails (
      id BIGINT AUTO_INCREMENT NOT NULL,
      orderId BIGINT NOT NULL,
      productId INT NOT NULL,
      price FLOAT NOT NULL DEFAULT 0,
      quantity INT NOT NULL DEFAULT 1,
      CONSTRAINT PK_OrderDetails PRIMARY KEY (id),
      CONSTRAINT FK_OrderDetails_Orders FOREIGN KEY (orderId) REFERENCES Orders (id) ON UPDATE CASCADE ON DELETE CASCADE,
      CONSTRAINT FK_OrderDetails_Products FOREIGN KEY (productId) REFERENCES Products (id) ON UPDATE CASCADE ON DELETE CASCADE
);

-- ✅ Bảng giỏ hàng (thêm mới)
CREATE TABLE CartItems (
    customerId INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    price FLOAT NOT NULL,
    added_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (customerId, product_id),
    FOREIGN KEY (customerId) REFERENCES Customers(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE ON UPDATE CASCADE
);


-- ✅ Bảng sản phẩm yêu thích
CREATE TABLE Favorites (
    customerId INT NOT NULL,
    productId INT NOT NULL,
    added_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (customerId, productId),
    FOREIGN KEY (customerId) REFERENCES Customers(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (productId) REFERENCES Products(id) ON DELETE CASCADE ON UPDATE CASCADE
);

--
-- Data for table `Roles`
--
INSERT INTO Roles (id, name)
VALUES
    ('DIRE', 'Directors'),
    ('STAF', 'Staffs'),
    ('CUST', 'Customers');

--
-- Data for table `Categories`
--
INSERT INTO Categories (id, name, note, description)
VALUES
    ('1000', 'Đồng hồ đeo tay', 'Đồng hồ', 'Đồng hồ đeo tay chất lượng cao'),
    ('1001', 'Máy tính xách tay', 'Máy tính', 'Các loại máy tính xách tay hiện đại'),
    ('1002', 'Máy ảnh', 'Máy ảnh', 'Máy ảnh chụp hình chuyên nghiệp và nghiệp dư'),
    ('1003', 'Điện thoại', 'Điện thoại', 'Điện thoại di động thông minh'),
    ('1004', 'Nước hoa', 'Nước hoa', 'Nước hoa thơm ngát các loại'),
    ('1005', 'Nữ trang', 'Nữ trang', 'Nữ trang sang trọng và tinh xảo'),
    ('1006', 'Nón thời trang', 'Nón', 'Nón thời trang đa dạng kiểu dáng'),
    ('1007', 'Túi xách du lịch', 'Túi xách', 'Túi xách du lịch bền bỉ và tiện lợi');

