package cherry.android.router.api.request;

import android.support.annotation.NonNull;

import cherry.android.router.api.exception.UnKnownUriException;
import cherry.android.router.api.utils.Logger;

/**
 * Created by LHEE on 2017/7/26.
 */

public class UnKnownRequest extends AbstractRequest<Object, Object> {

    public UnKnownRequest(@NonNull String uri) {
        super(uri);
    }

    @Override
    public Object invoke() {
        return null;
    }

    @Override
    public void request() {
        if (callback != null)
            callback.onFailed(this, "UnKnownRequest");
        else
            throw new UnKnownUriException("UnKnown Uri: " + this.uri);
    }
}
