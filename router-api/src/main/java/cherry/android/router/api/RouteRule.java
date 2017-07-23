package cherry.android.router.api;

import android.app.Activity;
import android.app.Fragment;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import cherry.android.router.api.utils.Logger;
import cherry.android.router.api.utils.Utils;

/**
 * Created by Administrator on 2017/5/25.
 */

public class RouteRule {

    private static final String TAG = "RouteRule";

    public static final int TYPE_ACTIVITY = 0x01;
    public static final int TYPE_FRAGMENT = 0x02;
    public static final int TYPE_MATCHER = 0x03;

    private Class<?> destination;
    private String uri;
    @Type
    private int type = TYPE_MATCHER;

    private String[] interceptorNames;
    private List<InterceptorMeta> interceptorList;

    @IntDef({TYPE_ACTIVITY, TYPE_FRAGMENT, TYPE_MATCHER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    RouteRule(String uri, Class<?> destination) {
        this(uri, destination, TYPE_MATCHER);
    }

    RouteRule(String uri, Class<?> destination, @Type int type) {
        this(uri, destination, type, null);
    }

    RouteRule(String uri, Class<?> destination, @Type int type, String[] interceptorNames) {
        this.uri = uri;
        this.destination = destination;
        this.type = type;
        this.interceptorNames = interceptorNames;
    }

    String getUri() {
        return uri;
    }

    public Class<?> getDestination() {
        return destination;
    }

    void setUri(String uri) {
        this.uri = uri;
    }

    @Type
    int getType() {
        return this.type;
    }


    void findInterceptors(Map<String, InterceptorMeta> interceptors) {
        if (interceptors == null || interceptors.isEmpty())
            return;
        if (this.interceptorNames == null || this.interceptorNames.length == 0)
            return;
        if (interceptorList == null)
            interceptorList = new ArrayList<>();
        for (String name : interceptorNames) {
            InterceptorMeta meta = interceptors.get(name);
            if (meta != null && !interceptorList.contains(meta)) {
                interceptorList.add(meta);
            }
        }
        Collections.sort(interceptorList);
    }

    boolean interceptor() {
        if (interceptorList == null || interceptorList.size() == 0) {
            Logger.w(TAG, "interceptor Meta List is Empty");
            return false;
        }
        for (InterceptorMeta meta : interceptorList) {
            if (meta.getInterceptor().intercept(this)) {
                return true;
            }
        }
        return false;
    }

    public static RouteRule newRule(String uri, Class<?> destination, String... interceptors) {
        return newRule(uri, destination, getTypeByClass(destination), interceptors);
    }

    public static RouteRule newRule(String uri, Class<?> destination, @Type int type, String... interceptors) {
        if (!Utils.checkRouteValid(uri))
            throw new IllegalArgumentException("Uri format invalid: " + uri);
        RouteRule routeRule = new RouteRule(uri, destination, type, interceptors);
        return routeRule;
    }

    static int getTypeByClass(Class<?> destination) {
        if (destination == null)
            return TYPE_MATCHER;
        if (Activity.class.isAssignableFrom(destination)) {
            return TYPE_ACTIVITY;
        } else if (Fragment.class.isAssignableFrom(destination)
                || android.support.v4.app.Fragment.class.isAssignableFrom(destination)) {
            return TYPE_FRAGMENT;
        }
        return TYPE_MATCHER;
    }

    @Override
    public String toString() {
        return "RouteRule{" +
                "destination=" + destination +
                ", uri='" + uri + '\'' +
                '}';
    }
}
