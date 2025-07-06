# WebShop - E-commerce Application

## 📋 Mô tả dự án

WebShop là một ứng dụng thương mại điện tử đầy đủ tính năng được xây dựng bằng **Spring Boot 3.3.5** và **Java 17**. Dự án này cung cấp một nền tảng mua sắm trực tuyến hoàn chỉnh với giao diện người dùng thân thiện và hệ thống quản trị mạnh mẽ.

## 🏗️ Kiến trúc hệ thống

### Frontend
- **Thymeleaf** - Template engine cho server-side rendering
- **AngularJS** - Frontend framework cho admin panel
- **Bootstrap** - UI framework responsive
- **jQuery** - JavaScript library

### Backend
- **Spring Boot 3.3.5** - Main framework
- **Spring Security 6** - Authentication & Authorization
- **Spring Data JPA** - Data access layer
- **Spring Boot Actuator** - Monitoring & metrics

### Database
- **MySQL 8** - Primary database
- **Hibernate** - ORM framework

### External Services
- **Cloudinary** - Image storage & management
- **VNPay** - Payment gateway integration
- **Gmail SMTP** - Email service
- **Google OAuth2** - Social login

## 🚀 Tính năng chính

### 🛒 Customer Features
- **Đăng ký/Đăng nhập** - Hỗ trợ đăng nhập bằng Google OAuth2
- **Quên mật khẩu** - Reset password qua email
- **Tìm kiếm sản phẩm** - Search theo tên, category (hỗ trợ không dấu)
- **Giỏ hàng** - Add/remove/update sản phẩm
- **Danh sách yêu thích** - Lưu sản phẩm favorite
- **Đặt hàng** - Checkout process hoàn chỉnh
- **Thanh toán** - Tích hợp VNPay payment gateway
- **Theo dõi đơn hàng** - Xem lịch sử và trạng thái đơn hàng

### 👨‍💼 Admin Features
- **Dashboard** - Thống kê tổng quan
- **Quản lý khách hàng** - CRUD customers
- **Quản lý sản phẩm** - CRUD products với upload hình ảnh
- **Quản lý danh mục** - CRUD categories
- **Quản lý đơn hàng** - Xem và cập nhật order status
- **Phân quyền** - Role-based access control
- **Upload hình ảnh** - Tích hợp Cloudinary

### 🔐 Security Features
- **Spring Security 6** - Authentication & Authorization
- **JWT Token** - Session management
- **OAuth2** - Google social login
- **Password Encryption** - BCrypt hashing
- **CSRF Protection** - Cross-site request forgery protection
- **Role-based Access** - DIRE, STAF, CUST roles

## 📦 Cấu trúc dự án

```
WebShop/
├── src/
│   ├── main/
│   │   ├── java/com/ptit/
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── controller/      # Web controllers
│   │   │   ├── rest/           # REST API controllers
│   │   │   ├── service/        # Business logic
│   │   │   ├── dao/            # Data access objects
│   │   │   ├── entity/         # JPA entities
│   │   │   ├── dto/            # Data transfer objects
│   │   │   ├── util/           # Utility classes
│   │   │   └── interceptor/    # Request interceptors
│   │   └── resources/
│   │       ├── static/         # Static resources
│   │       │   ├── admin/      # Admin panel files
│   │       │   ├── assets/     # CSS, JS, images
│   │       │   └── assetss/    # AngularJS controllers
│   │       └── templates/      # Thymeleaf templates
│   └── test/                   # Test files
├── database.sql                # Database schema
├── pom.xml                     # Maven configuration
└── README.md                   # Project documentation
```

## 🛠️ Cài đặt và chạy dự án

### Yêu cầu hệ thống
- **Java 17** 
- **Maven 3.6+**
- **MySQL 8.0+**

### 1. Clone repository
```bash
git clone https://github.com/your-username/WebShop.git
cd WebShop
```

