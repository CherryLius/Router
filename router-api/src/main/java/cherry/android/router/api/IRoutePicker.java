package cherry.android.router.api;

import java.util.Map;

/**
 * Created by Administrator on 2017/5/24.
 */

public interface IRoutePicker {
    void pick(Map<String, Class<?>> routeTable);
}
