module = angular.module('starter.services', []);

var base_url = "http://www.totobo.com.cn/"
var access_url = "http://www.totobo.com.cn/clientV2_android.php";
var news_url = "http://m.baidu.com";
var default_groupid = "100000";
var rc4_preshare_key = "rQlWqT9QhSN";

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

    return {
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
    }

    }

});



