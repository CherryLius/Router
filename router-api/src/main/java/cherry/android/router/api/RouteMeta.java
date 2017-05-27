package cherry.android.router.api;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.IntDef;
import android.support.v4.app.ActivityOptionsCompat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import cherry.android.router.api.utils.Logger;
import cherry.android.router.api.utils.Utils;

/**
 * Created by Administrator on 2017/5/25.
 */

public class RouteMeta {

    private static final String TAG = "RouteMeta";

    public static final int TYPE_ACTIVITY = 0x01;
    public static final int TYPE_FRAGMENT = 0x02;
    public static final int TYPE_MATCHER = 0x03;

    private Class<?> destination;
    private String uri;
    private Bundle arguments;
    private int requestCode = -1;
    private int enterAnim;
    private int exitAnim;
    private ActivityOptionsCompat optionsCompat;
    private boolean ignoreInterceptor;
    @Type
    private int type = TYPE_MATCHER;

    private String[] interceptorNames;
    private List<InterceptorMeta> interceptorList;

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

    Bundle getArgument() {
        return arguments;
    }

    void putExtra(String key, Object value) {
        Utils.putValue2Bundle(arguments, key, value);
    }

    void putExtra(Bundle value) {
        arguments.putAll(value);
    }

    void putExtra(PersistableBundle value) {
        arguments.putAll(value);
    }

    void setRequestCode(int requestCode) {
        this.requestCode = requestCode < 0 ? -1 : requestCode;
    }

    int getRequestCode() {
        return this.requestCode;
    }

    void transition(int enterAnim, int exitAnim) {
        this.enterAnim = enterAnim;
        this.exitAnim = exitAnim;
    }

    int getEnterAnim() {
        return this.enterAnim;
    }

    int getExitAnim() {
        return this.exitAnim;
    }

    void setOptionsCompat(ActivityOptionsCompat optionsCompat) {
        this.optionsCompat = optionsCompat;
    }

    ActivityOptionsCompat getOptionsCompat() {
        return this.optionsCompat;
    }

    void setIgnoreInterceptor(boolean ignore) {
        this.ignoreInterceptor = ignore;
    }

    boolean isIgnoreInterceptor() {
        return this.ignoreInterceptor;
    }

    void reset() {
        if (arguments != null)
            arguments.clear();
        requestCode = -1;
        enterAnim = 0;
        exitAnim = 0;
        optionsCompat = null;
        ignoreInterceptor = false;
    }

    private void parseQueryArgument() {
        Uri routeUri = Uri.parse(uri);
        if (arguments == null) {
            arguments = new Bundle();
        } else {
            arguments.clear();
        }
        for (String name : routeUri.getQueryParameterNames()) {
            arguments.putString(name, routeUri.getQueryParameter(name));
        }
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

    public Intent getIntent(Context context) {
        if (this.type == TYPE_FRAGMENT)
            return null;
        Intent intent = null;
        if (this.type == TYPE_ACTIVITY) {
            intent = new Intent(context, this.destination);
            intent.putExtras(this.arguments);
        } else if (this.type == TYPE_MATCHER) {
            if (uri.startsWith("http://") || uri.startsWith("https://")) {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(uri));
            }
        }
        if (intent != null && !(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

    public <T> T getFragment() {
        if (this.type == TYPE_MATCHER || this.type == TYPE_ACTIVITY)
            return null;
        try {
            Class<?> destination = this.destination;
            if (!Fragment.class.isAssignableFrom(destination)
                    && !android.support.v4.app.Fragment.class.isAssignableFrom(destination))
                throw new IllegalArgumentException("parameter must be android.app.Fragment or android.support.v4.app.Fragment");
            Constructor constructor = destination.getConstructor();
            Object object = constructor.newInstance();
            if (object instanceof Fragment) {
                ((Fragment) object).setArguments(this.arguments);
            } else if (object instanceof android.support.v4.app.Fragment) {
                ((android.support.v4.app.Fragment) object).setArguments(this.arguments);
            }
            return (T) object;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static RouteMeta newMeta(String uri, Class<?> destination, String... interceptors) {
        return newMeta(uri, destination, getTypeByClass(destination), interceptors);
    }

    public static RouteMeta newMeta(String uri, Class<?> destination, @Type int type, String... interceptors) {
        if (!Utils.checkRouteValid(uri))
            throw new IllegalArgumentException("Uri format invalid: " + uri);
        RouteMeta routeMeta = new RouteMeta(uri, destination, type, interceptors);
        return routeMeta;
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
}
