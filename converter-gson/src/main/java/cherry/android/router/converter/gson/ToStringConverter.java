package cherry.android.router.converter.gson;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import cherry.android.router.api.convert.Converter;
import cherry.android.router.api.utils.Logger;

/**
 * Created by LHEE on 2017/8/20.
 */

/*public*/ class ToStringConverter<T> implements Converter<T, String> {
    private Gson gson;
    private TypeAdapter<T> adapter;

    ToStringConverter(@NonNull Gson gson, @NonNull TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public String convert(T t) {
        return adapter.toJson(t);
    }
}
