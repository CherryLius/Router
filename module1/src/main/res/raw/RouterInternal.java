package cherry.android.router.api;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cherry.android.router.api.intercept.IInterceptor;
import cherry.android.router.api.utils.Logger;
import cherry.android.router.api.utils.Utils;

import static cherry.android.router.api.utils.Utils.getPicker;

/**
 * Created by Administrator on 2017/5/25.
 */

public final class RouterInternal {

    private static final String TAG = "RouterInternal";
    private static final String PACKAGE_NAME = "cherry.android.router";

    private static RouterInternal sInstance;

    private Map<String, RouteRule> mRouterTable = new RouteMap();
    private Map<String, InterceptorMeta> mInterceptorMap = new LinkedHashMap<>();
    private IInterceptor mGlobalInterceptor;
    private boolean mInitialized;
    private Context mContext;

    private RouteRule mRouteRule;

    static RouterInternal get() {
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
            RouteRule meta = Utils.findRouteMetaByClass(mRouterTable, entry.getValue());
            if (meta != null) {
                mRouterTable.put(entry.getKey(), meta);
            } else {
                mRouterTable.put(entry.getKey(), RouteRule.newRequest(entry.getKey(), entry.getValue()));
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
            throw new RuntimeException("Uri cannot be Empty");
        if (mRouteRule == null || !uri.equals(mRouteRule.getUri())) {
            mRouteRule = getRouteMeta(uri);
            if (mRouteRule != null) {
                mRouteRule.reset();
                mRouteRule.setUri(uri);
            } else {
                mRouteRule = RouteRule.newRequest(uri, null);
            }
        }
        return this;
    }

    private RouteRule getRouteMeta(String uri) {
        RouteRule routeRule = mRouterTable.get(uri);
        if (routeRule == null) {
            Logger.e(TAG, "uri Not Found Route: " + uri);
        }
        return routeRule;
    }

    boolean intercept(RouteRule routeRule) {
        if (routeRule.isIgnoreInterceptor())
            return false;
        if (mGlobalInterceptor != null && mGlobalInterceptor.intercept(routeRule)) {
            return true;
        }
        routeRule.findInterceptors(mInterceptorMap);
        if (routeRule.interceptor()) {
            return true;
        }
        return false;
    }

    public RouterInternal extra(String key, Object value) {
        if (mRouteRule != null) {
            mRouteRule.putExtra(key, value);
        }
        return this;
    }

    public RouterInternal allExtra(Bundle value) {
        if (mRouteRule != null) {
            mRouteRule.putExtra(value);
        }
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RouterInternal allExtra(PersistableBundle value) {
        if (mRouteRule != null) {
            mRouteRule.putExtra(value);
        }
        return this;
    }

    public RouterInternal transition(int enterAnim, int exitAnim) {
        if (mRouteRule != null) {
            mRouteRule.transition(enterAnim, exitAnim);
        }
        return this;
    }

    @TargetApi(16)
    public RouterInternal optionsCompat(ActivityOptionsCompat optionsCompat) {
        if (mRouteRule != null) {
            mRouteRule.setOptionsCompat(optionsCompat);
        }
        return this;
    }

    public RouterInternal requestCode(int requestCode) {
        if (mRouteRule != null) {
            mRouteRule.setRequestCode(requestCode);
        }
        return this;
    }

    public RouterInternal ignoreInterceptor(boolean ignore) {
        if (mRouteRule != null) {
            mRouteRule.setIgnoreInterceptor(ignore);
        }
        return this;
    }

    public void open() {
        open(mContext);
    }

    public void open(Context context) {
        open(context, null);
    }

    public void open(Context context, IRouteCallback callback) {
        if (mRouteRule == null) {
            Logger.e(TAG, "open failed");
            if (callback != null)
                callback.onFailed(mRouteRule, "open failed");
            return;
        }
        if (intercept(mRouteRule)) {
            if (callback != null)
                callback.onIntercept(mRouteRule);
            return;
        }
        if (context == null)
            context = mContext;
        Intent intent = getIntent(context);
        if (intent == null) {
            if (callback != null) {
                callback.onFailed(mRouteRule, "Not Get Any Intent");
            }
            return;
        } else {
            if (callback != null)
                callback.onSuccess(mRouteRule);
        }
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            Bundle options = mRouteRule.getOptionsCompat() == null ? null : mRouteRule.getOptionsCompat().toBundle();
            if (mRouteRule.getRequestCode() == -1) {
                ActivityCompat.startActivity(activity, intent, options);
            } else {
                ActivityCompat.startActivityForResult(activity, intent, mRouteRule.getRequestCode(), options);
            }
            if (mRouteRule.getEnterAnim() != 0 || mRouteRule.getExitAnim() != 0) {
                activity.overridePendingTransition(mRouteRule.getEnterAnim(), mRouteRule.getExitAnim());
            }
        } else {
            ContextCompat.startActivity(context, intent, null);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void open(Fragment fragment) {
        if (mRouteRule == null) {
            Logger.e(TAG, "open failed");
            return;
        }
        if (intercept(mRouteRule)) {
            return;
        }
        Intent intent = getIntent(fragment.getActivity());
        Bundle options = mRouteRule.getOptionsCompat() == null ? null : mRouteRule.getOptionsCompat().toBundle();
        if (mRouteRule.getRequestCode() == -1) {
            fragment.startActivity(intent, options);
        } else {
            fragment.startActivityForResult(intent, mRouteRule.getRequestCode(), options);
        }
        if (mRouteRule.getEnterAnim() != 0 || mRouteRule.getExitAnim() != 0) {
            fragment.getActivity().overridePendingTransition(mRouteRule.getEnterAnim(), mRouteRule.getExitAnim());
        }
    }

    public void open(android.support.v4.app.Fragment fragment) {
        if (mRouteRule == null) {
            Logger.e(TAG, "open failed");
            return;
        }
        if (intercept(mRouteRule)) {
            return;
        }
        Intent intent = getIntent(fragment.getActivity());
        Bundle options = mRouteRule.getOptionsCompat() == null ? null : mRouteRule.getOptionsCompat().toBundle();
        if (mRouteRule.getRequestCode() == -1) {
            fragment.startActivity(intent, options);
        } else {
            fragment.startActivityForResult(intent, mRouteRule.getRequestCode(), options);
        }
        if (mRouteRule.getEnterAnim() != 0 || mRouteRule.getExitAnim() != 0) {
            fragment.getActivity().overridePendingTransition(mRouteRule.getEnterAnim(), mRouteRule.getExitAnim());
        }
    }

    public Intent getIntent(Context context) {
        if (mRouteRule == null) {
            Logger.e(TAG, "getIntent failed");
            return null;
        }
        return mRouteRule.getIntent(context);
    }

    public <T> T getFragment() {
        if (mRouteRule == null) {
            return null;
        }
        return mRouteRule.getFragment();
    }
}
