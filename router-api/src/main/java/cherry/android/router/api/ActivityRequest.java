package cherry.android.router.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;

/**
 * Created by ROOT on 2017/7/25.
 */

public class ActivityRequest extends AbstractRequest<Intent> {

    private int requestCode = -1;
    private int enterAnim;
    private int exitAnim;
    private ActivityOptionsCompat optionsCompat;
    private boolean ignoreInterceptor;
    private Intent intent;
    private Context context;
    private RouteRule rule;

    public ActivityRequest(@NonNull RouteRule rule) {
        super(rule);
        this.rule = rule;
    }

    public ActivityRequest(@NonNull String uri, @NonNull RouteRule rule) {
        super(uri, rule);
        this.rule = rule;
    }

    public ActivityRequest(Class<?> destination) {
        super(destination);
    }

    void setRequestCode(int requestCode) {
        this.requestCode = requestCode < 0 ? -1 : requestCode;
    }

    int getRequestCode() {
        return this.requestCode;
    }

    void transition(int enterAnim, int exitAnim) {
        this.enterAnim = enterAnim;
        this.exitAnim = exitAnim;
    }

    int getEnterAnim() {
        return this.enterAnim;
    }

    int getExitAnim() {
        return this.exitAnim;
    }

    void setOptionsCompat(ActivityOptionsCompat optionsCompat) {
        this.optionsCompat = optionsCompat;
    }

    ActivityOptionsCompat getOptionsCompat() {
        return this.optionsCompat;
    }

    void ignoreInterceptor(boolean ignore) {
        this.ignoreInterceptor = ignore;
    }

    boolean isIgnoreInterceptor() {
        return this.ignoreInterceptor;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    RouteRule getRule() {
        return this.rule;
    }

    @Override
    public Intent invoke() {
        if (context == null)
            context = RouterInternal.get().getContext();
        intent = new Intent(context, this.destination);
        intent.putExtras(this.arguments);
        if (intent != null && !(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }
}
