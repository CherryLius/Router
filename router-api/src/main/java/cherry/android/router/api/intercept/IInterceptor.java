package cherry.android.router.api.intercept;

import cherry.android.router.api.RouteMeta;

/**
 * Created by Administrator on 2017/5/24.
 */

public interface IInterceptor {
    boolean intercept(RouteMeta routeMeta);
}
