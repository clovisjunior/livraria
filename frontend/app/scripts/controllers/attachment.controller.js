(function() {
	'use strict';

	angular.module('livraria').controller('AttachmentController', AttachmentController);

	AttachmentController.$inject = ['$scope', 'AlertService', 'Upload', 'FileSaver', 'AttachmentService', 'MyLibraryService'];

	function AttachmentController($scope, AlertService, Upload, FileSaver, AttachmentService, MyLibraryService) {

		$scope.anexo = {};
	    $scope.progress = 0;
	    $scope.labelArquivos = 'Nenhum arquivo selecionado';

	    $scope.download = function(bookId, filename){
	    	if(bookId){
	            AttachmentService.download(bookId).then(function(res){
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
	    		MyLibraryService.addToMyLibrary(book).then(function(res){
	                AlertService.addWithTimeout('success', 'Livro adicionado a sua biblioteca');
	            }, function(err){
	                AlertService.addWithTimeout('danger', 'Não foi possível recuperar anexo');
	            });
	    	}
	    }

	    $scope.remove = function(book){
	    	if(book){
	    		AttachmentService.remove(book).then(function(res){
	    			$scope.book.file = null;
	    			AlertService.addWithTimeout('success', 'Anexo do livro removido');
	            }, function(err){
	                AlertService.addWithTimeout('danger', 'Não foi possível remover anexo');
	            });
	    	}
	    }

    }

})();