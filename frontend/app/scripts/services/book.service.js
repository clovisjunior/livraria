(function() {
    'use strict';

    angular.module('livraria').factory('BookService', BookService);

    BookService.$inject = ['$http'];

    function BookService($http) {

        return {
            getBooks: getBooks,
            getBook: getBook,
            save: save,
            remove: remove,
            download: download,
            removeBook: removeBook
        };


        function getBooks(){

            return $http
                .get('api/book')
                .then(getBookSuccess);

            function getBookSuccess(response){
                return response.data;
            }

        }

        function getBook(id){
           return $http
                .get('api/book/' + id)
                .then(getBookSuccess);

            function getBookSuccess(response){
                return response.data;
            }
        }

        function save(book){
            return $http({
                data: book,
                url: 'api/book',
                method: book.id ? 'PUT' : 'POST'
            })
            .then(saveUpdateSuccess);

            function saveUpdateSuccess(response){
                return response.data;
            }
        }

        function remove(book){
            return $http
                .delete('api/book/' + book.id)
                .then(removeSuccess);

            function removeSuccess(response){
                return response.data;
            }
        }

        function download(bookId){
            return $http
                .get('api/book/download/' + bookId, {responseType:'arraybuffer'})
                .then(downloadSuccess);

            function downloadSuccess(response){
                return response;
            }
        }

        function removeBook(book){
            return $http
                .delete('api/book/download/' + book.id)
                .then(removeSuccess);

            function removeSuccess(response){
                return response;
            }
        }
       
    }
    
})();

