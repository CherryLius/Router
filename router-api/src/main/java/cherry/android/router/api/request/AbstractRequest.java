package cherry.android.router.api.request;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import java.util.Map;

import cherry.android.router.api.RequestOptions;
import cherry.android.router.api.RouteRule;
import cherry.android.router.api.RouterInternal;
import cherry.android.router.api.callback.RouterCallback;
import cherry.android.router.api.utils.Logger;
import cherry.android.router.api.utils.Utils;

/**
 * Created by ROOT on 2017/7/25.
 */

public abstract class AbstractRequest<T, R> implements Request<T, R> {
    private static final String TAG = "Request";
    protected Class<?> destination;
    protected String uri;
    protected RouteRule rule;
    protected RequestOptions options;
    protected RouterCallback callback;
    protected R host;

    public AbstractRequest(@NonNull String uri) {
        this.uri = uri;
    }

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
        Map<String, String> queryMap;
        if (routeUri.getQuery() == null) {
            int index = uri.indexOf('?');
            if (index == -1)
                return;
            String query = uri.substring(index + 1, uri.length());
            Logger.e(TAG, "sub query=" + query);
            queryMap = Utils.splitQueryParameters(query);
        } else {
            Logger.e(TAG, "uri.query=" + routeUri.getQuery());
            queryMap = Utils.splitQueryParameters(routeUri);
        }
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

    @Override
    public void callback(RouterCallback callback) {
        this.callback = callback;
    }

    @Override
    public void setHost(R r) {
        if (!(r instanceof Context) && !(r instanceof Fragment)
                && !(r instanceof android.app.Fragment)) {
            throw new IllegalArgumentException("Only support Context and Fragment");
        }
        this.host = r;
    }

    protected Context getContext() {
        if (host == null)
            return RouterInternal.get().getContext();
        if (host instanceof Context)
            return (Context) host;
        if (host instanceof Fragment)
            return ((Fragment) host).getActivity();
        if (host instanceof android.app.Fragment)
            return ((android.app.Fragment) host).getActivity();
        return RouterInternal.get().getContext();
    }
}
