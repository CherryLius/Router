package cherry.android.router.api;

import android.support.v4.app.ActivityOptionsCompat;
import android.text.TextUtils;

import cherry.android.router.api.request.ActionRequest;
import cherry.android.router.api.request.Request;

/**
 * Created by LHEE on 2017/7/23.
 */

/*package-private*/ abstract class Parameter<T, R> {
    String name;

    Parameter(String name) {
        this.name = name;
    }

    abstract void apply(T t, R args);

    static class QueryURL<R> extends Parameter<RouteUrl.Builder, R> {

        QueryURL(String name) {
            super(name);
            if (TextUtils.isEmpty(name))
                throw new NullPointerException("name should not be Null or Empty");
        }

        @Override
        void apply(RouteUrl.Builder builder, R args) {
            if (args != null)
                builder.addQueryParameter(name, args.toString());
        }
    }

    static class QueryRequest<R> extends Parameter<Request, R> {

        QueryRequest(String name) {
            super(name);
            if (TextUtils.isEmpty(name))
                throw new NullPointerException("name should not be Null or Empty");
        }

        @Override
        void apply(Request request, R args) {
            if (args != null) {
                request.getOptions().extra(name, args);
            }
        }
    }

    static class UriRequest<R> extends Parameter<ActionRequest, R> {

        UriRequest(String name) {
            super(name);
        }

        @Override
        void apply(ActionRequest request, R args) {
            if (args != null)
                request.setData(args.toString());
        }
    }

    static class OptionsCompat<R> extends Parameter<Request, R> {

        OptionsCompat(String name) {
            super(name);
        }

        @Override
        void apply(Request request, R args) {
            if (args != null && args instanceof ActivityOptionsCompat)
                request.getOptions().optionsCompat((ActivityOptionsCompat) args);
        }
    }
}
