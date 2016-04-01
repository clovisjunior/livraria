(function() {
    'use strict';

    angular.module('livraria').controller('ApplicationController', ApplicationController);

    ApplicationController.$inject = ['$rootScope', 'USER_ROLES', 'AuthService'];

    function ApplicationController($rootScope, USER_ROLES, AuthService) {

        $rootScope.userRoles = USER_ROLES;
        $rootScope.isAuthorized = AuthService.isAuthorized;

    }
    
})();
