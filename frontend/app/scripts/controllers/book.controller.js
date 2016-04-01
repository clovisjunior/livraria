(function() {
	'use strict';

	angular.module('livraria').controller('BookController', BookController);

	BookController.$inject = ['$scope', '$routeParams', '$location', 'BookService', 'AlertService'];

	function BookController($scope, $routeParams, $location, BookService, AlertService) {

		var id = $routeParams.id;
   		var path = $location.$$url;
   		$scope.books = [];
   		$scope.book = {}

		if(path === '/book') {
	        BookService.getBooks().then(
	            function(books) {
	                $scope.books = books;
	            },
	            function(res) {
	                var data = res.data;
                	var status = res.status;

	                if(status === 401) {
	                    AlertService.addWithTimeout('warning', data.message);
	                }
	                else {
	                    AlertService.addWithTimeout('danger', 'Não foi possível executar a operação');
	                }
	            }
	        );
	    }

	    if(path === '/book/edit') {
            $scope.book = {};
        }

	    if(path === '/book/edit/' + id) {
	        BookService.getBook(id).then(
	            function(book) {
	                $scope.book = book;                
	            },
	            function(res) {
	                var data = res.data;
                	var status = res.status;

	                if(status === 401) {
	                    AlertService.addWithTimeout('warning', data.message);
	                }
	                else {
	                    AlertService.addWithTimeout('danger', 'Não foi possível executar a operação');
	                }
	            }

	        );
	    }

	    $scope.save = function(){

	        $("[id$='-message']").text("");

	        BookService.save($scope.book).then(
	            function(book) {
	                AlertService.addWithTimeout('success', 'Livro salvo com sucesso');
	                $location.path('/book/edit/' + book.id);
	            },
	            function(res) {

	                var data = res.data;
	                var status = res.status;

	                if(status === 401) {
	                    AlertService.addWithTimeout('danger', 'Não foi possível executar a operação');
	                }
	                else if(status === 422) {
	                    $.each(data, function(i, violation) {
	                        $("#" + violation.property + "-message").text(violation.message);
	                    });
	                } else {
	                    AlertService.addWithTimeout('danger', 'Não foi possível executar a operação');
	                }

	            }
	        );
	    }

	    $scope.new = function(){
	    	$location.path('/book/edit');
	    }

	    $scope.remove = function(){
	    	BookService.remove($scope.book).then(function(res){
	    		AlertService.addWithTimeout('success', 'Livro removido com sucesso');
	    		$location.path('/book');
	    	}, function(err){
	    		var data = res.data;
            	var status = res.status;

                if(status === 401) {
                    AlertService.addWithTimeout('warning', data.message);
                }
                else {
                    AlertService.addWithTimeout('danger', 'Não foi possível executar a operação');
                }
	    	})
	    }
    }

})();