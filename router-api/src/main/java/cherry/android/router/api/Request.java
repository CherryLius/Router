package cherry.android.router.api;

/**
 * Created by LHEE on 2017/7/22.
 */

public interface Request<T> {
    T invoke();
}
