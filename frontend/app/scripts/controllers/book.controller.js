(function() {
	'use strict';

	angular.module('livraria').controller('BookController', BookController);

	BookController.$inject = ['$scope', '$routeParams', '$location', 'BookService', 'AlertService', 'Upload', 'FileSaver'];

	function BookController($scope, $routeParams, $location, BookService, AlertService, Upload, FileSaver) {

		$scope.anexo = {};
	    $scope.progress = 0;
	    $scope.labelArquivos = 'Nenhum arquivo selecionado';

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


	    $scope.download = function(bookId, filename){
	    	if(bookId){
	            BookService.download(bookId).then(function(res){
	            	if(res.status == 204){
	            		AlertService.addWithTimeout('info', 'O livro ainda não tem anexo');
	            	}
	            	else{
		                var blob = new Blob([res.data], {type: res.headers('Content-Type')});                
		                FileSaver.saveAs(blob, filename + '.pdf');
		            }
	            }, function(err){
	                AlertService.addWithTimeout('danger', 'Não foi possível recuperar livro');
	            });
	        }
	    }

	    $scope.uploadFiles = function (files, errFiles) {

	        $scope.progress = 0;
	        $scope.files = files;
	        $scope.errFiles = errFiles;

	        angular.forEach(files, function(file) {

	            $scope.labelArquivos = 'Anexando ' + file.name;
	            
	            file.upload = Upload.upload({
	                url: 'api/book/upload/' + $scope.book.id,
	                data: {file: file}
	            });

	            file.upload.then(function (response) {
                    file.result = response.data;
	                $scope.progress = 0;
	                $scope.labelArquivos = '';	 
	                $scope.book.file = [];               
	            }, function (response) {
	                if (response.status > 0)
	                    $scope.errorMsg = response.status + ': ' + response.data;
	            }, function (evt) {
	                var percent = parseInt(100.0 * evt.loaded / evt.total);
	                $scope.progress = (percent == 100) ? 0 : percent;
	            });
	        });

	        if (files.length == 1) {
	            $scope.labelArquivos = files[0].name;
	        } else if (files.length > 1) {
	            $scope.labelArquivos = files.length + ' arquivos selecionados';
	        }   
	        
	    }

	    $scope.addToMyLibrary = function(book){
	    	if(book){
	    		
	    	}
	    }

	    $scope.removeBook = function(book){
	    	if(book){
	    		BookService.removeBook(book).then(function(res){
	    			$scope.book.file = null;
	    			AlertService.addWithTimeout('success', 'Anexo do livro removido');
	            }, function(err){
	                AlertService.addWithTimeout('danger', 'Não foi possível remover anexo');
	            });
	    	}
	    }
    }

})();