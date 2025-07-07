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
        //tải khách hàng
        $http.get(url).then(resp => {
            $scope.items = resp.data;
        }).catch(error => {
            console.error("Error loading customers:", error);
        });

        //tải vai trò
        $http.get(url1).then(resp => {
            $scope.roles = resp.data;
        })
    }

    // Hiển thị Object.keys cho template
    $scope.Object = window.Object;

    // Lưu file đã chọn để tải lên sau
    $scope.selectedFile = null;
    
    //khởi đầu
    $scope.initialize();    
    $scope.reset = function () {
        $scope.form = {
            photo: 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750748027/cloud-upload_c6zitf.jpg', 
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
        if (!$scope.form.photo) {
            $scope.form.photo = null;
        }
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
        if (!$scope.form.username || !$scope.form.password || !$scope.form.fullname || !$scope.form.email) {
            sweetalert("Vui lòng điền đầy đủ thông tin!");
            return;
        }
        
        // Tải lên hình ảnh nếu có file mới được chọn, nếu không thì sử dụng hình mặc định hoặc hiện tại
        $scope.uploadImage().then(uploadResult => {
            var item = angular.copy($scope.form);
            item.photo = uploadResult.url; // Sử dụng URL hình ảnh đã tải lên hoặc mặc định
            item.public_id = uploadResult.publicId; // Lưu public_id
            
            // Đảm bảo không bao gồm ID cho thao tác tạo mới
            delete item.id;
            
            $http.post(`${url}`, item).then(resp => {
                resp.data.token = "token";
                $scope.items.push(resp.data);
                $scope.reset();
                sweetalert("Thêm mới thành công!");
            }).catch(error => {
                sweetalert("Lỗi thêm mới tài khoản!");
                console.log("Error", error);
            });
        }).catch(error => {
            sweetalert("Lỗi tải lên hình ảnh!");
            console.log("Upload Error", error);
        });
    }    
    
    //cập nhật sp
    $scope.update = function (skipReset) {
        // Kiểm tra các trường bắt buộc
        if (!$scope.form.id) {
            sweetalert("Không thể cập nhật tài khoản không có ID!");
            return;
        }
        
        // Lấy dữ liệu khách hàng cũ để kiểm tra hình ảnh cũ
        var oldCustomer = $scope.items.find(p => p.id == $scope.form.id);
        var oldPublicId = oldCustomer ? oldCustomer.public_id : null;
        
        // Tải lên hình ảnh trước nếu có file mới được chọn
        $scope.uploadImage().then(uploadResult => {
            var item = angular.copy($scope.form);
            item.photo = uploadResult.url; // Sử dụng URL hình ảnh đã tải lên
            item.public_id = uploadResult.publicId; // Lưu public_id mới
            
            $http.patch(`${url}/${item.id}`, item).then(resp => {
                var index = $scope.items.findIndex(p => p.id == item.id);
                $scope.items[index] = resp.data;

                // Xóa hình ảnh cũ khỏi Cloudinary nếu tồn tại và khác với hình mới
                if (oldPublicId && oldPublicId !== uploadResult.publicId && 
                    oldPublicId !== 'user_dlgoyb' && !oldPublicId.includes('default')) {
                    $scope.deleteImageFromCloudinary(oldPublicId);
                }

                if (!skipReset) {
                    $scope.reset();
                }

                sweetalert("Cập nhật tài khoản thành công!");
            }).catch(error => {
                sweetalert("Lỗi cập nhật tài khoản!");
            });
        }).catch(error => {
            sweetalert("Lỗi tải lên hình ảnh!");
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
            if (publicIdToDelete && publicIdToDelete !== 'user_dlgoyb' && 
                !publicIdToDelete.includes('default')) {
                $scope.deleteImageFromCloudinary(publicIdToDelete);
            }
            
            sweetalert("Xóa tài khoản thành công!");
        }).catch(error => {
            sweetalert("Lỗi xóa tài khoản!");
            console.log("Error", error);
        });
    }    
    
    //xem trước hình khi chọn file
    $scope.imageChanged = function (files) {
        if (!files || files.length === 0) {
            return;
        }
        
        var file = files[0];
        
        if (!file.type.startsWith('image/')) {
            sweetalert("Vui lòng chọn file hình ảnh!");
            return;
        }
        
        if (file.size >= 10 * 1024 * 1024) {
            sweetalert("File quá lớn! Vui lòng chọn file nhỏ hơn 10MB!");
            return;
        }
        
        $scope.selectedFile = file;
        
        var reader = new FileReader();
        reader.onload = function(e) {
            $scope.form.photo = e.target.result;
            $scope.$apply(); 
        };
        reader.readAsDataURL(file);
    }
    
    // Tải hình ảnh lên cloudinary
    $scope.uploadImage = function() {
        return new Promise((resolve, reject) => {
            if (!$scope.selectedFile) {
                if ($scope.form.photo && $scope.form.photo.startsWith('http')) {
                    // Trả về thông tin hình ảnh hiện có
                    resolve({
                        url: $scope.form.photo,
                        publicId: $scope.form.public_id || null
                    });
                } else {
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
                $scope.selectedFile = null; 
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
            console.log("✅ DELETE SUCCESS:", resp.data);
            if (resp.data.skipped) {
                console.log("ℹ️ Image deletion skipped (default image)");
            } else {
                console.log("🗑️ Image deleted from Cloudinary:", publicId);
            }
        })
        .catch(error => {
            console.error("❌ DELETE ERROR:", error);
        });
    }

    //phân trang
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