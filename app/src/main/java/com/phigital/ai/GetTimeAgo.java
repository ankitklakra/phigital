package com.phigital.ai;

import android.app.Application;

public class GetTimeAgo extends Application {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    public static String getTimeRemaining(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
//        if (time > now || time <= 0) {
//            return null;
//        }

        // TODO: localize
        final long diff = time - now;
        if (diff < SECOND_MILLIS) {
            return "Finished";
        }else if (diff < MINUTE_MILLIS) {
            return "1 min  ";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "2 min ";
        }else if (diff < 3 *MINUTE_MILLIS) {
            return "3 min  ";
        } else if (diff < 4 * MINUTE_MILLIS) {
            return "4 min ";
        } else if (diff < 5 * MINUTE_MILLIS) {
            return "5 min ";
        }else if (diff < 6 *MINUTE_MILLIS) {
            return "6 min  ";
        } else if (diff < 7 * MINUTE_MILLIS) {
            return "7 min ";
        } else if (diff < 8 * MINUTE_MILLIS) {
            return "8 min ";
        }else if (diff < 9 *MINUTE_MILLIS) {
            return "9 min  ";
        } else if (diff < 10 * MINUTE_MILLIS) {
            return "10 min ";
        } else if (diff < 60 * MINUTE_MILLIS) {
            return MINUTE_MILLIS / diff + " 1 hours ";
        } else if (diff < 120 * MINUTE_MILLIS) {
            return "2  hour ";
        } else if (diff < 24 * HOUR_MILLIS) {
            return HOUR_MILLIS / diff + "1 day ";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "2 days ";
        } else if (diff < 3 * DAY_MILLIS) {
            return "3 days ";
        } else if (diff < 4 * DAY_MILLIS) {
            return "4 days ";
        } else if (diff < 5 * DAY_MILLIS) {
            return "5 days ";
        } else if (diff < 6 * DAY_MILLIS) {
            return "6 days ";
        } else {
            return diff / DAY_MILLIS + " days ";
        }
    }

    public static String getNotificationTime(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "1m";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + "m";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "1 h";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + "h";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "2d";
        } else {
            return diff / DAY_MILLIS + "d";
        }
    }
}
