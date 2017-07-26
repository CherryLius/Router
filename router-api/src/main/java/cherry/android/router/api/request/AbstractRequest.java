package cherry.android.router.api.request;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import java.util.Map;

import cherry.android.router.api.RequestOptions;
import cherry.android.router.api.RouteRule;
import cherry.android.router.api.utils.Logger;
import cherry.android.router.api.utils.Utils;

/**
 * Created by ROOT on 2017/7/25.
 */

public abstract class AbstractRequest<T> implements Request<T> {
    protected Class<?> destination;
    protected String uri;
    protected RouteRule rule;
    protected RequestOptions options;

    public AbstractRequest(@NonNull RouteRule rule) {
        this.rule = rule;
        this.uri = rule.getUri();
        this.destination = rule.getDestination();
    }

    public AbstractRequest(@NonNull String uri, @NonNull RouteRule rule) {
        this.rule = rule;
        this.uri = uri;
        this.destination = rule.getDestination();
    }

    public AbstractRequest(Class<?> destination) {
        this.destination = destination;
    }

    private void parseQueryArgument() {
        if (TextUtils.isEmpty(uri))
            return;
        Uri routeUri = Uri.parse(uri);
        Logger.e("Test", "uri=" + routeUri.getQuery());
        Map<String, String> queryMap = Utils.splitQueryParameters(routeUri);
        for (Map.Entry<String, String> entry : queryMap.entrySet()) {
            this.options.getArguments().putString(entry.getKey(), entry.getValue());
        }
//        for (String name : routeUri.getQueryParameterNames()) {
//            this.options.getArguments().putString(name, routeUri.getQueryParameter(name));
//        }
    }

    @Override
    public RouteRule getRule() {
        return this.rule;
    }

    @Override
    public String getUri() {
        return this.uri;
    }

    @Override
    public Class<?> getDestination() {
        return destination;
    }

    @Override
    public void setOptions(RequestOptions options) {
        this.options = options;
        parseQueryArgument();
    }

    @Override
    public RequestOptions getOptions() {
        return this.options;
    }
}
