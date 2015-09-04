var BackendService = angular.module('BackendService', [])
    .service('Backend', ['$http', function ($http) {
        this.getUpdateDatetime = function () {
            return $http.get('/date');
        };

    }]);
