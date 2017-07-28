package cherry.android.router.api;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import cherry.android.router.api.utils.Utils;

/**
 * Created by ROOT on 2017/7/24.
 */

/*package-private*/ class RouteUrl {
    private String baseUrl;
    private List<String> queryNamesAndValues;

    private RouteUrl(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.queryNamesAndValues = builder.queryNamesAndValues;
    }

    public String toUrl() {
        if (queryNamesAndValues.size() == 0)
            return baseUrl;
        StringBuilder builder = new StringBuilder();
        builder.append(baseUrl).append('?');
        for (int i = 0; i < queryNamesAndValues.size(); i++) {
            builder.append(queryNamesAndValues.get(i));
            if (i < queryNamesAndValues.size() - 1)
                builder.append('&');
        }
        return builder.toString();
    }

    static class Builder {
        private String baseUrl;
        private List<String> queryNamesAndValues;

        Builder() {
            queryNamesAndValues = new ArrayList<>();
        }

        Builder addQueryParameter(String name, String value) {
            String query = name + "=" + value;
            queryNamesAndValues.add(query);
            return this;
        }

        RouteUrl build() {
            return new RouteUrl(this);
        }

        static Builder parse(@NonNull String baseUrl) {
            if (TextUtils.isEmpty(baseUrl) || !Utils.checkRouteValid(baseUrl)) {
                throw new IllegalArgumentException("invalid url: " + baseUrl);
            }
            Builder builder = new Builder();
            builder.baseUrl = baseUrl;
            return builder;
        }
    }
}
