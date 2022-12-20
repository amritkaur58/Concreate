package com.dailyreporting.app.Extras;

import timber.log.Timber;

public class Logger {
    public static void error(Exception ex) {
        Timber.e(ex.toString());
    }

    public static void info(String message) {
        Timber.i(message);
    }
}
