package com.pauselabs.pause.core;

import android.app.Activity;
import android.content.*;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import com.google.android.mms.ContentType;
import com.google.android.mms.pdu.*;
import com.pauselabs.R;
import com.pauselabs.pause.models.APN;
import com.pauselabs.pause.models.PauseBounceBackMessage;
import com.pauselabs.pause.models.PauseMMSPart;
import com.pauselabs.pause.services.PauseMmsTransportService;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import java.io.ByteArrayOutputStream;
import java.net.URI;

//import com.google.android.mms.pdu.*;

//import com.google.android.mms.pdu.*;

/**
 * This class is responsible for the actual sending of Pause bounce back messages
 */
public class PauseMessageSender {

    private static final String TAG = PauseMessageSender.class.getSimpleName();
    private Context context;

    public PauseMessageSender(Context context){
        this.context = context;

    }

    public PauseMessageSender(){

    }

    public void sendSmsMessage(String recipient, PauseBounceBackMessage pauseMessage){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(recipient, null, pauseMessage.getMessage(), null, null);

        Log.v(TAG, "attempting to send message to: " + recipient + " text: " + pauseMessage.getMessage());

        // Write sent sms to content sms table so message is displayed in users texting app i.e. google hangout
//        ContentValues values = new ContentValues();
//        values.put("address", recipient);
//        values.put("body", pauseMessage.getMessage());
//        PauseApplication.getInstance().getContentResolver().insert(Uri.parse("content://sms/sent"), values);

    }

    public void sendMmsMessage(final String recipient, final PauseBounceBackMessage pauseMessage){
        // Check if we have a network connection for sending MMS
        ConnectivityManager mConnMgr =  (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final int result = mConnMgr.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, "enableMMS");

        if(result != 0){
            // Establish connection
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            BroadcastReceiver receiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();

                    if (!action.equals(ConnectivityManager.CONNECTIVITY_ACTION))
                    {
                        return;
                    }

                    @SuppressWarnings("deprecation")
                    NetworkInfo mNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

                    if ((mNetworkInfo == null) || (mNetworkInfo.getType() != ConnectivityManager.TYPE_MOBILE_MMS))
                    {
                        return;
                    }

                    if (!mNetworkInfo.isConnected())
                    {
                        return;
                    } else
                    {
                        startMmsTransportSevice(context, buildMmsMessageBytes(recipient, pauseMessage));
                        context.unregisterReceiver(this);
                    }

                }

            };

