app.controller("authority-ctrl", function ($scope, $http, $location) {
    var url = "/rest/roles";
    var url1 = "/rest/authorities";
    var url2 = "/rest/customers";
    var url3 = "/rest/authorities";
    $scope.roles = [];
    $scope.users = [];
    $scope.authorities = [];

    var sweetalert = function (text) {
        Swal.fire({
            icon: "success",
            title: text,
            showConfirmButton: false,
            timer: 2000,
        });
    }

    $scope.initialize = function () {
        //load roles
        $http.get(url).then(resp => {
            $scope.roles = resp.data;
        }).catch(error => {
            console.error("Error loading roles:", error);
        });

        $http.get(url2).then(resp => {
            $scope.users = resp.data;
        }).catch(error => {
            console.error("Error loading users:", error);
        });

        $http.get(url3).then(resp => {
            $scope.authorities = resp.data;
        }).catch(error => {
            console.error("Error loading authorities:", error);
        });
    }

    $scope.authority_of = function (acc, role) {
        if ($scope.authorities && acc && role) {
            var found = $scope.authorities.find(ur => {
                var hasCustomer = ur.customer && ur.customer.username == acc.username;
                var hasRole = ur.role && ur.role.id == role.id;
                return hasCustomer && hasRole;
            });
            
            return found != null;
        }
        return false;
    }

    $scope.authority_changed = function (acc, role) {
        var authority = $scope.authorities.find(ur => ur.customer && ur.role && ur.customer.username == acc.username && ur.role.id == role.id);
        if (authority) {
            $scope.revoke_authority(authority);
        } else {
            authority = {
                customer: { id: acc.id },  // Chỉ gửi ID
                role: { id: role.id }      // Chỉ gửi ID
            };
            $scope.grant_authority(authority);
        }
    }

    //them moi authority
    $scope.grant_authority = function (authority) {
        $http.post(`${url1}`, authority).then(resp => {
            $scope.authorities.push(resp.data);
            sweetalert("Cấp quyền sử dụng thành công!");
        }).catch(error => {
            sweetalert("Cấp quyền sử dụng thất bại!");
        });
    }

    //xoa authority
    $scope.revoke_authority = function (authority) {
        $http.delete(`${url1}/${authority.id}`).then(resp => {
            var index = $scope.authorities.findIndex(a => a.id == authority.id);
            $scope.authorities.splice(index, 1);
            sweetalert("Thu hồi quyền sử dụng thành công!");
        }).catch(error => {
            sweetalert("Thu hồi quyền sử dụng thất bại!");
        });
    }

    $scope.initialize();

    //phan trang
    $scope.pager = {
        page: 0,
        size: 10,
        get users() {
            var start = this.page * this.size;
            return $scope.users.slice(start, start + this.size);
        },
        get count() {
            return Math.ceil(1.0 * $scope.users.length / this.size)
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