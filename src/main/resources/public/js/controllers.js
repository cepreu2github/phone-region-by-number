var phoneRegionApp = angular.module('phoneRegionApp', ['BackendService']);

phoneRegionApp.controller('MainController', function ($scope, Backend) {
    $scope.phones = [{"number": "81234356782", "region": "Тмутаракань"}, {
        "number": "84674356712",
        "region": "Залесье"
    }];
    Backend.getUpdateDatetime().then(function(result) {
        $scope.lastUpdated = result.data;
    });
});
