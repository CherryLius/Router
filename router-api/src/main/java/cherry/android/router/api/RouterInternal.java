package cherry.android.router.api;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.TextUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cherry.android.router.api.callback.RouterCallback;
import cherry.android.router.api.intercept.IInterceptor;
import cherry.android.router.api.request.AbstractRequest;
import cherry.android.router.api.request.ActionRequest;
import cherry.android.router.api.request.ActivityRequest;
import cherry.android.router.api.request.FragmentRequest;
import cherry.android.router.api.request.Request;
import cherry.android.router.api.request.UnKnownRequest;
import cherry.android.router.api.utils.Logger;
import cherry.android.router.api.utils.Utils;

import static cherry.android.router.api.utils.Utils.getPicker;

/**
 * Created by Administrator on 2017/5/25.
 */

public final class RouterInternal {

    private static final String TAG = "RouterInternal";
    private static final String PACKAGE_NAME = "cherry.android.router";

    private static volatile RouterInternal sInstance;

    private Map<String, RouteRule> mRouterTable = new RouteMap();
    private Map<String, RouteInterceptor> mInterceptorMap = new LinkedHashMap<>();
    private IInterceptor mGlobalInterceptor;
    private boolean mInitialized;
    private Context mContext;

    private Request mRequest;

    public static RouterInternal get() {
        if (sInstance == null)
            synchronized (RouterInternal.class) {
                if (sInstance == null)
                    sInstance = new RouterInternal();
            }
        return sInstance;
    }

    private RouterInternal() {
    }

    synchronized void init(@NonNull Context context) {
        if (!mInitialized) {
            mContext = context;
            List<String> classNames = Utils.getFileNameByPackage(context, PACKAGE_NAME);
            for (String className : classNames) {
                if (className.endsWith("_RoutePicker")) {
                    getPicker(className).pick(mRouterTable);
                } else if (className.endsWith("_InterceptorPicker")) {
                    getPicker(className).pick(mInterceptorMap);
                }
            }
            mInitialized = true;
        }
    }

    synchronized void destroy() {
        if (mInitialized) {
            mRouterTable.clear();
            mInterceptorMap.clear();
            mInitialized = false;
            mContext = null;
            sInstance = null;
        }
    }

    void addRoutePicker(@NonNull Router.RoutePicker picker) {
        Map<String, Class<?>> map = picker.pick();
        if (map == null || map.isEmpty()) return;
        for (Map.Entry<String, Class<?>> entry : map.entrySet()) {
            if (!Utils.checkRouteValid(entry.getKey()))
                throw new IllegalArgumentException("invalid uri: " + entry.getKey());
            if (mRouterTable.containsKey(entry.getKey()))
                continue;
            RouteRule rule = Utils.findRouteRuleByClass(mRouterTable, entry.getValue());
            if (rule != null) {
                mRouterTable.put(entry.getKey(), rule);
            } else {
                mRouterTable.put(entry.getKey(), RouteRule.newRule(entry.getKey(), entry.getValue()));
            }
        }
    }

    void addGlobalInterceptor(@NonNull IInterceptor interceptor) {
        mGlobalInterceptor = interceptor;
    }

    RouterInternal build(@NonNull String uri) {
        if (!mInitialized) {
            throw new RuntimeException("Router must be initialize");
        }
        if (TextUtils.isEmpty(uri))
            throw new NullPointerException("Uri cannot be Empty");
        RouteRule rule = mRouterTable.get(uri);
        mRequest = generateRequest(rule, uri);
        mRequest.setOptions(new RequestOptions());
        return this;
    }

    RouterInternal build(@NonNull Uri uri) {
        if (!mInitialized) {
            throw new RuntimeException("Router must be initialize");
        }
        if (uri == null)
            throw new NullPointerException("Uri cannot be Empty");
        RouteRule rule = mRouterTable.get(uri.getPath());
        mRequest = generateRequest(rule, uri.toString());
        mRequest.setOptions(new RequestOptions());
        return this;
    }

