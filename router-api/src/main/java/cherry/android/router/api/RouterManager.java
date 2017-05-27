package cherry.android.router.api;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cherry.android.router.api.intercept.IInterceptor;
import cherry.android.router.api.utils.Logger;
import cherry.android.router.api.utils.Utils;

/**
 * Created by Administrator on 2017/5/25.
 */

public final class RouterManager {

    private static final String TAG = "RouterManager";
    private static final String PACKAGE_NAME = "cherry.android.router";

    private static RouterManager sInstance;

    private Map<String, RouteMeta> mRouterTable = new RouteMap();
    private Map<String, InterceptorMeta> mInterceptorMap = new LinkedHashMap<>();
    private IInterceptor mGlobalInterceptor;
    private boolean mInitialized;
    private Context mContext;

    private RouteMeta mRouteMeta;

    static RouterManager instance() {
        if (sInstance == null)
            synchronized (RouterManager.class) {
                if (sInstance == null)
                    sInstance = new RouterManager();
            }
        return sInstance;
    }

    private RouterManager() {
    }

    synchronized void init(@NonNull Context context) {
        if (!mInitialized) {
            mContext = context;
            List<String> classNames = Utils.getFileNameByPackage(context, PACKAGE_NAME);
            for (String className : classNames) {
                if (className.endsWith("_RoutePicker")) {
                    pickRouteTable(className);
                } else if (className.endsWith("_InterceptorPicker")) {
                    pickInterceptor(className);
                }
            }
            mInitialized = true;
        }
    }

    private void addRoutePicker(@NonNull IRoutePicker picker) {
        picker.pick(mRouterTable);
    }

    void addRoutePicker(@NonNull Router.RoutePicker picker) {
        Map<String, Class<?>> map = picker.pick();
        if (map == null || map.isEmpty()) return;
        for (Map.Entry<String, Class<?>> entry : map.entrySet()) {
            if (!Utils.checkRouteValid(entry.getKey()))
                throw new IllegalArgumentException("invalid uri: " + entry.getKey());
            if (mRouterTable.containsKey(entry.getKey()))
                continue;
            RouteMeta meta = Utils.findRouteMetaByClass(mRouterTable, entry.getValue());
            if (meta != null) {
                mRouterTable.put(entry.getKey(), meta);
            } else {
                mRouterTable.put(entry.getKey(), RouteMeta.newMeta(entry.getKey(), entry.getValue()));
            }
        }
    }

    void addInterceptor(@NonNull InterceptorPicker picker) {
        picker.pick(mInterceptorMap);
    }

    void addGlobalInterceptor(@NonNull IInterceptor interceptor) {
        mGlobalInterceptor = interceptor;
    }

    RouterManager build(@NonNull String uri) {
        if (!mInitialized) {
            throw new RuntimeException("Router must be initialize");
        }
        if (TextUtils.isEmpty(uri))
            throw new RuntimeException("Uri cannot be Empty");
        if (mRouteMeta == null || !uri.equals(mRouteMeta.getUri())) {
            mRouteMeta = getRouteMeta(uri);
            if (mRouteMeta != null) {
                mRouteMeta.reset();
                mRouteMeta.setUri(uri);
            } else {
                mRouteMeta = RouteMeta.newMeta(uri, null);
            }
        }
        return this;
    }

    private RouteMeta getRouteMeta(String uri) {
        RouteMeta routeMeta = mRouterTable.get(uri);
        if (routeMeta == null) {
            Logger.e(TAG, "uri Not Found Route: " + uri);
        }
        return routeMeta;
    }

    boolean intercept(RouteMeta routeMeta) {
        if (routeMeta.isIgnoreInterceptor())
            return false;
        if (mGlobalInterceptor != null && mGlobalInterceptor.intercept(routeMeta)) {
            return true;
        }
        routeMeta.findInterceptors(mInterceptorMap);
        if (routeMeta.interceptor()) {
            return true;
        }
        return false;
    }

    public RouterManager extra(String key, Object value) {
        if (mRouteMeta != null) {
            mRouteMeta.putExtra(key, value);
        }
        return this;
    }

    public RouterManager allExtra(Bundle value) {
        if (mRouteMeta != null) {
            mRouteMeta.putExtra(value);
        }
        return this;
    }

    public RouterManager allExtra(PersistableBundle value) {
        if (mRouteMeta != null) {
            mRouteMeta.putExtra(value);
        }
        return this;
    }

    public RouterManager transition(int enterAnim, int exitAnim) {
        if (mRouteMeta != null) {
            mRouteMeta.transition(enterAnim, exitAnim);
        }
        return this;
    }

    @TargetApi(16)
    public RouterManager optionsCompat(ActivityOptionsCompat optionsCompat) {
        if (mRouteMeta != null) {
            mRouteMeta.setOptionsCompat(optionsCompat);
        }
        return this;
    }

    public RouterManager requestCode(int requestCode) {
        if (mRouteMeta != null) {
            mRouteMeta.setRequestCode(requestCode);
        }
        return this;
    }