            context.registerReceiver(receiver, filter);
        }
        else{
            // Connection established, proceed with sending of MMS
            startMmsTransportSevice(context, buildMmsMessageBytes(recipient, pauseMessage));
        }

    }

    public byte[] buildMmsMessageBytes(String recipient, PauseBounceBackMessage pauseBounceBackMessage) {
        //Create Pdu Bytes
        final PduBody body = new PduBody();

        PduPart part = new PduPart();

        Bitmap b = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.coachella);   // Whatever your bitmap is that you want to send
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();

        Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.drawable.coachella);
        //byte[] data  = BitmapUtil.createScaledBytes(context, uri, 640, 480, (300 * 1024) - 5000);

        part.setData(data);
        part.setDataUri(uri);
        part.setContentType(ContentType.IMAGE_PNG.getBytes());
        part.setContentId((System.currentTimeMillis()+"").getBytes());
        part.setName(("Image" + System.currentTimeMillis()).getBytes());

        body.addPart(part);

        String  number = ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();

        PduHeaders headers = new PduHeaders();

        final SendReq sendReq = new SendReq();
        sendReq.setFrom(new EncodedStringValue(number));
        sendReq.addTo(new EncodedStringValue(number));
        sendReq.setBody(body);

        PduComposer composer = new PduComposer(context, sendReq);
        byte[] bytesToSend = composer.make();

        return bytesToSend;

    }

    public void sendMmsMessage(final String recipient, final PauseMMSPart[] parts) {
        ConnectivityManager mConnMgr =  (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final int result = mConnMgr.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, "enableMMS");

        if (result != 0)
        {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            BroadcastReceiver receiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();

                    if (!action.equals(ConnectivityManager.CONNECTIVITY_ACTION))
                    {
                        return;
                    }

                    @SuppressWarnings("deprecation")
                    NetworkInfo mNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

                    if ((mNetworkInfo == null) || (mNetworkInfo.getType() != ConnectivityManager.TYPE_MOBILE_MMS))
                    {
                        return;
                    }

                    if (!mNetworkInfo.isConnected())
                    {
                        return;
                    } else
                    {
                        //sendData(recipient, parts);
                        sendTextSecureData();

                        context.unregisterReceiver(this);
                    }

                }

            };

            context.registerReceiver(receiver, filter);
        } else
        {
            //sendData(recipient, parts);
            sendTextSecureData();
        }

    }

    public void startMmsTransportSevice(Context context, byte[] bytesToSend) {
        Intent mmsTransportIntent = new Intent(context, PauseMmsTransportService.class);
        mmsTransportIntent.putExtra(Constants.Mms.MMS_BYTE_ARRAY_EXTRA, bytesToSend);
        context.startService(mmsTransportIntent);
    }

    public void sendTextSecureData() {
        new Thread(new Runnable() {

            @Override
            public void run() {

                //Create Pdu Bytes
                final PduBody body = new PduBody();

                PduPart part = new PduPart();

                Bitmap b = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.coachella);   // Whatever your bitmap is that you want to send
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] data = stream.toByteArray();

                Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.drawable.coachella);
                //byte[] data  = BitmapUtil.createScaledBytes(context, uri, 640, 480, (300 * 1024) - 5000);

                part.setData(data);
                part.setDataUri(uri);
                part.setContentType(ContentType.IMAGE_PNG.getBytes());
                part.setContentId((System.currentTimeMillis()+"").getBytes());
                part.setName(("Image" + System.currentTimeMillis()).getBytes());

                body.addPart(part);

                String  number = ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();

                PduHeaders headers = new PduHeaders();

                final SendReq sendReq = new SendReq();
                sendReq.setFrom(new EncodedStringValue(number));
                String nicksNumber = "2487610351";
                sendReq.addTo(new EncodedStringValue(nicksNumber));
                sendReq.setBody(body);

                PduComposer composer = new PduComposer(context, sendReq);
                byte[] bytesToSend = composer.make();

                MmsSendHelper sendHelper = new MmsSendHelper();

                try{
                    sendHelper.sendMms(context, bytesToSend, "", true, false);
                }
                catch(Exception e) {
                    Log.v(TAG, e.getMessage().toString());
                }






            }
        }).start();
    }

    public void sendData(final String recipient, final PauseMMSPart[] parts) {

        new Thread(new Runnable() {

            @Override
            public void run() {

                final com.google.android.mms.pdu.SendReq sendRequest = new com.google.android.mms.pdu.SendReq();

                final com.google.android.mms.pdu.EncodedStringValue[] phoneNumber = com.google.android.mms.pdu.EncodedStringValue.extract(recipient);

                if (phoneNumber != null && phoneNumber.length > 0)
                {
                    //sendRequest.addTo(phoneNumber[0]);
                }

                String  number         = ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
                if(number != null && number.trim().length() != 0){
                    sendRequest.setFrom(new EncodedStringValue(number));
                    sendRequest.addTo(new EncodedStringValue(number));  // Same to address as from

                }

                final PduBody pduBody = new PduBody();

                if (parts != null)
                {
                    for (PauseMMSPart pauseMMSpart : parts)
                    {
                        if (pauseMMSpart != null)
                        {
                            try
                            {
                                final PduPart partPdu = new PduPart();
                                partPdu.setName(pauseMMSpart.Name.getBytes());
                                partPdu.setContentType(pauseMMSpart.MimeType.getBytes());
                                partPdu.setData(pauseMMSpart.Data);
                                pduBody.addPart(partPdu);
                            } catch (Exception e)
                            {

                            }
                        }
                    }
                }

                sendRequest.setBody(pduBody);

                final PduComposer composer = new PduComposer(context, sendRequest);
                final byte[] bytesToSend = composer.make();

//                List<APN> apns = new ArrayList<APN>();
//
//                try
//                {
//                    APNHelper helper = new APNHelper(context);
//                    apns = helper.getMMSApns();
//                } catch (Exception e)
//                {
                    //APN apn = new APN(sharedPrefs.getString("mmsc_url", ""), sharedPrefs.getString("mms_port", ""), sharedPrefs.getString("mms_proxy", ""));
                    //apns.add(apn);
                    //APN myApn = new APN("http://mms.msg.eng.t-mobile.com/mms/wapenc", "310260", null);
//                }

                try {
                    //HttpUtils.httpConnection(context, 4444L, apns.get(0).MMSCenterUrl, bytesToSend, HttpUtils.HTTP_POST_METHOD, !TextUtils.isEmpty(apns.get(0).MMSProxy), apns.get(0).MMSProxy, Integer.parseInt(apns.get(0).MMSPort));
                    APN myApn = new APN("http://mms.msg.eng.t-mobile.com/mms/wapenc", "80", "");

                    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    String simOperator = tm.getSimOperator();

                            AndroidHttpClient client = AndroidHttpClient.newInstance("Android-Mms/2.0", context);
                    HttpParams params        = client.getParams();
                    HttpProtocolParams.setContentCharset(params, "UTF-8");
                    HttpConnectionParams.setSoTimeout(params, 20 * 1000);

                    URI targetUrl = new URI("http://mms.msg.eng.t-mobile.com/mms/wapenc");

                    HttpHost target = new HttpHost(targetUrl.getHost(), targetUrl.getPort(), HttpHost.DEFAULT_SCHEME_NAME);
                    HttpPost request = new HttpPost("http://mms.msg.eng.t-mobile.com/mms/wapenc");
                    ByteArrayEntity entity = new ByteArrayEntity(bytesToSend);

                    entity.setContentType("application/vnd.wap.mms-message");

                    request.setEntity(entity);
                    request.setParams(client.getParams());
                    request.addHeader("Accept", "*/*, application/vnd.wap.mms-message, application/vnd.wap.sic");
                    request.addHeader("x-wap-profile", "http://www.google.com/oha/rdf/ua-profile-kila.xml");
                    HttpResponse response = client.execute(target, request);
                    StatusLine status     = response.getStatusLine();


                    ConnectivityManager mConnMgr =  (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    mConnMgr.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE_MMS, "enableMMS");

                    IntentFilter filter = new IntentFilter();
                    filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
                    BroadcastReceiver receiver = new BroadcastReceiver() {

                        @Override
                        public void onReceive(Context context, Intent intent) {
                            Cursor query = context.getContentResolver().query(Uri.parse("content://mms"), new String[] {"_id"}, null, null, "date desc");
                            query.moveToFirst();
                            String id = query.getString(query.getColumnIndex("_id"));
                            query.close();

                            ContentValues values = new ContentValues();
                            values.put("msg_box", 2);
                            String where = "_id" + " = '" + id + "'";
                            context.getContentResolver().update(Uri.parse("content://mms"), values, where, null);

                            context.unregisterReceiver(this);
                        }

                    };

                    context.registerReceiver(receiver, filter);
                } catch (Exception e) {
                    Cursor query = context.getContentResolver().query(Uri.parse("content://mms"), new String[] {"_id"}, null, null, "date desc");
                    query.moveToFirst();
                    String id = query.getString(query.getColumnIndex("_id"));
                    query.close();

                    ContentValues values = new ContentValues();
                    values.put("msg_box", 5);
                    String where = "_id" + " = '" + id + "'";
                    context.getContentResolver().update(Uri.parse("content://mms"), values, where, null);

                    ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content).post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(context, "MMS Error", Toast.LENGTH_SHORT).show();
                        }

                    });
                }

            }

        }).start();
    }



}
