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
    }

    public static final class Mms {
        private Mms() {}

        /**
         * MMS byte array stored in intent extra
         */
        public static final String MMS_BYTE_ARRAY_EXTRA = "MMS_BYTE_ARRAY_EXTRA";
    }
}
