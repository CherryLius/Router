package cherry.android.router.converter.gson;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.io.IOException;

import cherry.android.router.api.convert.Converter;

/**
 * Created by LHEE on 2017/8/20.
 */

/*public*/ class ToClassConverter<R> implements Converter<String, R> {
    private Gson gson;
    private TypeAdapter<R> adapter;

    ToClassConverter(@NonNull Gson gson, @NonNull TypeAdapter<R> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public R convert(String s) throws IOException {
        return adapter.fromJson(s);
    }
}
