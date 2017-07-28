package cherry.android.router.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ROOT on 2017/7/25.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Action {
    String value();

    /**
     * Set an explicit MIME data type
     *
     * @return mimeType
     */
    String mimeType() default "";
}
