var ionicAlert = function(pop, title, template){
  pop.alert({
      title: title,
      template: template
    });

};

var global_scope;

var reg_global_login = function($scope) {global_scope = $scope;}
var global_login = function(){
  global_scope.login();
}

angular.module('starter.controllers', [])

.controller('VpnCtrl',  function($scope, $state, $http, $rootScope, $location, $stateParams,$ionicPopup, gatewayService) {
  $scope.btn_label = "轻触连接";
  $scope.btn_disable = false;
  $scope.connected = false;
  $scope.state = 'DISABLED';
  $scope.status_txt = "VPN初始化完成";
  $scope.timeit = null;
  $scope.current = 0;
  $scope.isSemi = false;
  $scope.radius = 150;
  $scope.color = "#45ccce"
  $scope.last_update = new Date().getTime()
  $scope.last_rx_bytes = 0
  $scope.last_tx_bytes = 0
  $scope.name = "loading";
  $scope.expire_date = "loading";
  $scope.user_info = null;
  $scope.account = gatewayService.get();
  $scope.expired = false;

  $scope.serverInfo = {'name': "请选择服务器"};

  console.log("dsh init....");

  console.log("state params ->" +$stateParams);
  console.log(JSON.stringify($stateParams));


  $scope.getStyle = function(){
        var transform = ($scope.isSemi ? '' : 'translateY(-50%) ') + 'translateX(-50%)';

        return {
            'top': $scope.isSemi ? 'auto' : '50%',
            'bottom': $scope.isSemi ? '5%' : 'auto',
            'left': '40%',
            'transform': transform,
            '-moz-transform': transform,
            '-webkit-transform': transform,
            'font-size': $scope.radius/3.5 + 'px',
            'color': '#fff'
        };
    };

  //当修改服务器地址.
  $scope.$on("changeServer", function(event,serverInfo){
      console.log("user changeServer")
      console.log(serverInfo)
      $scope.serverInfo = serverInfo;
      console.log("check account is login "+gatewayService.get());

      if (gatewayService.get()==null){

        console.log("jump to login");
        $state.go("login");

      }

      else{
         //修改服务器后更新本地用户保存的服务器信息.
          var acct = gatewayService.get();

          var options = {'vpnUsername': acct.user, 'vpnPassword': acct.password,
          'vpnHostName': $scope.serverInfo.name, 'vpnHost': $scope.serverInfo.hostip, 'vpnCert': $scope.serverInfo.cert};

          console.log("保存到本地:"+JSON.stringify(options));

          VPNManager.saveUser(function(result){
              console.log("修改本地服务器信息成功！")

          },function(err){
            console.log("修改本地服务器信息失败!"+err);
            ionicAlert($ionicPopup, "内部错误","无法更新本地服务器信息");
          }, options);

      }
      
      $scope.update_exp();

  });

  //当登陆成功.
  $scope.$on("loginok",function(event, account){
          console.log("login successfully vpn status update...");
          console.log(JSON.stringify(account));
          $scope.account = account;
          $scope.update_exp();

        });


  $scope.goState = function(st){
      $state.go(st);
  };

  $scope.refresh = function(){
    
    console.log("refresh");

    $rootScope.$broadcast("user_charge_ok",null);

  };


  // $scope.get_user_info = function() {

  //     gatewayService.get_user_info().success(function(result){
  //         if(result==null || result=="null"){
  //           $location.path("/tab/dash/login");
  //           return;
  //         }

  //         console.log(JSON.stringify(result));

  //         if(result.errcode == 2){
  //             $location.path("/tab/dash/login");
  //             return;
  //         }

  //         $scope.user_info = result
  //         gatewayService.set(result);

  //         console.log("get_user_info success......"+result.toString());

  //         $scope.name = result['name']
  //         $scope.expire_date = result['expire_date'];
  //         console.log($scope.name);
  //         console.log($scope.expire_date);

  //       });
  //   };

    //获取服务器列表.
    // gatewayService.get_server().success(function(result){

    //     if (result.find("<!") != -1) {
    //         $scope.alert("登陆失败","与服务端通讯异常！")
    //         gatewayService.set_server(null);
    //         }
    //     else{
    //       servers = gatewayService.decode(result)
    //       console.log("got server Groups:");
    //       console.log(servers);
    //       gatewayService.setServerGroup(servers)

    //     }
    // });
    // serverGroups = [{'name': "服务器1",'hostip': '192.168.1.1', 'load': 20},
    // {'name': "服务器2",'hostip': '192.168.1.1', 'load': 0}];

    // gatewayService.setServerGroup(serverGroups);

    // $scope.get_user_info();

     // var options = {'vpnHostName': 'AAAA', 'vpnUsername': 'zvpn', 'vpnPassword': 'zvpn123', 'vpnHost': '118.193.152.149'};

//      window.setTimeout(function(){
//        gatewayService.save_local(function(result){
//          console.log("SAVE OK");
//        }, function(err){
//         console.log("SAVE FAILED");
//        }, options)
//      }, 3000);





  var last_click = new Date().getTime();

  $scope.do_Action = function(){
     console.log("+++++++++++++ call doAction--- Connected :"+$scope.connected);
     var t2 = new Date().getTime();
     if(t2-last_click<=1000){
        console.log("ignore click<100ms");
        return ;
     }

     last_click = t2;

    if($scope.connected==true){
      console.log("已连接,忽略");
      $scope.disconnect();
      $scope.btn_label = " 连 接";

    }
    else {
      console.log("准备连接");
      $scope.connect()
    }

  };
  $scope.disconnect = function(){


//       VPNManager.unregisterCallback(function(result){
//        console.log("unregister callback:"+result);
//       });
//

       VPNManager.disable();

  };

//   window.setInterval(function(){
//    VPNManager.status(function(status){
//      console.log("VPN STATUS ->>>>"+status);
//    });
//
//  },3000);

  console.log("init.....dash");

  var state_listener = function(state){
          $scope.state = state;
          console.log("state change->>>>>>>"+$scope.state);
          if(state=='CONNECTED') {
            $scope.status_txt = "已连接";
            $scope.btn_label = "断 开";
            $scope.connected= true;
            $scope.current = 150;

            if($scope.timeit!=null){
                                clearInterval($scope.timeit);
                          }


          }
          else if(state=='CONNECTING') {
            $scope.status_txt = "正在连接...";
            $scope.btn_label = " 连接中";
            var steps = function(){

                 $scope.current += 10;
                 $scope.color="#E42323";
                  console.log("modify current:"+$scope.current);
                  $scope.$digest();

              };
              steps();
              if($scope.timeit!=null){
                    clearInterval($scope.timeit);
              }

              $scope.timeit = setInterval(steps,1000)

              $scope.$digest();
          }
          else if(state=='UNREACHABLE'){
             $scope.status_txt = "服务不可达";
             if($scope.timeit!=null){
                    clearInterval($scope.timeit);
              }
              $scope.color = "#45ccce"
             $scope.current = 0;
             $scope.btn_label = " 连 接";
             $scope.connected= false;

          }
          else if(state=='DISABLED') {
            $scope.status_txt = "准备就绪";
          }
          else if(state=='DISCONNECTED') {
            $scope.status_txt = "已断开";
            $scope.btn_label = " 连 接";
            $scope.connected= false;

          }
          else if(state=='DISCONNECTING'){
            $scope.status_txt = "正在断开连接...";

          }

          else if(state=='AUTH_FAILED') {
            $scope.status_txt = "认证失败";
            $scope.btn_label = " 连 接";
            clearInterval($scope.timeit);
            $scope.color = "#45ccce"
            $scope.current = 0;
            $scope.timeit = null;
            $scope.connected= false;

          }
          else {
            $scope.status_txt = "未知状态("+state+")";
            $scope.btn_label = " 重 连";
            clearInterval($scope.timeit);
            $scope.color = "#45ccce"
            $scope.current = 0;
            $scope.timeit = null;
            $scope.connected= false;

          }

           $scope.$digest();

        };


   window.setTimeout(function(){
      //VPNManager.registerCallback(state_listener, state_listener);
   }, 2000);


   $scope.connect = function(){

      console.log(VPNManager);
      console.log("register callback ");
      $scope.update_exp();
      if($scope.expired){
        ionicAlert($ionicPopup, "账号过期", "账户过期,无法使用,请先购买套餐!");
        return ;
      }

      VPNManager.registerCallback(state_listener, state_listener);
       VPNManager.loadUser(function(result){
       console.log("load User->"+JSON.stringify(result));
          if(result!=false){

            VPNManager.enable(function(result){
                 if(result){
                          $scope.status_txt = 'VPN初始化成功';
                        }
                      },
                      function(err){
                        console.log("enable vpn failed");
                        console.log(err)
                        $scope.status_txt = 'VPN初始化失败';

                      }, result);

          }
       }, function(err){

             var alertPopup = $ionicPopup.alert({
                                         title: '载入用户信息失败',
                                         template: '从本地载入用户信息失败:'+err
                                       });

          console.log("load User Failed!");

       });
    };

  $scope.update_exp = function(){
      exp  = $scope.account.expiration;
      $scope.expired = (new Date(exp.replace("-","/")))<(new Date());
      
      console.log("account expire:"+$scope.expired);

  };

  $scope.update_exp();

})

