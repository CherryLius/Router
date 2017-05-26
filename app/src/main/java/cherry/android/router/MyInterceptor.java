package cherry.android.router;

import android.util.Log;

import cherry.android.router.annotations.Interceptor;
import cherry.android.router.api.RouteMeta;
import cherry.android.router.api.intercept.IInterceptor;

/**
 * Created by Administrator on 2017/5/25.
 */

@Interceptor("m")
public class MyInterceptor implements IInterceptor {
    @Override
    public boolean intercept(RouteMeta routeMeta) {
        Log.e("Test", "intercept m");
        return false;
    }
}
