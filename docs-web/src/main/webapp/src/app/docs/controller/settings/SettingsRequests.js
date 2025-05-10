'use strict';

angular.module('docs').controller('SettingsRequests', function($scope, Restangular, $translate, $dialog) {
  $scope.requests = [];

  $scope.loadRequests = function() {
    Restangular.one('/user/register_request').get().then(function(list) {
      $scope.requests = list.requests || list;  
    });
  };

  $scope.approveRequest = function(req) {
    Restangular.all('/user/register_request/' + req.id + '/accept').post({ id: req.id }).then(function() {
      $scope.loadRequests();
    });
  };

  $scope.loadRequests();
});
