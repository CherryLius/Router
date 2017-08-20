package cherry.android.router.api;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;

import cherry.android.router.api.bundle.FieldBundle;
import cherry.android.router.api.convert.Converter;
import cherry.android.router.api.intercept.IInterceptor;
import cherry.android.router.api.utils.Logger;
import cherry.android.router.api.utils.Utils;

/**
 * Created by Administrator on 2017/5/24.
 */

public final class Router {

    private static final String TAG = "Router";
    private static boolean debuggable = false;
    private static final Map<Class<?>, Constructor<?>> ROUTERS = new LinkedHashMap<>();
    private static final Map<Method, ServiceMethod> SERVICE_METHOD = new LinkedHashMap<>();

    public static void init(@NonNull Context context) {
        RouterInternal.get().init(context);
    }

    public static void openLog(boolean showLog, boolean showStackTrace) {
        Logger.showLog(showLog);
        Logger.showStackTrace(showStackTrace);
    }

    public static void openDebug() {
        debuggable = true;
    }

    public static boolean debuggable() {
        return debuggable;
    }

    public static void addRoutePicker(@NonNull RoutePicker picker) {
        RouterInternal.get().addRoutePicker(picker);
    }

    public static void addGlobalInterceptor(IInterceptor interceptor) {
        RouterInternal.get().addGlobalInterceptor(interceptor);
    }

    public static RouterInternal build(@NonNull String uri) {
        return RouterInternal.get().build(uri);
    }

    public static RouterInternal build(@NonNull Uri uri) {
        return RouterInternal.get().build(uri);
    }

    public static <T> T create(final Class<T> service) {
        Utils.validateServiceInterface(service);
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        StringBuilder builder = new StringBuilder();
                        builder.append(service.getSimpleName())
                                .append(".")
                                .append(method.getName())
                                .append('(');
                        if (args != null) {
                            for (int i = 0; i < args.length; i++) {
                                builder.append(args[i]);
                                if (i < args.length - 1)
                                    builder.append(", ");
                            }
                        }
                        builder.append(')')
                                .append("::")
                                .append(method.getGenericReturnType());
                        Logger.i(TAG, "[Call] " + builder.toString());
                        if (method.getDeclaringClass() == Object.class)
                            return method.invoke(this, args);
                        return getServiceMethod(method).request(args);
                    }
                });
    }

    private static ServiceMethod getServiceMethod(Method method) {
        ServiceMethod serviceMethod = SERVICE_METHOD.get(method);
        if (serviceMethod != null)
            return serviceMethod;
        serviceMethod = new ServiceMethod.Builder(RouterInternal.get(), method).build();
        SERVICE_METHOD.put(method, serviceMethod);
        return serviceMethod;
    }

    public static void addConverterFactory(@NonNull Converter.Factory factory){
        RouterInternal.get().addConverterFactory(factory);
    }

    public static void destroy() {
        debuggable = false;
        RouterInternal.get().destroy();
    }

    public interface RoutePicker {
        Map<String, Class<?>> pick();
    }

    public static void inject(Activity activity) {
        createRouter(activity, FieldBundle.newBundle(activity));
    }

    public static void inject(Fragment fragment) {
        createRouter(fragment, FieldBundle.newBundle(fragment));
    }

    public static void inject(android.app.Fragment fragment) {
        createRouter(fragment, FieldBundle.newBundle(fragment));
    }

    public static void inject(Object target, Bundle bundle) {
        createRouter(target, FieldBundle.newBundle(bundle));
    }

    private static void createRouter(Object target, FieldBundle bundle) {
        Class<?> targetClass = target.getClass();
        Constructor<?> constructor = findRouterConstructor(targetClass);
        if (constructor == null) {
            Logger.e(TAG, "No Constructor Find for " + targetClass.getName());
            return;
        }
        try {
            constructor.newInstance(target, bundle);
        } catch (InstantiationException e) {
            throw new RuntimeException("Unable to toRequest " + constructor, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to toRequest " + constructor, e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new RuntimeException("Unable to create routing instance.", cause);
        }
    }

    private static Constructor<?> findRouterConstructor(Class<?> targetClass) {
        Constructor<?> constructor = ROUTERS.get(targetClass);
        if (constructor != null)
            return constructor;
        String className = targetClass.getName();
        Logger.i(TAG, "target class=" + className);
        try {
            Class<?> routerClass = Class.forName(className + "_Router");
            constructor = routerClass.getConstructor(targetClass, FieldBundle.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            constructor = findRouterConstructor(targetClass.getSuperclass());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find router constructor for " + className, e);
        }
        ROUTERS.put(targetClass, constructor);
        return constructor;
    }
}
