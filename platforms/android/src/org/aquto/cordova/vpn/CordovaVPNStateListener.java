package org.aquto.cordova.vpn;

import android.util.Log;

import org.apache.cordova.*;
import org.strongswan.android.logic.VpnStateService;

public class CordovaVPNStateListener implements VpnStateService.VpnStateListener {

    private String TAG = "CordovaVPNStateListener";

    private CallbackContext callbackContext;
    private VpnStateService mService;

    public CordovaVPNStateListener(CallbackContext _callbackContext, VpnStateService _mService) {
        callbackContext = _callbackContext;
        mService = _mService;
    }
  public void setCallbackContext(CallbackContext context){
    this.callbackContext = context;
  }

    @Override
    public void stateChanged() {
        VpnStateService.ErrorState eState = mService.getErrorState();
        VpnStateService.State newState = mService.getState();
          Log.d(TAG,"CordovaVPNStateListener callback="+callbackContext);
        Log.d(TAG, "CordovaVPNStateListener  state changed ->"+StateConversion.stateToString(newState));
        Log.d(TAG, "CordovaVPNStateListener  error state changed ->"+StateConversion.errorToString(eState));

        PluginResult pr;
        if(eState != VpnStateService.ErrorState.NO_ERROR)
            pr = new PluginResult(PluginResult.Status.ERROR, StateConversion.errorToString(eState));
        else
            pr = new PluginResult(PluginResult.Status.OK, StateConversion.stateToString(newState));
        pr.setKeepCallback(true);
        Log.d(TAG, "SEND CALLBACK "+pr);
        callbackContext.sendPluginResult(pr);
    }
}
