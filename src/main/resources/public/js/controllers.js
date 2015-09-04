var phoneRegionApp = angular.module('phoneRegionApp', ['angular-loading-bar', 'BackendService']).config(['cfpLoadingBarProvider', function(cfpLoadingBarProvider) {
    cfpLoadingBarProvider.includeSpinner = false;
}]);

phoneRegionApp.controller('MainController', function ($scope, $window, Backend) {
    Backend.getUpdateDatetime().then(function(result) {
        $scope.lastUpdated = result.data;
    });
    $scope.update = function() {
        Backend.performUpdate().then(function(result){
            var stat = result.data;
            $scope.lastUpdated = stat.date;
            $scope.updateDone = true;
            $scope.inserted = stat.inserted;
            $scope.updated = stat.updated;
            $scope.deleted = stat.deleted;
        });
    };
    $scope.checkPhone = function() {
        if ($scope.phonenumber.length == 0){
            $scope.regionError = NaN;
            $scope.regionMessage = NaN;
            return;
        }
        Backend.getRegionForNumber($scope.phonenumber).then(function(result) {
            $scope.regionError = NaN;
            $scope.regionMessage = result.data;
        }, function(result){
            $scope.regionError = result.data;
            $scope.regionMessage = NaN;
        });
    };
    $scope.uploadCSV = function(files) {
        var fd = new FormData();
        fd.append("file", files[0]);
        Backend.getRegionsForNumbers(fd).then(function(result) {
            $scope.phones = result.data;
            $scope.phonesExist = true;
        }, function(result){
            $scope.phonesExist = NaN;
            $window.alert(result.data);
        });
    };
});