    public RouterManager ignoreInterceptor(boolean ignore) {
        if (mRouteMeta != null) {
            mRouteMeta.setIgnoreInterceptor(ignore);
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
        if (mRouteMeta == null) {
            Logger.e(TAG, "open failed");
            if (callback != null)
                callback.onFailed(mRouteMeta, "open failed");
            return;
        }
        if (intercept(mRouteMeta)) {
            mRouteMeta.reset();
            if (callback != null)
                callback.onIntercept(mRouteMeta);
            return;
        }
        if (context == null)
            context = mContext;
        Intent intent = getIntent(context);
        if (intent == null) {
            if (callback != null) {
                callback.onFailed(mRouteMeta, "Not Get Any Intent");
            }
            return;
        } else {
            if (callback != null)
                callback.onSuccess(mRouteMeta);
        }
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            Bundle options = mRouteMeta.getOptionsCompat() == null ? null : mRouteMeta.getOptionsCompat().toBundle();
            if (mRouteMeta.getRequestCode() == -1) {
                ActivityCompat.startActivity(activity, intent, options);
            } else {
                ActivityCompat.startActivityForResult(activity, intent, mRouteMeta.getRequestCode(), options);
            }
            if (mRouteMeta.getEnterAnim() != 0 || mRouteMeta.getExitAnim() != 0) {
                activity.overridePendingTransition(mRouteMeta.getEnterAnim(), mRouteMeta.getExitAnim());
            }
        } else {
            ContextCompat.startActivity(context, intent, null);
        }
        mRouteMeta.reset();
    }

    public void open(Fragment fragment) {
        if (mRouteMeta == null) {
            Logger.e(TAG, "open failed");
            return;
        }
        if (intercept(mRouteMeta)) {
            mRouteMeta.reset();
            return;
        }
        Intent intent = getIntent(fragment.getActivity());
        Bundle options = mRouteMeta.getOptionsCompat() == null ? null : mRouteMeta.getOptionsCompat().toBundle();
        if (mRouteMeta.getRequestCode() == -1) {
            fragment.startActivity(intent, options);
        } else {
            fragment.startActivityForResult(intent, mRouteMeta.getRequestCode(), options);
        }
        if (mRouteMeta.getEnterAnim() != 0 || mRouteMeta.getExitAnim() != 0) {
            fragment.getActivity().overridePendingTransition(mRouteMeta.getEnterAnim(), mRouteMeta.getExitAnim());
        }
        mRouteMeta.reset();
    }

    public void open(android.support.v4.app.Fragment fragment) {
        if (mRouteMeta == null) {
            Logger.e(TAG, "open failed");
            return;
        }
        if (intercept(mRouteMeta)) {
            mRouteMeta.reset();
            return;
        }
        Intent intent = getIntent(fragment.getActivity());
        Bundle options = mRouteMeta.getOptionsCompat() == null ? null : mRouteMeta.getOptionsCompat().toBundle();
        if (mRouteMeta.getRequestCode() == -1) {
            fragment.startActivity(intent, options);
        } else {
            fragment.startActivityForResult(intent, mRouteMeta.getRequestCode(), options);
        }
        if (mRouteMeta.getEnterAnim() != 0 || mRouteMeta.getExitAnim() != 0) {
            fragment.getActivity().overridePendingTransition(mRouteMeta.getEnterAnim(), mRouteMeta.getExitAnim());
        }
        mRouteMeta.reset();
    }

    public Intent getIntent(Context context) {
        if (mRouteMeta == null) {
            Logger.e(TAG, "getIntent failed");
            return null;
        }
        return mRouteMeta.getIntent(context);
    }

    public <T> T getFragment() {
        if (mRouteMeta == null) {
            return null;
        }
        return mRouteMeta.getFragment();
    }

    private void pickRouteTable(@NonNull String className) {
        try {
            Class<?> cls = Class.forName(className);
            Constructor constructor = cls.getConstructor();
            IRoutePicker routePicker = (IRoutePicker) constructor.newInstance();
            addRoutePicker(routePicker);
        } catch (ClassNotFoundException e) {
            Logger.e(TAG, "ClassNotFound", e);
            throw new IllegalStateException("Class Not Found", e);
        } catch (NoSuchMethodException e) {
            Logger.e(TAG, "NoSuchMethodException", e);
        } catch (IllegalAccessException e) {
            Logger.e(TAG, "IllegalAccessException", e);
        } catch (InstantiationException e) {
            Logger.e(TAG, "InstantiationException", e);
        } catch (InvocationTargetException e) {
            Logger.e(TAG, "InvocationTargetException", e);
        }
    }

    private void pickInterceptor(@NonNull String className) {
        try {
            Class<?> cls = Class.forName(className);
            Constructor constructor = cls.getConstructor();
            InterceptorPicker picker = (InterceptorPicker) constructor.newInstance();
            addInterceptor(picker);
        } catch (ClassNotFoundException e) {
            Logger.e(TAG, "ClassNotFound", e);
            throw new IllegalStateException("Class Not Found", e);
        } catch (NoSuchMethodException e) {
            Logger.e(TAG, "NoSuchMethodException", e);
        } catch (IllegalAccessException e) {
            Logger.e(TAG, "IllegalAccessException", e);
        } catch (InstantiationException e) {
            Logger.e(TAG, "InstantiationException", e);
        } catch (InvocationTargetException e) {
            Logger.e(TAG, "InvocationTargetException", e);
        }
    }
}
