(function() {
    'use strict';

    angular.module('livraria').factory('DashboardService', DashboardService);

    DashboardService.$inject = ['$http'];

    function DashboardService($http) {

        return {
            getTotalUsers: getTotalUsers,
            getTotalBooks: getTotalBooks
        };


        function getTotalUsers(){

            return $http
                .get('api/user/count')
                .then(getTotalUsersSuccess);

            function getTotalUsersSuccess(response){
                return response.data;
            }

        }

        function getTotalBooks(){
           return $http
                .get('api/book/count')
                .then(getTotalBooksSuccess);

            function getTotalBooksSuccess(response){
                return response.data;
            }
        }
        
    }
    
})();

