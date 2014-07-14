package com.pauselabs.pause.core;

/**
 * Pause constants
 */
public class Constants {

    private Constants() {}

    public static final class Message {

        private Message() {}

        /**
         * SMS Intent
         */
        public static final String SMS_RECEIVED_INTENT = "android.provider.Telephony.SMS_RECEIVED";

        /**
         * SMS PDU (Protocol Data Unit) intent extra
         */
        public static final String PDUS_EXTRA = "pdus";

        /**
         * Message Bundle Identifier String
         */
        public static final String MESSAGE_BUNDLE = "MESSAGE_BUNDLE";

        /**
         * Message Parcel Identifier String
         */
        public static final String MESSAGE_PARCEL = "MESSAGE_PARCEL";

        /**
         * Missed call preference
         */
        public static final String MISSED_CALL_PREFERENCE = "MISSED_CALL_PREFERENCE";

        /**
         * Missed phone call older state
         */
        public static final String PREFERENCE_OLDER_PHONE_STATE = "olderPhoneState";

        /**
         * Last missed call time
         */
        public static final String PREFERENCE_LAST_MISSED_CALL_TIME = "lastMissedCallTime";

        /**
         * Phone State Change Filter
         */
        public static final String PHONE_STATE_CHANGE_INTENT = "android.intent.action.PHONE_STATE";

        /**
         * Phone Number incoming call
         */
        public static final String PREFERENCE_LAST_CALL_NUMBER = "PREFERENCE_LAST_CALL_NUMBER";

        /**
         * Pause Message SMS type
         */
        public static final String PAUSE_MESSAGE_SMS_TYPE = "SMS";

        /**
         * Pause Message MMS type
         */
        public static final String PAUSE_MESSAGE_MMS_TYPE = "MMS";

        /**
         * Pause Message Phone type
         */
        public static final String PAUSE_MESSAGE_PHONE_TYPE = "CALL";
    }

    public static final class Pause {

        private Pause() {}

        /**
         * Pause Bundle Identifier String
         */
        public static final String PAUSE_BUNDLE = "PAUSE_BUNDLE";

        /**
         * Pause Parcel Identifier String
         */
        public static final String PAUSE_PARCEL = "PAUSE_PARCEL";

        /**
         * Number of messages received that will trigger the sending of second bounce back
         */
        public static final int SECOND_BOUNCE_BACK_TRIGGER = 2;

        /**
         * Pause secondary Bounce Back message text
         */
        public static final String SECONDARY_BOUNCE_BACK_MESSAGE_TEXT = "You're receiving this automatic bounce back message courtesy of Pause, the easiest way to put your social life on hold.  www.pauselabs.com";

        /**
         * Pause Session Active
         */
        public static final int PAUSE_SESSION_STATE_ACTIVE = 0;

        /**
         * Pause Session Stopped
         */
        public static final int PAUSE_SESSION_STATE_STOPPED = 1;

        /**
         * Current Pause Message Parcel
         */
        public static final String PAUSE_MESSAGE_PARCEL = "PAUSE_MESSAGE_PARCEL";

        /**
         * Active Pause Message id saved in database
         */
        public static final String ACTIVE_PAUSE_DATABASE_ID_PREFS = "ACTIVE_PAUSE_DATABASE_ID_PREFS";
    }

    public static final class Mms {
        private Mms() {}

        /**
         * MMS byte array stored in intent extra
         */
        public static final String MMS_BYTE_ARRAY_EXTRA = "MMS_BYTE_ARRAY_EXTRA";

        /**
         * Intent Action MMS Received
         */
        public static final String ACTION_MMS_RECEIVED = "android.provider.Telephony.WAP_PUSH_RECEIVED";

        /**
         * MMS message data type
         */
        public static final String MMS_DATA_TYPE = "application/vnd.wap.mms-message";
    }
}