.controller("ServerCtrl", function($scope,$state,$rootScope, $ionicPopup, $state, gatewayService){

  $scope.serverGroups = gatewayService.getServerGroup();

  $scope.serverlist = $scope.serverGroups[1];
  $scope.grouplist = $scope.serverGroups[0];

  var sc = $scope;

  $scope.toVpn = function(server){
    console.log("call vpn server ->");
    console.log(JSON.stringify(server));

    if (gatewayService.get()==null){
        console.log("jump to login");

        $state.go("login");
        return ;
    }

    //获取服务端证书.
    gatewayService.get_cert(server.hostip).success(function(result){
       if (result.indexOf("<!") != -1) {
            ionicAlert($ionicPopup,"故障","无法获取服务器端证书");
          }
        else {
          console.log("get cert "+result)
          server.cert = result;
           //先广播,再跳转.当controller未初始化时.可世直接传递参数.
          
          $state.go("tab.vpn", {'serverInfo':server});
          $rootScope.$broadcast("changeServer",server);

        }

    }).error(function(err){
        ionicAlert($ionicPopup,"故障","无法获取服务器端证书("+err+")");
        return ;
    });

   

  };



  // $scope.translate([])
  
  //$scope.translate($scope.groups);

})
.controller('BuyCtrl', function($scope, $ionicPopup, $rootScope, gatewayService) {
  // With the new view caching in Ionic, Controllers are only called
  // when they are recreated or on app start, instead of every page change.
  // To listen for when this page is active (for example, to refresh data),
  // listen for the $ionicView.enter event:
  //
  //$scope.$on('$ionicView.enter', function(e) {
  //});

  $scope.buy_pkg = null;

  $scope.packages = gatewayService.get_sale_packages();

  $scope.paying = false;

  $scope.btn_txt = "提交订单"

  $scope.buy = function(pkg){
      // console.log("by pkg =>"+JSON.stringify(pkg));
      $scope.buy_pkg = pkg;
  }

  $scope.submit = function(){

      VPNManager.status(function(status){
        console.log("Vpn Status:"+status);
        if(status=='CONNECTED'){
          
          ionicAlert($ionicPopup, "温馨提示", "请先断开连接，再购买");

        }
        else 
          $scope.submit2();

    },function(err){
      ionicAlert($ionicPopup, "错误", "查询连接状态失败");
    });

  };


  $scope.submit2 = function(){


    
    var username = gatewayService.get().login;
    //var username='testvpn00'
    console.log("current login account "+username);
    $scope.paying = true;
    orderinfo = "goods_id="+$scope.buy_pkg.id+"&goods_name="+$scope.buy_pkg.title+"&username="+username+"&price="+$scope.buy_pkg.price;

    console.log("req order->"+JSON.stringify(orderinfo));

    $scope.btn_txt = "正在处理"

    // $rootScope.$broadcast("user_charge_ok",username);
    // global_login();

    gatewayService.get_order(orderinfo).success(function(result){

          console.log("提交订单成功:"+result);

          Alipay.pay({'order': result}, function(msgCode){
            if(msgCode=="9000"){
              ionicAlert($ionicPopup,"支付成功!","恭喜您，充值成功");
              $rootScope.$broadcast("user_charge_ok",username);
              global_login();
            }
          
            else {
              ionicAlert($ionicPopup,"支付失败","失败代码("+msgCode+")");
            }
            $scope.paying = false;
            $scope.btn_txt = "提交订单"


          },function(err){
            
             ionicAlert($ionicPopup,"支付失败","失败代码("+err+")");
              $scope.paying = false;
              $scope.btn_txt = "提交订单"
          });

    }).error(function(err){
        
        ionicAlert($ionicPopup,"失败","提交订单失败("+err+")");
          $scope.paying = false;
          $scope.btn_txt = "提交订单"
    });
    
  };

})

