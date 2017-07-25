package cherry.android.router.api;

import android.app.Fragment;
import android.content.Intent;

import java.lang.reflect.Type;

import cherry.android.router.api.utils.Utils;

/**
 * Created by ROOT on 2017/7/25.
 */

public class RouteAdapter implements RequestAdapter {

    private Type returnType;

    public RouteAdapter(Type returnType) {
        this.returnType = returnType;
    }

    @Override
    public Object adapt(Request request) {
        final Class<?> destination = request.getDestination();
        if (Utils.isFragment(destination)) {
            if (returnType.equals(void.class))
                throw new UnsupportedOperationException("must get returnType to hold Fragment");
            else if (returnType.equals(Fragment.class) || returnType.equals(android.support.v4.app.Fragment.class))
                return request.getFragment();
            else if (returnType.equals(Request.class))
                return request;
        }
        if (Utils.isActivity(destination)) {
            RouterInternal.get().request(request);
            return request;
        }
        Intent intent = request.getIntent(RouterInternal.get().getContext());
        if (intent != null) {
            RouterInternal.get().request(request);
            return intent;
        }
        throw new UnsupportedOperationException("UnSupport");
    }

}
