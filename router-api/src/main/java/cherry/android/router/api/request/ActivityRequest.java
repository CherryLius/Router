package cherry.android.router.api.request;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import cherry.android.router.api.RequestOptions;
import cherry.android.router.api.RouteRule;
import cherry.android.router.api.RouterInternal;
import cherry.android.router.api.utils.Logger;

/**
 * Created by ROOT on 2017/7/25.
 */

public class ActivityRequest<R> extends AbstractRequest<Intent> {

    private static final String TAG = "ActivityRequest";

    protected R host;

    public ActivityRequest(@NonNull RouteRule rule) {
        super(rule);
    }

    public ActivityRequest(@NonNull String uri, @NonNull RouteRule rule) {
        super(uri, rule);
    }

    public ActivityRequest(Class<?> destination) {
        super(destination);
    }

    public void setHost(R r) {
        if (!(r instanceof Context) && !(r instanceof Fragment)
                && !(r instanceof android.app.Fragment)) {
            throw new IllegalArgumentException("Only support Context and Fragment");
        }
        this.host = r;
    }

    protected Context getContext() {
        if (host == null)
            return RouterInternal.get().getContext();
        if (host instanceof Context)
            return (Context) host;
        if (host instanceof Fragment)
            return ((Fragment) host).getActivity();
        if (host instanceof android.app.Fragment)
            return ((android.app.Fragment) host).getActivity();
        return RouterInternal.get().getContext();
    }

    @Override
    public Intent invoke() {
        final Context context = getContext();
        Intent intent = new Intent(context, this.destination);
        intent.putExtras(this.options.getArguments());
        if (intent != null && !(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

    @Override
    public void request() {
        if (RouterInternal.get().intercept(this)) {
            if (callback != null)
                callback.onIntercept(this);
            return;
        }
        Intent intent = invoke();
        Bundle options = null;
        int requestCode = -1;
        int enterAnim = 0, exitAnim = 0;
        if (this.options != null) {
            requestCode = this.options.getRequestCode();
            enterAnim = this.options.getEnterAnim();
            exitAnim = this.options.getExitAnim();
            options = this.options.getOptionsCompat() != null ?
                    this.options.getOptionsCompat().toBundle() :
                    null;
        }
        if (requestCode == -1) {
            startActivity(intent, options);
        } else {
            startActivityForResult(intent, requestCode, options);
        }
        if (enterAnim != 0 || exitAnim != 0) {
            overridePendingTransition(enterAnim, exitAnim);
        }
        if (callback != null)
            callback.onSuccess(this);
    }

    private void startActivity(Intent intent, Bundle options) {
        if (host instanceof Activity) {
            ActivityCompat.startActivity((Context) host, intent, options);
        } else if (host instanceof Fragment) {
            ((Fragment) host).startActivity(intent, options);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && host instanceof android.app.Fragment) {
            ((android.app.Fragment) host).startActivity(intent, options);
        } else {
            ContextCompat.startActivity(getContext(), intent, options);
        }
    }

    private void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        if (host instanceof Activity) {
            ActivityCompat.startActivityForResult((Activity) host, intent, requestCode, options);
        } else if (host instanceof Fragment) {
            ((Fragment) host).startActivityForResult(intent, requestCode, options);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && host instanceof android.app.Fragment) {
            ((android.app.Fragment) host).startActivityForResult(intent, requestCode, options);
        } else {
            ContextCompat.startActivity(getContext(), intent, options);
            Logger.i(TAG, "cannot startActivityForResult");
        }
    }

    private void overridePendingTransition(int enterAnim, int exitAnim) {
        if (host instanceof Activity) {
            ((Activity) host).overridePendingTransition(enterAnim, exitAnim);
        } else if (host instanceof Fragment) {
            ((Fragment) host).getActivity().overridePendingTransition(enterAnim, exitAnim);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && host instanceof android.app.Fragment) {
            ((android.app.Fragment) host).getActivity().overridePendingTransition(enterAnim, exitAnim);
        }
    }

}
