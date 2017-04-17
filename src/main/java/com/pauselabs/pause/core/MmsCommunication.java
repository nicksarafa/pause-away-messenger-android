package com.pauselabs.pause.core;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.util.Log;
import com.pauselabs.pause.util.Conversions;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

/** Created by tyndallm on 6/18/14. */
public class MmsCommunication {

  protected static MmsConnectionParameters getLocalMmsConnectionParameters(Context context) {
    MmsConnectionParameters params = ApnDefaults.getMmsConnectionParameters(context);
    return params;
  }

  protected static boolean checkRouteToHost(
      Context context, MmsConnectionParameters.Apn parameters, String url, boolean usingMmsRadio)
      throws IOException {
    if (parameters == null || !parameters.hasProxy())
      return checkRouteToHost(context, Uri.parse(url).getHost(), usingMmsRadio);
    else return checkRouteToHost(context, parameters.getProxy(), usingMmsRadio);
  }

  private static boolean checkRouteToHost(Context context, String host, boolean usingMmsRadio)
      throws IOException {
    InetAddress inetAddress = InetAddress.getByName(host);

    if (!usingMmsRadio) {
      if (inetAddress.isSiteLocalAddress()) {
        throw new IOException("RFC1918 address in non-MMS radio situation!");
      }

      return true;
    }

    Log.w(
        "MmsCommunication",
        "Checking route to address: " + host + " , " + inetAddress.getHostAddress());

    byte[] ipAddressBytes = inetAddress.getAddress();

    if (ipAddressBytes != null && ipAddressBytes.length == 4) {
      int ipAddress = Conversions.byteArrayToIntLittleEndian(ipAddressBytes, 0);
      ConnectivityManager manager =
          (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      final int TYPE_MOBILE_MMS = 2;
      return manager.requestRouteToHost(TYPE_MOBILE_MMS, ipAddress);
    }
    return true;
  }

  protected static AndroidHttpClient constructHttpClient(
      Context context, MmsConnectionParameters.Apn mmsConfig) {
    AndroidHttpClient client = AndroidHttpClient.newInstance("Android-Mms/2.0", context);
    HttpParams params = client.getParams();
    HttpProtocolParams.setContentCharset(params, "UTF-8");
    HttpConnectionParams.setSoTimeout(params, 20 * 1000);

    if (mmsConfig.hasProxy()) {
      ConnRouteParams.setDefaultProxy(
          params, new HttpHost(mmsConfig.getProxy(), mmsConfig.getPort()));
    }

    return client;
  }

  protected static byte[] parseResponse(HttpEntity entity) throws IOException {
    if (entity == null || entity.getContentLength() == 0) return null;

    if (entity.getContentLength() < 0) throw new IOException("Unknown content length!");

    byte[] responseBytes = new byte[(int) entity.getContentLength()];
    DataInputStream dataInputStream = new DataInputStream(entity.getContent());
    dataInputStream.readFully(responseBytes);
    dataInputStream.close();

    entity.consumeContent();
    return responseBytes;
  }

  protected static class MmsConnectionParameters {
    public class Apn {
      private final String mmsc;
      private final String proxy;
      private final String port;

      public Apn(String mmsc, String proxy, String port) {
        this.mmsc = mmsc;
        this.proxy = proxy;
        this.port = port;
      }

      public boolean hasProxy() {
        return proxy != null;
      }

      public String getMmsc() {
        return mmsc;
      }

      public String getProxy() {
        if (!hasProxy()) return null;

        return proxy;
      }

      public int getPort() {
        if (port == null) return 80;

        return Integer.parseInt(port);
      }
    }

    private List<Apn> apn = new ArrayList<Apn>();

    public MmsConnectionParameters(String mmsc, String proxy, String port) {
      apn.add(new Apn(mmsc, proxy, port));
    }

    public MmsConnectionParameters add(String mmsc, String proxy, String port) {
      apn.add(new Apn(mmsc, proxy, port));
      return this;
    }

    public List<Apn> get() {
      return apn;
    }
  }
}
