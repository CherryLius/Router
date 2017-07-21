package cherry.android.router.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by ROOT on 2017/7/21.
 */

public class IProxy {

    public static <T> T create(Class<T> service,final Object target) {
        return (T) Proxy.newProxyInstance(service.getClassLoader(),
                new Class[]{service}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return method.invoke(target, args);
                    }
                });
    }
}
