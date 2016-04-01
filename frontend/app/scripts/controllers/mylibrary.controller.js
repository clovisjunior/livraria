(function() {
	'use strict';

	angular.module('livraria').controller('MyLibraryController', MyLibraryController);

	MyLibraryController.$inject = ['$scope', 'AlertService', 'MyLibraryService'];

	function MyLibraryController($scope, AlertService, MyLibraryService) {
	    
	    $scope.myLibraries = [];

		$scope.listMyBooks = function(){
			MyLibraryService.listMyBooks().then(function(myLibraries){
				$scope.myLibraries = myLibraries;
			}, function(res){				
				AlertService.addWithTimeout('danger', 'Não foi possível recuperar seus livros');
			});
		}

		$scope.listMyBooks();
    }

})();