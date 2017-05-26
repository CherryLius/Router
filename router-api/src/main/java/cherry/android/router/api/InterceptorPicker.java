package cherry.android.router.api;

import java.util.Map;

/**
 * Created by Administrator on 2017/5/25.
 */

public interface InterceptorPicker {
    void pick(Map<String, InterceptorMeta> interceptors);
}
