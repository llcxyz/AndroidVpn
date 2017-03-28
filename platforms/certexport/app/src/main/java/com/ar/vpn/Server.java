package com.ar.vpn;

/**
 * Created by aron on 2016/10/19.
 */
public class Server {
  private String name;
  private String hostip;
  private String cert;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getHostip() {
    return hostip;
  }

  public void setHostip(String hostip) {
    this.hostip = hostip;
  }

  public String getCert() {
    return cert;
  }

  public void setCert(String cert) {
    this.cert = cert;
  }
}
