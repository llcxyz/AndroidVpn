package com.ionicframework.gdvpn874918;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Encrypt and decrypt messages using AES 256 bit encryption that are compatible with AESCrypt-ObjC and AESCrypt Ruby.
 * <p/>
 * Created by  on 04/10/2014.
 */
public final class AESHelper {

  private static final byte[] ivBytes = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

  private static final String padding = "*";

  public static String AesDecrypt(String pwd, String base64Text) throws Exception {

    byte[] encrypted1 = Base64.decode(base64Text,Base64.NO_WRAP);
    Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
    SecretKeySpec keyspec = new SecretKeySpec(pwd.getBytes(), "AES");
    IvParameterSpec ivspec = new IvParameterSpec(ivBytes);

    cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
    byte[] original = cipher.doFinal(encrypted1);
    String originalString = new String(original,"UTF-8");
    return originalString;

  }

}
