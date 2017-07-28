package cherry.android.router.compiler.generate;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.Map;

import cherry.android.router.compiler.InterceptorClass;
import cherry.android.router.compiler.common.Values;

/**
 * Created by LHEE on 2017/7/22.
 */

public class InterceptorGenerator extends PickerGenerator<InterceptorClass> {
    public InterceptorGenerator(String moduleName) {
        super(moduleName);
    }

    @Override
    String getPickerName() {
        return "_InterceptorPicker";
    }

    @Override
    TypeName getGenericType() {
        return ParameterizedTypeName.get(ClassName.get(Map.class),
                ClassName.get(String.class), Values.ROUTE_INTERCEPTOR);
    }
}
