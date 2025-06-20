app = angular.module("admin-app", ["ngRoute"]);

app.config(function ($routeProvider) {
    $routeProvider
        .when("/dashboard", {
            templateUrl: "/admin/dashboard.html",
            controller: "dashboard-ctrl"
        })
        .when("/customer", {
            templateUrl: "/admin/customer/index.html",
            controller: "customer-ctrl"
        })
        .when("/product", {
            templateUrl: "/admin/product/index.html",
            controller: "product-ctrl"
        })
        .when("/category", {
            templateUrl: "/admin/category/index.html",
            controller: "category-ctrl"
        })
        .when("/authorize", {
            templateUrl: "/admin/authority/index.html",
            controller: "authority-ctrl"
        })
        .when("/unauthorized", {
            templateUrl: "/admin/authority/unauthorized.html",
            controller: "authority-ctrl"
        })
        .otherwise({
            templateUrl: "/admin/dashboard.html",
            controller: "dashboard-ctrl"
        });
});