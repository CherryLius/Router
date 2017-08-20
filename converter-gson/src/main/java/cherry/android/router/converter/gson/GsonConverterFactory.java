package cherry.android.router.converter.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import cherry.android.router.api.convert.Converter;

/**
 * Created by LHEE on 2017/8/20.
 */

public class GsonConverterFactory extends Converter.Factory {

    private Gson gson;

    public static GsonConverterFactory create() {
        Gson gson = new Gson();
        return new GsonConverterFactory(gson);
    }

    private GsonConverterFactory(Gson gson) {
        this.gson = gson;
    }

    @Override
    public Converter<?, String> stringConverter(Type type) {
//        if (!isJsonType(type)) {
//            return null;
//        }
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new ToStringConverter<>(gson, adapter);
    }

    @Override
    public Converter<String, ?> classConverter(Type type) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new ToClassConverter<>(gson, adapter);
    }

    private static final Class<?>[] RAW_CLASS = {
            int.class, long.class, short.class,
            float.class, double.class,
            char.class, boolean.class,
            String.class, Integer.class, Float.class, Double.class,
            Boolean.class, Short.class, Long.class, Boolean.class, Character.class
    };

    private static boolean isJsonType(Type type) {
        for (int i = 0; i < RAW_CLASS.length; i++) {
            Class<?> clazz = RAW_CLASS[i];
            if (type.equals(clazz)) {
                return false;
            }
        }
        return true;
    }
}
