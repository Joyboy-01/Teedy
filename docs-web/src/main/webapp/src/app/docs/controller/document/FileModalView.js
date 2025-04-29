'use strict';

/**
 * File modal view controller.
 */
angular.module('docs').controller('FileModalView', function($scope, $state, $stateParams, $q, $dialog, $timeout, 
  $http, Restangular, $translate, Upload, $sce) {
  
  // 获取文件信息
  $scope.file = {};
  $scope.fileContent = null;
  
  // 如果fileId存在，加载文件
  if ($stateParams.fileId) {
    Restangular.one('file', $stateParams.fileId).get().then(function(data) {
      $scope.file = data;
      // 尝试为文本类型文件加载内容
      if ($scope.isTextFile($scope.file.mimetype)) {
        loadFileContent($scope.file.id);
      } else if ($scope.file.mimetype === 'application/pdf') {
        // 对于PDF文件，预设trustedFileUrl用于iframe显示
        $scope.trustedFileUrl = $sce.trustAsResourceUrl('../api/file/' + $scope.file.id + '/data');
      }
    });
  }
  
  /**
   * 判断是否为文本文件
   */
  $scope.isTextFile = function(mimetype) {
    return mimetype && (
      mimetype.indexOf('text/') === 0 || 
      mimetype === 'application/markdown' || 
      mimetype.indexOf('markdown') !== -1
    );
  };
  
  /**
   * 加载文件内容
   */
  function loadFileContent(fileId) {
    return $http.get('../api/file/' + fileId + '/data', {
      transformResponse: function(data) { return data; } // 不自动解析JSON
    }).then(function(response) {
      $scope.fileContent = response.data;
      return response.data;
    });
  }
  
  /**
   * 返回上一页
   */
  $scope.closeFile = function() {
    $state.go('^', {id: $stateParams.id});
  };
  
  /**
   * 支持的翻译语言列表
   */
  $scope.supportedLanguages = [
    {code: 'en', name: 'English'},
    {code: 'zh', name: '中文'},
    {code: 'fr', name: 'Français'},
    {code: 'es', name: 'Español'},
    {code: 'de', name: 'Deutsch'},
    {code: 'ja', name: '日本語'},
    {code: 'ko', name: '한국어'},
    {code: 'ru', name: 'Русский'}
  ];
  
  /**
   * 提取文件内容并翻译后在新窗口展示
   */
  $scope.extractAndTranslate = function(targetLanguage) {
    // 获取文件ID
    var fileId = $scope.file.id || $stateParams.fileId;
    
    // 创建一个新窗口来显示加载中
    var newWindow = window.open('', '_blank');
    newWindow.document.write('<html><head><title>翻译中...</title></head><body><h3>正在提取并翻译内容，请稍候...</h3></body></html>');
    
    // 首先获取文件内容
    var contentUrl = '../api/file/' + fileId + '/data';
    if ($scope.file.mimetype === 'application/pdf') {
      contentUrl += '?mode=text'; // 对于PDF使用文本提取模式
    }
    
    $http.get(contentUrl, {
      transformResponse: function(data) { return data; } // 不自动解析JSON
    }).then(function(response) {
      var extractedText = response.data;
      
      // 调用翻译API
      Restangular.one('translation', targetLanguage).customPOST({
        text: extractedText
      }).then(function(data) {
        // 翻译成功，在新窗口中显示翻译结果
        var translatedText = data.translated;
        var originalText = extractedText;
        var fileName = $scope.file.name;
        var langName = _.find($scope.supportedLanguages, {code: targetLanguage}).name;
        
        // 生成HTML并显示在新窗口
        var html = '<html><head><title>翻译: ' + fileName + '</title>';
        html += '<style>';
        html += 'body { font-family: Arial, sans-serif; margin: 20px; }';
        html += '.header { background: #f5f5f5; padding: 10px; margin-bottom: 20px; border-bottom: 1px solid #ddd; }';
        html += '.content { white-space: pre-wrap; word-break: break-word; }';
        html += '.translated { margin-top: 20px; padding: 15px; background: #f9f9f9; border-left: 4px solid #4285f4; }';
        html += '.original { margin-top: 30px; padding: 15px; color: #666; border-top: 1px solid #eee; }';
        html += '.title { color: #333; }';
        html += '</style></head><body>';
        
        html += '<div class="header">';
        html += '<h2 class="title">文件翻译: ' + fileName + '</h2>';
        html += '<p>翻译为: ' + langName + '</p>';
        html += '</div>';
        
        html += '<h3>翻译内容:</h3>';
        html += '<div class="content translated">' + translatedText.replace(/\n/g, '<br>') + '</div>';
        
        html += '<h3>原始内容:</h3>';
        html += '<div class="content original">' + originalText.replace(/\n/g, '<br>') + '</div>';
        
        html += '</body></html>';
        
        newWindow.document.open();
        newWindow.document.write(html);
        newWindow.document.close();
      }, function(error) {
        // 翻译失败
        newWindow.document.open();
        newWindow.document.write('<html><head><title>翻译错误</title></head><body><h3>翻译失败</h3><p>抱歉，翻译过程中发生错误。</p></body></html>');
        newWindow.document.close();
      });
    }, function(error) {
      // 内容提取失败
      newWindow.document.open();
      newWindow.document.write('<html><head><title>内容提取错误</title></head><body><h3>内容提取失败</h3><p>抱歉，无法提取文件内容。</p></body></html>');
      newWindow.document.close();
    });
  };
  
  /**
   * 下载文件
   */
  $scope.openFile = function() {
    window.open('../api/file/' + $scope.file.id + '/data', '_blank');
  };
  
  /**
   * 查看文本内容
   */
  $scope.openFileContent = function() {
    window.open('../api/file/' + $scope.file.id + '/data?size=content', '_blank');
  };
  
  /**
   * 打印文件
   */
  $scope.printFile = function() {
    window.frames['fileFrame'].focus();
    window.frames['fileFrame'].print();
  };
  
  /**
   * 获取前一个文件/后一个文件相关函数
   */
  $scope.previousFile = function() { return false; };
  $scope.nextFile = function() { return false; };
  $scope.goPreviousFile = function() {};
  $scope.goNextFile = function() {};
  $scope.canDisplayPreview = function() {
    return $scope.file && $scope.file.mimetype && 
           ($scope.file.mimetype.indexOf('image/') === 0 || 
            $scope.file.mimetype === 'application/pdf');
  };
  
  console.log('Supported languages:', $scope.supportedLanguages);
});