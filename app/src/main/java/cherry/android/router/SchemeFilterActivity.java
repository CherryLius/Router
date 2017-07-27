package cherry.android.router;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import cherry.android.router.api.Router;
import cherry.android.router.api.callback.RouterCallback;
import cherry.android.router.api.callback.SimpleCallback;
import cherry.android.router.api.request.Request;
import cherry.android.router.api.utils.Logger;

public class SchemeFilterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri uri = getIntent().getData();
        Logger.i("Test", uri.getPath());
        Router.build(uri).open(this, new SimpleCallback() {
            @Override
            public void onSuccess(Request request) {
                super.onSuccess(request);
                finish();
            }
        });
    }
}
