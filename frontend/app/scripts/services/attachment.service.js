(function() {
    'use strict';

    angular.module('livraria').factory('AttachmentService', AttachmentService);

    AttachmentService.$inject = ['$http'];

    function AttachmentService($http) {

        return {
            download: download,
            remove: remove
        };

        function download(bookId){
            return $http
                .get('api/book/download/' + bookId, {responseType:'arraybuffer'})
                .then(downloadSuccess);

            function downloadSuccess(response){
                return response;
            }
        }

        function remove(book){
            return $http
                .delete('api/book/download/' + book.id)
                .then(removeSuccess);

            function removeSuccess(response){
                return response;
            }
        }
    }
    
})();