.controller('ChatDetailCtrl', function($scope, $stateParams, $state, $location, $sce, Chats,gatewayService) {
  $scope.chat = Chats.get($stateParams.chatId);
  $scope.account = gatewayService.get();
  $scope.buy_url = "";
   $scope.$on("loginok", function(event, msg){
    console.log("evetn fire ---"+event);
    });

   $scope.trustHtml = function(html) {
          return $sce.trustAsHtml(html);
    }

})

.controller("NewsCtrl", function($scope,$stateParams, $location, $sce, gatewayService){
         $scope.news_url = "";

          $scope.trustSrc = function(src) {
                return $sce.trustAsResourceUrl(src);
          }

         $scope.news_url = "http://m.news.baidu.com/news#/?_k=7kt5jo";


})
.controller("ChatDetailBuyCtrl", function($scope,$stateParams, $location, $sce,Chats, gatewayService){
  var sale_package_id =  $stateParams.sale_package_id;
  var pay_user_id = $stateParams.pay_user_id;
  $scope.buy_url = "";

  $scope.trustSrc = function(src) {
        return $sce.trustAsResourceUrl(src);
  }

  console.log("CHat Detail Buy Ctrl:"+sale_package_id+", "+pay_user_id);

  $scope.buy = function(){
        console.log("buy.....");
        var base_url = gatewayService.get_base_url()
        var url= base_url+"/market/pay/alipay?sale_package_id="+sale_package_id
        +"&pay_user_id="+pay_user_id+"&service=alipay.wap.create.direct.pay.by.user";
        $scope.buy_url = url;

      };

  $scope.buy();

})

