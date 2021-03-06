package cherry.android.router.api;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import cherry.android.router.api.utils.Utils;

import static cherry.android.router.api.utils.Utils.getDestinationType;

/**
 * Created by Administrator on 2017/5/25.
 */

public class RouteRule {

    public static final int TYPE_ACTIVITY = 0x01;
    public static final int TYPE_FRAGMENT = 0x02;
    public static final int TYPE_MATCHER = 0x03;

    private String uri;
    private Class<?> destination;
    private String[] interceptorNames;
    private List<RouteInterceptor> interceptorList;

    @Type
    private int type = TYPE_MATCHER;

    @IntDef({TYPE_ACTIVITY, TYPE_FRAGMENT, TYPE_MATCHER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    RouteRule(String uri, Class<?> destination) {
        this(uri, destination, TYPE_MATCHER);
    }

    RouteRule(String uri, Class<?> destination, @Type int type) {
        this(uri, destination, type, null);
    }

    RouteRule(String uri, Class<?> destination, @Type int type, String[] interceptorNames) {
        this.uri = uri;
        this.destination = destination;
        this.type = type;
        this.interceptorNames = interceptorNames;
    }

    public String getUri() {
        return this.uri;
    }

    public Class<?> getDestination() {
        return this.destination;
    }

    List<RouteInterceptor> getInterceptors() {
        return this.interceptorList;
    }

    @Type
    int getType() {
        return this.type;
    }


    void findInterceptors(Map<String, RouteInterceptor> interceptors) {
        if (interceptors == null || interceptors.isEmpty())
            return;
        if (this.interceptorNames == null || this.interceptorNames.length == 0)
            return;
        if (interceptorList == null)
            interceptorList = new ArrayList<>();
        for (String name : interceptorNames) {
            RouteInterceptor meta = interceptors.get(name);
            if (meta != null && !interceptorList.contains(meta)) {
                interceptorList.add(meta);
            }
        }
        Collections.sort(interceptorList);
    }

    public static RouteRule newRule(String uri, Class<?> destination, String... interceptors) {
        return newRule(uri, destination, getDestinationType(destination), interceptors);
    }

    public static RouteRule newRule(String uri, Class<?> destination, @Type int type, String... interceptors) {
        if (!Utils.checkRouteValid(uri))
            throw new IllegalArgumentException("Uri format invalid: " + uri);
        return new RouteRule(uri, destination, type, interceptors);
    }

    @Override
    public String toString() {
        return "RouteRule{" +
                "destination=" + destination +
                ", uri='" + uri + '\'' +
                '}';
    }
}