    private Request generateRequest(RouteRule rule, String uri) {
        if (rule != null) {
            if (rule.getType() == RouteRule.TYPE_FRAGMENT) {
                return new FragmentRequest(uri, rule);
            } else if (rule.getType() == RouteRule.TYPE_ACTIVITY) {
                return new ActivityRequest(uri, rule);
            }
            Logger.w(TAG, "rule uri:" + rule.getUri());
        }
        Logger.w(TAG, "generate Action, Uri Cannot Find Route:" + uri);
        if (uri.startsWith("http://") || uri.startsWith("https://"))
            return new ActionRequest.Builder().setAction(Intent.ACTION_VIEW).setUri(uri).build();
        return new UnKnownRequest(uri);
    }

    public boolean intercept(Request request) {
        RequestOptions options = request.getOptions();
        boolean ignore = options != null && options.isIgnoreInterceptor();
        if (mGlobalInterceptor != null && mGlobalInterceptor.intercept(request)
                && !ignore) {
            return true;
        }
        if (ignore) return false;
        RouteRule rule = request.getRule();
        if (rule == null)
            return false;
        rule.findInterceptors(mInterceptorMap);
        if (rule.getInterceptors() == null || rule.getInterceptors().size() == 0) {
            Logger.w(TAG, "interceptor List is Empty");
            return false;
        }
        for (RouteInterceptor meta : rule.getInterceptors()) {
            if (meta.getInterceptor().intercept(request)) {
                return true;
            }
        }
        return false;
    }

    public RouterInternal extra(String key, Object value) {
        if (mRequest != null) {
            RequestOptions options = mRequest.getOptions();
            if (options != null)
                options.extra(key, value);
        }
        return this;
    }

    public RouterInternal extra(Bundle value) {
        if (mRequest != null) {
            RequestOptions options = mRequest.getOptions();
            if (options != null)
                options.extra(value);
        }
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RouterInternal extra(PersistableBundle value) {
        if (mRequest != null && mRequest instanceof AbstractRequest) {
            RequestOptions options = mRequest.getOptions();
            if (options != null)
                options.extra(value);
        }
        return this;
    }

    public RouterInternal transition(int enterAnim, int exitAnim) {
        if (mRequest != null) {
            RequestOptions options = mRequest.getOptions();
            if (options != null)
                options.transition(enterAnim, exitAnim);
        }
        return this;
    }

    @TargetApi(16)
    public RouterInternal optionsCompat(ActivityOptionsCompat optionsCompat) {
        if (mRequest != null) {
            RequestOptions options = mRequest.getOptions();
            if (options != null)
                options.optionsCompat(optionsCompat);
        }
        return this;
    }

    public RouterInternal requestCode(int requestCode) {
        if (mRequest != null) {
            RequestOptions options = mRequest.getOptions();
            if (options != null)
                options.requestCode(requestCode);
        }
        return this;
    }

    public RouterInternal ignoreInterceptor(boolean ignore) {
        if (mRequest != null) {
            RequestOptions options = mRequest.getOptions();
            if (options != null)
                options.ignoreInterceptor(ignore);
        }
        return this;
    }

    public RouterInternal flags(int flags) {
        if (mRequest != null) {
            RequestOptions options = mRequest.getOptions();
            if (options != null)
                options.flags(flags);
        }
        return this;
    }

    RouteRule getRouteRule(@NonNull String uri) {
        return mRouterTable.get(uri);
    }

    RouteRule getRouteRule(@NonNull Class<?> className) {
        return Utils.findRouteRuleByClass(mRouterTable, className);
    }

    public Context getContext() {
        return mContext;
    }

    public void open() {
        open(mContext, null);
    }

    public <T> void open(T t) {
        open(t, null);
    }

    public <T> void open(T t, RouterCallback callback) {
        if (!Utils.isFragment(t.getClass()) && !Utils.isActivity(t.getClass())
                && !t.getClass().equals(Context.class))
            throw new IllegalArgumentException("Only Support Activity Fragment and Context");
        if (mRequest == null) {
            Logger.e(TAG, "open failed");
            if (callback != null)
                callback.onFailed(mRequest, "No Request");
            return;
        }
        mRequest.setHost(t);
        mRequest.callback(callback);
        mRequest.request();
    }

    public <T> T invoke() {
        if (mRequest == null) {
            throw new NullPointerException("request Null");
        }
        return (T) mRequest.invoke();
    }
}
