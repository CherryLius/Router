package cherry.android.router.api;

import java.util.LinkedHashMap;

/**
 * Created by Administrator on 2017/5/25.
 */

public class RouteMap extends LinkedHashMap<String, RouteRule> {

    @Override
    public RouteRule put(String key, RouteRule value) {
        key = parseUri(key);
        return super.put(key, value);
    }

    @Override
    public RouteRule get(Object key) {
        if (key instanceof String) {
            String uri = String.valueOf(key);
            return super.get(parseUri(uri));
        }
        return super.get(key);
    }

    @Override
    public boolean containsKey(Object key) {
        if (key instanceof String) {
            String uri = String.valueOf(key);
            return super.containsKey(parseUri(uri));
        }
        return super.containsKey(key);
    }

    private static String parseUri(String uri) {
        int argIndex = uri.indexOf('?');
        if (argIndex != -1) {
            return uri.substring(0, argIndex);
        }
        return uri;
    }


}
