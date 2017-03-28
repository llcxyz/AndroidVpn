// Ionic Starter App

// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
// 'starter.services' is found in services.js
// 'starter.controllers' is found in controllers.js
angular.module('starter', ['angular-svg-round-progressbar', 'tabSlideBox', 'ionic', 'starter.controllers', 'starter.services'])

.run(function($ionicPlatform,$ionicPopup) {

  $ionicPlatform.ready(function() {
    // Hide the accessory bar by default (remove this to show the accessory bar above the keyboard
    // for form inputs)
    if (window.cordova && window.cordova.plugins && window.cordova.plugins.Keyboard) {
      cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
      cordova.plugins.Keyboard.disableScroll(false);

    }
    if (window.StatusBar) {
      // org.apache.cordova.statusbar required
      StatusBar.styleDefault();
    }
    //console.log("Vpn");
    console.log(VPNManager);
    console.log(Alipay);
    
   // var options = {'vpnUsername': 'zvpn', 'vpnPassword': 'zvpn123', 'vpnHost': '118.193.152.149'};

    window.addEventListener('click', function(event) {
      if (Object.prototype.toString.call(event) == '[object PointerEvent]') {
        event.stopPropagation();
      }
    }
    , true);


  });

  $ionicPlatform.registerBackButtonAction(function(e){
    console.log("back press!");

      var confirmPopup = $ionicPopup.confirm({
       title: '退出Fans加速器',
       template: '您确定要退出程序吗?'
     });
     confirmPopup.then(function(res) {
       if(res) {
         console.log('You are sure');
         ionic.Platform.exitApp();
       } else {
         console.log('You are not sure');

       }
     });

  },101);

})
//   $ionicPlatform.registerBackButtonAction(function (e) {  
  
//             e.preventDefault();  
  
//             function showConfirm() {  
//                 var confirmPopup = $ionicPopup.confirm({  
//                     title: '<strong>退出应用?</strong>',  
//                     template: '你确定要退出应用吗?',  
//                     okText: '退出',  
//                     cancelText: '取消'  
//                 });

//                 confirmPopup.then(function (res) {  
//                     if (res) {  
//                         ionic.Platform.exitApp();  
//                     } else {  
//                         // Don't close  
//                     }  
//                 });  
//               }
//           return false;
//           },101);

// })
.config(['$httpProvider', function($httpProvider) {
$httpProvider.defaults.withCredentials = true;
}])
.config(function($provide) {
     $provide.decorator('$state', function($delegate, $stateParams) {
         $delegate.forceReload = function() {
             return $delegate.go($delegate.current, $stateParams, {
                 reload: true,
                 inherit: false,
                 notify: true
             });
         };
         return $delegate;
     });
 })
.config(function($stateProvider, $urlRouterProvider,$ionicConfigProvider) {

  // Ionic uses AngularUI Router which uses the concept of states
  // Learn more here: https://github.com/angular-ui/ui-router
  // Set up the various states which the app can be in.
  // Each state's controller can be found in controllers.js
  $ionicConfigProvider.platform.ios.tabs.style('standard'); 
        $ionicConfigProvider.platform.ios.tabs.position('bottom');
        $ionicConfigProvider.platform.android.tabs.style('standard');
        $ionicConfigProvider.platform.android.tabs.position('bottom');

        $ionicConfigProvider.platform.ios.navBar.alignTitle('center'); 
        $ionicConfigProvider.platform.android.navBar.alignTitle('left');

        $ionicConfigProvider.platform.ios.backButton.previousTitleText('').icon('ion-ios-arrow-thin-left');
        $ionicConfigProvider.platform.android.backButton.previousTitleText('').icon('ion-android-arrow-back');        

        $ionicConfigProvider.platform.ios.views.transition('ios'); 
        $ionicConfigProvider.platform.android.views.transition('android');

  $stateProvider

  // setup an abstract state for the tabs directive
    .state('tab', {
    url: '/tab',
    abstract: true,
    templateUrl: 'templates/tabs.html'
  })
  .state('login',{
      url:'/login',
      templateUrl:'templates/login.html',
      controller:'DashCtrlLogin'
    })

  .state('reg', {
      url: '/reg',
      templateUrl: 'templates/reg.html',
      controller: 'AccountRegCtrl'
      
  })

  // Each tab has its own nav history stack:

  .state('tab.vpn', {
    url: '/vpn',
    views: {
      'tab-vpn': {
        params: {},
        templateUrl: 'templates/tab-vpn.html',
        controller: 'VpnCtrl'
      }
    }
  })

  .state('tab.dash-login', {
    url: '/dash/login',
    views: {
      'tab-dash': {
        templateUrl: 'templates/tab-login.html',
        controller: 'DashCtrlLogin'
      }
    }
  })

  .state("tab.server", {
    url:"/server",
    views: {
      'tab-server': {
        templateUrl : 'templates/tab-server.html',
        controller: 'ServerCtrl'
      }
    }

  })


  .state('tab.buy', {
      url: '/buy',
      views: {
        'tab-buy': {
          templateUrl: 'templates/tab-buy.html',
          controller: 'BuyCtrl'
        }
      }
    })
    .state('tab.chat-detail', {
      url: '/chats/:chatId',
      views: {
        'tab-chats': {
          templateUrl: 'templates/chat-detail.html',
          controller: 'ChatDetailCtrl'
        }
      }
    })
  .state('tab.chat-detail-buy', {
    url: '/chatsdetail/:sale_package_id/:pay_user_id',
    views: {
      'tab-chats': {
      templateUrl: 'templates/chat-detail-buy.html',
      controller: 'ChatDetailBuyCtrl'
      }
    }
  })
  .state('tab.account', {
    url: '/account',
    views: {
      'tab-account': {
        templateUrl: 'templates/tab-account.html',
        controller: 'AccountCtrl'
      }
    }
  })
 

  .state('tab.account-detail', {
      url: '/account/detail',
      views: {
        'tab-account': {
          templateUrl: 'templates/tab-account-detail.html',
          controller: 'AccountDetailCtrl'
        }
      }
    })

  .state('tab.news', {
          url: '/news',
          views: {
            'tab-news': {
              templateUrl: 'templates/tab-news.html',
              controller: 'NewsCtrl'
            }
          }
   });

  // if none of the above states are matched, use this as the fallback
  $urlRouterProvider.otherwise('/login');

});
