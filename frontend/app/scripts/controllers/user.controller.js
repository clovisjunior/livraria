(function() {
	'use strict';

	angular.module('livraria').controller('UserController', UserController);

	UserController.$inject = ['$scope', '$routeParams', '$location', 'UserService', 'AlertService'];

	function UserController($scope, $routeParams, $location, UserService, AlertService) {

		var id = $routeParams.id;
   		var path = $location.$$url;
   		$scope.users = [];
   		$scope.user = {}

		if(path === '/user') {
	        UserService.getUsers().then(
	            function(users) {
	                $scope.users = users;
	            },
	            function(response) {
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

	    if(path === '/user/edit/' + id) {
	        UserService.getUser(id).then(
	            function(user) {
	                $scope.user = user;                
	            },
	            function(response) {
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

	        UserService.save($scope.user).then(
	            function(user) {
	                AlertService.addWithTimeout('success', 'Usuário salvo com sucesso');
	            },
	            function(response) {

	                var data = response.data;
	                var status = response.status;

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
	    	$location.path('/user/edit');
	    }

    }

})();