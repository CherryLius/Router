package cherry.android.router.api;

import android.content.Intent;
import android.text.TextUtils;

import cherry.android.router.api.utils.Logger;
import cherry.android.router.api.utils.Utils;

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

    static class QueryIntent<R> extends Parameter<Intent, R> {

        QueryIntent(String name) {
            super(name);
        }

        @Override
        void apply(Intent intent, R args) {
            if (args != null)
                Utils.putValue2Bundle(intent.getExtras(), name, args);
        }
    }
}
