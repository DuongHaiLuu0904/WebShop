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

--
-- Data for table `Customers`
--
INSERT INTO Customers (id, username, password, fullname, email, photo, token)
VALUES
    (1, 'admin', '123', 'Nguyễn Hoàng Duy', 'duyplusdz@gmail.com', 'user.png', 'token'),
    (2, 'staff', '123', 'Lê Thị Đậu', 'duyplus1999@gmail.com', 'user.png', 'token'),
    (3, 'user', '123', 'Nguyễn Văn Tèo', 'duyplus0909@gmail.com', 'user.png', 'token'),
    (4, 'anatr', 'anatr', 'Ana Trujillo', 'anatr@gmail.com', 'user.png', 'token'),
    (5, 'anton', 'anton', 'Antonio Moreno', 'anton@gmail.com', 'user.png', 'token'),
    (6, 'arout', 'arout', 'Thomas Hardy', 'arout@gmail.com', 'user.png', 'token'),
    (7, 'bergs', 'bergs', 'Christina Berglund', 'bergs@gmail.com', 'user.png', 'token'),
    (8, 'blaus', 'blaus', 'Hanna Moos', 'blaus@gmail.com', 'user.png', 'token'),
    (9, 'blonp', 'blonp', 'Frédérique Citeaux', 'blonp@gmail.com', 'user.png', 'token'),
    (10, 'bolid', 'bolid', 'Martín Sommer', 'bolid@gmail.com', 'user.png', 'token'),
    (11, 'bonap', 'bonap', 'Laurence Lebihan', 'bonap@gmail.com', 'user.png', 'token'),
    (12, 'bottm', 'bottm', 'Elizabeth Lincoln', 'bottm@gmail.com', 'user.png', 'token'),
    (13, 'bsbev', 'bsbev', 'Victoria Ashworth', 'bsbev@gmail.com', 'user.png', 'token'),
    (14, 'cactu', 'cactu', 'Patricio Simpson', 'cactu@gmail.com', 'user.png', 'token'),
    (15, 'centc', 'centc', 'Francisco Chang', 'centc@gmail.com', 'user.png', 'token'),
    (16, 'chops', 'chops', 'Yang Wang', 'chops@gmail.com', 'user.png', 'token'),
    (17, 'commi', 'commi', 'Pedro Afonso', 'commi@gmail.com', 'user.png', 'token'),
    (18, 'consh', 'consh', 'Elizabeth Brown', 'consh@gmail.com', 'user.png', 'token'),
    (19, 'dracd', 'dracd', 'Sven Ottlieb', 'dracd@gmail.com', 'user.png', 'token'),
    (20, 'dumon', 'dumon', 'Janine Labrune', 'dumon@gmail.com', 'user.png', 'token'),
    (21, 'eastc', 'eastc', 'Ann Devon', 'eastc@gmail.com', 'user.png', 'token'),
    (22, 'ernsh', 'ernsh', 'Roland Mendel', 'ernsh@gmail.com', 'user.png', 'token'),
    (23, 'famia', 'famia', 'Aria Cruz', 'famia@gmail.com', 'user.png', 'token'),
    (24, 'fissa', 'fissa', 'Diego Roel', 'fissa@gmail.com', 'user.png', 'token'),
    (25, 'folig', 'folig', 'Martine Rancé', 'folig@gmail.com', 'user.png', 'token');

--
-- Data for table `Roles`
--
INSERT INTO Roles (id, name)
VALUES
    ('DIRE', 'Directors'),
    ('STAF', 'Staffs'),
    ('CUST', 'Customers');

--
-- Data for table `Authorities`
--
INSERT INTO Authorities (id, customerId, roleId)
VALUES
    (1, 1, 'DIRE'),
    (2, 1, 'STAF'),
    (3, 1, 'CUST'),
    (4, 2, 'STAF'),
    (5, 2, 'CUST'),
    (6, 3, 'CUST');


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


