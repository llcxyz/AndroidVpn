package com.ar.vpn;

import android.util.Log;

import org.strongswan.android.data.VpnProfile;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by aron on 2016/10/13.
 */
public class ZvpnProfileFactory {

  public Object getProfile(ZvpnProfile profile){
    if(profile.getVpnType().equals(ZvpnType.IKEV2_CERT_EAP) || profile.getVpnType().equals(ZvpnType.IKEV2_EAP)){
        return StrongsWanVpnProfile(profile);
    }
    else return LegacyVpnProfile(profile);
  }

  private  VpnProfile StrongsWanVpnProfile(ZvpnProfile profile){
    return new VpnProfile();
  }

  private Object LegacyVpnProfile(ZvpnProfile profile){

    Object vpnProfile = null;

    try {
      Class vpnProfileClass = Class.forName("com.android.internal.net.VpnProfile");
      Constructor[] constructors = vpnProfileClass.getConstructors();
      for(Constructor c: constructors){
        Log.d("ZVPNProfileFactory", "Constructor:"+c.toGenericString());
      }
      vpnProfile = vpnProfileClass.getConstructors()[1].newInstance(profile.getKey());

      Reflect.set(vpnProfile,"server", profile.getGateway());
      Reflect.set(vpnProfile,"username", profile.getUsername());
      Reflect.set(vpnProfile, "password", profile.getPassword());
      Reflect.set(vpnProfile, "ipsecSecret",profile.getPsk());

    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }

    return vpnProfile;
  }
}
