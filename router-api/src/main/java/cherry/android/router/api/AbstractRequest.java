package cherry.android.router.api;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import cherry.android.router.api.utils.Utils;

/**
 * Created by ROOT on 2017/7/25.
 */

public abstract class AbstractRequest<T> implements Request<T> {
    protected Class<?> destination;
    protected String uri;
    protected Bundle arguments;

    public AbstractRequest(@NonNull RouteRule rule) {
        this.uri = rule.getUri();
        this.destination = rule.getDestination();
        this.arguments = new Bundle();
        parseQueryArgument();
    }

    public AbstractRequest(@NonNull String uri, @NonNull RouteRule rule) {
        this.uri = uri;
        this.destination = rule.getDestination();
        this.arguments = new Bundle();
        parseQueryArgument();
    }

    public AbstractRequest(Class<?> destination) {
        this.destination = destination;
        this.arguments = new Bundle();
    }

    private void parseQueryArgument() {
        Uri routeUri = Uri.parse(uri);
        for (String name : routeUri.getQueryParameterNames()) {
            arguments.putString(name, routeUri.getQueryParameter(name));
        }
    }

    public Class<?> getDestination() {
        return destination;
    }

    void putExtra(String key, Object value) {
        Utils.putValue2Bundle(arguments, key, value);
    }

    void putExtra(Bundle value) {
        arguments.putAll(value);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void putExtra(PersistableBundle value) {
        arguments.putAll(value);
    }
}
