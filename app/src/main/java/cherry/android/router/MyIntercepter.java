package cherry.android.router;

import cherry.android.router.annotations.Interceptor;
import cherry.android.router.api.intercept.IInterceptor;

/**
 * Created by Administrator on 2017/5/25.
 */

@Interceptor("m")
public class MyIntercepter implements IInterceptor {
    @Override
    public boolean intercept() {
        return false;
    }
}
