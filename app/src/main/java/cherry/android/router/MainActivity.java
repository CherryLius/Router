package cherry.android.router;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.Map;

import cherry.android.router.activity.TestActivity;
import cherry.android.router.api.IRoutePicker;
import cherry.android.router.api.RouteMeta;
import cherry.android.router.api.Router;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button_0).setOnClickListener(this);

        Router.init(getApplicationContext());
        Router.addRoutePicker(new IRoutePicker() {
            @Override
            public void pick(Map<String, RouteMeta> routeTable) {
                routeTable.put("test/test?id=1", RouteMeta.newMeta(
                        "test/test?id=1",
                        TestActivity.class,
                        RouteMeta.TYPE_ACTIVITY
                ));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_0:
                Router.build("test/test?id=1&name=Tom&page=10#1").open(this);
                break;
        }
    }
}
