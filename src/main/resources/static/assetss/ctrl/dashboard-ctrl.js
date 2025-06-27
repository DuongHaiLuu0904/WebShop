app.controller("dashboard-ctrl", function ($scope, $http) {
    console.log("Dashboard controller loaded");
    
    // Initialize data
    $scope.productCount = "Loading...";
    $scope.categoryCount = "Loading...";
    $scope.customerCount = "Loading...";
    $scope.revenue = "Loading...";
    $scope.latestCustomer = null;

    // Fetch product count and calculate revenue
    $http.get("/rest/products").then(resp => {
        console.log("Products loaded:", resp.data.length);
        $scope.productCount = resp.data.length;
        
        // Calculate total revenue (price * quantity for all products)
        var totalRevenue = 0;
        resp.data.forEach(product => {
            if (product.price && product.quantity) {
                totalRevenue += product.price * product.quantity;
            }
        });
        $scope.revenue = "$" + totalRevenue.toLocaleString('en-US', {minimumFractionDigits: 2});
    }).catch(error => {
        $scope.productCount = "Error";
        $scope.revenue = "Error";
    });

    // Fetch category count
    $http.get("/rest/categories").then(resp => {
        $scope.categoryCount = resp.data.length;
    }).catch(error => {
        $scope.categoryCount = "Error";
    });

    // Fetch customer count and latest customer
    $http.get("/rest/customers").then(resp => {
        $scope.customerCount = resp.data.length;
        if (resp.data.length > 0) {
            $scope.latestCustomer = resp.data[resp.data.length - 1];
        }
    }).catch(error => {
        $scope.customerCount = "Error";
    });
});
