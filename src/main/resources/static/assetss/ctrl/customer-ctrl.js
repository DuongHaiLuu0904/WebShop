app.controller("customer-ctrl", function ($scope, $http) {
    var url = "/rest/customers";
    var url1 = "/rest/roles";
    var url2 = "/rest/upload/images";
    
    $scope.roles = [];
    $scope.items = [];
    $scope.form = {}; 
    
    var sweetalert = function (text) {
        Swal.fire({
            icon: "success",
            title: text,
            showConfirmButton: false,
            timer: 2000,
        });
    };   

    $scope.initialize = function () {
        //load customer
        $http.get(url).then(resp => {
            $scope.items = resp.data;
        }).catch(error => {
            console.error("Error loading customers:", error);
        });

        //load roles
        $http.get(url1).then(resp => {
            $scope.roles = resp.data;
        })
    }

    // Expose Object.keys to scope for template
    $scope.Object = window.Object;

    //khoi dau
    $scope.initialize();    
    $scope.reset = function () {
        $scope.form = {
            photo: 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750748027/cloud-upload_c6zitf.jpg', 
        };
    }    
    
    //hien thi len form
    $scope.edit = function (item) {
        $scope.form = angular.copy(item);
        if (!$scope.form.photo) {
            $scope.form.photo = null;
        }
        $(".nav-tabs a:eq(0)").tab('show');
    }

    //them sp moi
    $scope.create = function () {
        var item = angular.copy($scope.form);
        $http.post(`${url}`, item).then(resp => {
            resp.data.token = "token";
            $scope.items.push(resp.data);
            $scope.reset();
            sweetalert("Thêm mới thành công!");
        }).catch(error => {
            sweetalert("Lỗi thêm mới tài khoản!");
            console.log("Error", error);
        });
    }    
    
    //cap nhat sp
    $scope.update = function (skipReset) {
        var item = angular.copy($scope.form);
        $http.patch(`${url}/${item.id}`, item).then(resp => {
            var index = $scope.items.findIndex(p => p.id == item.id);
            $scope.items[index] = resp.data;

            if (!skipReset) {
                $scope.reset();
            }

            sweetalert("Cập nhật tài khoản thành công!");
        }).catch(error => {
            sweetalert("Lỗi cập nhật tài khoản!");
            console.log("Error", error);
        });
    }

    //xoa sp
    $scope.delete = function (item) {
        $http.delete(`${url}/${item.id}`).then(resp => {
            var index = $scope.items.findIndex(p => p.id == item.id);
            $scope.items.splice(index, 1);
            $scope.reset();
            sweetalert("Xóa tài khoản thành công!");
        }).catch(error => {
            sweetalert("Lỗi xóa tài khoản!");
            console.log("Error", error);
        });
    }    
    
    //upload hinh
    $scope.imageChanged = function (files) {
        if (!files || files.length === 0) {
            sweetalert("Vui lòng chọn file!");
            return;
        }

        var file = files[0];

        // Validate file type
        if (!file.type.startsWith('image/')) {
            sweetalert("Vui lòng chọn file hình ảnh!");
            return;
        }

        if (file.size > 5 * 1024 * 1024) {
            sweetalert("File quá lớn! Vui lòng chọn file nhỏ hơn 5MB!");
            return;
        }

        var data = new FormData();
        data.append('file', file);

        $http.post(url2, data, {
            transformRequest: angular.identity,
            headers: { 'Content-Type': undefined }
        })
        .then(resp => {
            if (resp.data && resp.data.url) {
                $scope.form.photo = resp.data.url;
                sweetalert("Tải lên hình ảnh thành công!");
                if ($scope.form.id) {
                    $scope.update(true); 
                }
            } else {
                sweetalert("Lỗi: Không nhận được URL ảnh từ server!");
            }
        })
        .catch(error => {
            sweetalert("Lỗi tải lên hình ảnh: " + (error.data ? error.data.message : error.statusText));
        })
    }

    //phan trang
    $scope.pager = {
        page: 0,
        size: 10,
        get items() {
            var start = this.page * this.size;
            return $scope.items.slice(start, start + this.size);
        },
        get count() {
            return Math.ceil(1.0 * $scope.items.length / this.size)
        },
        first() {
            this.page = 0;
        },
        prev() {
            this.page--;
            if (this.page < 0) {
                this.last();
            }
        },
        next() {
            this.page++;
            if (this.page >= this.count) {
                this.first();
            }
        },
        last() {
            this.page = this.count - 1;
        }
    }

});