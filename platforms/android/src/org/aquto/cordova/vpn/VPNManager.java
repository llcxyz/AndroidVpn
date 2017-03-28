package org.aquto.cordova.vpn;

import android.content.*;
import android.content.pm.PackageManager;
import android.app.Service;
import android.net.*;
import android.os.*;
import android.util.Base64;
import android.util.Log;

import com.ionicframework.gdvpn874918.MainActivity;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.cordova.*;
import org.strongswan.android.logic.*;
import org.strongswan.android.data.*;
import org.json.*;
import org.strongswan.android.security.LocalCertificateStore;
import org.strongswan.android.security.TrustedCertificateEntry;

public class VPNManager extends CordovaPlugin  implements  VpnStateService.VpnStateListener{

  private static final String LOG_TAG = "VPNManager";

  private CallbackContext traffic_callbackContext;

  private CallbackContext status_callbackContext;


  private CallbackContext callbackcontext;


  private static final String  params_db = "params";


  @Override
  public void stateChanged() {

    VpnStateService.ErrorState eState = mService.getErrorState();
    VpnStateService.State newState = mService.getState();
//    Log.d(TAG, "CordovaVPNStateListener  state changed ->"+StateConversion.stateToString(newState));
//    Log.d(TAG, "CordovaVPNStateListener  error state changed ->"+StateConversion.errorToString(eState));

//    PluginResult pr;
//    if(eState != VpnStateService.ErrorState.NO_ERROR)
//      pr = new PluginResult(PluginResult.Status.ERROR, StateConversion.errorToString(eState));
//    else
//      pr = new PluginResult(PluginResult.Status.OK, StateConversion.stateToString(newState));
//    pr.setKeepCallback(true);
//    callbackContext.sendPluginResult(pr);

//    if(newState== VpnStateService.State.CONNECTED){
//      if(trafficThread!=null){
//        trafficThread.start();
//      }
//      else{
//        trafficThread = new tfThread();
//        trafficThread.start();
//
//      }
//
//    }
//    else if(newState== VpnStateService.State.DISABLED){
//      if(trafficThread!=null){
//        trafficThread.stop_loop=true;
//        trafficThread=null;
//      }
//    }
  }

  public enum ErrorCode {
        NOT_SUPPORTED,
        MISSING_FIELDS,
        UNKNOWN_ERROR,
        PERMISSION_NOT_GRANTED,
        DISALLOWED_NETWORK_TYPE
    }

    private final class PluginActions {
        public static final String NEEDS_PROFILE = "needsProfile";
        public static final String STATUS = "status";
        public static final String IS_VPN_CAPABLE = "isVpnCapable";
        public static final String ENABLE = "enable";
        public static final String DISABLE = "disable";
        public static final String REGISTER_CALLBACK = "registerCallback";
        public static final String UNREGISTER_CALLBACK = "unregisterCallback";
        public static final String TRAFFIC_STATS = "traffic_stats";
        public static final String SAVE_USER = "saveUser";
        public static final String LOAD_USER = "loadUser";
        public static final String GET_PARAMS = "getParams";
        public static final String SET_PARAMS = "setParams";

    }

    private final class JSONParameters {
        public static final String VPN_HOST = "vpnHost";
        public static final String VPN_USERNAME = "vpnUsername";
        public static final String VPN_PASSWORD = "vpnPassword";
        public static final String UP = "up";
        public static final String USER_CERTIFICATE = "userCertificate";
        public static final String USER_CERTIFICATE_PASSWORD = "userCertificatePassword";
        public static final String CA_CERTIFICATE = "caCertificate";
        public static final String VPN_HOST_NAME = "vpnHostName";
         public static final String VPN_CERT = "vpnCert";


    }

    private static final String TAG = VPNManager.class.getSimpleName();
    private static final int RESULT_OK = -1;
    private static final int PREPARE_VPN_SERVICE = 0;

    private ConnectionValidityChecker validityChecker;
    private CallbackContext callbackContext;
    private VpnProfile vpnInfo;
    private VpnStateService mService;
    private CordovaVPNStateListener stateListener;

    private VpnProfileDataSource dataSource;

    private tfThread  trafficThread  = null;



