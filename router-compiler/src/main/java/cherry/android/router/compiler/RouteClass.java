package cherry.android.router.compiler;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import cherry.android.router.annotations.Route;
import cherry.android.router.compiler.common.Values;

/**
 * Created by Administrator on 2017/5/25.
 */

public class RouteClass {

    private Types mTypeUtils;
    private Elements mElementUtils;
    private TypeElement mTypeElement;
    private String[] mUris;
    private String[] mInterceptors;

    public RouteClass(TypeElement typeElement, Elements elementUtils, Types typeUtils) {
        this.mTypeElement = typeElement;
        this.mElementUtils = elementUtils;
        this.mTypeUtils = typeUtils;
        Route route = mTypeElement.getAnnotation(Route.class);
        mUris = route.value();
        mInterceptors = route.interceptor();
        for (String uri : mUris) {
            System.err.printf("uri=%s\n", uri);
        }
    }

    public CodeBlock generateCode() {
        CodeBlock.Builder codeBuilder = CodeBlock.builder();

        String type = "TYPE_MATCHER";
        if (isSubType(Values.ACTIVITY_CLASS_NAME)) {
            type = "TYPE_ACTIVITY";
        } else if (isSubType(Values.FRAGMENT_CLASS_NAME)
                || isSubType(Values.SUPPORT_V4_FRAGMENT_CLASS_NAME)) {
            type = "TYPE_FRAGMENT";
        }
        StringBuilder metaBuilder = new StringBuilder();
        if (mInterceptors != null && mInterceptors.length > 0) {
            for (String interceptor : mInterceptors) {
                metaBuilder.append(", ")
                        .append('\"')
                        .append(interceptor)
                        .append('\"');
            }
        }
        String statement = String.format("$T.fillRouteTable(routeTable,$S, $T.class, $T.$N%s)", metaBuilder.toString());
        for (String uri : mUris) {
            codeBuilder.addStatement(statement, Values.ROUTE_UTILS,
                    uri,
                    TypeName.get(mTypeElement.asType()),
                    Values.ROUTE_META,
                    type);
        }
        return codeBuilder.build();
    }

    private boolean isSubType(String type) {
        return mTypeUtils.isSubtype(mTypeElement.asType(), mElementUtils.getTypeElement(type).asType());
    }
}
