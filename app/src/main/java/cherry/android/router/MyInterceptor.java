package cherry.android.router;

import cherry.android.router.annotations.Interceptor;
import cherry.android.router.api.RouteMeta;
import cherry.android.router.api.intercept.IInterceptor;
import cherry.android.router.api.utils.Logger;

/**
 * Created by Administrator on 2017/5/25.
 */

@Interceptor("m")
public class MyInterceptor implements IInterceptor {
    @Override
    public boolean intercept(RouteMeta routeMeta) {
        Logger.e("Test", "intercept m");
        return false;
    }
}
