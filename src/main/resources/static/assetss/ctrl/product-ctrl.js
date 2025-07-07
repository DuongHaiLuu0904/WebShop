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
        //tải danh sách sản phẩm
        $http.get(url).then(resp => {
            $scope.items = resp.data;
            $scope.items.forEach(item => {
                item.createDate = new Date(item.createDate)
            })
        });

        //tải danh mục
        $http.get(url1).then(resp => {
            $scope.cates = resp.data;
        });
    }

    //khởi đầu
    $scope.initialize();

    // Chức năng tìm kiếm
    $scope.searchText = '';
    
    // Hàm lấy danh sách đã lọc
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

    //xóa form
    $scope.reset = function () {
        $scope.form = {
            createDate: new Date(),
            image: 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750748027/cloud-upload_c6zitf.jpg',
            available: true,
        };
        $scope.selectedFile = null; // Xóa file đã chọn
        
        // Xóa input file
        var fileInput = document.getElementById('image');
        if (fileInput) {
            fileInput.value = '';
            if (fileInput.nextElementSibling) {
                fileInput.nextElementSibling.innerText = 'Choose file';
            }
        }
    }

    //hiển thị lên form
    $scope.edit = function (item) {
        $scope.form = angular.copy(item);
        $scope.selectedFile = null; // Xóa file đã chọn khi chỉnh sửa mục hiện có
        
        // Xóa input file
        var fileInput = document.getElementById('image');
        if (fileInput) {
            fileInput.value = '';
            if (fileInput.nextElementSibling) {
                fileInput.nextElementSibling.innerText = 'Choose file';
            }
        }
        
        $(".nav-tabs a:eq(0)").tab('show');
    }

    //thêm sp mới
    $scope.create = function () {
        // Kiểm tra các trường bắt buộc
        if (!$scope.form.name || !$scope.form.price || !$scope.form.category || !$scope.form.category.id) {
            sweetalert("Vui lòng điền đầy đủ thông tin!");
            return;
        }
        
        // Tải lên hình ảnh nếu có file mới được chọn, nếu không thì sử dụng hình mặc định hoặc hiện tại
        $scope.uploadImage().then(uploadResult => {
            var item = angular.copy($scope.form);
            item.image = uploadResult.url; // Sử dụng URL hình ảnh đã tải lên hoặc mặc định
            item.public_id = uploadResult.publicId; // Lưu public_id
            
            // Đảm bảo không bao gồm ID cho thao tác tạo mới
            delete item.id;
            
            $http.post(`${url}`, item).then(resp => {
                resp.data.createDate = new Date(resp.data.createDate)
                $scope.items.push(resp.data);
                $scope.reset();
                sweetalert("Thêm mới thành công!");
            }).catch(error => {
                sweetalert("Lỗi thêm mới sản phẩm!");
                console.log("Error", error);
            });
        }).catch(error => {
            sweetalert("Lỗi tải lên hình ảnh!");
            console.log("Upload Error", error);
        });
    }

    //cập nhật sp
    $scope.update = function () {
        if (!$scope.form.id) {
            sweetalert("Không thể cập nhật sản phẩm không có ID!");
            return;
        }
        
        var oldProduct = $scope.items.find(p => p.id == $scope.form.id);
        var oldPublicId = oldProduct ? oldProduct.public_id : null;
        
        // Tải lên hình ảnh trước nếu có file mới được chọn
        $scope.uploadImage().then(uploadResult => {
            var item = angular.copy($scope.form);
            item.image = uploadResult.url; RL
            item.public_id = uploadResult.publicId; 
            
            $http.put(`${url}/${item.id}`, item).then(resp => {
                var index = $scope.items.findIndex(p => p.id == item.id);
                $scope.items[index] = item;
                
                // Xóa hình ảnh cũ khỏi Cloudinary nếu tồn tại và khác với hình mới
                if (oldPublicId && oldPublicId !== uploadResult.publicId && 
                    oldPublicId !== 'cloud-upload_c6zitf' && !oldPublicId.includes('default')) {
                    $scope.deleteImageFromCloudinary(oldPublicId);
                }
                
                $scope.reset();
                sweetalert("Cập nhật sản phẩm thành công!");
            }).catch(error => {
                sweetalert("Lỗi cập nhật sản phẩm!");
                console.log("Error", error);
            });
        }).catch(error => {
            sweetalert("Lỗi tải lên hình ảnh!");
            console.log("Upload Error", error);
        });
    }

    //xóa sp
    $scope.delete = function (item) {
        // Lưu public_id trước khi xóa
        var publicIdToDelete = item.public_id;
        
        $http.delete(`${url}/${item.id}`).then(resp => {
            var index = $scope.items.findIndex(p => p.id == item.id);
            $scope.items.splice(index, 1);
            $scope.reset();
            
            // Xóa hình ảnh khỏi Cloudinary nếu tồn tại và không phải hình mặc định
            if (publicIdToDelete && publicIdToDelete !== 'cloud-upload_c6zitf' && 
                !publicIdToDelete.includes('default')) {
                $scope.deleteImageFromCloudinary(publicIdToDelete);
            }
            
            sweetalert("Xóa sản phẩm thành công!");
        }).catch(error => {
            sweetalert("Lỗi xóa sản phẩm!");
            console.log("Error", error);
        });
    }    
    
    // Lưu file đã chọn để tải lên sau
    $scope.selectedFile = null;
    
    //xem trước hình khi chọn file
    $scope.imageChanged = function (files) {
        if (!files || files.length === 0) {
            return;
        }
        
        var file = files[0];
        
        // Kiểm tra loại file
        if (!file.type.startsWith('image/')) {
            sweetalert("Vui lòng chọn file hình ảnh!");
            return;
        }
        
        if (file.size >= 10 * 1024 * 1024) {
            sweetalert("File quá lớn! Vui lòng chọn file nhỏ hơn 10MB!");
            return;
        }
        
        // Lưu file để tải lên sau
        $scope.selectedFile = file;
        
        // Tạo URL xem trước
        var reader = new FileReader();
        reader.onload = function(e) {
            $scope.form.image = e.target.result;
            $scope.$apply(); // Kích hoạt chu kỳ digest vì đây là bên ngoài Angular
        };
        reader.readAsDataURL(file);
    }
    
    // Tải hình ảnh lên cloudinary
    $scope.uploadImage = function() {
        return new Promise((resolve, reject) => {
            if (!$scope.selectedFile) {
                // Nếu không có file mới nào được chọn, kiểm tra nếu hình hiện tại là URL hợp lệ hoặc base64
                if ($scope.form.image && $scope.form.image.startsWith('http')) {
                    // Đã là URL Cloudinary hợp lệ, trả về nó với public_id hiện có
                    resolve({
                        url: $scope.form.image,
                        publicId: $scope.form.public_id || null
                    });
                } else {
                    // Nếu là base64 hoặc không hợp lệ, sử dụng hình mặc định
                    resolve({
                        url: 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750748027/cloud-upload_c6zitf.jpg',
                        publicId: 'cloud-upload_c6zitf'
                    });
                }
                return;
            }
            
            var data = new FormData();
            data.append('file', $scope.selectedFile);
            
            $http.post(url2, data, {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
            })
            .then(resp => {
                $scope.selectedFile = null; // Xóa file đã chọn sau khi tải lên
                resolve({
                    url: resp.data.url,
                    publicId: resp.data.publicId
                });
            })
            .catch(error => {
                console.log("Upload error", error);
                reject(error);
            });
        });
    }

    // Xóa hình ảnh khỏi Cloudinary
    $scope.deleteImageFromCloudinary = function(publicId) {
        if (!publicId) {
            console.log("No publicId provided for deletion");
            return;
        }
        
        // Sử dụng POST request với form data để tránh vấn đề mã hóa URL
        var data = new FormData();
        data.append('publicId', publicId);
        
        $http.post('/rest/upload/delete', data, {
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined}
        })
        .then(resp => {
            if (resp.data.skipped) {
                console.log("ℹ️ Image deletion skipped (default image)");
            } else {
                console.log("🗑️ Image deleted from Cloudinary:", publicId);
            }
        })
        .catch(error => {
            console.error("❌ DELETE ERROR:", error);
            console.error("Failed to delete image from Cloudinary:", publicId);
        });
    }    
    
    //phân trang
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