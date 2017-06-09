package cherry.android.router.compiler.util;

import com.squareup.javapoet.TypeName;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Created by Administrator on 2017/6/9.
 */

public final class Utils {

    public static TypeName getTypeName(Elements elementUtils, String type) {
        return TypeName.get(getTypeMirror(elementUtils,type));
    }

    public static TypeMirror getTypeMirror(Elements elementUtils, String type) {
        return elementUtils.getTypeElement(type).asType();
    }

    public static boolean isSubType(Types typeUtils, TypeMirror t1, TypeMirror t2) {
        return typeUtils.isSubtype(t1, t2);
    }
}
