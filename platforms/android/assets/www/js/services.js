module = angular.module('starter.services', []);

var base_url = "http://www.assss.com.cn/"
var access_url = "http://www.assss.cn/clientV2_android.php";
var news_url = "http://m.baidu.com";
var default_groupid = "100000";
var rc4_preshare_key = "vr3lWqT9QhSN";

var pay_url = "http://www.67hj.cn/getkeyandroid";

module.factory('Chats', function($http) {

  var chats =[]

  return {

    set:function(cts){
      chats = cts;
    },

    all: function() {
        return $http.get(access_url+"/get_sale_packages")
    },
    remove: function(chat) {
      chats.splice(chats.indexOf(chat), 1);
    },
    get: function(chatId) {
      for (var i = 0; i < chats.length; i++) {
        if (chats[i].id === parseInt(chatId)) {
          return chats[i];
        }
      }
      return null;
    }
  };
});

module.factory("gatewayService" , function($http){

    var account = null;
    var serverGroup = null;
    var request = function(msg){
       var encrypt_text = bin2hex(rc4(JSON.stringify(msg),rc4_preshare_key));
       return $http.get(access_url+"?"+encrypt_text);
    };
    var login = null;
    var pwd = null;

    var sale_package = [
      {'id': 1, 'title': '月付','desc': '30天','price': 30, 'save':null, 'sprice': null},
      {'id': 3, 'title': '季付','desc': '90天','price': 80, 'save':'省10元', 'sprice': '90.00'},
      {'id': 4, 'title': '半年','desc': '186天','price': 155, 'save':'省25元', 'sprice': '180.00'},
      {'id': 5, 'title': '全年','desc': '372天','price': 300, 'save':'省60元', 'sprice': '360.00'},
      {'id': 23, 'title': '两年','desc': '744天','price': 598, 'save':'省122元', 'sprice': '720.00'}

      ];

    return {

    get_sale_packages:function(){
        return sale_package;
    },

    setServerGroup:function(serverGroups){
        serverGroup = serverGroups;
    },
    getServerGroup:function(){
        return serverGroup;
    },
    set:function(acct){
      account = acct;
    },
    get_access_url:function(){
      return access_url;
    },
    get_base_url:function(){
      return base_url;
    },
    get:function(){
      return account;
    },

    decode:function(result){
        return JSON.parse(rc4(hex2bin(result),rc4_preshare_key))
    },

    signin: function(login, pwd){
      var msg = {"groupid":default_groupid,"action":"cheuser","user":login,"pwd":pwd};

      return request(msg);

    },

    signup:function(login, pwd){
      var msg = {"groupid":100000,"action":"reguser","user":login,"pwd":pwd,
      "grou":2030438,"name":"android","qmsn":"android","phoe":"android","addr":"android"}
      return request(msg);
    },

    signout:function(){
       return $http.get(access_url+"/signout");
    },
    
    get_server:function(){
      var msg = {"groupid":100000,"action":"server"}
      return request(msg);
    },

    get_cert:function(hostip){
      var msg = {"groupid":100000,"action":"get_x509","ip":hostip}
      return request(msg);
    },

    get_user_info: function(login, pwd){

      var msg = {"groupid":default_groupid,"action":"cheuser","user":login,"pwd":pwd};
      return request(msg);

    },

    save_local:function(options){
      return VPNManager.saveUser;
    },

    get_order:function(options){
        return $http.post(pay_url,options, {'headers':{'Content-Type': 'application/x-www-form-urlencoded'}});
    },

    pay:function(options){
      return 
    }

    }

});



