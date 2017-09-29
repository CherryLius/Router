package cherry.android.router.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.android.internal.util.Predicate;

import java.io.IOException;
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
import cherry.android.router.api.callback.RouterCallback;
import cherry.android.router.api.convert.Converter;
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
    private final RouterInternal routerInternal;
    private Class<?> className;
    private String baseUrl;
    private String action;
    private String mimeType;
    private Parameter<?, ?>[] parameters;
    private RequestOptions options;
    private RequestAdapter requestAdapter;

    private ServiceMethod(Builder builder) {
        this.routerInternal = builder.routerInternal;
        this.className = builder.className;
        this.baseUrl = builder.baseUrl;
        this.action = builder.action;
        this.mimeType = builder.mimeType;
        this.parameters = builder.parameters;
        this.options = builder.options;
        this.requestAdapter = new RouteAdapter(builder.returnType);
    }

    private Object getHost(Object[] args) {
        return filterArgs(args, new Predicate<Object>() {
            @Override
            public boolean apply(Object obj) {
                return obj instanceof Context
                        || obj instanceof Fragment
                        || obj instanceof android.app.Fragment;
            }
        });
//        lambda
//        return filterArgs(args, (obj)->{
//           return obj instanceof Context
//                    || obj instanceof Fragment
//                    || obj instanceof android.app.Fragment;
//        });
    }

    private Object getCallback(Object[] args) {
        return filterArgs(args, new Predicate<Object>() {
            @Override
            public boolean apply(Object o) {
                return o instanceof RouterCallback;
            }
        });
    }

    private static Object filterArgs(Object[] args, Predicate<Object> predicate) {
        if (args == null)
            return null;
        for (int i = 0; i < args.length; i++) {
            Object obj = args[i];
            if (predicate != null && predicate.apply(obj)) {
                return obj;
            }
        }
        return null;
    }

    public Object request(Object[] args) {
        Request request = toRequest(args);
        Object host = getHost(args);
        if (host != null)
            request.setHost(host);
        Object callback = getCallback(args);
        if (callback != null)
            request.callback((RouterCallback) callback);
        return requestAdapter.adapt(request);
    }

    private Request toRequest(Object[] args) {
        try {
            Parameter<Object, Object>[] params = (Parameter<Object, Object>[]) parameters;
            final int argsCount = args != null ? args.length : 0;
            if (argsCount != params.length)
                throw new IllegalArgumentException("Arguments count (" + argsCount
                        + ") doesn't match expected count(" + params.length + ")");
            this.options.getArguments().clear();
            if (className != null) {
                Logger.d(TAG, "classRequest");
                return classRequest(params, args);
            } else if (action != null) {
                Logger.d(TAG, "actionRequest");
                return actionRequest(params, args);
            }
            return urlRequest(params, args);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private Request urlRequest(Parameter<Object, Object>[] parameters, Object[] args) throws IOException {
        RouteUrl.Builder builder = RouteUrl.Builder.parse(this.baseUrl);
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i] == null)
                continue;
            parameters[i].apply(builder, args[i]);
        }
        RouteUrl routeUrl = builder.build();
        String url = routeUrl.toUrl();
        Logger.i(TAG, "urlRequest=" + url);
        RouteRule rule = this.routerInternal.getRouteRule(url);
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

    private Request classRequest(Parameter<Object, Object>[] parameters, Object[] args) throws IOException {
        AbstractRequest request;
        RouteRule rule = this.routerInternal.getRouteRule(className);
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

    private Request actionRequest(Parameter<Object, Object>[] parameters, Object[] args) throws IOException {
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
        private RouterInternal routerInternal;
        private Class<?> className;
        private String baseUrl;
        private String action;
        private String mimeType;
        private final Annotation[] methodAnnotations;
        private final Annotation[][] parameterAnnotationArray;
        private Parameter<?, ?>[] parameters;
        private final Type[] parameterTypes;
        private final Type returnType;
        private RequestOptions options;

        Builder(@NonNull RouterInternal routerInternal, @NonNull Method method) {
            Logger.i(TAG, "method=" + method);
            this.routerInternal = routerInternal;
            this.methodAnnotations = method.getAnnotations();
            this.parameterAnnotationArray = method.getParameterAnnotations();
            parameterTypes = method.getGenericParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                Logger.e(TAG, "types[" + i + "]=" + parameterTypes[i]);
            }
            returnType = method.getGenericReturnType();
            Logger.i(TAG, "GenericReturnType=" + returnType);
            Class clazz = method.getReturnType();
            Logger.i(TAG, "returnType=" + clazz);
            Logger.i(TAG, "declaringClass=" + method.getDeclaringClass());
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
                Type parameterType = this.parameterTypes[i];
                if (annotations.length > 1)
                    throw new IllegalArgumentException("Service method's one Parameter should have at most one Annotation.");
                for (Annotation annotation : annotations) {
                    if (annotation instanceof Query) {
                        Query query = (Query) annotation;
                        Converter<Object, String> converter = this.routerInternal.stringConverter(parameterType, annotation);
                        if (!TextUtils.isEmpty(baseUrl)) {
                            parameters[i] = new Parameter.QueryURL<>(query.value(), converter, query.encoded());
                        } else {
                            parameters[i] = new Parameter.QueryRequest<>(query.value(), converter);
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
                        .requestCode(op.requestCode())
                        .flags(op.flags());
            }
        }

    }
}
