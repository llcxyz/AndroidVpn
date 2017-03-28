package com.ar.vpn;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by aron on 2016/10/13.
 */
public class Reflect {

  public static Object iConnectivityManagerInstance(Context context){
    ConnectivityManager connectivityManager =
      (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    //ConnectivityManager类
    Class<?> connectivityManagerClass = null;
    //ConnectivityManager类中的字段
    Field connectivityManagerField = null;

    //IConnectivityManager接口
    Class<?> iConnectivityManagerClass = null;
    //IConnectivityManager接口的对象
    Object iConnectivityManagerObject = null;
    //IConnectivityManager接口的对象的方法
    Method setMobileDataEnabledMethod = null;

    try {
      //取得ConnectivityManager类
      connectivityManagerClass = Class.forName(connectivityManager.getClass().getName());
      //取得ConnectivityManager类中的字段mService
      connectivityManagerField = connectivityManagerClass.getDeclaredField("mService");
      //取消访问私有字段的合法性检查
      //该方法来自java.lang.reflect.AccessibleObject
      connectivityManagerField.setAccessible(true);

      iConnectivityManagerObject = connectivityManagerField.get(connectivityManager);
      //得到mService所属接口的Class
      iConnectivityManagerClass = Class.forName(iConnectivityManagerObject.getClass().getName());
      //取得IConnectivityManager接口中的setMobileDataEnabled(boolean)方法
      //该方法来自java.lang.Class.getDeclaredMethod
      Method[] methods = iConnectivityManagerClass.getDeclaredMethods();
      for (final Method method : methods) {
          Log.i("Reflect", "IConnectivityManager Method: " + method.getName());
      }

    }  catch (NoSuchFieldException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return iConnectivityManagerObject;

  }

  private  Object IConnectivityManagerInstance1111(Context context){
    ConnectivityManager connectivityManager =
      (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    //ConnectivityManager类
    Class<?> connectivityManagerClass = null;
    //ConnectivityManager类中的字段
    Field connectivityManagerField = null;

    //IConnectivityManager接口
    Class<?> iConnectivityManagerClass = null;
    //IConnectivityManager接口的对象
    Object iConnectivityManagerObject = null;
    //IConnectivityManager接口的对象的方法
    Method setMobileDataEnabledMethod = null;

    try {
      //取得ConnectivityManager类
      connectivityManagerClass = Class.forName(connectivityManager.getClass().getName());
      //取得ConnectivityManager类中的字段mService
      connectivityManagerField = connectivityManagerClass.getDeclaredField("mService");
      //取消访问私有字段的合法性检查
      //该方法来自java.lang.reflect.AccessibleObject
      connectivityManagerField.setAccessible(true);

      iConnectivityManagerObject = connectivityManagerField.get(connectivityManager);
      //得到mService所属接口的Class
      iConnectivityManagerClass = Class.forName(iConnectivityManagerObject.getClass().getName());
      //取得IConnectivityManager接口中的setMobileDataEnabled(boolean)方法
      //该方法来自java.lang.Class.getDeclaredMethod
      Method[] methods = iConnectivityManagerClass.getDeclaredMethods();
      for (final Method method : methods) {
        Log.i("Reflect", "IConnectivityManager Method: " + method.getName());
      }

      Class vpnProfileClass = Class.forName("com.android.internal.net.VpnProfile");
      System.out.println("vpn Porfile Class:" + vpnProfileClass);
      setMobileDataEnabledMethod =
        iConnectivityManagerClass.getDeclaredMethod("startLegacyVpn", vpnProfileClass);
      //取消访问私有方法的合法性检查
      //该方法来自java.lang.reflect.AccessibleObject
      setMobileDataEnabledMethod.setAccessible(true);
      //调用setMobileDataEnabled方法

      setMobileDataEnabledMethod.invoke(iConnectivityManagerObject, true);
      System.out.println("Calling Mobile ......OFF");
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static Class getLegeryVpnProfileClass(){
    try {
      Class vpnProfileClass = Class.forName("com.android.internal.net.VpnProfile");
      return vpnProfileClass;

    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static Object call(Object object, String method, Class type, Object value){

    try {
      Method m = object.getClass().getDeclaredMethod(method, type);
      return m.invoke(object, value);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;

  }
  public static Object call2(Object object, String method, Object value, Object value2,  Class<?>... parameterTypes){

    try {
      Method m = object.getClass().getDeclaredMethod(method, parameterTypes);
      return m.invoke(object, value,value2);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;

  }


  public static void set(Object object, String fieldName, Object value){
    Field fieldServer = null;
    try {
      fieldServer = object.getClass().getField(fieldName);
      fieldServer.setAccessible(true);
      fieldServer.set(object, value);

    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }

  }
  public static void setInt(Object object, String fieldName, int value){
    Field fieldServer = null;
    try {
      fieldServer = object.getClass().getField(fieldName);
      fieldServer.setAccessible(true);
      fieldServer.setInt(object, value);

    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }

  }

}
