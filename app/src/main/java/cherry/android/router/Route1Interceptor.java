package cherry.android.router;

import cherry.android.router.annotations.Interceptor;
import cherry.android.router.api.request.Request;
import cherry.android.router.api.intercept.IInterceptor;
import cherry.android.router.api.utils.Logger;

/**
 * Created by Administrator on 2017/5/26.
 */
@Interceptor(value = "route1", priority = 0)
public class Route1Interceptor implements IInterceptor {
    @Override
    public boolean intercept(Request request) {
        Logger.i("Test", "intercept in 1 :" + request.getDestination());
        return false;
    }
}
