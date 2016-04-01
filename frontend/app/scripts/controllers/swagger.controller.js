(function() {

	'use strict';

	angular.module('livraria').controller('SwaggerController', SwaggerController);

	SwaggerController.inject = ['$scope'];

    function SwaggerController($scope) {
        $scope.isLoading = true;
        $scope.swaggerUrl = 'api/swagger.json';
    }

})();