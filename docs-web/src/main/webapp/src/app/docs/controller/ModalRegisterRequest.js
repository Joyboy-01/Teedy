'use strict';

angular.module('docs').controller('ModalRegisterRequest', function($scope, $uibModalInstance, Restangular, $translate) {
  $scope.register = {};
  $scope.registerStatus = '';

  $scope.submit = function() {
    Restangular.one('user').post('register_request', $scope.register).then(function() {
      $scope.registerStatus = $translate.instant('register_request.success');
    }, function(e) {
      if (e.data.type === 'AlreadyExistingUsername') {
        $scope.registerStatus = $translate.instant('register_request.already_exists');
      } else if (e.data.type === 'AlreadyRequested') {
        $scope.registerStatus = $translate.instant('register_request.already_requested');
      } else {
        $scope.registerStatus = $translate.instant('register_request.error');
      }
    });
  };

  $scope.close = function() {
    $uibModalInstance.close();
  };
});
