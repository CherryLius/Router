package cherry.android.router.api.convert;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Created by LHEE on 2017/8/19.
 */

public interface Converter<T, R> {
    R convert(T t) throws IOException;

    abstract class Factory {
        public Converter<?, String> stringConverter(Type type) {
            return null;
        }

        public Converter<String, ?> classConverter(Type type) {
            return null;
        }
    }
}
