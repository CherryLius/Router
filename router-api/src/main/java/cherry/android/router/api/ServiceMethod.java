package cherry.android.router.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import cherry.android.router.annotations.Action;
import cherry.android.router.annotations.ClassName;
import cherry.android.router.annotations.Options;
import cherry.android.router.annotations.OptionsCompat;
import cherry.android.router.annotations.Query;
import cherry.android.router.annotations.URL;
import cherry.android.router.annotations.Uri;
import cherry.android.router.api.adapter.RequestAdapter;
import cherry.android.router.api.adapter.RouteAdapter;
import cherry.android.router.api.request.AbstractRequest;
import cherry.android.router.api.request.ActionRequest;
import cherry.android.router.api.request.ActivityRequest;
import cherry.android.router.api.request.FragmentRequest;
import cherry.android.router.api.request.Request;
import cherry.android.router.api.request.UnKnownRequest;
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
    private RequestOptions options;
    private RequestAdapter requestAdapter;
    private Object host;

    private ServiceMethod(Builder builder) {
        this.className = builder.className;
        this.baseUrl = builder.baseUrl;
        this.action = builder.action;
        this.mimeType = builder.mimeType;
        this.parameters = builder.parameters;
        this.args = builder.args;
        this.options = builder.options;
        this.requestAdapter = new RouteAdapter(builder.returnType);
        this.host = getHost();
    }

    private Object getHost() {
        if (this.args == null)
            return null;
        for (int i = 0; i < this.args.length; i++) {
            Object obj = this.args[i];
            if (obj instanceof Context
                    || obj instanceof Fragment
                    || obj instanceof android.app.Fragment)
                return obj;
        }
        return null;
    }

    public Object request() {
        Request request = toRequest();
        if (this.host != null) request.setHost(host);
        return requestAdapter.adapt(request);
    }

    private Request toRequest() {
        Parameter<Object, Object>[] params = (Parameter<Object, Object>[]) parameters;
        final int argsCount = args != null ? args.length : 0;
        if (argsCount != params.length)
            throw new IllegalArgumentException("Arguments count (" + argsCount
                    + ") doesn't match expected count(" + params.length + ")");
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
            return new UnKnownRequest(url);
        Request request;
        if (rule.getType() == RouteRule.TYPE_ACTIVITY) {
            request = new ActivityRequest(url, rule);
        } else
            request = new FragmentRequest(url, rule);
        request.setOptions(this.options);
        return request;
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
        request.setOptions(this.options);
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
        request.setOptions(this.options);
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
        private RequestOptions options;

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
            this.options = new RequestOptions();
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
                if (annotations.length > 1)
                    throw new IllegalArgumentException("Service method's one Parameter should have at most one Annotation.");
                for (Annotation annotation : annotations) {
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
                    } else if (annotation instanceof OptionsCompat) {
                        parameters[i] = new Parameter.OptionsCompat<>(null);
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
            } else if (annotation instanceof Options) {
                Options op = (Options) annotation;
                options.transition(op.enterAnim(), op.exitAnim())
                        .ignoreInterceptor(op.ignoreInterceptor())
                        .requestCode(op.requestCode());
            }
        }

    }
}