  private final Object mServiceLock = new Object();
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            synchronized(mServiceLock) {
                mService = null;
            }
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            synchronized(mServiceLock) {
                mService = ((VpnStateService.LocalBinder)service).getService();
                mService.registerListener(VPNManager.this);
            }
        }
    };

  public String getMD5(String info)
  {
    try
    {
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      md5.update(info.getBytes("UTF-8"));
      byte[] encryption = md5.digest();

      StringBuffer strBuf = new StringBuffer();
      for (int i = 0; i < encryption.length; i++)
      {
        if (Integer.toHexString(0xff & encryption[i]).length() == 1)
        {
          strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
        }
        else
        {
          strBuf.append(Integer.toHexString(0xff & encryption[i]));
        }
      }

      return strBuf.toString();
    }
    catch (NoSuchAlgorithmException e)
    {
      return "";
    }
    catch (UnsupportedEncodingException e)
    {
      return "";
    }
  }

    @Override
    public void initialize(final CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
//        if(validityChecker==null) {
//          validityChecker = new ConnectionValidityChecker(cordova.getActivity());
//          validityChecker.register();
//        }
        Intent stateIntent = new Intent(cordova.getActivity(), VpnStateService.class);
        cordova.getActivity().startService(stateIntent);
        cordova.getActivity().bindService(stateIntent, mServiceConnection, Service.BIND_AUTO_CREATE);
      Log.w(LOG_TAG, "VPN Plugin initialize"+cordova);
      if(dataSource==null) {
        dataSource = new VpnProfileDataSource(cordova.getActivity());
        dataSource.open();
      }
      if(trafficThread==null) {
        trafficThread = new tfThread();
      }

    }

    @Override
    public void onDestroy() {
        if(mService != null)
            cordova.getActivity().unbindService(mServiceConnection);
        //validityChecker.unregister();

    }

  @Override
  public void onResume(boolean multitasking) {
    if(stateListener!=null){
      System.out.println("state listener...."+stateListener);
      stateListener.stateChanged();
    }

  }

    private PluginResult error(ErrorCode error) {
        return new PluginResult(PluginResult.Status.ERROR, error.toString());
    }

    /**
    * Prepare the VpnService. If this succeeds the current VPN profile is
    * started.
    * @param profileInfo a bundle containing the information about the profile to be started
    */
    protected PluginResult prepareVpnService(final VpnProfile profile, CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
        this.vpnInfo = profile;
        Intent intent;
        try {
            intent = VpnService.prepare(cordova.getActivity().getApplicationContext());
        } catch(IllegalStateException ex) {
            /* this happens if the always-on VPN feature (Android 4.2+) is activated */
            return error(ErrorCode.NOT_SUPPORTED);
        }
        if(intent != null) {
            try {
                Log.d(TAG, "Prepare Vpn Service Intent"+intent);
                cordova.startActivityForResult((CordovaPlugin)this, intent, PREPARE_VPN_SERVICE);
                // MainActivity.this.startActivityForResult(intent, PREPARE_VPN_SERVICE);

                return new PluginResult(PluginResult.Status.OK, VpnStateService.State.CONNECTING.toString());

            } catch(ActivityNotFoundException ex) {
                /* it seems some devices, even though they come with Android 4,
                * don't have the VPN components built into the system image.
                * com.android.vpndialogs/com.android.vpndialogs.ConfirmDialog
                * will not be found then */
                return error(ErrorCode.NOT_SUPPORTED);
            }
        } else {
            /* user already granted permission to use VpnService */
//            Log.d(LOG_TAG, "enable Connection.....");
//            cordova.getThreadPool().submit(new Runnable() {
//              @Override
//              public void run() {
//                enableConnection(profile);
//              }
//            });

          onActivityResult(PREPARE_VPN_SERVICE, RESULT_OK, intent);

          //enableConnection(profile);
            return new PluginResult(PluginResult.Status.OK, VpnStateService.State.CONNECTING.toString());
        }
    }

    private void enableConnection(VpnProfile profile) {

        Intent cintent = new Intent(cordova.getActivity(), CharonVpnService.class);
      Bundle mProfile  = new Bundle();
      mProfile.putLong("_id", profile.getId());
      mProfile.putString("gatway", profile.getGateway());
      mProfile.putString("username", profile.getUsername());
      mProfile.putString("password", profile.getPassword());
     // mProfile.putString("certificateAlias", profile.getCertificateAlias());
      mProfile.putBoolean("org.strongswan.android.MainActivity.REQUIRES_PASSWORD",true);
      mProfile.putString("vpn_type", VpnType.IKEV2_EAP.getIdentifier());

      cintent.putExtras(mProfile);

      cordova.getActivity().startService(cintent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        switch(requestCode) {
            case PREPARE_VPN_SERVICE:
                if(resultCode == RESULT_OK) {
                 cordova.getThreadPool().submit(new Runnable() {
                   @Override
                   public void run() {
                     enableConnection(vpnInfo);
                   }
                 });
                  //enableConnection(vpnInfo);
                }
                else
                    callbackContext.sendPluginResult(error(ErrorCode.PERMISSION_NOT_GRANTED));
                break;
            default:
                super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    private VpnProfile toVpnProfile(JSONObject provisioningJson) throws Exception {
        String gateway, username, password, name;
        gateway = provisioningJson.getString(JSONParameters.VPN_HOST);
        username = provisioningJson.getString(JSONParameters.VPN_USERNAME);
        password = provisioningJson.getString(JSONParameters.VPN_PASSWORD);
        name = provisioningJson.getString(JSONParameters.VPN_HOST_NAME);
        String cert = provisioningJson.getString(JSONParameters.VPN_CERT);

       // b64UserCert = provisioningJson.getString(JSONParameters.USER_CERTIFICATE);
      //  userCertPassword = provisioningJson.getString(JSONParameters.USER_CERTIFICATE_PASSWORD);
      //  b64CaCert = provisioningJson.getString(JSONParameters.CA_CERTIFICATE);
       Log.d(LOG_TAG, "GATEWAY="+gateway+", username="+username+", password="+password);

        if(gateway == null || username == null || password == null
//          || b64UserCert == null || userCertPassword == null || b64CaCert == null

          ) return null;

//
//      TrustedCertificateManager.TrustedCertificateSource mSource = TrustedCertificateManager.TrustedCertificateSource.USER;
//
//      Hashtable<String,X509Certificate> localObject = TrustedCertificateManager.getInstance().load().getCACertificates(mSource);
//      System.out.println("OK->>>>>:"+localObject);
//
//      ArrayList<TrustedCertificateEntry> localArrayList = new ArrayList();
//      Iterator<Map.Entry<String, X509Certificate>> localObject2 = localObject.entrySet().iterator();
//      while ((localObject2).hasNext())
//      {
//        Map.Entry localEntry = (Map.Entry)((Iterator)localObject2).next();
//        localArrayList.add(new TrustedCertificateEntry((String)localEntry.getKey(), (X509Certificate)localEntry.getValue()));
//      }
//
//      Collections.sort(localArrayList);
//
//      for(TrustedCertificateEntry entry: localArrayList){
//        System.out.println("证书别名->"+entry.getAlias()+":"+entry.getSubjectPrimary());
//
//        String base64 = Base64.encodeToString(entry.getCertificate().getEncoded(),Base64.NO_WRAP);
//        System.out.println("Base64="+base64);
//        cert = base64;
//      }
//
//      LocalCertificateStore st = new LocalCertificateStore();
//
//      for(String alias : st.aliases()){
//        System.out.println("Cert Alias->"+alias);
//      }
       //  cert = "MIIDKDCCAhCgAwIBAgIIFg3v0VPSy94wDQYJKoZIhvcNAQELBQAwMjELMAkGA1UEBhMCQ0gxDzANBgNVBAoTBkZBTlNOQjESMBAGA1UEAxMJRkFOU05CIENBMB4XDTE2MTAxNzA0MjYxN1oXDTE5MTAxNzA0MjYxN1owMjELMAkGA1UEBhMCQ0gxDzANBgNVBAoTBkZBTlNOQjESMBAGA1UEAxMJRkFOU05CIENBMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2BM8R5L+ouJSqIxvJAel/pu6GaoMUWtHJB2seT+ppMCND/9ektkLa9J1DZ/GMLSwXYHzoMghd+d3B+HTn6X8IZKZF9cJ7mdc2WIxjg3n8GwFDzPa0bZf1GSEoRYCYnHIEwKBYXypfuS2Jjtl1XLKYoae13q4JqUmWgnxcTrei3HJEFP7h6k4fLLatdkqHkrhC+VA542FQRwdhEdxw9M+BYGbQ6IZU33xC1iSCCtKks1orcnroPmwL3tx21hySs0Z1Q2OCH+g3bvMVrLh5AhD+C4kRH0INqp/xtzWLWYe/N9Gduwan2+3tsYvzWOfyXMZ9dzZyBk9NQZ+jV7uIU3UgwIDAQABo0IwQDAPBgNVHRMBAf8EBTADAQH/MA4GA1UdDwEB/wQEAwIBBjAdBgNVHQ4EFgQUMqjgIbP7XqaCZ1VAgyIgRj23ToowDQYJKoZIhvcNAQELBQADggEBAJvty8UUoIbFAOCuNVOYsJvayQnPNVhoPtz2Ttng8Hg1MePIWytwrbTU/wAXa5cv+rHC5eclDeWwIsBLQ0Qf4J8if95Kp6H2+sz9DfToWDs5H6DJzWptnOy+VIGeM56Z+XXhb62aq7Kvs3CT6L8SX1WZBGKKxUX6qrc6s+SZFqtQS6hxum11TQdxZnCsdML6AV/FgZK82iFoX+Ir1d3Q8/phHNEXrZl8e1ZnKw1DmCi/f684yalz1/jkN2ffSAtcRqvphf6/jyp5R37i46DqmEYMxIQvrBCXwCFoKTznojqxPioEab3UJSHotDim/2HKePYJvjWObK7Mt3DSWWlCiwA=";
        // Prepare the VPN profile object
        VpnProfile vpnInfo = new VpnProfile();
         vpnInfo.setName(name);

        vpnInfo.setGateway(gateway);
        vpnInfo.setUsername(username);
        vpnInfo.setPassword(password);
        vpnInfo.setVpnType(VpnType.IKEV2_EAP);
        vpnInfo.setUserCertificateAlias(cert);
//         userCertPassword = "gdvpn";
//         vpnInfo.setCertificateAlias("user:d02ee339.0");
//         vpnInfo.setUserCertificateAlias("user:d02ee339.0");
        List<VpnProfile> profiles = dataSource.getAllVpnProfiles();
        if(profiles.size()>0){
            vpnInfo.setId(profiles.get(0).getId());
            dataSource.updateVpnProfile(vpnInfo);
        }
        else {
          dataSource.insertProfile(vpnInfo);
        }
       vpnInfo = dataSource.getAllVpnProfiles().get(0);
        //dataSource.close();

         //vpnInfo.setUserCertificatePassword(userCertPassword);

//        // Import the user certificate

//        UserCredentialManager.getInstance().storeCredentials(b64UserCert.getBytes(), userCertPassword.toCharArray());
//
//        // Decode the CA certificate from base64 to an X509Certificate
//        byte[] decoded = android.util.Base64.decode(b64CaCert.getBytes(), 0);
//        CertificateFactory factory = CertificateFactory.getInstance("X.509");
//        InputStream in = new ByteArrayInputStream(decoded);
//        X509Certificate certificate = (X509Certificate)factory.generateCertificate(in);
//
//        // And then import it into the Strongswan LocalCertificateStore
//        KeyStore store = KeyStore.getInstance("LocalCertificateStore");
//        store.load(null, null);
//        store.setCertificateEntry(null, certificate);
//        TrustedCertificateManager.getInstance().reset();

        return vpnInfo;
    }

    private PluginResult handleNeedsProfileAction(CallbackContext callbackContext) {
        return new PluginResult(PluginResult.Status.OK, false);
    }

    private PluginResult handleStatusAction() {
        if(mService != null)
            return new PluginResult(PluginResult.Status.OK, StateConversion.stateToString(mService.getState()));
        else
            return new PluginResult(PluginResult.Status.OK, StateConversion.stateToString(VpnStateService.State.DISABLED));
    }

    private PluginResult handleIsVpnCapableAction() {
        boolean result;
        try {
            final Intent intent = VpnService.prepare(cordova.getActivity());
            final PackageManager packageManager = cordova.getActivity().getPackageManager();
            if(intent != null) {
                List resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                result = (resolveInfo.size() > 0);
            } else {
                /* user already granted permission to use VpnService */
                result = true;
            }
        } catch(IllegalStateException ex) {
            /* this happens if the always-on VPN feature (Android 4.2+) is activated */
            result = false;
        }
        return new PluginResult(PluginResult.Status.OK, result);
    }

    private PluginResult handleEnableAction(JSONArray args, CallbackContext callbackContext) {
      try {

          JSONObject provisioningJson = args.getJSONObject(0);
          VpnProfile profile = toVpnProfile(provisioningJson);
          if(profile == null)
            return error(ErrorCode.MISSING_FIELDS);

          return prepareVpnService(profile, callbackContext);

      } catch(JSONException je) {
        je.printStackTrace();
        return error(ErrorCode.MISSING_FIELDS);
      } catch(Exception e) {
        e.printStackTrace();
        Log.e(TAG, "Unknown error enabling VPN", e);
        return error(ErrorCode.UNKNOWN_ERROR);
      }

    }

    private PluginResult handleDisableAction() {
        // tear down the active VPN connection
        if(mService != null)
            mService.disconnect();
        return new PluginResult(PluginResult.Status.OK, true);
    }

    private PluginResult handleRegisterCallbackAction(CallbackContext callbackContext) {
        if(stateListener == null) {
          //mService.unregisterListener(stateListener);
          Log.d(TAG,"register new CordoveVpnStateListener....");
          stateListener = new CordovaVPNStateListener(callbackContext, mService);
          mService.registerListener(stateListener);
        }

       this.callbackContext = callbackContext;

       Log.d(TAG, "refresh cordoveVpnSate listener, callback="+callbackContext);

       stateListener.setCallbackContext(callbackContext);
       VpnStateService.State newState = mService.getState();
        PluginResult res = new PluginResult(PluginResult.Status.OK, StateConversion.stateToString(newState));
        res.setKeepCallback(true);
        return res;
    }

  private PluginResult handleLoadUserCallbackAction(CallbackContext callbackContext){
    PluginResult pr;
    List<VpnProfile> list = dataSource.getAllVpnProfiles();
    Log.d(TAG, "载入已登录用户"+list.size());
    if(list.size()>0){
        VpnProfile profile = list.get(0);
        JSONObject json = new JSONObject();
      try {
        json.put(JSONParameters.VPN_USERNAME, profile.getUsername());
        json.put(JSONParameters.VPN_PASSWORD, profile.getPassword());
       // json.put("password_md5", getMD5(profile.getPassword()));
        json.put(JSONParameters.VPN_HOST_NAME, profile.getName());
        json.put(JSONParameters.VPN_HOST, profile.getGateway());
        json.put(JSONParameters.VPN_CERT, profile.getUserCertificateAlias());
        pr = new PluginResult(PluginResult.Status.OK, json);
        return pr;
      } catch (JSONException e) {

        e.printStackTrace();

      }

      Log.d(TAG, "无已登录用户");

    }


    return new PluginResult(PluginResult.Status.ERROR, false);

  }

  private PluginResult handleSaveCallbackAction(JSONArray args, CallbackContext callbackContext){

    try {

      JSONObject obj = args.getJSONObject(0);
      String username = obj.getString(JSONParameters.VPN_USERNAME);
      String password = obj.getString(JSONParameters.VPN_PASSWORD);
      String gateway = obj.getString(JSONParameters.VPN_HOST);
      String name = obj.getString(JSONParameters.VPN_HOST_NAME);
      String cert = obj.getString(JSONParameters.VPN_CERT);

      VpnProfile profile = new VpnProfile();

      profile.setUsername(username);
      profile.setPassword(password);
      profile.setName(name);
      profile.setGateway(gateway);
      profile.setUserCertificateAlias(cert);
      profile.setVpnType(VpnType.IKEV2_EAP);

      List<VpnProfile> list = dataSource.getAllVpnProfiles();
      if(list.size()>0){
           profile = list.get(0);
          profile.setUsername(username);
          profile.setPassword(password);
           profile.setName(name);
           profile.setGateway(gateway);
           profile.setUserCertificateAlias(cert);
          dataSource.updateVpnProfile(profile);
      }
      else {
        dataSource.insertProfile(profile);
      }

      return new PluginResult(PluginResult.Status.OK, true);

    } catch (JSONException e) {
      e.printStackTrace();
    }


    return new PluginResult(PluginResult.Status.ERROR,false);


  }

  private PluginResult handleTrafficStatsCallbackAction(CallbackContext callbackContext) {

//    JSONObject j = new JSONObject();
//    try {
//      j.put("totalrxbytes", TrafficStats.getTotalRxBytes());
//      j.put("totaltxbytes", TrafficStats.getTotalTxBytes());
//
//    } catch (JSONException e) {
//      e.printStackTrace();
//    }
    traffic_callbackContext = callbackContext;

    PluginResult pr = new PluginResult(PluginResult.Status.OK, true);
    pr.setKeepCallback(true);

    return pr;

//    callbackContext.sendPluginResult(pr);
//    pr.setKeepCallback(true);
//    return pr;

  }





    private PluginResult handleUnregisterCallbackAction(CallbackContext callbackContext) {
        if(stateListener != null) {
            mService.unregisterListener(stateListener);
            stateListener = null;
        }
        return new PluginResult(PluginResult.Status.OK, true);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
      if(action.equals(PluginActions.NEEDS_PROFILE))
        callbackContext.sendPluginResult(handleNeedsProfileAction(callbackContext));
      else if(action.equals(PluginActions.STATUS))
        callbackContext.sendPluginResult(handleStatusAction());
      else if(action.equals(PluginActions.IS_VPN_CAPABLE))
        callbackContext.sendPluginResult(handleIsVpnCapableAction());
      else if(action.equals(PluginActions.ENABLE))
        callbackContext.sendPluginResult(handleEnableAction(args, callbackContext));
      else if(action.equals(PluginActions.DISABLE))
        callbackContext.sendPluginResult(handleDisableAction());
      else if(action.equals(PluginActions.REGISTER_CALLBACK))
        callbackContext.sendPluginResult(handleRegisterCallbackAction(callbackContext));
      else if(action.equals(PluginActions.UNREGISTER_CALLBACK))
        callbackContext.sendPluginResult(handleUnregisterCallbackAction(callbackContext));
      else if(action.equals(PluginActions.TRAFFIC_STATS)){
        callbackContext.sendPluginResult(handleTrafficStatsCallbackAction(callbackContext));
      }
      else if(action.equals(PluginActions.SAVE_USER)){
        callbackContext.sendPluginResult(handleSaveCallbackAction(args, callbackContext));
      }
      else if(action.equals(PluginActions.LOAD_USER)){
        callbackContext.sendPluginResult(handleLoadUserCallbackAction( callbackContext));
      }
      else if(action.equals(PluginActions.SET_PARAMS)){
        callbackContext.sendPluginResult(handlerSetParamsCallbackAction(args, callbackContext));
      }
      else if(action.equals(PluginActions.GET_PARAMS)){
        callbackContext.sendPluginResult(handlerGetParamsCallbackAction(callbackContext));
      }
      else
        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.INVALID_ACTION, ""));
      return true;


    }

  private PluginResult handlerGetParamsCallbackAction(CallbackContext callbackContext) {


    try {
      SharedPreferences p = cordova.getActivity().getSharedPreferences(params_db, cordova.getActivity().MODE_PRIVATE);
      String json = p.getString("params","{}");
      JSONObject obj = new JSONObject(json);

      return new PluginResult(PluginResult.Status.OK, obj);

    } catch (JSONException e) {
      e.printStackTrace();
    }

    return new PluginResult(PluginResult.Status.ERROR,false);

  }

  private PluginResult handlerSetParamsCallbackAction( JSONArray args, CallbackContext callbackContext) {
    JSONObject obj = null;
    try {
      obj = args.getJSONObject(0);

      SharedPreferences p = cordova.getActivity().getSharedPreferences(params_db, cordova.getActivity().MODE_PRIVATE);
      SharedPreferences.Editor editor = p.edit();
      editor.putString("params", obj.toString());
      editor.commit();
      return new PluginResult(PluginResult.Status.OK, true);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    return new PluginResult(PluginResult.Status.ERROR,false);
  }


  class tfThread extends  Thread {

    public  boolean stop_loop= false;


    @Override
    public void run() {
      JSONObject j = null;
      while(!stop_loop) {

        try {
          Thread.sleep(3000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        try {
          j = new JSONObject();
          j.put("totalrxbytes", TrafficStats.getTotalRxBytes());
          j.put("totaltxbytes", TrafficStats.getTotalTxBytes());

        } catch (JSONException e) {
          e.printStackTrace();
        }
        Log.d(TAG, "traffic_callback_context="+traffic_callbackContext+", j="+j);

        if (traffic_callbackContext != null && j != null) {
          PluginResult pr;
          pr = new PluginResult(PluginResult.Status.OK, j);
          pr.setKeepCallback(true);
          callbackContext.sendPluginResult(pr);
          Log.d(TAG, "发送流量统计信息");

        }
      }

    }
  }
}
