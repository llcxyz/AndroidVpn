cordova.define("cordova-plugin-alipay.alipay", function(require, exports, module) {

  var exec = require('cordova/exec');
  console.log("load alipay plugin");
  module.exports = {
      pay: function(orderInfo,onSuccess,onError){
          exec(onSuccess, onError, "Alipay", "pay", [orderInfo]);
      }
  };

});



/**

navigator.weixin.pay({"seller":"007slm@163.com",subject":"x51","body":"x5企业版","price":"0.01","tradeNo":"123456","timeout":"30m","notifyUrl":"wwww.justep.com"},function(msgCode){alert(msgCode)},function(msg){alert(msg)})
**/



