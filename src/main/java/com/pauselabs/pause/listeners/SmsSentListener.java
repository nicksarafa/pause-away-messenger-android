package com.pauselabs.pause.listeners;

import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.Telephony;

import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.models.PauseConversation;
import com.pauselabs.pause.models.PauseMessage;

import java.util.Date;

/**
 * Created by Passa on 12/18/14.
 */
public class SmsSentListener extends ContentObserver {

    private final String TAG = SmsSentListener.class.getSimpleName();

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public SmsSentListener(Handler handler) {
        super(handler);

    }

    public void onChange(boolean selfChange){
        Cursor cursor = PauseApplication.getInstance().getContentResolver().query(Uri.parse("content://sms"), null, null, null, null);
        if (cursor.moveToNext()) {
            String protocol = cursor.getString(cursor.getColumnIndex("protocol"));
            int type = cursor.getInt(cursor.getColumnIndex("type"));
            // Only processing outgoing sms event & only when it
            // is sent successfully (available in SENT box).
            if (protocol != null || type != Telephony.Sms.MESSAGE_TYPE_SENT) {
                return;
            }
            int dateColumn = cursor.getColumnIndex("date");
            int bodyColumn = cursor.getColumnIndex("body");
            int addressColumn = cursor.getColumnIndex("address");
            String from = "0";
            String to = cursor.getString(addressColumn);
            Date now = new Date(cursor.getLong(dateColumn));
            String message = cursor.getString(bodyColumn);

            PauseMessage userMessage = new PauseMessage(from,to,message);

            PauseConversation conversation = PauseApplication.getCurrentSession().getConversationBySender(to);
            conversation.addMessageFromUser(userMessage);
        }
    }
}
