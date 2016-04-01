(function() {
	'use strict';

	angular.module('livraria').controller('DashboardController', DashboardController);

	DashboardController.$inject = ['$scope', 'DashboardService'];

	function DashboardController($scope, DashboardService) {

		$scope.userNumber = 0;
		$scope.bookNumber = 0;

		DashboardService.getTotalUsers().then(function(total){
			$scope.userNumber = total;
		});

		DashboardService.getTotalBooks().then(function(total){
			$scope.bookNumber = total;
		});

    }

})();