### 2. Cài đặt database
```bash
# Tạo database
mysql -u root -p
CREATE DATABASE shopweb;
USE shopweb;

# Import database schema
mysql -u root -p shopweb < database.sql
```

### 3. Cấu hình environment variables
Tạo file `.env` trong thư mục root:
```env
# Database
DB_PASSWORD=your_mysql_password

# Mail Configuration
MAIL_USERNAME=your_gmail@gmail.com
MAIL_PASSWORD=your_app_password

# Google OAuth2
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret

# VNPay
VNPAY_TMN_CODE=your_vnpay_tmn_code
VNPAY_HASH_SECRET=your_vnpay_hash_secret

# Cloudinary
CLOUD_NAME=your_cloudinary_name
CLOUD_KEY=your_cloudinary_key
CLOUD_SECRET=your_cloudinary_secret
```

### 4. Chạy ứng dụng
```bash
# Cài đặt dependencies
mvn clean install

# Chạy application
mvn spring-boot:run
```

Hoặc sử dụng VS Code task:
```bash
# Trong VS Code, nhấn Ctrl+Shift+P và chọn "Tasks: Run Task"
# Chọn "Run Spring Boot Application"
```

### 5. Truy cập ứng dụng
- **Website**: http://localhost:8080
- **Admin Panel**: http://localhost:8080/admin
- **API Documentation**: http://localhost:8080/actuator

## 🔧 Cấu hình chi tiết

### Database Configuration
```properties
# MySQL Connection
spring.datasource.url=jdbc:mysql://localhost:3306/shopweb?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=none
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.show-sql=false
```

### Security Configuration
```java
// OAuth2 Login
spring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID}
spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET}
```

### File Upload Configuration
```properties
# MULTIPART
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=20MB
spring.servlet.multipart.max-request-size=50MB
```

## 📊 Database Schema

### Main Tables
- **Customers** - User accounts và profiles
- **Products** - Product catalog
- **Categories** - Product categories
- **Orders** - Order information
- **OrderDetails** - Order line items
- **CartItems** - Shopping cart items
- **Favorites** - User favorites
- **Roles** - User roles (DIRE, STAF, CUST)
- **Authorities** - User permissions

### Key Relationships
- Customer → Orders (One-to-Many)
- Order → OrderDetails (One-to-Many)
- Product → Category (Many-to-One)
- Customer → CartItems (One-to-Many)
- Customer → Favorites (One-to-Many)

## 🔌 API Endpoints

### Authentication
- `POST /auth/login` - User login
- `POST /auth/register` - User registration
- `GET /auth/logout` - User logout
- `POST /auth/forgot-password` - Password reset

### Products
- `GET /rest/products` - Get all products
- `GET /rest/products/{id}` - Get product by ID
- `POST /rest/products` - Create new product
- `PUT /rest/products/{id}` - Update product
- `DELETE /rest/products/{id}` - Delete product

### Cart
- `GET /rest/cart/{customerId}` - Get cart items
- `POST /rest/cart/{customerId}/add/{productId}` - Add to cart
- `PUT /rest/cart/{customerId}/update/{productId}` - Update cart item
- `DELETE /rest/cart/{customerId}/remove/{productId}` - Remove from cart

### Orders
- `GET /rest/orders` - Get all orders
- `POST /rest/orders` - Create new order
- `GET /rest/orders/{id}` - Get order by ID
- `PUT /rest/orders/{id}/payment-status` - Update payment status

### Favorites
- `GET /rest/favorites` - Get user favorites
- `POST /rest/favorites` - Add to favorites
- `DELETE /rest/favorites/{productId}` - Remove from favorites

## 💳 Payment Integration

### VNPay Configuration
```properties
# VNPay Settings
vnpay.tmn-code=${VNPAY_TMN_CODE}
vnpay.hash-secret=${VNPAY_HASH_SECRET}
vnpay.url=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html
vnpay.return-url=http://localhost:8080/vnpay/return
vnpay.ipn-url=http://localhost:8080/vnpay/ipn
```

