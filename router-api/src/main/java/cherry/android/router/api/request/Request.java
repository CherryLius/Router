package cherry.android.router.api.request;

/**
 * Created by LHEE on 2017/7/22.
 */

public interface Request<T> {
    String getUri();

    Class<?> getDestination();

    T invoke();

    void request();
}
