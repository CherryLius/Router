package cherry.android.router.api;

import android.text.TextUtils;

import cherry.android.router.api.request.AbstractRequest;
import cherry.android.router.api.request.ActionRequest;
import cherry.android.router.api.utils.Logger;

/**
 * Created by LHEE on 2017/7/23.
 */

/*package-private*/ abstract class Parameter<T, R> {
    String name;

    Parameter(String name) {
        if (TextUtils.isEmpty(name))
            throw new NullPointerException("name should not be Null or Empty");
        this.name = name;
    }

    abstract void apply(T t, R args);

    static class QueryURL<R> extends Parameter<RouteUrl.Builder, R> {

        QueryURL(String name) {
            super(name);
        }

        @Override
        void apply(RouteUrl.Builder builder, R args) {
            Logger.i("Test", "args=" + args);
            if (args != null)
                builder.addQueryParameter(name, args.toString());
        }
    }

    static class QueryRequest<R> extends Parameter<AbstractRequest, R> {

        QueryRequest(String name) {
            super(name);
        }

        @Override
        void apply(AbstractRequest request, R args) {
            if (args != null)
                request.putExtra(name, args);
        }
    }

    static class UriRequest<R> extends Parameter<ActionRequest, R> {

        UriRequest(String name) {
            super("uri=" + name);
        }

        @Override
        void apply(ActionRequest request, R args) {
            if (args != null)
                request.setData(args.toString());
        }
    }
}
