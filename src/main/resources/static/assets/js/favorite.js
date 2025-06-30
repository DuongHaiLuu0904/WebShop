// Favorite Service - Quản lý chức năng yêu thích
app.service("favoriteService", function ($http, $rootScope) {
    var self = this;
    this.favorites = [];
    this.favoriteCount = 0;

    // Load danh sách yêu thích từ server
    this.loadFavorites = function () {
        if (!$rootScope.$auth || !$rootScope.$auth.id) {
            this.favorites = [];
            this.favoriteCount = 0;
            return Promise.resolve([]);
        }

        return $http.get("/rest/favorites").then(resp => {
            if (resp.data.success) {
                self.favorites = resp.data.favorites;
                self.favoriteCount = resp.data.count;
                return self.favorites;
            }
            return [];
        }).catch(error => {
            console.error("Error loading favorites:", error);
            self.favorites = [];
            self.favoriteCount = 0;
            return [];
        });
    };

    // Kiểm tra sản phẩm có trong danh sách yêu thích không
    this.isFavorite = function (productId) {
        if (!$rootScope.$auth || !$rootScope.$auth.id) {
            return Promise.resolve(false);
        }

        return $http.get("/rest/favorites/check/" + productId).then(resp => {
            return resp.data.success ? resp.data.isFavorite : false;
        }).catch(error => {
            console.error("Error checking favorite:", error);
            return false;
        });
    };

    // Toggle trạng thái yêu thích
    this.toggleFavorite = function (productId) {
        if (!$rootScope.$auth || !$rootScope.$auth.id) {
            return Promise.reject({
                message: "Bạn cần đăng nhập để sử dụng tính năng này"
            });
        }

        return $http.post("/rest/favorites/toggle/" + productId).then(resp => {
            if (resp.data.success) {
                // Cập nhật lại danh sách favorites
                self.loadFavorites();
                return resp.data;
            }
            throw new Error(resp.data.message);
        });
    };

    // Xóa khỏi danh sách yêu thích
    this.removeFromFavorites = function (productId) {
        if (!$rootScope.$auth || !$rootScope.$auth.id) {
            return Promise.reject({
                message: "Bạn cần đăng nhập để sử dụng tính năng này"
            });
        }

        return $http.delete("/rest/favorites/remove/" + productId).then(resp => {
            if (resp.data.success) {
                // Cập nhật lại danh sách favorites
                self.loadFavorites();
                return resp.data;
            }
            throw new Error(resp.data.message);
        });
    };

    // Load favorites khi service được khởi tạo
    if ($rootScope.$auth) {
        this.loadFavorites();
    }

    // Listen cho authentication changes và authenticationLoaded event
    $rootScope.$on('authenticationLoaded', function () {
        self.loadFavorites();
    });

    $rootScope.$watch(function () {
        return $rootScope.$auth;
    }, function (newVal, oldVal) {
        if (newVal && newVal.id && (!oldVal || newVal.id !== oldVal.id)) {
            self.loadFavorites();
        } else if (!newVal) {
            self.favorites = [];
            self.favoriteCount = 0;
        }
    });
});

// Directive cho nút yêu thích
app.directive("favoriteButton", function (favoriteService) {
    return {
        restrict: "E",
        template: `
            <a href="javascript:void(0);" class="add-wishlist" ng-click="toggleFavorite()" 
               ng-class="{'favorited': isFavorite}">
                <i class="zmdi zmdi-favorite{{isFavorite ? '' : '-outline'}} zmdi-hc-fw icon"></i>
            </a>
        `,
        scope: {
            productId: "="
        },
        link: function (scope, element, attrs) {
            scope.isFavorite = false;

            // Kiểm tra trạng thái yêu thích ban đầu
            function checkFavoriteStatus() {
                favoriteService.isFavorite(scope.productId).then(function (isFav) {
                    scope.isFavorite = isFav;
                    scope.$apply();
                });
            }

            // Toggle yêu thích
            scope.toggleFavorite = function () {
                favoriteService.toggleFavorite(scope.productId).then(function (resp) {
                    scope.isFavorite = resp.isFavorite;

                    // Show notification
                    Swal.fire({
                        icon: "success",
                        title: resp.message,
                        showConfirmButton: false,
                        timer: 2000,
                    });
                }).catch(function (error) {
                    // Show error notification
                    Swal.fire({
                        icon: "error",
                        title: error.message || "Có lỗi xảy ra",
                        showConfirmButton: false,
                        timer: 2000,
                    });
                });
            };

            // Load trạng thái ban đầu
            if (scope.productId) {
                checkFavoriteStatus();
            }

            // Watch cho thay đổi productId
            scope.$watch('productId', function (newVal) {
                if (newVal) {
                    checkFavoriteStatus();
                }
            });
        }
    };
});