--
-- Data for table `Products`
--
INSERT INTO Products (id, name, image, price, quantity, createDate, available, categoryId)
VALUES
    (1001, 'Aniseed Syrup', '1001.jpg', 190, 1, '1980-03-29', 0, '1000'),
    (1002, 'Change', '1002.jpg', 19, 3, '1982-12-18', 0, '1000'),
    (1003, 'Aniseed Syrup', '1003.jpg', 10, 5, '1973-06-14', 1, '1001'),
    (1004, 'Chef Cajun Seasoning', '1004.jpg', 22, 5, '1976-03-10', 0, '1001'),
    (1005, 'Chef Antons Gumbo Mix', '1005.jpg', 21.35, 5, '1978-12-06', 1, '1002'),
    (1006, 'Grandmas Boysenberry', '1006.jpg', 25, 2, '1981-09-03', 0, '1001'),
    (1007, 'Uncle Bobs Dried', '1007.jpg', 30, 2, '1983-03-13', 0, '1006'),
    (1008, 'Northwood Cranberry', '1008.jpg', 40, 2, '1985-09-20', 0, '1001'),
    (1009, 'Mishi Kobe Niku', '1009.jpg', 97, 29, '1987-06-06', 1, '1006'),
    (1010, 'Ikura', '1010.jpg', 31, 31, '1991-05-23', 0, '1007'),
    (1011, 'Queso Cabrales', '1011.jpg', 30, 5, '1989-07-06', 1, '1003'),
    (1012, 'Queso Manchego', '1012.jpg', 38, 86, '1993-12-08', 0, '1005'),
    (1013, 'Konbu', '1013.jpg', 6, 24, '1993-07-05', 1, '1004'),
    (1014, 'Tofu', '1014.jpg', 23.25, 35, '1995-12-09', 0, '1003'),
    (1015, 'Genen Shouyu', '1015.jpg', 15.5, 39, '1997-11-07', 1, '1002'),
    (1016, 'Pavlova', '1016.jpg', 17.45, 29, '1998-06-30', 0, '1002'),
    (1017, 'Alice Mutton', '1017.jpg', 39, 0, '1999-09-15', 1, '1003'),
    (1018, 'Carnarvon Tigers', '1018.jpg', 62.5, 42, '2001-11-24', 0, '1002'),
    (1019, 'Teatime Chocolate Biscuits', '1019.jpg', 9.2, 25, '2003-02-03', 1, '1002'),
    (1020, 'Sir Rodney Scones', '1020.jpg', 10, 0, '2005-12-24', 0, '1004'),
    (1021, 'Gustaf S Knackebrod', '1021.jpg', 21, 76, '2006-05-10', 1, '1002'),
    (1022, 'Tunnbrod', '1022.jpg', 9, 67, '2008-11-14', 0, '1003'),
    (1023, 'Guarana Fantastica', '1023.jpg', 4.5, 20, '2007-07-01', 1, '1006'),
    (1024, 'NuNuCa Nuß-Nougat', '1024.jpg', 14, 39, '2006-02-19', 0, '1004'),
    (1025, 'Gumbars', '1025.jpg', 31.23, 15, '2005-04-21', 1, '1003'),
    (1026, 'Schoggi Schokolade', '1026.jpg', 43.9, 49, '2006-08-17', 0, '1003'),
    (1027, 'Rosenblum Bratwurst', '1027.jpg', 17.45, 0, '2009-05-15', 1, '1002'),
    (1028, 'Thuringer Rostbratwurst', '1028.jpg', 12.5, 32, '2005-06-25', 0, '1006'),
    (1029, 'Sasquatch Ale', '1029.jpg', 14, 6, '2009-12-15', 1, '1005'),
    (1030, 'Steeleye Stout', '1030.jpg', 18, 55, '2007-01-19', 0, '1003'),
    (1031, 'Inlagd Sill', '1031.jpg', 19, 42, '2009-02-22', 1, '1005'),
    (1032, 'Gravad lax', '1032.jpg', 26, 21, '2008-03-10', 0, '1003'),
    (1033, 'Cote de Blaye', '1033.jpg', 263.5, 0, '2008-07-15', 1, '1003'),
    (1034, 'Chartreuse verte', '1034.jpg', 18, 25, '2006-12-20', 0, '1004'),
    (1035, 'Boston Crab Meat', '1035.jpg', 18.4, 123, '2009-11-15', 1, '1004'),
    (1036, 'Jack s New England Clam Chowder', '1036.jpg', 9.65, 85, '2009-04-18', 0, '1006'),
    (1037, 'Singaporean Hokkien Fried Mee', '1037.jpg', 12, 26, '2006-02-24', 1, '1005'),
    (1038, 'Ipoh Coffee', '1038.jpg', 46, 17, '2005-03-18', 0, '1004'),
    (1039, 'Gula Malacca', '1039.jpg', 19.45, 27, '2008-06-21', 1, '1006'),
    (1040, 'Rogede sild', '1040.jpg', 9.5, 5, '2008-08-10', 0, '1006'),
    (1041, 'Spegesild', '1041.jpg', 12, 95, '2009-07-01', 1, '1005'),
    (1042, 'Zaanse koeken', '1042.jpg', 9.5, 36, '2009-01-02', 0, '1003'),
    (1043, 'Chocolade', '1043.jpg', 12.75, 15, '2005-04-10', 1, '1003'),
    (1044, 'Maxilaku', '1044.jpg', 20, 10, '2008-10-04', 0, '1005'),
    (1045, 'Valkoinen suklaa', '1045.jpg', 16.25, 65, '2007-06-09', 1, '1002'),
    (1046, 'Manjar blanco', '1046.jpg', 39, 23, '2008-11-02', 0, '1004'),
    (1047, 'Gnocchi di nonna Alice', '1047.jpg', 38, 21, '2008-01-19', 1, '1003'),
    (1048, 'Ravioli Angelo', '1048.jpg', 19.5, 36, '2007-10-07', 0, '1004'),
    (1049, 'Escargots de Bourgogne', '1049.jpg', 13.25, 62, '2005-12-05', 1, '1005'),
    (1050, 'Raclette Courdavault', '1050.jpg', 55, 79, '2005-07-01', 0, '1004'),
    (1051, 'Camembert Pierrot', '1051.jpg', 34, 19, '2009-04-08', 1, '1003'),
    (1052, 'Sirop d erable', '1052.jpg', 28.5, 113, '2006-08-02', 0, '1004'),
    (1053, 'Tarte au sucre', '1053.jpg', 49.3, 17, '2008-06-19', 1, '1002'),
    (1054, 'Vegie-spread', '1054.jpg', 43.9, 24, '2006-10-10', 0, '1005'),
    (1055, 'Wimmers Koniglich', '1055.jpg', 12.6, 1, '2007-04-13', 1, '1006'),
    (1056, 'Mozzarella di Giovanni', '1056.jpg', 34.8, 14, '2009-05-06', 0, '1003'),
    (1057, 'Röd Kaviar', '1057.jpg', 36.5, 60, '2007-06-29', 1, '1005'),
    (1058, 'Longlife Tofu', '1058.jpg', 10, 4, '2008-11-21', 0, '1004'),
    (1059, 'White Clover Honey', '1059.jpg', 15.75, 52, '2009-07-13', 1, '1001'),
    (1060, 'Monte Carlo', '1060.jpg', 7.75, 1, '2007-09-02', 0, '1002'),
    (1061, 'Alice s Kitchen Apple Pie', '1061.jpg', 9.5, 12, '2006-01-30', 1, '1005'),
    (1062, 'Sirop d orgeat', '1062.jpg', 18, 5, '2009-06-20', 0, '1002'),
    (1063, 'Queen of Puddings', '1063.jpg', 18.4, 20, '2009-09-27', 1, '1006'),
    (1064, 'Wimmers Semmelknadel', '1064.jpg', 33.25, 7, '2009-05-15', 0, '1004'),
    (1065, 'Louisiana Fiery Sauce', '1065.jpg', 21.05, 7, '2008-05-15', 0, '1001'),
    (1066, 'Louisiana Spiced Okra', '1066.jpg', 17, 5, '2011-02-10', 1, '1001'),
    (1067, 'Laughing Lumberjack', '1067.jpg', 14, 5, '2010-12-05', 1, '1000'),
    (1068, 'Scottish Longbreads', '1068.jpg', 12.5, 5, '2009-07-08', 0, '1002'),
    (1069, 'Gudbrandsdalsost', '1069.jpg', 36, 5, '2011-03-09', 0, '1003'),
    (1070, 'Outback Lager', '1070.jpg', 15, 5, '2009-02-21', 0, '1000'),
    (1071, 'Flotemysost', '1071.jpg', 21.5, 5, '1980-09-04', 1, '1003'),
    (1072, 'Mozzarella Giovanni', '1072.jpg', 34.8, 5, '1983-06-03', 0, '1003'),
    (1073, 'Rad Kaviar', '1073.jpg', 15, 5, '1982-12-03', 0, '1007'),
    (1074, 'Longlife Tofu', '1074.jpg', 10, 5, '1982-09-27', 0, '1006'),
    (1075, 'RhanbrAu Klosterbier', '1075.jpg', 7.75, 5, '1982-10-31', 0, '1000'),
    (1076, 'Lakkalik Ari', '1076.jpg', 18, 5, '1970-07-28', 0, '1000'),
    (1077, 'Original Frankfurter', '1077.gif', 13, 5, '1976-04-04', 0, '1001'),
    (1081, 'Orable Chai', '1081.jpg', 19, 3, '1984-04-04', 0, '1000'),
    (1083, 'Mishi Kobe Niku', '1083.jpg', 97, 2, '1989-07-23', 0, '1005');

