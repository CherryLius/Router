package cherry.android.router.compiler;

import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

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

    public TypeMirror asType() {
        return mFieldElement.asType();
    }

    public String getFieldName() {
        return mFieldElement.getSimpleName().toString();
    }

    public TypeName getTypeName() {
        return TypeName.get(mFieldElement.asType());
    }

    public String getKey() {
        return mKeyName;
    }

    public boolean isNonNull() {
        return mNonNull;
    }

}
