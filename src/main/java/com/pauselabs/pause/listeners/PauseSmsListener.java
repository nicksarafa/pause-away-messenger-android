package com.pauselabs.pause.listeners;

import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.Telephony;
import android.util.Log;

import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.model.Constants;
import com.pauselabs.pause.model.PauseConversation;
import com.pauselabs.pause.model.PauseMessage;

/**
 * Created by Passa on 12/18/14.
 */
public class PauseSmsListener extends ContentObserver {

    private final String TAG = PauseSmsListener.class.getSimpleName();

    private int previousCount;

    private Handler handler;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public PauseSmsListener(Handler handler) {
        super(handler);

        this.handler = handler;

        Injector.inject(this);

        previousCount = getNewSmsCursor().getCount();
    }

    public void onChange(boolean selfChange){
        Cursor cursor = getNewSmsCursor();
        int newCount = cursor.getCount();

        Log.i(TAG,"prev count: " + previousCount);
        Log.i(TAG,"new count: " + newCount);

        if (newCount > previousCount) {
            if (cursor.moveToFirst()) {
                PauseMessage newMessage;

                int type = cursor.getInt(cursor.getColumnIndex("type"));
                int dateColumn = cursor.getColumnIndex("date");
                int bodyColumn = cursor.getColumnIndex("body");
                int addressColumn = cursor.getColumnIndex("address");

                String from, to, number = "";
                String message = cursor.getString(bodyColumn);
                Long date = cursor.getLong(dateColumn);

                if (type == Telephony.Sms.MESSAGE_TYPE_SENT) {
                    from = "0";
                    to = cursor.getString(addressColumn);
                    number = to;

                    PauseConversation activeConversation = PauseApplication.getCurrentSession().getConversationByContactNumber(number);

                    if (activeConversation.getMessages().size() > 0 && activeConversation.getLastMessage().getType() == Constants.Message.Type.SMS_PAUSE_OUTGOING && activeConversation.getLastMessage().getId() == newCount) {
                        Log.i(TAG,"Pause sent message");

                        PauseApplication.sendToast("Replied to message from " + activeConversation.getContactName() + " Ü");
                    } else {
                        Log.i(TAG,"User sent message");

                        newMessage = new PauseMessage(from, to, message, date, Constants.Message.Type.SMS_OUTGOING);
                        PauseApplication.handleMessageSent(newMessage);
                    }
                } else if (type == Telephony.Sms.MESSAGE_TYPE_INBOX) {
                    Log.i(TAG, "Message received");

                    PauseApplication.numSMS++;

                    from = cursor.getString(addressColumn);
                    to = "0";

                    newMessage = new PauseMessage(from, to, message, date, Constants.Message.Type.SMS_INCOMING);
                    PauseApplication.handleMessageReceived(newMessage);
                }
            }

            previousCount = newCount;
        }

        cursor.close();
    }

    /**
     * Since PauseMessageReceivedService is only initiated from a PauseSessionService,
     * we can assume that our Pause Application will always have a non-null Session at this point.
     * This function will be used to update the active session by updating or creating a new
     * Conversation based on the message received.
     * The creating or modifying of conversations should happen here, not in Session!
     * @param message
     */
    private void updatePauseSession(PauseMessage message) {

    }

    public static Cursor getNewSmsCursor() {
        return PauseApplication.getInstance().getContentResolver().query(Uri.parse("content://sms"), null, null, null, null);
    }
}
