(function() {
    'use strict';

    angular.module('livraria').factory('UserService', UserService);

    UserService.$inject = ['$http'];

    function UserService($http) {

        return {
            getUsers: getUsers,
            getUser: getUser,
            save: save
        };


        function getUsers(){

            return $http
                .get('api/user')
                .then(getUserSuccess);

            function getUserSuccess(response){
                return response.data;
            }

        }

        function getUser(id){
           return $http
                .get('api/user/' + id)
                .then(getUserSuccess);

            function getUserSuccess(response){
                return response.data;
            }
        }

        function save(user){
            return $http({
                data: user,
                url: 'api/user',
                method: user.id ? 'PUT' : 'POST'
            })
            .then(saveUpdateSuccess);

            function saveUpdateSuccess(response){
                return response.data;
            }
        }
    }
    
})();

