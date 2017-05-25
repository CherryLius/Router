package cherry.android.router.api;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Administrator on 2017/5/25.
 */

public class RouteMeta {
    private Class<?> destination;
    private String uri;
    private Bundle mArguments;
    @Type
    private int type = TYPE_MATCHER;

    public static final int TYPE_ACTIVITY = 0x01;
    public static final int TYPE_FRAGMENT = 0x02;
    public static final int TYPE_MATCHER = 0x03;

    @IntDef({TYPE_ACTIVITY, TYPE_FRAGMENT, TYPE_MATCHER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    public RouteMeta(String uri, Class<?> destination) {
        this(uri, destination, TYPE_MATCHER);
    }

    RouteMeta(String uri, Class<?> destination, @Type int type) {
        this.uri = uri;
        this.destination = destination;
        this.type = type;
    }

    String getUri() {
        return uri;
    }

    Class<?> getDestination() {
        return destination;
    }

    void setDestination(Class<?> destination) {
        this.destination = destination;
    }

    void setUri(String uri) {
        this.uri = uri;
    }

    int getType() {
        return type;
    }

    void setType(int type) {
        this.type = type;
    }

    void reset() {
        this.uri = null;
        this.destination = null;
    }

    Bundle getQueryArgument() {
        Uri routeUri = Uri.parse(uri);
        if (mArguments == null) {
            mArguments = new Bundle();
        } else {
            mArguments.clear();
        }
        for (String name : routeUri.getQueryParameterNames()) {
            mArguments.putString(name, routeUri.getQueryParameter(name));
        }
        return mArguments;
    }

    public static RouteMeta newMeta(String uri, Class<?> destination, @Type int type) {
        RouteMeta routeMeta = new RouteMeta(uri, destination, type);
        return routeMeta;
    }
}
