(function() {
    'use strict';

    angular.module('livraria').factory('MyLibraryService', MyLibraryService);

    MyLibraryService.$inject = ['$http'];

    function MyLibraryService($http) {

        return {
            addToMyLibrary: addToMyLibrary,
            listMyBooks: listMyBooks           
        };


        function addToMyLibrary(book){

            return $http
                .post('api/mylibrary', book)
                .then(addToMyLibrarySuccess);

            function addToMyLibrarySuccess(response){
                return response.data;
            }

        }

        function listMyBooks(){
            return $http
                .get('api/mylibrary')
                .then(listMyBooksSuccess);

            function listMyBooksSuccess(response){
                return response.data;
            }
        }
       
    }
    
})();

