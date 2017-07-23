package cherry.android.router.compiler.generate;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.Map;

import cherry.android.router.compiler.RouteClass;

import static cherry.android.router.compiler.common.Values.ROUTE_RULE;

/**
 * Created by LHEE on 2017/7/22.
 */

public class RouteGenerator extends PickerGenerator<RouteClass> {
    public RouteGenerator(String moduleName) {
        super(moduleName);
    }

    @Override
    String getPickerName() {
        return "_RoutePicker";
    }

    @Override
    TypeName getGenericType() {
        ParameterizedTypeName param = ParameterizedTypeName.get(ClassName.get(Map.class),
                ClassName.get(String.class), ROUTE_RULE);
        return param;
    }
}
