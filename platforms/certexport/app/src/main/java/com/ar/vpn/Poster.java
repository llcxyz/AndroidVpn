package com.ar.vpn;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aron on 2016/10/18.
 */
public class Poster {

  private String TAG = "Poster";

  private String access_url = "http://www.totobo.com.cn/clientV2_android.php";


  private String rc4_key = "rQlWqT9QhSN";

  public boolean upload_cert(Server server) {
    Map<String, Object> params = new HashMap<>();
    params.put("groupid", 100000);
    params.put("action", "update_x509_eimvlpw");
    params.put("ip", server.getHostip());
    params.put("x509_base64", server.getCert());
    String resp = httpGet(params);
    Log.d(TAG, "上传结果:" + resp);
    if (resp.startsWith("succeed")) {
      return true;
    } else return false;

  }

  public List<Server> get_servers() {
    List<Server> cars = new ArrayList<>();
    Map<String, Object> params = new HashMap<>();
    params.put("groupid", 100000);
    params.put("action", "server");

    String resp = httpGet(params);
    if (!resp.startsWith("<!")) {
      String result = RC4.decry_RC4(resp, rc4_key);

      try {
        JSONObject jsonObject = new JSONObject(result);
        JSONArray serverList = jsonObject.getJSONArray("serverlist");
        for (int i = 0; i < serverList.length(); i++) {
          JSONObject jserver = (JSONObject) serverList.get(i);
          Server server = new Server();
          server.setName(jserver.getString("name"));
          server.setHostip(jserver.getString("hostip"));
          cars.add(server);

        }
        Log.i(TAG, "获得服务器数量:" + cars.size());

      } catch (JSONException e) {
        e.printStackTrace();
      }

    } else
      Log.e(TAG, "解码失败！！");

    return cars;

  }


  private String httpGet(Map<String, Object> params) {

    URL url = null;
    JSONObject json = new JSONObject(params);
    //Log.d(TAG, "原始请求:"+params);
    String encrypt = RC4.encry_RC4_string(json.toString(), rc4_key);
    //Log.d(TAG, "加密参数:"+encrypt);
    try {
      url = new URL(access_url + "?" + encrypt);

    } catch (MalformedURLException e) {
      e.printStackTrace();
      return null;
    }
    //Log.d(TAG, "定位服务:"+url.toString());
    HttpURLConnection conn = null;
    try {
      conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      //conn.setDoOutput(true);
      // conn.setDoInput(true);

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      if (conn.getResponseCode() == 200) {

        InputStream is = conn.getInputStream();
        //将InputStream转换成byte数组,getBytesByInputStream会关闭输入流
        byte[] responseBody = getBytesByInputStream(is);
        String ret = new String(responseBody, "utf-8");
        return ret;

      } else {
        Log.e(TAG, "发送定位数据故障:HTTP(" + conn.getResponseCode() + ")");

      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;

  }

  private byte[] getBytesByInputStream(InputStream is) {
    byte[] bytes = null;
    BufferedInputStream bis = new BufferedInputStream(is);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    BufferedOutputStream bos = new BufferedOutputStream(baos);
    byte[] buffer = new byte[1024 * 8];
    int length = 0;
    try {
      while ((length = bis.read(buffer)) > 0) {
        bos.write(buffer, 0, length);
      }
      bos.flush();
      bytes = baos.toByteArray();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        bos.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
      try {
        bis.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return bytes;
  }


  public String getAccess_url() {
    return access_url;
  }

  public void setAccess_url(String access_url) {
    this.access_url = access_url;
  }
}
