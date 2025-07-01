// Favorite Service - Quản lý chức năng yêu thích
app.service("favoriteService", function($http, $rootScope) {
    var self = this;
    this.favorites = [];
    this.favoriteCount = 0;
    
    // Load danh sách yêu thích từ server
    this.loadFavorites = function() {
        if (!$rootScope.$auth || !$rootScope.$auth.id) {
            this.favorites = [];
            this.favoriteCount = 0;
            return Promise.resolve([]);
        }

        return $http({
            method: 'GET',
            url: "/rest/favorites",
            timeout: 10000, // 10 second timeout
            cache: false
        }).then(resp => {
            if (resp.data.success) {
                self.favorites = resp.data.favorites;
                self.favoriteCount = resp.data.count;
                return self.favorites;
            }
            return [];
        }).catch(error => {
            // Only log error if it's not a cancellation
            if (error.status !== -1 && error.xhrStatus !== 'abort') {
                console.error("Error loading favorites:", error);
            }
            self.favorites = [];
            self.favoriteCount = 0;
            return [];
        });
    };

    // Kiểm tra sản phẩm có trong danh sách yêu thích không
    this.isFavorite = function(productId) {
        if (!$rootScope.$auth || !$rootScope.$auth.id) {
            return Promise.resolve(false);
        }

        return $http({
            method: 'GET',
            url: "/rest/favorites/check/" + productId,
            timeout: 5000, // 5 second timeout
            cache: false
        }).then(resp => {
            return resp.data.success ? resp.data.isFavorite : false;
        }).catch(error => {
            // Only log error if it's not a cancellation
            if (error.status !== -1 && error.xhrStatus !== 'abort') {
                console.error("Error checking favorite:", error);
            }
            return false;
        });
    };

    // Toggle trạng thái yêu thích
    this.toggleFavorite = function(productId) {
        if (!$rootScope.$auth || !$rootScope.$auth.id) {
            return Promise.reject({message: "Bạn cần đăng nhập để sử dụng tính năng này"});
        }

        return $http({
            method: 'POST',
            url: "/rest/favorites/toggle/" + productId,
            timeout: 10000, // 10 second timeout
            cache: false
        }).then(resp => {
            if (resp.data.success) {
                // Cập nhật lại danh sách favorites
                self.loadFavorites();
                return resp.data;
            }
            throw new Error(resp.data.message);
        });
    };

    // Xóa khỏi danh sách yêu thích
    this.removeFromFavorites = function(productId) {
        if (!$rootScope.$auth || !$rootScope.$auth.id) {
            return Promise.reject({message: "Bạn cần đăng nhập để sử dụng tính năng này"});
        }

        return $http({
            method: 'DELETE',
            url: "/rest/favorites/remove/" + productId,
            timeout: 10000, // 10 second timeout
            cache: false
        }).then(resp => {
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
    $rootScope.$on('authenticationLoaded', function() {
        self.loadFavorites();
    });

    $rootScope.$watch(function() {
        return $rootScope.$auth;
    }, function(newVal, oldVal) {
        if (newVal && newVal.id && (!oldVal || newVal.id !== oldVal.id)) {
            self.loadFavorites();
        } else if (!newVal) {
            self.favorites = [];
            self.favoriteCount = 0;
        }
    });
});

// Controller cho trang favorites
app.controller("favoriteCtrl", function($scope, $http, $rootScope) {
    // Khởi tạo danh sách favorites
    $scope.favorites = [];
    $scope.isLoading = false;

    // Load danh sách sản phẩm yêu thích
    $scope.loadFavorites = function() {
        if ($scope.isLoading) return; // Prevent multiple simultaneous requests
        
        $scope.isLoading = true;
        
        $http({
            method: 'GET',
            url: "/rest/favorites",
            timeout: 10000,
            cache: false
        }).then(resp => {
            if (resp.data.success) {
                $scope.favorites = resp.data.favorites;
            } else {
                console.log("API returned error:", resp.data.message);
            }
            $scope.isLoading = false;
        }).catch(error => {
            // Only log error if it's not a cancellation
            if (error.status !== -1 && error.xhrStatus !== 'abort') {
                console.error("Error loading favorites:", error);
            }
            $scope.isLoading = false;
        });
    };

    // Xóa sản phẩm khỏi danh sách yêu thích
    $scope.removeFromFavorites = function(productId) {
        if (confirm("Bạn có chắc muốn xóa sản phẩm này khỏi danh sách yêu thích?")) {
            $http({
                method: 'DELETE',
                url: "/rest/favorites/remove/" + productId,
                timeout: 10000,
                cache: false
            }).then(resp => {
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
                // Only show error if it's not a cancellation
                if (error.status !== -1 && error.xhrStatus !== 'abort') {
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
                }
            });
        }
    };

    // Thêm vào giỏ hàng - sử dụng cart service từ parent scope
    $scope.addToCart = function(productId) {
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
            
            $http({
                method: 'POST',
                url: "/rest/cart/" + $rootScope.$auth.id + "/add/" + productId,
                timeout: 10000,
                cache: false
            }).then(resp => {
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
                // Only show error if it's not a cancellation
                if (error.status !== -1 && error.xhrStatus !== 'abort') {
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
                }
            });
        }
    };

    // Load dữ liệu khi controller được khởi tạo
    $scope.loadFavorites();
});

// Directive cho nút yêu thích
app.directive("favoriteButton", function(favoriteService) {
    return {
        restrict: "E",
        template: `
            <a href="javascript:void(0);" class="add-wishlist" ng-click="toggleFavorite()" 
               ng-class="{'favorited': isFavorite, 'loading': isLoading}" ng-disabled="isLoading">
                <i class="zmdi zmdi-favorite{{isFavorite ? '' : '-outline'}} zmdi-hc-fw icon" 
                   ng-if="!isLoading"></i>
                <i class="zmdi zmdi-refresh zmdi-hc-spin zmdi-hc-fw icon" 
                   ng-if="isLoading"></i>
            </a>
        `,
        scope: {
            productId: "="
        },
        link: function(scope, element, attrs) {
            scope.isFavorite = false;
            scope.isLoading = false; // Add loading state to prevent rapid clicks
            
            // Kiểm tra trạng thái yêu thích ban đầu
            function checkFavoriteStatus() {
                if (scope.isLoading) return; // Don't check if already loading
                
                favoriteService.isFavorite(scope.productId).then(function(isFav) {
                    scope.isFavorite = isFav;
                    if (!scope.$$phase) {
                        scope.$apply();
                    }
                });
            }
            
            // Toggle yêu thích
            scope.toggleFavorite = function() {
                if (scope.isLoading) return; // Prevent rapid clicks
                
                scope.isLoading = true;
                favoriteService.toggleFavorite(scope.productId).then(function(resp) {
                    scope.isFavorite = resp.isFavorite;
                    scope.isLoading = false;
                    
                    // Show notification
                    Swal.fire({
                        icon: "success",
                        title: resp.message,
                        showConfirmButton: false,
                        timer: 2000,
                    });
                }).catch(function(error) {
                    scope.isLoading = false;
                    
                    // Only show error notification if it's not a cancellation
                    if (error.status !== -1 && error.xhrStatus !== 'abort') {
                        Swal.fire({
                            icon: "error",
                            title: error.message || "Có lỗi xảy ra",
                            showConfirmButton: false,
                            timer: 2000,
                        });
                    }
                });
            };
            
            // Load trạng thái ban đầu
            if (scope.productId) {
                checkFavoriteStatus();
            }

            // Watch cho thay đổi productId
            scope.$watch('productId', function(newVal) {
                if (newVal && !scope.isLoading) {
                    checkFavoriteStatus();
                }
            });
        }
    };
});

// Filter để kiểm tra favorite
app.filter('isFavorite', function(favoriteService) {
    return function(productId) {
        if (!favoriteService.favorites) return false;
        return favoriteService.favorites.some(function(product) {
            return product.id === productId;
        });
    };
});
