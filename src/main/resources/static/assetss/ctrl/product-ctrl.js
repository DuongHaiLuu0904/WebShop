app.controller("product-ctrl", function ($scope, $http) {
    var url = "/rest/products";
    var url1 = "/rest/categories";
    var url2 = "/rest/upload/images";
    $scope.items = [];
    $scope.cates = [];
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
        //load product
        $http.get(url).then(resp => {
            $scope.items = resp.data;
            $scope.items.forEach(item => {
                item.createDate = new Date(item.createDate)
            })
        });

        //load categories
        $http.get(url1).then(resp => {
            $scope.cates = resp.data;
        });
    }

    //khoi dau
    $scope.initialize();

    // Search functionality
    $scope.searchText = '';
    
    // Function to get filtered items
    $scope.getFilteredItems = function() {
        if (!$scope.searchText) return $scope.items;
        
        var searchLower = $scope.searchText.toLowerCase();
        return $scope.items.filter(function(item) {
            return (item.name && item.name.toLowerCase().includes(searchLower)) ||
                   (item.id && item.id.toString().includes(searchLower)) ||
                   (item.price && item.price.toString().includes(searchLower)) ||
                   (item.category && item.category.name && item.category.name.toLowerCase().includes(searchLower));
        });
    };

    //xoa form
    $scope.reset = function () {
        $scope.form = {
            createDate: new Date(),
            image: 'https://res.cloudinary.com/djhidgxfo/image/upload/v1/images/cloud-upload.jpg',
            available: true,
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
            resp.data.createDate = new Date(resp.data.createDate)
            $scope.items.push(resp.data);
            $scope.reset();
            sweetalert("Thêm mới thành công!");
        }).catch(error => {
            sweetalert("Lỗi thêm mới sản phẩm!");
            console.log("Error", error);
        });
    }

    //cap nhat sp
    $scope.update = function () {
        var item = angular.copy($scope.form);
        $http.put(`${url}/${item.id}`, item).then(resp => {
            var index = $scope.items.findIndex(p => p.id == item.id);
            $scope.items[index] = item;
            $scope.reset();
            sweetalert("Cập nhật sản phẩm thành công!");
        }).catch(error => {
            sweetalert("Lỗi cập nhật sản phẩm!");
            console.log("Error", error);
        });
    }

    //xoa sp
    $scope.delete = function (item) {
        $http.delete(`${url}/${item.id}`).then(resp => {
            var index = $scope.items.findIndex(p => p.id == item.id);
            $scope.items.splice(index, 1);
            $scope.reset();
            sweetalert("Xóa sản phẩm thành công!");
        }).catch(error => {
            sweetalert("Lỗi xóa sản phẩm!");
            console.log("Error", error);
        });
    }    //upload hinh
    $scope.imageChanged = function (files) {
        var data = new FormData();
        data.append('file', files[0]);
        $http.post(url2, data, {
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined}
        }).then(resp => {
            // Use URL from Cloudinary response, fallback to old format
            $scope.form.image = resp.data.url;
        }).catch(error => {
            sweetalert("Lỗi tải lên hình ảnh!");
            console.log("Error", error);
        })
    }    //phan trang
    $scope.pager = {
        page: 0,
        size: 10,
        get items() {
            var filteredItems = $scope.getFilteredItems();
            var start = this.page * this.size;
            return filteredItems.slice(start, start + this.size);
        },
        get count() {
            var filteredItems = $scope.getFilteredItems();
            return Math.ceil(1.0 * filteredItems.length / this.size);
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