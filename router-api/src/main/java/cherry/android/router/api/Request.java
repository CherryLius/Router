package cherry.android.router.api;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityOptionsCompat;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import cherry.android.router.api.utils.Utils;

import static cherry.android.router.api.RouteRule.TYPE_ACTIVITY;
import static cherry.android.router.api.RouteRule.TYPE_FRAGMENT;
import static cherry.android.router.api.RouteRule.TYPE_MATCHER;

/**
 * Created by LHEE on 2017/7/22.
 */

public class Request {
    private Class<?> destination;
    private String uri;
    private Bundle arguments;
    private int requestCode = -1;
    private int enterAnim;
    private int exitAnim;
    private ActivityOptionsCompat optionsCompat;
    private boolean ignoreInterceptor;
    private RouteRule rule;

    public Request(@NonNull RouteRule rule) {
        this.rule = rule;
        this.destination = rule.getDestination();
    }

    public Request(@NonNull String uri, @NonNull RouteRule rule) {
        this.rule = rule;
        this.uri = uri;
        this.destination = rule.getDestination();
        this.arguments = new Bundle();
        parseQueryArgument();
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

    void ignoreInterceptor(boolean ignore) {
        this.ignoreInterceptor = ignore;
    }

    boolean isIgnoreInterceptor() {
        return this.ignoreInterceptor;
    }

    private void parseQueryArgument() {
        Uri routeUri = Uri.parse(uri);
        for (String name : routeUri.getQueryParameterNames()) {
            arguments.putString(name, routeUri.getQueryParameter(name));
        }
    }

    RouteRule getRule() {
        return this.rule;
    }

    public Intent getIntent(Context context) {
        final int type = this.rule.getType();
        if (type == TYPE_FRAGMENT)
            return null;
        Intent intent = null;
        if (type == TYPE_ACTIVITY) {
            intent = new Intent(context, this.destination);
            intent.putExtras(this.arguments);
        } else if (type == TYPE_MATCHER) {
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
        final int type = this.rule.getType();
        if (type == TYPE_MATCHER || type == TYPE_ACTIVITY)
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
        throw new UnsupportedOperationException("cannot create Fragment with uri=" + this.uri);
    }
}
