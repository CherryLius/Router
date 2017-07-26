package cherry.android.router.api;

import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.AnimRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityOptionsCompat;

import cherry.android.router.api.utils.Utils;

/**
 * Created by ROOT on 2017/7/26.
 */

public class RequestOptions {
    @AnimRes
    private int enterAnim;
    @AnimRes
    private int exitAnim;
    private boolean ignoreInterceptor;
    private int requestCode;
    private ActivityOptionsCompat optionsCompat;
    private Bundle arguments;

    RequestOptions() {
        arguments = new Bundle();
    }

    RequestOptions transition(@AnimRes int enterAnim, @AnimRes int exitAnim) {
        this.enterAnim = enterAnim;
        this.exitAnim = exitAnim;
        return this;
    }

    RequestOptions ignoreInterceptor(boolean ignore) {
        this.ignoreInterceptor = ignore;
        return this;
    }

    RequestOptions requestCode(int requestCode) {
        this.requestCode = requestCode < 0 ? -1 : requestCode;
        return this;
    }

    RequestOptions optionsCompat(ActivityOptionsCompat optionsCompat) {
        this.optionsCompat = optionsCompat;
        return this;
    }

    RequestOptions extra(@NonNull String key, Object value) {
        Utils.putValue2Bundle(arguments, key, value);
        return this;
    }

    RequestOptions extra(Bundle value) {
        arguments.putAll(value);
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    RequestOptions extra(PersistableBundle value) {
        arguments.putAll(value);
        return this;
    }

    @AnimRes
    public int getEnterAnim() {
        return this.enterAnim;
    }

    @AnimRes
    public int getExitAnim() {
        return this.exitAnim;
    }

    public boolean isIgnoreInterceptor() {
        return this.ignoreInterceptor;
    }

    public int getRequestCode() {
        return this.requestCode;
    }

    public Bundle getArguments() {
        return this.arguments;
    }

    public ActivityOptionsCompat getOptionsCompat() {
        return this.optionsCompat;
    }
}
