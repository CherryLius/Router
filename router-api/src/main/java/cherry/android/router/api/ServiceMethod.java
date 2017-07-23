package cherry.android.router.api;

import android.support.annotation.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import cherry.android.router.annotations.ClassName;
import cherry.android.router.annotations.URL;
import cherry.android.router.api.utils.Logger;

/**
 * Created by LHEE on 2017/7/22.
 */
/*package-private*/ class ServiceMethod {
    private ServiceMethod() {

    }

    static class Builder {
        private Class<?> className;
        private String baseUrl;
        final Annotation[] methodAnnotations;
        final Annotation[][] parameterAnnotationArray;

        Builder(@NonNull Method method) {
            Logger.i("Test", "method=" + method);
            this.methodAnnotations = method.getAnnotations();
            this.parameterAnnotationArray = method.getParameterAnnotations();
        }

        public ServiceMethod build() {
            for (Annotation annotation : methodAnnotations) {
                parseMethodAnnotation(annotation);
            }
            parseParameterAnnotation();
            return new ServiceMethod();
        }

        private void parseParameterAnnotation() {
            int length = parameterAnnotationArray.length;
            for (int i = 0; i < length; i++) {
                Annotation[] annotations = parameterAnnotationArray[i];
                if (annotations.length > 1 || annotations.length < 1)
                    throw new IllegalArgumentException("Service method's one Parameter should have one Annotation.");

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
