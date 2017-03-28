//
//  VPNManager.js
//

cordova.define("cordova-plugin-vpn.vpnmanager", function(require, exports, module) {

var exec = require('cordova/exec');
console.log("cordova plugin vpn init....");

var VPNManager= {
    isVpnCapable: function(success, error, options) {
        cordova.exec(success, error, "VPNManager", "isVpnCapable", [options]);
    },
    needsProfile: function(success, error, options) {
        cordova.exec(success, error, "VPNManager", "needsProfile", [options]);
    },
    // attempt to start the vpn
    enable: function(success, error, options) {
        cordova.exec(success, error, "VPNManager", "enable", [options]);
    },
    // attempt to disable the vpn
    disable: function(success, error, options) {
        cordova.exec(success, error, "VPNManager", "disable", [options]);
    },
    provision: function(success, error, options) {
        cordova.exec(success, error, "VPNManager", "provision", [options]);
    },
    status: function(success, error, options) {
        cordova.exec(success, error, "VPNManager", "status", [options]);
    },
    registerCallback: function(success, error, options) {
        cordova.exec(success, error, "VPNManager", "registerCallback", [options]);
    },
    traffic_stats: function(success,error, options){
      cordova.exec(success, error, "VPNManager", "traffic_stats", [options]);
    },
    saveUser:function(success, error, options){
      cordova.exec(success, error, "VPNManager", "saveUser", [options]);
    },
    loadUser:function(success, error, options){
         cordova.exec(success, error, "VPNManager", "loadUser", [options]);
    },
    unregisterCallback: function(success, error, options) {
        cordova.exec(success, error, "VPNManager", "unregisterCallback", [options]);
    },
    
    setParams:function(success, error,options){
        cordova.exec(success,error,"VPNManager","setParams",[options]);
    },

    getParams:function(success, error, options){
        cordova.exec(success,error,"VPNManager","getParams", [options]);
    }

};

module.exports = VPNManager;

});