.controller('AccountCtrl', function($scope,$state, gatewayService) {
 $scope.account = gatewayService.get();

  $scope.settings = {
    enableFriends: true
  };
   
  $scope.signout = function(){
      // gatewayService.signout().success(function(result){
      //  console.log("result = "+result);
      //  $state.go("login");
      // });
      
      $state.go("login");

  }

})
.controller("AccountDetailCtrl", function($scope, $state, gatewayService){
    $scope.account = gatewayService.get();
   

})
.controller("DashCtrlLogin", function($scope,$ionicPopup,$rootScope,$state, $location, $window, gatewayService){
  $scope.account = null;
  $scope.username = "";
  $scope.password= "";
  $scope.remember = false;
  $scope.autologin = false;

  $scope.login_txt = "登录"
  $scope.login_state='done';
  $scope.$on("loginok", function(event, msg){
      console.log("evetn fire ---"+event);
  });

  $scope.saveParams = function(){
      console.log("save:"+$scope.remember+", auto login:"+$scope.autologin);
      
      VPNManager.setParams(function(result){},function(err){

      }, {'remember': $scope.remember,'autologin': $scope.autologin});


  };

  $scope.loadParams = function(){

      VPNManager.getParams(function(result){
              console.log("get params:"+JSON.stringify(result));
              if(result.remember!=undefined){
                $scope.remember = result.remember;
                $scope.autologin = result.autologin;
                $scope.$digest();
                
                if(result.autologin){
                    console.log("auto login!!");
                    $scope.loadUser(true);
                }
                else if(result.remember){
                    $scope.loadUser(false);
                }

              }



      });
  };

  //载入本地存贮用户.
  $scope.loadUser = function(islogin){

    VPNManager.loadUser(function(result){
        console.log("Load Local Saved User:"+JSON.stringify(result));
        //var acct = {'user': result.vpnUsername,'password': result.vpnPassword, ''}
        gatewayService.set(result);
        $scope.account = gatewayService.get();
        $scope.username = result.vpnUsername;
        $scope.password = result.vpnPassword;
        if(islogin)  $scope.login();


    },function(err){
        console.log("Load Local Saved User Failed:"+err);
        $state.go("login");
    });
  };

  
  window.setTimeout($scope.loadParams, 1000);

  $scope.login = function(){
     console.log("login.....");
     $scope.login_txt = "登录中..."
     $scope.login_state='ing';
     //$scope.$digest();
     console.log("username="+this.username+", password="+this.password);

     $scope.username = this.username;
     $scope.password=  this.password;

    //var password = $scope.password

    gatewayService.signin($scope.username, $scope.password).success(function(result){
        console.log("gatewayService.sigin response->"+result);
        if (result.indexOf("<!") != -1) {
            ionicAlert($ionicPopup, "登陆失败","无法识别的错误");
            gatewayService.set(null);
            return ;
        }
        else {

          result = gatewayService.decode(result)

          console.log("Login Result->"+JSON.stringify(result));

          if(result.expiration==null){
            ionicAlert($ionicPopup,"登陆失败","密码错误");
            $scope.login_state='done';
            $scope.login_txt = "登录";
            return ;
          };
          result['login'] = $scope.username;
          result['password'] = $scope.password;

          gatewayService.set(result);

          $scope.login_state='done';

          $scope.login_txt = "登录";

          result['password'] = $scope.password;

          reg_global_login($scope);

          if($scope.remember) {
              
              console.log("save ....username = "+$scope.username+", pass="+$scope.password);

              //save User Name
              var options = {'vpnHostName': 'not set', 'vpnCert': '', 'vpnUsername': $scope.username,
                    'vpnPassword': $scope.password, 'vpnHost': 'not set','vpnCert': ''};
              console.log("保存当前登录用户->"+JSON.stringify(options));

                VPNManager.saveUser(function(result){
                            console.log("保存账户到本地成功:"+result);

                         }, function(err){
                            console.log("保存账户到本地失败:"+err);
                         }, options);
          }

          $state.go("tab.vpn", result);
          $rootScope.$broadcast("loginok",result);

        }

    }).error(function(err){
      var alertPopup = $ionicPopup.alert({
               title: '登录失败',
               template: '无法登录:'+err
             });
      $scope.login_txt = "登录";
      $scope.login_state='done';
    });
  };

  //载入服务到列表.
  $scope.loadServerList = function(){
       console.log("try to fetch server list");
      gatewayService.get_server().success(function(result){
        if (result.indexOf("<!") != -1) {
            ionicAlert($ionicPopup,"故障","无法获取服务器列表");
          }
        else {
          var result = gatewayService.decode(result)
          //console.log("get server List->"+JSON.stringify(result));
          
          var groups = {}
          // 把服务器组也作为一个Item.
          for(var k in result.grouplist){
            // console.log(" s=>"+k);
            groups[result.grouplist[k]] = {'name': k,'serverlist':[]}
          };

          //console.log("ServerGroups->"+JSON.stringify(groups));

          // {‘groupname':xxx, 'serverlist': [{'hostip','xxx'}]}
          for(var s in result.serverlist){
              //console.log("Server -> "+ JSON.stringify(result.serverlist[s]));
              var serv = result.serverlist[s]
              if (groups[serv.servergroup] != undefined){
                groups[serv.servergroup].serverlist.push(serv);
              }
          };

          console.log("Groups:->"+JSON.stringify(groups));
          
          

          $scope.translate(groups);

          //$rootScope.broadcast("serverload", serverGroups);

      }});

  };

  var id=0;
  $scope.translate = function(groups){
      var serverGroups = [];
       var gplist = [];
       var srvlist = [];
       for(var g in groups){
              // serverGroups.push({'isgroup': true,'name': groups[g].name});
              
              gplist.push({'name': groups[g].name,'id': g});
              var servlist = groups[g].serverlist;
              
              for(var s in servlist){
                servlist[s]['isgroup'] = false;
                servlist[s]['id'] = id;
                servlist[s]['servname'] = servlist[s]['name'];
                id+=1; 
                //serverGroups.push(servlist[s]);
                
              }

              srvlist.push(servlist);

          };

          console.log(JSON.stringify(serverGroups));

          

          // $scope.serverGroups = serverGroups;
          $scope.grouplist = gplist;
          $scope.serverlist = srvlist;

          console.log("groups list:"+JSON.stringify($scope.grouplist));
          console.log("server list:"+JSON.stringify($scope.serverlist));

          serverGroups = [gplist,srvlist];
          gatewayService.setServerGroup(serverGroups);

         
  };


  $scope.loadServerList();

  $scope.$on("user_charge_ok", function(event,info){
    console.log("user charge ok try to login");
    $scope.login();
  });

  // if(gatewayService.get()==null){

  //     $state.go("tab.dash", {});

  // };

  $scope.register = function(){
    console.log("try to account register");
    $state.go("reg");
  };

  $scope.focusInput = function(){
      console.log("alet");
        
        var pass = angular.element(document.getElementById("login-panel"));
        var realTop = 10;
          
        pass.css('transform', 'translate3d(0px,' + '-' + realTop + 'px, 0px) scale(1)');
        console.log(pass);
        pass.addClass("pop");

  }


})

