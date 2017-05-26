package cherry.android.router.api;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import cherry.android.router.api.intercept.IInterceptor;

/**
 * Created by Administrator on 2017/5/26.
 */

public class InterceptorMeta implements Comparable<InterceptorMeta> {
    private String name;
    private int priority;
    private Class<? extends IInterceptor> interceptor;
    private IInterceptor mInterceptor;

    public InterceptorMeta(@NonNull Class<? extends IInterceptor> interceptor, String name, int priority) {
        this.interceptor = interceptor;
        this.name = name;
        this.priority = priority;
    }

    String getName() {
        return name;
    }

    synchronized IInterceptor getInterceptor() {
        if (mInterceptor == null) {
            mInterceptor = newInterceptor();
            if (mInterceptor == null)
                throw new IllegalStateException("Cannot get A IInterceptor instance");
        }
        return mInterceptor;
    }

    private IInterceptor newInterceptor() {
        try {
            Constructor constructor = interceptor.getConstructor();
            IInterceptor interceptor = (IInterceptor) constructor.newInstance();
            return interceptor;
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

    @Override
    public int compareTo(@NonNull InterceptorMeta o) {
        if (this.priority != o.priority) {
            return this.priority - o.priority;
        }
        if (this.name != o.name) {
            if (TextUtils.isEmpty(this.name)) return -1;
            if (TextUtils.isEmpty(o.name)) return 1;
            return this.name.compareToIgnoreCase(o.name);
        }
        return this.interceptor.getSimpleName().compareToIgnoreCase(o.interceptor.getSimpleName());
    }
}
