package com.ar.vpn;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;

import android.net.VpnService;
import android.os.UserHandle;
import android.util.Log;

import org.apache.cordova.CordovaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by aron on 2016/10/13.
 */
public class Zvpn {
  private static String TAG = "Zvpn";

  private Object IConnectivityManager ;
  private Context context;

  private ZvpnProfileFactory factory;
  private  ZvpnProfile profile;

  private Activity activity;

  public Zvpn(ZvpnProfile profile, Context context, Activity activity){
    this.context= context;
    this.activity = activity;
    this.profile = profile;

    IConnectivityManager = Reflect.iConnectivityManagerInstance(context);
    factory = new ZvpnProfileFactory();
  }

  public static final String DIALOGS_PACKAGE = "com.android.vpndialogs";

  protected void onActivityResult(int request, int result, Intent data) {
    if (result == this.activity.RESULT_OK) {
      startLegacyVpn(this.profile);
    }
  }

  public Intent prepare(){

    PackageManager pm = this.context.getPackageManager();
    try {

      ApplicationInfo app = pm.getApplicationInfo(DIALOGS_PACKAGE, 0);
      Log.d(TAG, "APP ID:"+app.uid);

    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }

    Intent intent = VpnService.prepare(this.context);
    Log.d(TAG, "iNETNT=:"+intent);
    if (intent != null) {
      this.activity.startActivityForResult(intent, 0);
    } else {
        onActivityResult(0, this.activity.RESULT_OK, null);
    }

//    Object ret = Reflect.call2(IConnectivityManager,"prepareVpn",  null,context.getPackageName(), String.class,String.class);
//    Log.d(TAG, "Zvpn Prepare:"+ret);
//VpnConfig.DIALOGS_PACKAGE
//    Intent intent = new Intent();
//
//    intent.setClassName(DIALOGS_PACKAGE, DIALOGS_PACKAGE + ".ConfirmDialog");
//    return intent;
    return null;
  }
  public void startLegacyVpn(ZvpnProfile profile){

   // Intent intent = prepare();

    // this.activity.startActivityForResult( intent, 0);
    Log.d(TAG, "START LEGACYVPN.....");
    Class vpnProfileClass = Reflect.getLegeryVpnProfileClass();
    Reflect.call(IConnectivityManager, "startLegacyVpn", vpnProfileClass,factory.getProfile(profile));

  }
}
