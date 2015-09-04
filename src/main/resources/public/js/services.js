var BackendService = angular.module('BackendService', [])
    .service('Backend', ['$http', function ($http) {
        this.getUpdateDatetime = function () {
            return $http.get('/date');
        };
        this.performUpdate = function () {
            return $http.post('/update', '');
        };
        this.getRegionForNumber = function ($number) {
            return $http.get('/number', { ignoreLoadingBar: true, params: { number: $number } });
        };
        this.getRegionsForNumbers = function ($fd) {
            return $http.post('/numbers', $fd, {
                withCredentials: true,
                headers: {'Content-Type': undefined },
                transformRequest: angular.identity
            });
        }

    }]);
