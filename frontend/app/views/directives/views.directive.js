(function() {
	'use strict';

	angular.module('livraria')
	 
	.directive('alerts', function() {
	    return {
	        restrict: 'E',
	        templateUrl: 'views/partials/alerts.html'
	    };
	})

	.directive('attachment', function(){
		return {
			restrict: 'E',
	        scope: {
	            book: '='
	        },
	        templateUrl: 'views/partials/attachment.html'
		}
	})
	
})();