.controller("AccountRegCtrl", function($scope,$ionicPopup, $state, gatewayService){
  $scope.reg = null;
  $scope.login = null;
  $scope.password = null;
  $scope.reg_state = null;
  $scope.reg_txt = "快速注册";
  $scope.back = function(){
      $state.go("login");
  };

  $scope.register = function(){
    if(this.login==null || this.login==""
      || this.password==null || this.password=="" ||
      this.password2==null || this.password2=="")
    {
        ionicAlert($ionicPopup, '验证失败','用户名密码必须填！');
        return ;
    }

    if(this.password != this.password2){
      ionicAlert($ionicPopup, '验证失败','2次输入密码不一致');
      return ;
    }

    console.log("signup ->"+this.login+", pass="+this.password);
    $scope.login = this.login;

    $scope.reg_txt="正在注册,请稍等";
    $scope.reg_state = "ing";

    gatewayService.signup(this.login, this.password).success(function(result){
        console.log(" register login "+JSON.stringify(result));

         // if (result.indexOf("<!") != -1 ) {
         //    ionicAlert($ionicPopup, "注册失败","无法识别的错误");
         //    return ;
         // }
         //服务端返回显式错误.
          $scope.reg_state = "done";
          $scope.reg_txt = "快速注册";
          if(result.result=='failure'){
           ionicAlert($ionicPopup, "注册失败","该用户名已被注册!");
            return ;
          }
          else {

            ionicAlert($ionicPopup,'注册成功',"恭喜您:"+$scope.login+',注册成功');
            $state.go("login");
          }

    }).error(function(err){
      ionicAlert($ionicPopup,'注册失败','注册失败,原因('+err+")");
      $scope.reg_state = "done";
      $scope.reg_txt = "快速注册";
      return ;

    });
  };

});
