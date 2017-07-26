package cherry.android.router.api.request;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * Created by ROOT on 2017/7/25.
 */

public class ActionRequest extends ActivityRequest {

    private String action;
    private String type;

    private ActionRequest(Builder builder) {
        super(void.class);
        this.action = builder.action;
        this.uri = builder.uri;
        this.type = builder.type;
    }

    public void setData(@NonNull String uri) {
        this.uri = uri;
    }

    @Override
    public Intent invoke() {
        final Context context = getContext();
        Intent intent = new Intent(this.action);
        if (!TextUtils.isEmpty(this.type))
            intent.setType(this.type);
        if (!TextUtils.isEmpty(this.uri))
            intent.setData(Uri.parse(this.uri));
        intent.putExtras(this.options.getArguments());
        if (intent != null && !(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }


    public static class Builder {
        private String action;
        private String uri;
        private String type;

        public Builder setAction(@NonNull String action) {
            this.action = action;
            return this;
        }

        public Builder setUri(String uri) {
            this.uri = uri;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public ActionRequest build() {
            return new ActionRequest(this);
        }
    }
}
