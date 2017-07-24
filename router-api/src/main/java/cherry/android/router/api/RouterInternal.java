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

    private Request mRequest;

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
            throw new RuntimeException("Uri cannot be Empty");
        RouteRule rule = mRouterTable.get(uri);
        if (rule == null)
            throw new NullPointerException("Uri Can Not Find Route:" + uri);
        mRequest = new Request(uri, rule);
        return this;
    }

    private boolean intercept(Request request) {
        if (request.isIgnoreInterceptor())
            return false;
        RouteRule rule = request.getRule();
        if (mGlobalInterceptor != null && mGlobalInterceptor.intercept(rule)) {
            return true;
        }
        rule.findInterceptors(mInterceptorMap);
        return rule.interceptor();
    }

    public RouterInternal extra(String key, Object value) {
        if (mRequest != null) {
            mRequest.putExtra(key, value);
        }
        return this;
    }

    public RouterInternal extra(Bundle value) {
        if (mRequest != null) {
            mRequest.putExtra(value);
        }
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RouterInternal extra(PersistableBundle value) {
        if (mRequest != null) {
            mRequest.putExtra(value);
        }
        return this;
    }

    public RouterInternal transition(int enterAnim, int exitAnim) {
        if (mRequest != null) {
            mRequest.transition(enterAnim, exitAnim);
        }
        return this;
    }

    @TargetApi(16)
    public RouterInternal optionsCompat(ActivityOptionsCompat optionsCompat) {
        if (mRequest != null) {
            mRequest.setOptionsCompat(optionsCompat);
        }
        return this;
    }

    public RouterInternal requestCode(int requestCode) {
        if (mRequest != null) {
            mRequest.setRequestCode(requestCode);
        }
        return this;
    }

    public RouterInternal ignoreInterceptor(boolean ignore) {
        if (mRequest != null) {
            mRequest.ignoreInterceptor(ignore);
        }
        return this;
    }

    RouteRule getRouteRule(@NonNull String uri) {
        return mRouterTable.get(uri);
    }


    public void request(Request request) {
        if (intercept(request)) {
            return;
        }
        Intent intent = request.getIntent(mContext);
        if (mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            Bundle options = request.getOptionsCompat() == null ? null : request.getOptionsCompat().toBundle();
            if (request.getRequestCode() == -1) {
                ActivityCompat.startActivity(activity, intent, options);
            } else {
                ActivityCompat.startActivityForResult(activity, intent, request.getRequestCode(), options);
            }
            if (request.getEnterAnim() != 0 || request.getExitAnim() != 0) {
                activity.overridePendingTransition(request.getEnterAnim(), request.getExitAnim());
            }
        } else {
            ContextCompat.startActivity(mContext, intent, null);
        }
    }

    public void open() {
        open(mContext);
    }

    public void open(Context context) {
        open(context, null);
    }

    public void open(Context context, IRouteCallback callback) {
        if (mRequest == null) {
            Logger.e(TAG, "open failed");
            if (callback != null)
                callback.onFailed(mRequest, "open failed");
            return;
        }
        if (intercept(mRequest)) {
            if (callback != null)
                callback.onIntercept(mRequest);
            return;
        }
        if (context == null)
            context = mContext;
        Intent intent = getIntent(context);
        if (intent == null) {
            if (callback != null) {
                callback.onFailed(mRequest, "Not Get Any Intent");
            }
            return;
        } else {
            if (callback != null)
                callback.onSuccess(mRequest);
        }
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            Bundle options = mRequest.getOptionsCompat() == null ? null : mRequest.getOptionsCompat().toBundle();
            if (mRequest.getRequestCode() == -1) {
                ActivityCompat.startActivity(activity, intent, options);
            } else {
                ActivityCompat.startActivityForResult(activity, intent, mRequest.getRequestCode(), options);
            }
            if (mRequest.getEnterAnim() != 0 || mRequest.getExitAnim() != 0) {
                activity.overridePendingTransition(mRequest.getEnterAnim(), mRequest.getExitAnim());
            }
        } else {
            ContextCompat.startActivity(context, intent, null);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void open(Fragment fragment) {
        if (mRequest == null) {
            Logger.e(TAG, "open failed");
            return;
        }
        if (intercept(mRequest)) {
            return;
        }
        Intent intent = getIntent(fragment.getActivity());
        Bundle options = mRequest.getOptionsCompat() == null ? null : mRequest.getOptionsCompat().toBundle();
        if (mRequest.getRequestCode() == -1) {
            fragment.startActivity(intent, options);
        } else {
            fragment.startActivityForResult(intent, mRequest.getRequestCode(), options);
        }
        if (mRequest.getEnterAnim() != 0 || mRequest.getExitAnim() != 0) {
            fragment.getActivity().overridePendingTransition(mRequest.getEnterAnim(), mRequest.getExitAnim());
        }
    }

    public void open(android.support.v4.app.Fragment fragment) {
        if (mRequest == null) {
            Logger.e(TAG, "open failed");
            return;
        }
        if (intercept(mRequest)) {
            return;
        }
        Intent intent = getIntent(fragment.getActivity());
        Bundle options = mRequest.getOptionsCompat() == null ? null : mRequest.getOptionsCompat().toBundle();
        if (mRequest.getRequestCode() == -1) {
            fragment.startActivity(intent, options);
        } else {
            fragment.startActivityForResult(intent, mRequest.getRequestCode(), options);
        }
        if (mRequest.getEnterAnim() != 0 || mRequest.getExitAnim() != 0) {
            fragment.getActivity().overridePendingTransition(mRequest.getEnterAnim(), mRequest.getExitAnim());
        }
    }

    public Intent getIntent(Context context) {
        if (mRequest == null) {
            Logger.e(TAG, "getIntent failed");
            return null;
        }
        return mRequest.getIntent(context);
    }

    public <T> T getFragment() {
        if (mRequest == null) {
            return null;
        }
        return mRequest.getFragment();
    }
}
