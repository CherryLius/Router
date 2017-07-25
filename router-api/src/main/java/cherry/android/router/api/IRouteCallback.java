package cherry.android.router.api;

import cherry.android.router.api.request.Request;

/**
 * Created by Administrator on 2017/5/27.
 */

public interface IRouteCallback {
    void onSuccess(Request request);

    void onIntercept(Request request);

    void onFailed(Request request, String reason);
}
