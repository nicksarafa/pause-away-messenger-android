package com.pauselabs.pause.core;


import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.util.Log;
import com.google.android.mms.pdu.PduParser;
import com.google.android.mms.pdu.SendConf;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class MmsSendHelper extends MmsCommunication {

    private final static String TAG = MmsSendHelper.class.getSimpleName();

    private static byte[] makePost(Context context, MmsConnectionParameters.Apn parameters, byte[] mms)
            throws IOException
    {
        AndroidHttpClient client = null;

        try {
            Log.w(TAG, "Sending MMS1 of length: " + (mms != null ? mms.length : "null"));
            client                 = constructHttpClient(context, parameters);
            URI targetUrl          = new URI(parameters.getMmsc());

            if (targetUrl.getHost() == null)
                throw new IOException("Invalid target host: " + targetUrl.getHost() + " , " + targetUrl);

            HttpHost target        = new HttpHost(targetUrl.getHost(), targetUrl.getPort(), HttpHost.DEFAULT_SCHEME_NAME);
            HttpPost request       = new HttpPost(parameters.getMmsc());
            ByteArrayEntity entity = new ByteArrayEntity(mms);

            entity.setContentType("application/vnd.wap.mms-message");

            request.setEntity(entity);
            request.setParams(client.getParams());
            request.addHeader("Accept", "*/*, application/vnd.wap.mms-message, application/vnd.wap.sic");
            request.addHeader("x-wap-profile", "http://www.google.com/oha/rdf/ua-profile-kila.xml");
            HttpResponse response = client.execute(target, request);
            StatusLine status     = response.getStatusLine();

            if (status.getStatusCode() != 200)
                throw new IOException("Non-successful HTTP response: " + status.getReasonPhrase());

            return parseResponse(response.getEntity());
        } catch (URISyntaxException use) {
            Log.w(TAG, use);
            throw new IOException("Couldn't parse URI.");
        } finally {
            if (client != null)
                client.close();
        }
    }

    public static SendConf sendMms(Context context, byte[] mms, String apn,
                                   boolean usingMmsRadio, boolean useProxyIfAvailable)
            throws IOException
    {
        byte[] response = sendBytes(context, mms, apn, usingMmsRadio, useProxyIfAvailable);
        return (SendConf) new PduParser(response).parse();
    }

    private static byte[] sendBytes(Context context, byte[] mms, String apn,
                                    boolean usingMmsRadio, boolean useProxyIfAvailable)
            throws IOException
    {
        Log.w(TAG, "Sending MMS of length: " + mms.length);
        try {
            MmsConnectionParameters parameters = getLocalMmsConnectionParameters(context);
            for (MmsConnectionParameters.Apn param : parameters.get()) {
                if (checkRouteToHost(context, param, param.getMmsc(), usingMmsRadio)) {
                    byte[] response = makePost(context, param, mms);
                    if (response != null) return response;
                }
            }
            throw new IOException("Connection manager could not obtain route to host.");
        } catch (Exception aue) {
            Log.w(TAG, aue);
            throw new IOException("Failed to get MMSC information...");
        }
    }
}
