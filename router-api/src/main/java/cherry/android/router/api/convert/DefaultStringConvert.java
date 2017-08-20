package cherry.android.router.api.convert;

/**
 * Created by LHEE on 2017/8/20.
 */

public class DefaultStringConvert<T> implements Converter<T, String> {
    @Override
    public String convert(T t) {
        return t.toString();
    }
}
