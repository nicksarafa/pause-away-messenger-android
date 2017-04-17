package com.pauselabs.pause.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.google.android.mms.pdu.SendConf;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.core.MmsSendHelper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

/** This service is responsible for sending the actual MMS network request */
public class PauseMmsTransportService extends IntentService {
  private static final String TAG = PauseMmsTransportService.class.getSimpleName();
  private static final int SUCCESS = 128; // Ok
  public static final int MESSAGE_TYPE_OUTBOX = 4;
  private static final Uri THREAD_ID_CONTENT_URI = Uri.parse("content://mms-sms/threadID");
  private MmsSendHelper sendHelper;

  public PauseMmsTransportService() {
    super("PauseMmsTransportService");
    sendHelper = new MmsSendHelper();
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    Bundle extras = intent.getExtras();
    if (extras != null) {
      byte[] bytesToSend = extras.getByteArray(Constants.Mms.MMS_BYTE_ARRAY_EXTRA);
      String recipient = extras.getString(Constants.Message.PAUSE_MESSAGE_RECIPIENT_EXTRA);
      Set<String> recipients = new HashSet<String>();
      recipients.add(recipient);

      try {
        // TODO chnage so proxy and radio can be toggled
        SendConf response =
            sendHelper.sendMms(PauseApplication.getInstance(), bytesToSend, "", true, false);

        if (response.getResponseStatus() == SUCCESS) {
          // write mms to database to be displayed in messaging apps

          String pathToImage =
              PauseApplication.getCurrentSession().getActiveBounceBackMessage().getPathToImage();
          Bitmap activePauseBitmap = BitmapFactory.decodeFile(pathToImage);
          ByteArrayOutputStream stream = new ByteArrayOutputStream();
          activePauseBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
          byte[] byteArray = stream.toByteArray();

          insert(this, recipients, Constants.Message.PAUSE_MESSAGE_SUBJECT, byteArray);
        }
      } catch (Exception e) {
        Log.v(TAG, e.getMessage().toString());
      }
    }
  }

  public static Uri insert(
      Context context, Set<String> recipients, String subject, byte[] imageBytes) {
    try {
      Uri destUri = Uri.parse("content://mms");

      // Get thread id
      long thread_id = getThreadId(context, recipients);
      Log.e(">>>>>>>", "Thread ID is " + thread_id);

      // Create a dummy sms
      ContentValues dummyValues = new ContentValues();
      dummyValues.put("thread_id", thread_id);
      dummyValues.put("body", "Dummy SMS body.");
      Uri dummySms =
          context.getContentResolver().insert(Uri.parse("content://sms/sent"), dummyValues);

      // Create a new message entry
      long now = System.currentTimeMillis();
      ContentValues mmsValues = new ContentValues();
      mmsValues.put("thread_id", thread_id);
      mmsValues.put("date", now / 1000L);
      mmsValues.put("msg_box", MESSAGE_TYPE_OUTBOX);
      //mmsValues.put("m_id", System.currentTimeMillis());
      mmsValues.put("read", 1);
      mmsValues.put("sub", subject);
      mmsValues.put("sub_cs", 106);
      mmsValues.put("ct_t", "application/vnd.wap.multipart.related");
      mmsValues.put("exp", imageBytes.length);
      mmsValues.put("m_cls", "personal");
      mmsValues.put("m_type", 128); // 132 (RETRIEVE CONF) 130 (NOTIF IND) 128 (SEND REQ)
      mmsValues.put("v", 19);
      mmsValues.put("pri", 129);
      mmsValues.put("tr_id", "T" + Long.toHexString(now));
      mmsValues.put("resp_st", 128);

      // Insert message
      Uri res = context.getContentResolver().insert(destUri, mmsValues);
      String messageId = res.getLastPathSegment().trim();
      Log.e(">>>>>>>", "Message saved as " + res);

      // Create part
      createPart(context, messageId, imageBytes);

      // Create addresses
      for (String addr : recipients) {
        createAddr(context, messageId, addr);
      }

      //res = Uri.parse(destUri + "/" + messageId);

      // Delete dummy sms
      context.getContentResolver().delete(dummySms, null, null);

      return res;
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  private static Uri createPart(Context context, String id, byte[] imageBytes) throws Exception {
    ContentValues mmsPartValue = new ContentValues();
    mmsPartValue.put("mid", id);
    mmsPartValue.put("ct", "image/png");
    mmsPartValue.put("cid", "<" + System.currentTimeMillis() + ">");
    Uri partUri = Uri.parse("content://mms/" + id + "/part");
    Uri res = context.getContentResolver().insert(partUri, mmsPartValue);
    Log.e(">>>>>>>", "Part uri is " + res.toString());

    // Add data to part
    OutputStream os = context.getContentResolver().openOutputStream(res);
    ByteArrayInputStream is = new ByteArrayInputStream(imageBytes);
    byte[] buffer = new byte[256];
    for (int len = 0; (len = is.read(buffer)) != -1; ) {
      os.write(buffer, 0, len);
    }
    os.close();
    is.close();

    return res;
  }

  private static Uri createAddr(Context context, String id, String addr) throws Exception {
    ContentValues addrValues = new ContentValues();
    addrValues.put("address", addr);
    addrValues.put("charset", "106");
    addrValues.put("type", 151); // TO
    Uri addrUri = Uri.parse("content://mms/" + id + "/addr");
    Uri res = context.getContentResolver().insert(addrUri, addrValues);
    Log.e(">>>>>>>", "Addr uri is " + res.toString());

    return res;
  }

  /**
   * Retrieve message thread from content resolver for the specified conversation. Since we have
   * received a message from someone in order to trigger the bounce back, there must be an existing
   * thread
   *
   * @param context
   * @param recipients
   * @return
   */
  private static Long getThreadId(Context context, Set<String> recipients) {
    long threadId = 0;

    //TODO doesn't correctly handle multiple recipients

    Uri.Builder uriBuilder = THREAD_ID_CONTENT_URI.buildUpon();
    for (String recipient : recipients) {
      uriBuilder.appendQueryParameter("recipient", recipient);
    }
    Uri uri = uriBuilder.build();

    Cursor cursor = context.getContentResolver().query(uri, new String[] {"_id"}, null, null, null);
    if (cursor != null) {
      try {
        if (cursor.moveToFirst()) {
          threadId = cursor.getLong(0);
        }
      } finally {
        cursor.close();
      }
    }
    return threadId;
  }
}
