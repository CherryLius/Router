package cherry.android.router.api;

/**
 * Created by Administrator on 2017/5/27.
 */

public interface IRouteCallback {
    void onSuccess(RouteMeta routeMeta);

    void onIntercept(RouteMeta routeMeta);

    void onFailed(RouteMeta routeMeta, String reason);
}
