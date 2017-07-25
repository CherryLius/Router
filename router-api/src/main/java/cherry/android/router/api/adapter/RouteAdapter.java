package cherry.android.router.api.adapter;

import android.app.Fragment;

import java.lang.reflect.Type;

import cherry.android.router.api.request.ActionRequest;
import cherry.android.router.api.request.ActivityRequest;
import cherry.android.router.api.request.FragmentRequest;
import cherry.android.router.api.request.Request;

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
        if (request instanceof FragmentRequest) {
            if (returnType.equals(void.class))
                throw new UnsupportedOperationException("must get returnType to hold Fragment");
            else if (returnType.equals(Fragment.class) || returnType.equals(android.support.v4.app.Fragment.class))
                return request.invoke();
            else if (returnType.equals(Request.class))
                return request;
        } else if (request instanceof ActionRequest) {
            request.request();
            return request.invoke();
        } else if (request instanceof ActivityRequest) {
            request.request();
            return request;
        }
        throw new UnsupportedOperationException("UnSupport");
    }

}
