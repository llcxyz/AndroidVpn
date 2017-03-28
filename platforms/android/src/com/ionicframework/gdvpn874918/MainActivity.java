package com.ionicframework.gdvpn874918;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;

import org.apache.cordova.CordovaActivity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.GeneralSecurityException;

public class MainActivity extends CordovaActivity
{

  public static final boolean USE_BYOD = true;

  private static final int PREPARE_VPN_SERVICE = 0;


  @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        MobclickAgent.setScenarioType(this.getApplicationContext(), MobclickAgent.EScenarioType.E_UM_NORMAL);
        // Set by <content src="index.html" /> in config.xml

//       new Thread(new Runnable() {
//         @Override
//         public void run() {
//
//           try {
//             Thread.sleep(2);
//           } catch (InterruptedException e) {
//             e.printStackTrace();
//           }
//           testEncrytt();
//
//         }
//       }).start();


//      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//        WebView webView = (WebView)super.appView;
//        CookieManager cookieManager = CookieManager.getInstance();
//        cookieManager.setAcceptThirdPartyCookies(webView,true);
//      }


        loadUrl(launchUrl);


//      Process.myUid();
//      ZvpnProfile profile = new ZvpnProfile();
//      profile.setUsername("aaaaa");
//      profile.setGateway("192.168.60.253");
//      profile.setVpnType(ZvpnType.PPTP);
//      profile.setPassword("aaaaa");
//
//
//      Zvpn z = new Zvpn(profile, this.getApplicationContext(),this);
//      z.prepare();
//      z.startLegacyVpn(profile);

    }


  public void testVpn(){

    ConnectivityManager connectivityManager =
      (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

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


      //实例化mService
      //该get()方法来自java.lang.reflect.Field
      //一定要注意该get()方法的参数:
      //它是mService所属类的对象
      //完整例子请参见:
      //http://blog.csdn.net/lfdfhl/article/details/13509839
      iConnectivityManagerObject = connectivityManagerField.get(connectivityManager);
      //得到mService所属接口的Class
      iConnectivityManagerClass = Class.forName(iConnectivityManagerObject.getClass().getName());
      //取得IConnectivityManager接口中的setMobileDataEnabled(boolean)方法
      //该方法来自java.lang.Class.getDeclaredMethod
      Method[] methods = iConnectivityManagerClass.getDeclaredMethods();
      for (final Method method : methods) {

        Log.i("TESTING", "Method: " + method.getName());

      }
      Class vpnProfileClass  = Class.forName("com.android.internal.net.VpnProfile");

      System.out.println("vpn Porfile Class:"+vpnProfileClass);
      setMobileDataEnabledMethod =
        iConnectivityManagerClass.getDeclaredMethod("startLegacyVpn", vpnProfileClass);
      //取消访问私有方法的合法性检查
      //该方法来自java.lang.reflect.AccessibleObject
      setMobileDataEnabledMethod.setAccessible(true);
      //调用setMobileDataEnabled方法

      setMobileDataEnabledMethod.invoke(iConnectivityManagerObject,true);
      System.out.println("Calling Mobile ......OFF");

    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }


  public void onResume(){
    super.onResume();
  }

  private void testEncrytt(){
    String pwd = "1234567812345678";
    String encryp_data = "IdL5heksc9KZp73oP6VFGw==";
    Log.i(TAG, new String(Base64.decode("VYADCmolMHXFe0qu7rxeQQ==",Base64.NO_WRAP)));
    try {
      String data = AESHelper.AesDecrypt(pwd,encryp_data);
      Log.e(TAG,"解密数据:"+data);

    } catch (GeneralSecurityException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
