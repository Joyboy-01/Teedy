'use strict';

/**
 * File view controller.
 */
angular.module('docs').controller('FileModalView', function($scope, $state, $stateParams, $q, $dialog, $timeout, 
  $http, Restangular, $translate, Upload) {
  var modal = $uibModal.open({
    windowClass: 'modal modal-fileview',
    templateUrl: 'partial/docs/file.view.html',
    controller: 'FileModalView',
    size: 'lg'
  });

  // Returns to document view on file close
  modal.closed = false;
  modal.result.then(function() {
    modal.closed = true;
  }, function() {
    modal.closed = true;
    $timeout(function () {
      // After all router transitions are passed,
      // if we are still on the file route, go back to the document
      if ($state.current.name === 'document.view.content.file' || $state.current.name === 'document.default.file') {
        $state.go('^', {id: $stateParams.id});
      }
    });
  });
  $scope.supportedLanguages = [
    {code: 'en', name: 'English'},
    {code: 'zh', name: '中文'},
    {code: 'fr', name: 'Français'},
    {code: 'es', name: 'Español'},
    {code: 'de', name: 'Deutsch'},
    {code: 'ja', name: '日本語'},
    {code: 'ko', name: '한国어'},
    {code: 'ru', name: 'Русский'}
  ];
  /**
   * 翻译文件内容
   */
  $scope.translateContent = function(targetLanguage) {
    if (!$scope.file || !$scope.file.content) {
      return;
    }
    
    // 保存原始内容（如果还未保存）
    if (!$scope.originalContent) {
      $scope.originalContent = $scope.file.content;
    }
    
    // 显示加载状态
    $scope.isTranslating = true;
    
    // 调用翻译API
    Restangular.one('translation', targetLanguage).customPOST({
      text: $scope.file.content
    }).then(function(data) {
      // 更新文件内容为翻译后的内容
      $scope.file.content = data.translated;
      $scope.currentLanguage = targetLanguage;
      $scope.isTranslated = true;
      $scope.isTranslating = false;
    }, function(response) {
      $scope.isTranslating = false;
      var title = $translate.instant('document.view.translation_error_title');
      var msg = $translate.instant('document.view.translation_error_message');
      var btns = [{result: 'ok', label: $translate.instant('ok'), cssClass: 'btn-primary'}];
      $dialog.messageBox(title, msg, btns);
    });
  };
  
  /**
   * 恢复原始文件内容
   */
  $scope.restoreOriginal = function() {
    if ($scope.originalContent) {
      $scope.file.content = $scope.originalContent;
      $scope.isTranslated = false;
      $scope.currentLanguage = null;
    }
  };

  
});