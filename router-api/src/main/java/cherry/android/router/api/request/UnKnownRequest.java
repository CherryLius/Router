package cherry.android.router.api.request;

import android.support.annotation.NonNull;

/**
 * Created by LHEE on 2017/7/26.
 */

public class UnKnownRequest extends AbstractRequest<Object> {

    public UnKnownRequest(@NonNull String uri) {
        super(uri, null);
    }

    @Override
    public Object invoke() {
        return null;
    }

    @Override
    public void request() {
        if (callback != null)
            callback.onFailed(this, "UnKnownRequest");
    }
}
