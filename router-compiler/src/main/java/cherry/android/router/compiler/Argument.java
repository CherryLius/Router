package cherry.android.router.compiler;

import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import cherry.android.router.annotations.Args;

/**
 * Created by Administrator on 2017/6/8.
 */

public class Argument {
    private VariableElement mFieldElement;
    private String mKeyName;
    private boolean mNonNull;

    public Argument(Element element) {
        if (!element.getKind().isField())
            throw new IllegalStateException(String.format("Only field can be annotated with @%s",
                    Argument.class.getSimpleName()));
        mFieldElement = (VariableElement) element;
        Args annotation = mFieldElement.getAnnotation(Args.class);
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
        TypeName typeName = TypeName.get(mFieldElement.asType());
        if (typeName instanceof ParameterizedTypeName) {
            ParameterizedTypeName parameterizedTypeName = (ParameterizedTypeName) typeName;
            return parameterizedTypeName.rawType;
        }
        return typeName;
    }

    public String getKey() {
        return mKeyName;
    }

    public boolean isNonNull() {
        return mNonNull;
    }

}
