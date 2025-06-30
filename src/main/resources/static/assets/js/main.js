const app = angular.module("shopping-app", []);
// Make app available globally for other scripts
window.shoppingApp = app;

app.run(function ($http, $rootScope) {
    $http.get(`/rest/auth/authentication`).then(resp => {
        if (resp.data && resp.data.user) {
            // Chuyển đổi cấu trúc để dễ sử dụng
            $auth = $rootScope.$auth = {
                id: resp.data.user.id, // Customer ID (integer)
                username: resp.data.user.username,
                user: resp.data.user,
                token: resp.data.token
            };
            $http.defaults.headers.common["Authorization"] = $auth.token;

            // Trigger cart and favorites load after authentication is set
            $rootScope.$broadcast('authenticationLoaded');
        }
    });
})

app.controller("shopping-ctrl", function ($scope, $http, $rootScope, $timeout, favoriteService) {
    var url = "/rest/products";
    var url1 = "/rest/orders";

    var sweetalert = function (text) {
        Swal.fire({
            icon: "success",
            title: text,
            showConfirmButton: false,
            timer: 2000,
        });
    }

    // Expose favorite service to scope
    $scope.favoriteService = favoriteService;
    
    // Quick favorite functions for scope
    $scope.toggleFavorite = function(productId) {
        return favoriteService.toggleFavorite(productId).then(function(resp) {
            sweetalert(resp.message);
            return resp;
        }).catch(function(error) {
            Swal.fire({
                icon: "error",
                title: error.message || "Có lỗi xảy ra",
                showConfirmButton: false,
                timer: 2000,
            });
        });
    };

    $scope.isFavorite = function(productId) {
        if (!$rootScope.$auth || !$rootScope.$auth.id) return false;
        if (!favoriteService.favorites) return false;
        return favoriteService.favorites.some(function(product) {
            return product.id === productId;
        });
    };

    // Quan ly gio hang
    $scope.cart = {
        items: [],
        count: 0,
        amount: 0,        // Them sp vao gio hang
        add(id) {
            // Kiểm tra authentication, nếu không có thì thử refresh từ server
            if (!$rootScope.$auth || !$rootScope.$auth.id) {
                $http.get(`/rest/auth/authentication`).then(resp => {
                    if (resp.data && resp.data.user) {
                        $rootScope.$auth = {
                            id: resp.data.user.id,
                            username: resp.data.user.username,
                            user: resp.data.user,
                            token: resp.data.token
                        };
                        $http.defaults.headers.common["Authorization"] = $rootScope.$auth.token;
                        this.add(id); // Retry
                    } else {
                        sweetalert("Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng!");
                        window.location.href = "/auth/login/form";
                    }
                }).catch(error => {
                    sweetalert("Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng!");
                    window.location.href = "/auth/login/form";
                });
                return;
            }

            $http.post(`/rest/cart/${$rootScope.$auth.id}/add/${id}`)
                .then(resp => {
                    sweetalert("Đã thêm sản phẩm vào giỏ hàng!");
                    this.loadFromServer();
                })
                .catch(error => {
                    sweetalert("Lỗi khi thêm sản phẩm vào giỏ hàng!");
                });
        },        // Them sp vao gio hang va chuyen den trang cart
        addAndGoToCart(id) {
            // Kiểm tra authentication, nếu không có thì thử refresh từ server
            if (!$rootScope.$auth || !$rootScope.$auth.id) {
                $http.get(`/rest/auth/authentication`).then(resp => {
                    if (resp.data && resp.data.user) {
                        $rootScope.$auth = {
                            id: resp.data.user.id,
                            username: resp.data.user.username,
                            user: resp.data.user,
                            token: resp.data.token
                        };
                        $http.defaults.headers.common["Authorization"] = $rootScope.$auth.token;
                        this.addAndGoToCart(id); // Retry
                    } else {
                        sweetalert("Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng!");
                        window.location.href = "/auth/login/form";
                    }
                }).catch(error => {
                    sweetalert("Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng!");
                    window.location.href = "/auth/login/form";
                });
                return;
            }

            $http.post(`/rest/cart/${$rootScope.$auth.id}/add/${id}`)
                .then(resp => {
                    sweetalert("Đã thêm sản phẩm vào giỏ hàng!");
                    this.loadFromServer();
                    // Chuyển đến trang giỏ hàng sau 1 giây
                    $timeout(function () {
                        window.location.href = "/cart/view";
                    }, 1000);
                })
                .catch(error => {
                    sweetalert("Lỗi khi thêm sản phẩm vào giỏ hàng!");
                });
        },        // Xoa sp khoi gio hang
        remove(id) {
            if (!$rootScope.$auth || !$rootScope.$auth.id) {
                return;
            }

            $http.delete(`/rest/cart/${$rootScope.$auth.id}/remove/${id}`)
                .then(resp => {
                    sweetalert("Đã xóa sản phẩm khỏi giỏ hàng!");
                    this.loadFromServer();
                })
                .catch(error => {
                    sweetalert("Lỗi khi xóa sản phẩm!");
                });
        },        // Cap nhat so luong sp trong gio hang
        updateQuantity(productId, quantity) {
            if (!$rootScope.$auth || !$rootScope.$auth.id) {
                return;
            }

            if (quantity <= 0) {
                this.remove(productId);
                return;
            }

            $http.put(`/rest/cart/${$rootScope.$auth.id}/update/${productId}?quantity=${quantity}`)
                .then(resp => {
                    this.loadFromServer();
                })
                .catch(error => {
                    sweetalert("Lỗi khi cập nhật số lượng!");
                    this.loadFromServer(); // Reload to revert changes
                });
        },        // Xoa sach sp trong gio hang
        clear() {
            if (!$rootScope.$auth || !$rootScope.$auth.id) {
                return;
            }

            $http.delete(`/rest/cart/${$rootScope.$auth.id}/clear`)
                .then(resp => {
                    sweetalert("Đã xóa tất cả sản phẩm trong giỏ hàng!");
                    this.loadFromServer();
                })
                .catch(error => {
                    sweetalert("Lỗi khi xóa giỏ hàng!");
                });
        },

        // Tinh thanh tien cua 1 sp
        amt_of(item) {
            return item.quantity * item.price;
        },

        // Tang so luong san pham
        increaseQuantity(productId) {
            var item = this.items.find(item => item.id == productId);
            if (item) {
                this.updateQuantity(productId, item.qty + 1);
            }
        },

        // Giam so luong san pham
        decreaseQuantity(productId) {
            var item = this.items.find(item => item.id == productId);
            if (item && item.qty > 1) {
                this.updateQuantity(productId, item.qty - 1);
            } else if (item && item.qty === 1) {
                this.remove(productId);
            }
        },        // Tai gio hang tu server
        loadFromServer() {
            if (!$rootScope.$auth || !$rootScope.$auth.id) {
                this.items = [];
                this.count = 0;
                this.amount = 0;
                return;
            }

            var cart = this;

            // Load cart items
            $http.get(`/rest/cart/${$rootScope.$auth.id}`)
                .then(resp => {
                    cart.items = resp.data.map(item => {
                        return {
                            id: item.product.id,
                            name: item.product.name,
                            image: item.product.image,
                            price: item.price,
                            qty: item.quantity,
                            quantity: item.quantity,
                            product: item.product
                        };
                    });

                    // Load cart count
                    return $http.get(`/rest/cart/${$rootScope.$auth.id}/count`);
                })
                .then(resp => {
                    cart.count = resp.data;

                    // Load cart amount
                    return $http.get(`/rest/cart/${$rootScope.$auth.id}/amount`);
                })
                .then(resp => {
                    cart.amount = resp.data;

                    // Force digest cycle if not already in progress
                    if (!$scope.$$phase) {
                        $scope.$apply();
                    }
                })
                .catch(error => {
                    cart.items = [];
                    cart.count = 0;
                    cart.amount = 0;
                });
        }
    }

    // Load gio hang khi khoi tao - nhưng chờ đến khi authentication được load
    $scope.$on('authenticationLoaded', function () {
        $scope.cart.loadFromServer();
    });

    // Nếu đã có auth thì load ngay
    if ($rootScope.$auth) {
        $scope.cart.loadFromServer();
    }    // Watch for authentication changes to reload cart
    $scope.$watch(function () {
        return $rootScope.$auth;
    }, function (newVal, oldVal) {
        if (newVal && newVal.id && (!oldVal || newVal.id !== oldVal.id)) {
            $scope.cart.loadFromServer();
        } else if (!newVal) {
            // User logged out, clear cart
            $scope.cart.items = [];
            $scope.cart.count = 0;
            $scope.cart.amount = 0;
        }
    });    // Thanh toán
    $scope.order = {
        get customer() {
            return { id: $rootScope.$auth ? $rootScope.$auth.id : null };
        },
        createDate: new Date(),
        address: "",
        get orderDetails() {
            return $scope.cart.items.map(item => {
                return {
                    product: { id: item.id },
                    price: item.price,
                    quantity: item.qty || item.quantity
                }
            });
        },        purchase() {
            if (!$rootScope.$auth || !$rootScope.$auth.id) {
                sweetalert("Vui lòng đăng nhập để đặt hàng!");
                return;
            }

            if ($scope.cart.items.length === 0) {
                sweetalert("Giỏ hàng trống!");
                return;
            }

            if (!this.address || this.address.trim() === "") {
                sweetalert("Vui lòng nhập địa chỉ giao hàng!");
                return;
            }

            // Lưu thông tin đơn hàng vào sessionStorage để sử dụng sau khi thanh toán thành công
            var orderData = {
                customer: { 
                    id: $rootScope.$auth.id,
                    username: $rootScope.$auth.username 
                },
                address: this.address,
                orderDetails: $scope.cart.items.map(item => {
                    return {
                        product: { id: item.id },
                        price: item.price,
                        quantity: item.qty || item.quantity
                    }
                }),
                totalAmount: $scope.cart.amount
            };
            
            sessionStorage.setItem('pendingOrder', JSON.stringify(orderData));
            console.log("Saved pending order data:", orderData);
              // Tạo yêu cầu thanh toán VNPAY với orderId tạm thời
            var tempOrderId = 'TEMP_' + Date.now();
            var paymentData = {
                orderId: tempOrderId,
                amount: Math.round($scope.cart.amount),
                orderInfo: "Thanh toan don hang",
                customerId: $rootScope.$auth.id
            };
            
            console.log("Sending payment data to VNPay:", paymentData);
            
            // Gọi API tạo URL thanh toán VNPAY
            $http.post("/vnpay/create-payment", paymentData).then(resp => {
                console.log("Payment response received:", resp.data);
                if (resp.data && resp.data.paymentUrl) {
                    console.log("Redirecting to payment URL:", resp.data.paymentUrl);
                    // Chuyển hướng đến trang thanh toán VNPAY
                    window.location.href = resp.data.paymentUrl;
                } else {
                    sweetalert("Không nhận được URL thanh toán từ VNPay");
                }
            }).catch(error => {
                console.log("Payment URL Error:", error);
                var errorMessage = "Lỗi tạo URL thanh toán";
                if (error.data && error.data.error) {
                    errorMessage += ": " + error.data.error;
                } else if (error.data) {
                    errorMessage += ": " + error.data;
                } else if (error.message) {
                    errorMessage += ": " + error.message;
                }
                sweetalert(errorMessage);
            });
        }
    }
})