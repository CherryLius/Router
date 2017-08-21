package cherry.android.router.api;

import android.support.v4.app.ActivityOptionsCompat;
import android.text.TextUtils;

import java.io.IOException;

import cherry.android.router.api.convert.Converter;
import cherry.android.router.api.request.ActionRequest;
import cherry.android.router.api.request.Request;

/**
 * Created by LHEE on 2017/7/23.
 */

/*package-private*/ abstract class Parameter<T, R> {
    String name;
    Converter<R, String> converter;

    Parameter(String name, Converter<R, String> converter) {
        this.name = name;
        this.converter = converter;
    }

    abstract void apply(T t, R args) throws IOException;

    static class QueryURL<R> extends Parameter<RouteUrl.Builder, R> {


        QueryURL(String name, Converter<R, String> converter) {
            super(name, converter);
            if (TextUtils.isEmpty(name))
                throw new NullPointerException("name should not be Null or Empty");
        }

        @Override
        void apply(RouteUrl.Builder builder, R args) throws IOException {
            if (args == null) return;
            String value = converter.convert(args);
            if (value == null) return;
            builder.addQueryParameter(name, value);
        }
    }

    static class QueryRequest<R> extends Parameter<Request, R> {

        QueryRequest(String name, Converter<R, String> converter) {
            super(name, converter);
            if (TextUtils.isEmpty(name))
                throw new NullPointerException("name should not be Null or Empty");
        }

        @Override
        void apply(Request request, R args) throws IOException {
            if (args == null) return;
            String value = converter.convert(args);
            if (value == null) return;
            request.getOptions().extra(name, value);
        }
    }

    static class UriRequest<R> extends Parameter<ActionRequest, R> {

        UriRequest(String name) {
            super(name, null);
        }

        @Override
        void apply(ActionRequest request, R args) throws IOException {
            if (args == null) return;
            request.setData(args.toString());
        }
    }

    static class OptionsCompat<R> extends Parameter<Request, R> {

        OptionsCompat(String name) {
            super(name, null);
        }

        @Override
        void apply(Request request, R args) {
            if (args != null && args instanceof ActivityOptionsCompat)
                request.getOptions().optionsCompat((ActivityOptionsCompat) args);
        }
    }
}
