package cherry.android.router.api.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Administrator on 2017/5/26.
 */

public final class Logger {
    private static String sTag = "Router.";
    private static boolean sLoggable = false;
    private static boolean sTraceStack = false;

    public static void setTag(String tag) {
        if (!TextUtils.isEmpty(tag)) {
            if (sTag.endsWith("."))
                sTag = tag;
            else
                sTag = tag + ".";
        }
    }

    public static void showLog(boolean show) {
        sLoggable = show;
    }

    public static void showStackTrace(boolean show) {
        sTraceStack = show;
    }

    public static void i(String tag, String msg) {
        if (sLoggable)
            Log.i(sTag + tag, buildMessage(msg));
    }

    public static void i(String tag, String format, Object... args) {
        i(tag, String.format(format, args));
    }

    public static void d(String tag, String msg) {
        if (sLoggable)
            Log.d(sTag + tag, buildMessage(msg));
    }

    public static void d(String tag, String format, Object... args) {
        d(tag, String.format(format, args));
    }

    public static void v(String tag, String msg) {
        if (sLoggable)
            Log.v(sTag + tag, buildMessage(msg));
    }

    public static void v(String tag, String format, Object... args) {
        v(tag, String.format(format, args));
    }

    public static void w(String tag, String msg) {
        if (sLoggable)
            Log.w(sTag + tag, buildMessage(msg));
    }

    public static void w(String tag, String format, Object... args) {
        w(tag, String.format(format, args));
    }

    public static void e(String tag, String msg) {
        if (sLoggable)
            Log.e(sTag + tag, buildMessage(msg));
    }

    public static void e(String tag, String format, Object... args) {
        e(tag, String.format(format, args));
    }

    public static void e(String tag, String msg, Throwable t) {
        if (sLoggable)
            Log.e(sTag + tag, buildMessage(msg), t);
    }

    private static String buildMessage(String msg) {
        if (!sTraceStack) return msg;
        Throwable t = new Throwable();
        StackTraceElement[] stackElements = t.getStackTrace();
        if (stackElements != null) {
            StringBuilder sb = new StringBuilder();
            String className = stackElements[2].getClassName();
            String methodName = stackElements[2].getMethodName();
            return sb.append("{")
                    .append(className)
                    .append("}")
                    .append(".")
                    .append(methodName)
                    .append("() ")
                    .append(msg).toString();
        } else {
            return msg;
        }
    }
}
