package cherry.android.router;

import cherry.android.router.annotations.Interceptor;
import cherry.android.router.api.RouteMeta;
import cherry.android.router.api.intercept.IInterceptor;
import cherry.android.router.api.utils.Logger;

/**
 * Created by Administrator on 2017/5/26.
 */

@Interceptor(value = "route2", priority = 2)
public class Route2Interceptor implements IInterceptor {
    @Override
    public boolean intercept(RouteMeta routeMeta) {
        Logger.i("Test", "intercept in 2 :" + routeMeta.getDestination());
        return false;
    }
}