// Filter để kiểm tra favorite
app.filter('isFavorite', function (favoriteService) {
    return function (productId) {
        if (!favoriteService.favorites) return false;
        return favoriteService.favorites.some(function (product) {
            return product.id === productId;
        });
    };
});

// Controller cho trang favorites
app.controller("favoriteCtrl", function ($scope, $http, $rootScope) {
    // Khởi tạo danh sách favorites
    $scope.favorites = [];

    // Load danh sách sản phẩm yêu thích
    $scope.loadFavorites = function () {
        $http.get("/rest/favorites").then(resp => {
            if (resp.data.success) {
                $scope.favorites = resp.data.favorites;
            }
        }).catch(error => {
            console.error("Error loading favorites:", error);
        });
    };

    // Xóa sản phẩm khỏi danh sách yêu thích
    $scope.removeFromFavorites = function (productId) {
        $http.delete("/rest/favorites/remove/" + productId).then(resp => {
            if (resp.data.success) {
                // Xóa khỏi danh sách hiển thị
                $scope.favorites = $scope.favorites.filter(p => p.id !== productId);

                // Show success notification
                if (typeof Swal !== 'undefined') {
                    Swal.fire({
                        icon: "success",
                        title: resp.data.message,
                        showConfirmButton: false,
                        timer: 2000,
                    });
                } else {
                    alert(resp.data.message);
                }
            } else {
                if (typeof Swal !== 'undefined') {
                    Swal.fire({
                        icon: "error",
                        title: resp.data.message,
                        showConfirmButton: false,
                        timer: 2000,
                    });
                } else {
                    alert(resp.data.message);
                }
            }
        }).catch(error => {
            console.error("Error removing from favorites:", error);
            if (typeof Swal !== 'undefined') {
                Swal.fire({
                    icon: "error",
                    title: "Có lỗi xảy ra khi xóa sản phẩm",
                    showConfirmButton: false,
                    timer: 2000,
                });
            } else {
                alert("Có lỗi xảy ra khi xóa sản phẩm");
            }
        });
    };

    // Thêm vào giỏ hàng - sử dụng cart service từ parent scope
    $scope.addToCart = function (productId) {
        // Kiểm tra nếu parent scope có cart service
        if ($scope.$parent && $scope.$parent.cart) {
            $scope.$parent.cart.add(productId);
        } else if ($rootScope.cart) {
            $rootScope.cart.add(productId);
        } else {
            // Fallback: gọi trực tiếp API cart
            if (!$rootScope.$auth || !$rootScope.$auth.id) {
                if (typeof Swal !== 'undefined') {
                    Swal.fire({
                        icon: "warning",
                        title: "Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng!",
                        showConfirmButton: false,
                        timer: 2000,
                    });
                } else {
                    alert("Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng!");
                }
                return;
            }

            $http.post("/rest/cart/" + $rootScope.$auth.id + "/add/" + productId).then(resp => {
                if (typeof Swal !== 'undefined') {
                    Swal.fire({
                        icon: "success",
                        title: "Đã thêm sản phẩm vào giỏ hàng!",
                        showConfirmButton: false,
                        timer: 2000,
                    });
                } else {
                    alert("Đã thêm sản phẩm vào giỏ hàng!");
                }
            }).catch(error => {
                if (typeof Swal !== 'undefined') {
                    Swal.fire({
                        icon: "error",
                        title: "Lỗi khi thêm sản phẩm vào giỏ hàng!",
                        showConfirmButton: false,
                        timer: 2000,
                    });
                } else {
                    alert("Lỗi khi thêm sản phẩm vào giỏ hàng!");
                }
            });
        }
    };

    // Load dữ liệu khi controller được khởi tạo
    $scope.loadFavorites();
});