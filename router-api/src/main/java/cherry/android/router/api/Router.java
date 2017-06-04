package cherry.android.router.api;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Map;

import cherry.android.router.api.intercept.IInterceptor;
import cherry.android.router.api.utils.Logger;

/**
 * Created by Administrator on 2017/5/24.
 */

public final class Router {

    private static boolean debuggable = false;

    public static void init(@NonNull Context context) {
        RouterManager.instance().init(context);
    }

    public static void openLog(boolean showLog, boolean showStackTrace) {
        Logger.showLog(showLog);
        Logger.showStackTrace(showStackTrace);
    }

    public static void openDebug() {
        Logger.i("Test", BuildConfig.DEBUG + "");
        debuggable = true;
    }

    public static boolean debuggable() {
        return debuggable;
    }

    public static void addRoutePicker(@NonNull RoutePicker picker) {
        RouterManager.instance().addRoutePicker(picker);
    }

    public static void addGlobalInterceptor(IInterceptor interceptor) {
        RouterManager.instance().addGlobalInterceptor(interceptor);
    }

    public static RouterManager build(@NonNull String uri) {
        return RouterManager.instance().build(uri);
    }

    public static void destroy() {
        debuggable = false;
        RouterManager.instance().destroy();
    }

    public interface RoutePicker {
        Map<String, Class<?>> pick();
    }
}
