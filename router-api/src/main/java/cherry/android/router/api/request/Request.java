package cherry.android.router.api.request;

import cherry.android.router.api.RequestOptions;
import cherry.android.router.api.RouteRule;
import cherry.android.router.api.callback.RouterCallback;

/**
 * Created by LHEE on 2017/7/22.
 */

public interface Request<T, R> {
    String getUri();

    Class<?> getDestination();

    T invoke();

    void request();

    void setOptions(RequestOptions options);

    RequestOptions getOptions();

    RouteRule getRule();

    void callback(RouterCallback callback);

    void setHost(R host);

}