### Payment Flow
1. Customer proceeds to checkout
2. Order is created with PENDING status
3. VNPay payment URL is generated
4. Customer completes payment on VNPay
5. VNPay redirects back with payment result
6. Order status is updated to PAID/FAILED

## 📧 Email Configuration

### Gmail SMTP Setup
```properties
# Mail Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Email Features
- **Password Reset** - Send reset link via email
- **Order Confirmation** - Send order details
- **Registration Welcome** - Welcome new users

## 🖼️ Image Management

### Cloudinary Integration
```properties
# Cloudinary Configuration
cloud.name=${CLOUD_NAME}
cloud.key=${CLOUD_KEY}
cloud.secret=${CLOUD_SECRET}
```

### Upload Features
- **Product Images** - Upload via admin panel
- **User Avatars** - Profile picture upload
- **Automatic Optimization** - Cloudinary auto-optimization
- **CDN Delivery** - Fast image delivery

## 🔍 Search Features

### Product Search
- **Keyword Search** - Search by product name
- **Category Filter** - Filter by category
- **Accent Insensitive** - Vietnamese accent support
- **Combined Search** - Keyword + Category

### Search Implementation
```java
// Vietnamese accent-insensitive search
@Query("SELECT p FROM Product p WHERE " +
       "LOWER(REPLACE(REPLACE(REPLACE(p.name, 'á', 'a'), 'à', 'a'), 'ạ', 'a')) " +
       "LIKE LOWER(CONCAT('%', :keyword, '%'))")
List<Product> findByNameIgnoringAccents(@Param("keyword") String keyword);
```

## 📱 Responsive Design

### Frontend Technologies
- **Bootstrap 5** - Mobile-first responsive design
- **jQuery** - DOM manipulation
- **AngularJS** - Admin panel SPA
- **Thymeleaf** - Server-side templating

### Responsive Features
- **Mobile Navigation** - Collapsible menu
- **Product Grid** - Responsive product cards
- **Cart Management** - Mobile-optimized cart
- **Touch-friendly** - Mobile gesture support

## 🚀 Deployment

### Production Checklist
1. **Database Setup**
   - Create production database
   - Run database migrations
   - Configure connection pooling

2. **Environment Variables**
   - Set production environment variables
   - Configure SSL certificates
   - Set up monitoring

3. **Application Configuration**
   - Set `spring.profiles.active=prod`
   - Configure logging levels
   - Enable compression

4. **Security**
   - Enable HTTPS
   - Configure CORS policies
   - Set up rate limiting

### Docker Deployment
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/SOF306-ASM-0.0.1.war app.war
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.war"]
```

## 🧪 Testing

### Test Structure
```
src/test/java/com/ptit/
├── controller/     # Controller tests
├── service/        # Service layer tests
├── repository/     # Repository tests
└── integration/    # Integration tests
```

### Running Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ProductServiceTest

# Run with coverage
mvn test jacoco:report
```

## 📚 Documentation

### Code Documentation
- **Javadoc** - API documentation
- **Swagger** - REST API documentation
- **Database Schema** - ERD diagrams

### User Documentation
- **User Manual** - Customer features
- **Admin Guide** - Admin panel usage
- **API Reference** - Developer guide

## 🤝 Contributing

### Development Workflow
1. Fork the repository
2. Create feature branch: `git checkout -b feature/new-feature`
3. Commit changes: `git commit -m 'Add new feature'`
4. Push to branch: `git push origin feature/new-feature`
5. Submit pull request

### Code Standards
- **Java Coding Standards** - Follow Google Java Style Guide
- **Commit Messages** - Use conventional commits
- **Documentation** - Update README for new features
- **Testing** - Write unit tests for new code

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

- **Student Name** - Dương Hải Lưu
- **Course** - SOF306 - Java Web Programming
- **Assignment** - Final Project

## 📞 Support

For support and questions:
- **Email**: hailuu0904@gmail.com
---

**WebShop** - A complete e-commerce solution built with Spring Boot 💻🛒
