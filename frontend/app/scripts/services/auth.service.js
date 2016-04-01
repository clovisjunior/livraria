(function() {
    'use strict';

    angular.module('livraria').factory('AuthService', AuthService);

    AuthService.$inject = ['$http', 'AppService', '$rootScope'];

    function AuthService($http, AppService, $rootScope) {

        var authService = {};

        authService.login = function (credentials) {

            AppService.removeToken();

            return $http
                .post('api/auth', credentials)
                .success(function (res, status, headers) {

                    AppService.setToken(headers('Set-Token'));
                    $rootScope.currentUser = AppService.getUserFromToken();
                    return res;
                }
            );

        };

        authService.logout = function () {

            return $http
                .delete('api/auth')
                .then(function (response) {
                    AppService.removeToken();
                }
            );
        };

        authService.isAuthenticated = function () {
            $rootScope.currentUser = AppService.getUserFromToken();
            return $rootScope.currentUser ? true : false;
        };

        authService.isAuthorized = function (authorizedRoles) {

            if (authService.isAuthenticated()) {

                if (!angular.isArray(authorizedRoles)) {
                    authorizedRoles = [authorizedRoles];
                }

                var hasAuthorizedRole = false;

                var perfil = $rootScope.currentUser.perfil;

                if (perfil !== undefined && perfil !== null) {
                    for (var i = 0; i < authorizedRoles.length; i++) {
                        if (authorizedRoles[i] === perfil) {
                            hasAuthorizedRole = true;
                            break;
                        }

                    }
                }
            } else {
                return false;
            }

            return hasAuthorizedRole;
        };

        return authService;
    }
    
})();

