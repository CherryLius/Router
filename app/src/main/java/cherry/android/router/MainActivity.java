package cherry.android.router;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import cherry.android.router.activity.TestActivity;
import cherry.android.router.api.RouteMeta;
import cherry.android.router.api.Router;
import cherry.android.router.api.intercept.IInterceptor;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button_0).setOnClickListener(this);

        Router.init(getApplicationContext());
        Router.addGlobalInterceptor(new IInterceptor() {
            @Override
            public boolean intercept(RouteMeta routeMeta) {
                String message = String.format("Global interceptor : \nuri=%s,\nclass=%s", routeMeta.getUri(), routeMeta.getDestination());
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                Log.e("Test", message);
                return false;
            }
        });
        Router.addRoutePicker(new Router.RoutePicker() {
            @Override
            public Map<String, Class<?>> pick() {
                Map<String, Class<?>> map = new HashMap<>();
                map.put("test/test?id=1", TestActivity.class);
                return map;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_0:
                Router.build("test/test?id=1&name=Tom&page=10#1").open();
                break;
        }
    }
}
