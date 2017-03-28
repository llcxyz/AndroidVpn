// Ionic Starter App

// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
// 'starter.services' is found in services.js
// 'starter.controllers' is found in controllers.js
angular.module('starter', ['angular-svg-round-progressbar','ionic', 'starter.controllers', 'starter.services'])

.run(function($ionicPlatform) {
  $ionicPlatform.ready(function() {
    // Hide the accessory bar by default (remove this to show the accessory bar above the keyboard
    // for form inputs)
    if (window.cordova && window.cordova.plugins && window.cordova.plugins.Keyboard) {
      cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
      cordova.plugins.Keyboard.disableScroll(true);

    }
    if (window.StatusBar) {
      // org.apache.cordova.statusbar required
      StatusBar.styleDefault();
    }
    //console.log("Vpn");
    console.log(VPNManager);

   // var options = {'vpnUsername': 'zvpn', 'vpnPassword': 'zvpn123', 'vpnHost': '118.193.152.149'};

    window.addEventListener('click', function(event) {
      if (Object.prototype.toString.call(event) == '[object PointerEvent]') {
        event.stopPropagation();
      }
    }
    , true);

//    VPNManager.enable(function(result){
//      console.log("enable vpn Ok!");
//      console.log(result);
//    },
//    function(err){
//      console.log("enable vpn failed");
//      console.log(err)
//    }, options);
})
})
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

  // Each tab has its own nav history stack:

  .state('tab.dash', {
    url: '/dash',
    views: {
      'tab-dash': {
        params: {},
        templateUrl: 'templates/tab-dash.html',
        controller: 'DashCtrl'
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


  .state('tab.chats', {
      url: '/chats',
      views: {
        'tab-chats': {
          templateUrl: 'templates/tab-chats.html',
          controller: 'ChatsCtrl'
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
  .state('tab.account-reg', {
      url: '/account/reg',
      views: {
        'tab-account': {
          templateUrl: 'templates/tab-reg.html',
          controller: 'AccountRegCtrl'
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
  $urlRouterProvider.otherwise('/tab/dash/login');

});
