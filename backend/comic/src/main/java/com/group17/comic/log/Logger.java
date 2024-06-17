package com.group17.comic.log;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Logger {
    private Logger() {}

    public static void logInfo(String message) {
        log.info(message);
    }

    public static void logWarning(String message) {
        log.warn(message);
    }

    public static void logError(String message, Exception exception) {
        log.error(message, exception);
    }

    public static void logDebug(String message, Object args) {
        log.debug(message, args);
    }

    public static void logTrace(String message, Exception ex, Object instance) {
        log.trace(message, ex, instance);
    }
}
