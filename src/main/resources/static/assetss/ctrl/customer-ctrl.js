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
    }

    $scope.initialize = function () {
        //load customer
        $http.get(url).then(resp => {
            $scope.items = resp.data;
        });

        //load roles
        $http.get(url1).then(resp => {
            $scope.roles = resp.data;
        })
    }

    //khoi dau
    $scope.initialize();

    //xoa form
    $scope.reset = function () {
        $scope.form = {
            image: 'cloud-upload.jpg',
        };
    }

    //hien thi len form
    $scope.edit = function (item) {
        $scope.form = angular.copy(item);
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
    $scope.update = function () {
        var item = angular.copy($scope.form);
        $http.put(`${url}/${item.username}`, item).then(resp => {
            var index = $scope.items.findIndex(p => p.username == item.username);
            $scope.items[index] = item;
            $scope.reset();
            sweetalert("Cập nhật tài khoản thành công!");
        }).catch(error => {
            sweetalert("Lỗi cập nhật tài khoản!");
            console.log("Error", error);
        });
    }

    //xoa sp
    $scope.delete = function (item) {
        $http.delete(`${url}/${item.username}`).then(resp => {
            var index = $scope.items.findIndex(p => p.username == item.username);
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
        var data = new FormData();
        data.append('file', files[0]);
        $http.post(url2, data, {
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined}
        }).then(resp => {
            $scope.form.image = resp.data.name;
        }).catch(error => {
            sweetalert("Lỗi tải lên hình ảnh!");
            console.log("Error", error);
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