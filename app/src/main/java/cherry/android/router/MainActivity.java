package cherry.android.router;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import cherry.android.router.api.RouteMeta;
import cherry.android.router.api.Router;
import cherry.android.router.api.intercept.IInterceptor;
import cherry.android.router.api.utils.Logger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mEditText = (EditText) findViewById(R.id.edit_text);
        findViewById(R.id.button_0).setOnClickListener(this);
        findViewById(R.id.button_1).setOnClickListener(this);
        findViewById(R.id.button_2).setOnClickListener(this);
        findViewById(R.id.button_3).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_0:
                Router.init(this);
                Router.openLog(true, true);
                Router.addRoutePicker(new Router.RoutePicker() {
                    @Override
                    public Map<String, Class<?>> pick() {
                        Map<String, Class<?>> map = new HashMap<>();
                        map.put("/route2/activity", Route2Activity.class);
                        return map;
                    }
                });
                Router.addGlobalInterceptor(new IInterceptor() {
                    @Override
                    public boolean intercept(RouteMeta routeMeta) {
                        Logger.w("Test", "intercept on global: " + routeMeta.getDestination());
                        return false;
                    }
                });
                break;
            case R.id.button_1:
                if (!TextUtils.isEmpty(mEditText.getText().toString())) {
                    Router.build(mEditText.getText().toString()).open();
                }
                break;
            case R.id.button_2:
                Router.build("activity://cherry/route1?id=1&name=route1").open();
                break;
            case R.id.button_3:
                Router.build("/route2/activity").requestCode(100).open();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            Toast.makeText(this, "onActivityResult route2", Toast.LENGTH_SHORT).show();
        }
    }
}
