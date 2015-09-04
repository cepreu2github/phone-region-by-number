var phoneRegion = angular.module('phoneRegion', []);

phoneRegion.controller('MainController', ['$scope', function ($scope) {
    $scope.phones = [{"number": "81234356782", "region": "Тмутаракань"}, {
        "number": "84674356712",
        "region": "Залесье"
    }];
}]);
