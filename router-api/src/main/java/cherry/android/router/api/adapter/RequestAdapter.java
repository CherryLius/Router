package cherry.android.router.api.adapter;

import cherry.android.router.api.request.Request;

/**
 * Created by ROOT on 2017/7/25.
 */

public interface RequestAdapter {
    Object adapt(Request request);
}
