package cherry.android.router.compiler;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.TypeElement;

import cherry.android.router.annotations.Interceptor;
import cherry.android.router.compiler.common.Values;

/**
 * Created by Administrator on 2017/5/25.
 */

public class InterceptorClass {

    private TypeElement mTypeElement;
    private String mName;
    private int mPriority;

    public InterceptorClass(TypeElement typeElement) {
        mTypeElement = typeElement;
        Interceptor interceptor = mTypeElement.getAnnotation(Interceptor.class);
        mName = interceptor.value();
        mPriority = interceptor.priority();
    }

    private TypeName getTypeName() {
        return TypeName.get(mTypeElement.asType());
    }

    public CodeBlock generateCode() {
        CodeBlock.Builder codeBuilder = CodeBlock.builder();
        codeBuilder.addStatement("interceptors.put($S, new $T($T.class, $S, $L))",
                mName, Values.INTERCEPTOR_META, getTypeName(), mName, mPriority);
        return codeBuilder.build();
    }

}
