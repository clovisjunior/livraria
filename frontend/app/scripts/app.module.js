(function() {
    'use strict';

    angular.module('livraria', [
        'ngRoute',
        'config',
        'ngFileUpload',
        'ngFileSaver',
        'countTo',
        'swaggerUi'
    ]).config(['$routeProvider', 'USER_ROLES',
        function ($routeProvider, USER_ROLES) {

            $routeProvider

                .when('/', {
                    templateUrl: 'views/dashboard/dashboard.html',
                    controller: 'DashboardController',                    
                    data: {
                        authorizedRoles: [USER_ROLES.NOT_LOGGED]
                    }
                })

                .when('/login', {
                    templateUrl: 'views/login.html',
                    controller: 'AuthController',
                    data: {
                        authorizedRoles: [USER_ROLES.NOT_LOGGED]
                    }
                })

                .when('/dashboard', {
                    templateUrl: 'views/dashboard/dashboard.html',
                    controller: 'DashboardController',
                    data: {
                        authorizedRoles: [USER_ROLES.NOT_LOGGED]
                    }
                })

                .when('/403', {
                    templateUrl: 'views/403.html',
                    data: {
                        authorizedRoles: [USER_ROLES.NOT_LOGGED]
                    }
                })

                .when('/swagger', {
                    templateUrl: 'views/swagger.html',
                    controller: 'SwaggerController',
                    data: {authorizedRoles: [USER_ROLES.ADMINISTRADOR, USER_ROLES.USUARIO]
                    }
                })

                .otherwise({
                    redirectTo: '/',
                    data: {
                        authorizedRoles: [USER_ROLES.NOT_LOGGED]
                    }
                });

        }
    ])

    .config(['$httpProvider', function ($httpProvider) {

        $httpProvider.interceptors.push(['$q', '$rootScope', 'AppService', 'ENV', 'APP_EVENTS', 'AUTH_EVENTS', function ($q, $rootScope, AppService, ENV, APP_EVENTS, AUTH_EVENTS) {
            return {
                'request': function (config) {
                    $rootScope.$broadcast('loading-started');

                    var token = AppService.getToken();
                    if (config.url.indexOf("api") !== -1) {
                        config.url = ENV.apiEndpoint + config.url;
                    }

                    if (token) {
                        config.headers['Authorization'] = "Token " + token;
                    }

                    return config || $q.when(config);
                },
                'response': function (response) {
                    $rootScope.$broadcast('loading-complete');
                    return response || $q.when(response);
                },
                'responseError': function (response) {
                    $rootScope.$broadcast('loading-complete');

                    $rootScope.$broadcast({
                        '-1': APP_EVENTS.offline,
                        0: APP_EVENTS.offline,
                        404: APP_EVENTS.offline,
                        503: APP_EVENTS.offline,
                        401: AUTH_EVENTS.notAuthenticated,
                        //403: AUTH_EVENTS.notAuthorized,
                        419: AUTH_EVENTS.sessionTimeout,
                        440: AUTH_EVENTS.sessionTimeout
                    }[response.status], response);

                    return $q.reject(response);

                },
                'requestError': function (rejection) {
                    $rootScope.$broadcast('loading-complete');
                    return $q.reject(rejection);
                }
            };
        }]);

    }])

    .run(['$rootScope', '$location', '$window', 'AUTH_EVENTS', 'APP_EVENTS', 'USER_ROLES', 'AuthService', 'AppService', 'AlertService',
        function ($rootScope, $location, $window, AUTH_EVENTS, APP_EVENTS, USER_ROLES, AuthService, AppService, AlertService) {

            $rootScope.$on('$routeChangeStart', function (event, next) {

                if (next.redirectTo !== '/') {
                    var authorizedRoles = next.data.authorizedRoles;

                    if (authorizedRoles.indexOf(USER_ROLES.NOT_LOGGED) === -1) {

                        if (!AuthService.isAuthorized(authorizedRoles)) {
                            event.preventDefault();
                            if (AuthService.isAuthenticated()) {
                                // user is not allowed
                                $rootScope.$broadcast(AUTH_EVENTS.notAuthorized);
                            } else {
                                // user is not logged in
                                $rootScope.$broadcast(AUTH_EVENTS.notAuthenticated);
                            }
                        }
                    }
                }
            });


            $rootScope.$on(AUTH_EVENTS.notAuthorized, function () {
                $location.path("/403");
            });

            $rootScope.$on(AUTH_EVENTS.notAuthenticated, function () {
                $rootScope.currentUser = null;
                AppService.removeToken();
                $location.path("/login");
            });

            $rootScope.$on(AUTH_EVENTS.loginFailed, function () {
                AppService.removeToken();
                $location.path("/login");
            });

            $rootScope.$on(AUTH_EVENTS.logoutSuccess, function () {
                $rootScope.currentUser = null;
                AppService.removeToken();
                $location.path("/");
            });

            $rootScope.$on(AUTH_EVENTS.loginSuccess, function () {
                $location.path("/");
            });

            $rootScope.$on(APP_EVENTS.offline, function () {
                AlertService.clear();
                AlertService.addWithTimeout('danger', 'Servidor esta temporariamente indisponível, tente mais tarde');
            });

            // Check if a new cache is available on page load.
            $window.addEventListener('load', function (e) {
                $window.applicationCache.addEventListener('updateready', function (e) {
                    if ($window.applicationCache.status === $window.applicationCache.UPDATEREADY) {
                        // Browser downloaded a new app cache.
                        $window.location.reload();
                        alert('Uma nova versão será carregada!');
                    }
                }, false);
            }, false);

        }
    ])

    .constant('APP_EVENTS', {
        offline: 'app-events-offline'
    })

    .constant('AUTH_EVENTS', {
        loginSuccess: 'auth-login-success',
        loginFailed: 'auth-login-failed',
        logoutSuccess: 'auth-logout-success',
        sessionTimeout: 'auth-session-timeout',
        notAuthenticated: 'auth-not-authenticated',
        notAuthorized: 'auth-not-authorized'
    })

    .constant('USER_ROLES', {
        ADMINISTRADOR: 'ADMINISTRADOR',
        USUARIO: 'USUARIO',
        NOT_LOGGED: 'NOT_LOGGED'
    })

})();



