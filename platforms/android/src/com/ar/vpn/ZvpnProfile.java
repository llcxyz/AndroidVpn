package com.ar.vpn;

import org.strongswan.android.data.VpnType;

/**
 * Created by aron on 2016/10/13.
 */
public class ZvpnProfile {

  private String gateway;

  private String username;

  private String password;

  private ZvpnType vpnType;

  private String Certificate;

  private String psk;

  private String key = "#####";

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public ZvpnType getVpnType() {
    return vpnType;
  }

  public void setVpnType(ZvpnType vpnType) {
    this.vpnType = vpnType;
  }

  public String getCertificate() {
    return Certificate;
  }

  public void setCertificate(String certificate) {
    Certificate = certificate;
  }

  public String getGateway() {
    return gateway;
  }

  public void setGateway(String gateway) {
    this.gateway = gateway;
  }

  public String getPsk() {
    return psk;
  }

  public void setPsk(String psk) {
    this.psk = psk;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }
}
