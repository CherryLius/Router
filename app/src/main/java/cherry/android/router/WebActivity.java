package cherry.android.router;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import cherry.android.router.annotations.Args;
import cherry.android.router.annotations.Route;
import cherry.android.router.api.Router;
import cherry.android.router.api.utils.Logger;

@Route("cherry://activity/web")
public class WebActivity extends AppCompatActivity {

    WebView webView;
    @Args
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        webView = (WebView) findViewById(R.id.web_view);
        Router.inject(this);
        Logger.i("Test", "url=" + url);
        webView.loadUrl(url);
    }
}
