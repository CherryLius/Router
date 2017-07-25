package cherry.android.router.api;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import cherry.android.router.annotations.Action;
import cherry.android.router.annotations.ClassName;
import cherry.android.router.annotations.Query;
import cherry.android.router.annotations.URL;
import cherry.android.router.annotations.Uri;
import cherry.android.router.api.utils.Logger;
import cherry.android.router.api.utils.Utils;

/**
 * Created by LHEE on 2017/7/22.
 */
/*package-private*/ class ServiceMethod {
    private static final String TAG = "ServiceMethod";
    private Class<?> className;
    private String baseUrl;
    private String action;
    private String mimeType;
    private Parameter<?, ?>[] parameters;
    private Object[] args;
    private RequestAdapter requestAdapter;

    private ServiceMethod(Builder builder) {
        this.className = builder.className;
        this.baseUrl = builder.baseUrl;
        this.action = builder.action;
        this.mimeType = builder.mimeType;
        this.parameters = builder.parameters;
        this.args = builder.args;
        requestAdapter = new RouteAdapter(builder.returnType);
    }

    public Object request() {
        return requestAdapter.adapt(toRequest());
    }

    private Request toRequest() {
        Parameter<Object, Object>[] params = (Parameter<Object, Object>[]) parameters;
        final int argsCount = args != null ? args.length : 0;
        if (argsCount != params.length)
            throw new IllegalArgumentException("Arguments count (" + argsCount
                    + ") doesn't match expected count(" + params.length + ")");
        Logger.e(TAG, "action=" + action);
        if (className != null) {
            return classRequest(params);
        } else if (action != null) {
            return actionRequest(params);
        }
        return urlRequest(params);
    }

    private Request urlRequest(Parameter<Object, Object>[] parameters) {
        RouteUrl.Builder builder = RouteUrl.Builder.parse(this.baseUrl);
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i] == null)
                continue;
            parameters[i].apply(builder, args[i]);
        }
        RouteUrl routeUrl = builder.build();
        String url = routeUrl.toUrl();
        Logger.i(TAG, url);
        RouteRule rule = RouterInternal.get().getRouteRule(url);
        if (rule == null)
            throw new NullPointerException("RouteRule Not Found in Route Table: " + url);
        if (rule.getType() == RouteRule.TYPE_ACTIVITY)
            return new ActivityRequest(rule);
        else
            return new FragmentRequest(rule);
    }

    private Request classRequest(Parameter<Object, Object>[] parameters) {
        AbstractRequest request;
        RouteRule rule = RouterInternal.get().getRouteRule(className);
        if (rule == null) {
            if (Utils.isActivity(className)) {
                request = new ActivityRequest(className);
            } else {
                request = new FragmentRequest(className);
            }
        } else {
            if (rule.getType() == RouteRule.TYPE_ACTIVITY)
                request = new ActivityRequest(rule);
            else
                request = new FragmentRequest(rule);
        }
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i] == null)
                continue;
            parameters[i].apply(request, args[i]);
        }
        return request;
    }

    private Request actionRequest(Parameter<Object, Object>[] parameters) {
        ActionRequest request = new ActionRequest.Builder()
                .setAction(action)
                .setType(mimeType).build();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i] == null)
                continue;
            parameters[i].apply(request, args[i]);
        }
        return request;
    }

    static class Builder {
        private static final String TAG = "Builder";
        private Class<?> className;
        private String baseUrl;
        private String action;
        private String mimeType;
        final Annotation[] methodAnnotations;
        final Annotation[][] parameterAnnotationArray;
        private Parameter<?, ?>[] parameters;
        private Object[] args;
        private Type returnType;


        Builder(@NonNull Method method, Object[] args) {
            Logger.i(TAG, "method=" + method);
            this.methodAnnotations = method.getAnnotations();
            this.parameterAnnotationArray = method.getParameterAnnotations();
            Type[] types = method.getGenericParameterTypes();
            for (int i = 0; i < types.length; i++) {
                Logger.e(TAG, "types[" + i + "]=" + types[i]);
            }
            returnType = method.getGenericReturnType();
            Logger.i(TAG, "GenericReturnType=" + returnType);
            Class clazz = method.getReturnType();
            Logger.i(TAG, "returnType=" + clazz);
            Logger.i(TAG, "declaringClass=" + method.getDeclaringClass());
            this.args = args;
        }

        public ServiceMethod build() {
            for (Annotation annotation : methodAnnotations) {
                parseMethodAnnotation(annotation);
            }
            if (!TextUtils.isEmpty(baseUrl) && className != null && action != null) {
                throw new IllegalArgumentException("URL/ClassName/Action should not be used together");
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
                    if (annotation instanceof Query) {
                        Query query = (Query) annotation;
                        if (!TextUtils.isEmpty(baseUrl)) {
                            parameters[i] = new Parameter.QueryURL<>(query.value());
                        } else {
                            parameters[i] = new Parameter.QueryRequest<>(query.value());
                        }
                    } else if (annotation instanceof Uri) {
                        Uri uri = (Uri) annotation;
                        parameters[i] = new Parameter.UriRequest<>(uri.value());
                    }
                }
            }
        }

        private void parseMethodAnnotation(Annotation annotation) {
            if (annotation instanceof ClassName) {
                ClassName cn = (ClassName) annotation;
                this.className = cn.value();
                Utils.checkValidDestination(this.className);
            } else if (annotation instanceof URL) {
                URL url = (URL) annotation;
                this.baseUrl = url.value();
            } else if (annotation instanceof Action) {
                Action act = (Action) annotation;
                this.action = act.value();
                this.mimeType = act.mimeType();
            }
        }

    }
}
