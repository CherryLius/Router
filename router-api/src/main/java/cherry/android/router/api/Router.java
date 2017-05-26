package cherry.android.router.api;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Map;

import cherry.android.router.api.intercept.IInterceptor;

/**
 * Created by Administrator on 2017/5/24.
 */

public final class Router {

    public static void init(@NonNull Context context) {
        RouterManager.instance().init(context);
    }

    public static RouterManager build(@NonNull String uri) {
        return RouterManager.instance().build(uri);
    }

    public static void addRoutePicker(@NonNull RoutePicker picker) {
        RouterManager.instance().addRoutePicker(picker);
    }

    public static void addGlobalInterceptor(IInterceptor interceptor) {
        RouterManager.instance().addGlobalInterceptor(interceptor);
    }

    public interface RoutePicker {
        Map<String, Class<?>> pick();
    }
}
