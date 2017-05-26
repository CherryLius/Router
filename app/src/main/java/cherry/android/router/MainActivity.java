package cherry.android.router;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import cherry.android.router.api.RouteMeta;
import cherry.android.router.api.Router;
import cherry.android.router.api.intercept.IInterceptor;
import cherry.android.router.api.utils.Logger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button_0).setOnClickListener(this);
        findViewById(R.id.button_1).setOnClickListener(this);

        Router.init(getApplicationContext());
        Router.openLog(true, false);
        Router.addGlobalInterceptor(new IInterceptor() {
            @Override
            public boolean intercept(RouteMeta routeMeta) {
                String message = String.format("Global interceptor : \nuri=%s,\nclass=%s", routeMeta.getUri(), routeMeta.getDestination());
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                Logger.e("Test", message);
                return false;
            }
        });
        Router.addRoutePicker(new Router.RoutePicker() {
            @Override
            public Map<String, Class<?>> pick() {
                Map<String, Class<?>> map = new HashMap<>();
                map.put("test/test?id=1", TestActivity.class);
                map.put("test/activity2", Test2Activity.class);
                return map;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_0:
                Router.build("test/test?id=1&name=Tom&page=10#1")
                        .extra("text", "Router")
                        .extra("index", 10000)
                        .extra("array", new String[]{"111"})
                        .extra("intArray", new int[]{101})
                        .requestCode(100)
                        .open(this);
                break;
            case R.id.button_1:
                Router.build("test/activity2")
                        .open(this);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.i("Test", "requestCode=" + requestCode);
    }
}
