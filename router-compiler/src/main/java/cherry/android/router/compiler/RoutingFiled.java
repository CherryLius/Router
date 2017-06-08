package cherry.android.router.compiler;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

import cherry.android.router.annotations.RouteField;

/**
 * Created by Administrator on 2017/6/8.
 */

public class RoutingFiled {
    private VariableElement mFieldElement;
    private String mKeyName;
    private boolean mNonNull;

    public RoutingFiled(Element element) {
        if (!element.getKind().isField())
            throw new IllegalStateException(String.format("Only field can be annotated with @%s",
                    RoutingFiled.class.getSimpleName()));
        mFieldElement = (VariableElement) element;
        RouteField annotation = mFieldElement.getAnnotation(RouteField.class);
        mKeyName = annotation.name();
        mNonNull = annotation.nonNull();
        if (mKeyName.isEmpty())
            mKeyName = getFieldName();
    }

    public String getFieldName() {
        return mFieldElement.getSimpleName().toString();
    }

}
