package com.Joseph.WEE_GEnder_Tracker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    public static String formatTimestamp(long timestamp) {
        return new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                .format(new Date(timestamp));
    }
}