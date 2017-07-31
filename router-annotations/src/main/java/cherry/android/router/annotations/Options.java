package cherry.android.router.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by LHEE on 2017/7/25.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Options {
    int requestCode() default -1;

    boolean ignoreInterceptor() default false;

    int enterAnim() default -1;

    int exitAnim() default -1;

    int flags() default -1;
}
