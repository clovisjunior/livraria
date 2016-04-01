(function() {
	'use strict';

	angular.module('livraria')
	 
	.directive('alerts', function() {
	    return {
	        restrict: 'E',
	        templateUrl: 'views/partials/alerts.html'
	    };
	})
	
})();