package cherry.android.router.api;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import cherry.android.router.annotations.ClassName;
import cherry.android.router.annotations.Query;
import cherry.android.router.annotations.URL;
import cherry.android.router.api.utils.Logger;

/**
 * Created by LHEE on 2017/7/22.
 */
/*package-private*/ class ServiceMethod {
    private static final String TAG = "ServiceMethod";
    private Class<?> className;
    private String baseUrl;
    private Parameter<?, ?>[] parameters;
    private Object[] args;

    private ServiceMethod(Builder builder) {
        this.className = builder.className;
        this.baseUrl = builder.baseUrl;
        this.parameters = builder.parameters;
        this.args = builder.args;
    }

    Request toRequest() {
        RouteUrl.Builder builder = RouteUrl.Builder.parse(this.baseUrl);
        Parameter<Object, Object>[] params = (Parameter<Object, Object>[]) parameters;
        final int argsCount = args != null ? args.length : 0;
        if (argsCount != params.length)
            throw new IllegalArgumentException("Arguments count (" + argsCount
                    + ") doesn't match expected count(" + params.length + ")");
        for (int i = 0; i < params.length; i++) {
            if (params[i] == null)
                continue;
            params[i].apply(builder, args[i]);
        }
        RouteUrl routeUrl = builder.build();
        String url = routeUrl.toUrl();
        Logger.i(TAG, url);
        RouteRule rule = RouterInternal.get().getRouteRule(url);
        if (rule == null)
            throw new NullPointerException("RouteRule Not Found in Route Table: " + url);
        return new Request(url, rule);
    }

    static class Builder {
        private static final String TAG = "Builder";
        private Class<?> className;
        private String baseUrl;
        final Annotation[] methodAnnotations;
        final Annotation[][] parameterAnnotationArray;
        Parameter<?, ?>[] parameters;
        Object[] args;

        Builder(@NonNull Method method, Object[] args) {
            Logger.i(TAG, "method=" + method);
            this.methodAnnotations = method.getAnnotations();
            this.parameterAnnotationArray = method.getParameterAnnotations();
            Type[] types = method.getGenericParameterTypes();
            for (int i = 0; i < types.length; i++) {
                Logger.e(TAG, "types[" + i + "]=" + types[i]);
            }
            Type type = method.getGenericReturnType();
            Logger.i(TAG, "GenericReturnType=" + type);
            Class clazz = method.getReturnType();
            Logger.i(TAG, "returnType=" + clazz);
            Logger.i(TAG, "declaringClass=" + method.getDeclaringClass());
            this.args = args;
        }

        public ServiceMethod build() {
            for (Annotation annotation : methodAnnotations) {
                parseMethodAnnotation(annotation);
            }
            if (!TextUtils.isEmpty(baseUrl) && className != null) {
                throw new IllegalArgumentException("URL and ClassName should not be used together");
            }
            parseParameterAnnotation();
            return new ServiceMethod(this);
        }

        private void parseParameterAnnotation() {
            int length = parameterAnnotationArray.length;
            parameters = new Parameter[length];
            for (int i = 0; i < length; i++) {
                Annotation[] annotations = parameterAnnotationArray[i];
                Logger.v(TAG, "annotations=" + annotations.length);
                if (annotations.length > 1)
                    throw new IllegalArgumentException("Service method's one Parameter should have at most one Annotation.");
                for (Annotation annotation : annotations) {
                    Logger.d(TAG, "parameter: " + annotation);
                    Query query = (Query) annotation;
                    parameters[i] = new Parameter.QueryURL<>(query.value());
                }
            }
        }

        private void parseMethodAnnotation(Annotation annotation) {
            if (annotation instanceof ClassName) {
                ClassName cn = (ClassName) annotation;
                this.className = cn.value();
            } else if (annotation instanceof URL) {
                URL url = (URL) annotation;
                this.baseUrl = url.value();
            }
        }

    }
}
