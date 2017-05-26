package cherry.android.router.api;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/25.
 */

public class RouteMeta {
    public static final int TYPE_ACTIVITY = 0x01;
    public static final int TYPE_FRAGMENT = 0x02;
    public static final int TYPE_MATCHER = 0x03;

    private Class<?> destination;
    private String uri;
    private Bundle mArguments;
    @Type
    private int type = TYPE_MATCHER;

    private String[] interceptorNames;
    private List<InterceptorMeta> mInterceptorList;


    @IntDef({TYPE_ACTIVITY, TYPE_FRAGMENT, TYPE_MATCHER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    RouteMeta(String uri, Class<?> destination) {
        this(uri, destination, TYPE_MATCHER);
    }

    RouteMeta(String uri, Class<?> destination, @Type int type) {
        this(uri, destination, type, null);
    }

    RouteMeta(String uri, Class<?> destination, @Type int type, String[] interceptorNames) {
        this.uri = uri;
        this.destination = destination;
        this.type = type;
        this.interceptorNames = interceptorNames;
    }

    public String getUri() {
        return uri;
    }

    public Class<?> getDestination() {
        return destination;
    }

    void setUri(String uri) {
        this.uri = uri;
        parseQueryArgument();
    }

    int getType() {
        return type;
    }

    Bundle getArgument() {
        return mArguments;
    }

    private void parseQueryArgument() {
        Uri routeUri = Uri.parse(uri);
        if (mArguments == null) {
            mArguments = new Bundle();
        } else {
            mArguments.clear();
        }
        for (String name : routeUri.getQueryParameterNames()) {
            mArguments.putString(name, routeUri.getQueryParameter(name));
        }
    }

    void findInterceptors(Map<String, InterceptorMeta> interceptors) {
        if (interceptors == null || interceptors.isEmpty())
            return;
        if (this.interceptorNames == null || this.interceptorNames.length == 0)
            return;
        if (mInterceptorList == null)
            mInterceptorList = new ArrayList<>();
        for (String name : interceptorNames) {
            InterceptorMeta meta = interceptors.get(name);
            if (meta != null && !mInterceptorList.contains(meta)) {
                mInterceptorList.add(meta);
            }
        }
        Collections.sort(mInterceptorList);
    }

    boolean interceptor() {
        if (mInterceptorList == null || mInterceptorList.size() == 0) {
            Log.e("Test", "interceptor Meta List is Empty");
            return false;
        }
        for (InterceptorMeta meta : mInterceptorList) {
            if (meta.getInterceptor().intercept(this)) {
                return true;
            }
        }
        return false;
    }

    public static RouteMeta newMeta(String uri, Class<?> destination, String... interceptors) {
        return newMeta(uri, destination, getTypeByClass(destination), interceptors);
    }

    public static RouteMeta newMeta(String uri, Class<?> destination, @Type int type, String... interceptors) {
        RouteMeta routeMeta = new RouteMeta(uri, destination, type, interceptors);
        return routeMeta;
    }

    static int getTypeByClass(Class<?> destination) {
        String className = destination.getCanonicalName();
        if (className.equals("android.app.Activity"))
            return TYPE_ACTIVITY;
        else if (className.equals("android.app.Fragment")
                || className.equals("android.support.v4.app.Fragment"))
            return TYPE_FRAGMENT;
        return TYPE_MATCHER;
    }
}
