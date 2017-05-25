package cherry.android.router.api;

import java.util.Map;

import cherry.android.router.api.intercept.IInterceptor;

/**
 * Created by Administrator on 2017/5/25.
 */

public interface InterceptorPicker {
    void pick(Map<String, Class<? extends IInterceptor>> interceptors);
}
