app.controller("dashboard-ctrl", function ($scope, $http) {
   
    // Khởi tạo dữ liệu
    $scope.productCount = "Loading...";
    $scope.categoryCount = "Loading...";
    $scope.customerCount = "Loading...";
    $scope.revenue = "Loading...";
    $scope.latestCustomer = null;

    // Lấy số lượng sản phẩm và tính toán doanh thu
    $http.get("/rest/products").then(resp => {
        $scope.productCount = resp.data.length;
        
        // Tính tổng doanh thu (giá * số lượng cho tất cả sản phẩm)
        var totalRevenue = 0;
        resp.data.forEach(product => {
            if (product.price && product.quantity) {
                totalRevenue += product.price * product.quantity;
            }
        });
        $scope.revenue = "$" + totalRevenue.toLocaleString('en-US', {minimumFractionDigits: 2});
    }).catch(error => {
        $scope.productCount = error;
        $scope.revenue = error;
    });

    // Lấy số lượng danh mục
    $http.get("/rest/categories").then(resp => {
        $scope.categoryCount = resp.data.length;
    }).catch(error => {
        $scope.categoryCount = error;
    });

    // Lấy số lượng khách hàng và khách hàng mới nhất
    $http.get("/rest/customers").then(resp => {
        $scope.customerCount = resp.data.length;
        if (resp.data.length > 0) {
            $scope.latestCustomer = resp.data[resp.data.length - 1];
        }
    }).catch(error => {
        $scope.customerCount = error;
    });
});
