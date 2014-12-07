package com.pauselabs.pause.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.google.android.mms.ContentType;
import com.google.android.mms.pdu.*;
import com.pauselabs.pause.models.PauseBounceBackMessage;
import com.pauselabs.pause.services.PauseMmsTransportService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

//import com.google.android.mms.pdu.*;

//import com.google.android.mms.pdu.*;

/**
 * This class is responsible for the actual sending of Pause bounce back messages
 */
public class PauseMessageSender {

    private static final String TAG = PauseMessageSender.class.getSimpleName();
    private Context mContext;
    private static final int MAX_SMS_LENGTH = 160;

    public PauseMessageSender(Context context){
        this.mContext = context;

    }

    public void sendSmsMessage(String recipient, PauseBounceBackMessage pauseMessage){
        SmsManager smsManager = SmsManager.getDefault();

        StringBuilder builder = new StringBuilder();
        builder.append(pauseMessage.getMessage());

        if(pauseMessage.getLocation() != null && !pauseMessage.getLocation().equals("")){
            builder.append(", " + pauseMessage.getLocation());
        }

        if(pauseMessage.getEndTime() != null && pauseMessage.getEndTime() != 0){
            SimpleDateFormat df = new SimpleDateFormat("h:mm a");

            Calendar endTime = Calendar.getInstance();
            endTime.setTimeInMillis(pauseMessage.getEndTime());

            Date endTimeDate = new Date(pauseMessage.getEndTime());
            builder.append(", until " + df.format(endTimeDate).toString());
        }

        builder.append(" ");
        String message = builder.toString();
        int length = message.length();

        // the Android divideMessage works by encoding each character as 7 bits, sms texts can only be upto 160 chars.
        // for some reason if the message contains a character such as an apostrophe it encodes these as 16 bits,
        // and can cause messages which are less than 160 characters to be divided
        ArrayList<String> messageParts = smsManager.divideMessage(message);


        smsManager.sendMultipartTextMessage(recipient, null, messageParts, null, null);


        Log.v(TAG, "attempting to send message to: " + recipient + " text: " + message);

    }

    public void sendMmsMessage(final String recipient, final PauseBounceBackMessage pauseMessage){
        // Check if we have a network connection for sending MMS
        ConnectivityManager mConnMgr =  (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
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
                        startMmsTransportSevice(context, recipient, buildMmsMessageBytes(recipient, pauseMessage));
                        context.unregisterReceiver(this);
                    }

                }

            };

            mContext.registerReceiver(receiver, filter);
        }
        else{
            // Connection established, proceed with sending of MMS
            startMmsTransportSevice(mContext, recipient, buildMmsMessageBytes(recipient, pauseMessage));
        }

    }

    /**
     * This function starts the intent service that will be responsible for making the actual HTTP POST.
     * This function is only called after we have verified we are connected to the network and can send an MMS
     * @param context
     * @param bytesToSend
     */
    public void startMmsTransportSevice(Context context, String recipient, byte[] bytesToSend) {
        Intent mmsTransportIntent = new Intent(context, PauseMmsTransportService.class);
        mmsTransportIntent.putExtra(Constants.Mms.MMS_BYTE_ARRAY_EXTRA, bytesToSend);
        mmsTransportIntent.putExtra(Constants.Message.PAUSE_MESSAGE_RECIPIENT_EXTRA, recipient);
        context.startService(mmsTransportIntent);
    }

    /**
     *  This function is responsible for drawing the Active Pause message details onto a bitmap
     *  The bitmap that this function generates will be the final image to be sent over MMS
     */
    public BitmapDrawable generateMessageBitmap(PauseBounceBackMessage pauseBounceBackMessage) {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inSampleSize = 2;
        Bitmap workingBitmap = BitmapFactory.decodeFile(pauseBounceBackMessage.getPathToImage(), o);

        Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);

        return new BitmapDrawable(mContext.getResources(), mutableBitmap);
    }

    /**
     * Helper function converts device pixels to bitmap pixels
     * @param context
     * @param nDP
     * @return
     */
    public static int convertToPixels(Context context, int nDP)
    {
        final float conversionScale = context.getResources().getDisplayMetrics().density;

        return (int) ((nDP * conversionScale) + 0.5f) ;

    }

    /**
     * This function is responsible for taking our actual Pause Bounce Back message and converting it into bytes which
     * we can send over the network
     * @param recipient
     * @param pauseBounceBackMessage
     * @return
     */
    public byte[] buildMmsMessageBytes(String recipient, PauseBounceBackMessage pauseBounceBackMessage) {
        //Create Pdu Bytes
        final PduBody body = new PduBody();

        PduPart part = new PduPart();
//        BitmapFactory.Options o = new BitmapFactory.Options();
//        o.inSampleSize = 2;
//        Bitmap b = BitmapFactory.decodeFile(pauseBounceBackMessage.getPathToImage(), o);   // Whatever your bitmap is that you want to send

        Bitmap b = generateMessageBitmap(pauseBounceBackMessage).getBitmap();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] data = stream.toByteArray();

        Uri uri = Uri.fromFile(new File(pauseBounceBackMessage.getPathToImage()));
                //Uri uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.drawable.coachella);
                //byte[] data  = BitmapUtil.createScaledBytes(mContext, uri, 640, 480, (300 * 1024) - 5000);

                part.setData(data);
        part.setDataUri(uri);

        if(pauseBounceBackMessage.getPathToImage().contains(".jpg")){
            part.setContentType(ContentType.IMAGE_JPEG.getBytes());
        }
        else if(pauseBounceBackMessage.getPathToImage().contains(".png")){
            part.setContentType(ContentType.IMAGE_PNG.getBytes());
        }

        part.setContentId((System.currentTimeMillis()+"").getBytes());
        part.setName(("Image" + System.currentTimeMillis()).getBytes());

        body.addPart(part);

        String number = ((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();

        PduHeaders headers = new PduHeaders();

        final SendReq sendReq = new SendReq();
        sendReq.setFrom(new EncodedStringValue(number));
        sendReq.addTo(new EncodedStringValue(recipient));
        sendReq.setBody(body);

        PduComposer composer = new PduComposer(mContext, sendReq);
        byte[] bytesToSend = composer.make();

        return bytesToSend;

    }



}
