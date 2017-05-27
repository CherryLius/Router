package cherry.android.router;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import cherry.android.router.api.IRouteCallback;
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
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Button button = (Button) findViewById(R.id.button_1);
                button.setText("OPEN ROUTE " + s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        findViewById(R.id.button_0).setOnClickListener(this);
        findViewById(R.id.button_1).setOnClickListener(this);
        findViewById(R.id.button_2).setOnClickListener(this);
        findViewById(R.id.button_3).setOnClickListener(this);
        findViewById(R.id.button_4).setOnClickListener(this);
        findViewById(R.id.button_5).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_0:
                Router.openLog(true, true);
                Router.init(this);
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
                Router.build("/route2/activity").requestCode(100)
                        .transition(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                        .open(this, new IRouteCallback() {
                            @Override
                            public void onSuccess(RouteMeta routeMeta) {
                                Logger.i("Test", "onSuccess");
                            }

                            @Override
                            public void onIntercept(RouteMeta routeMeta) {
                                Logger.e("Test", "onIntercept");
                            }

                            @Override
                            public void onFailed(RouteMeta routeMeta, String reason) {
                                Logger.e("Test", "onFailed");
                            }
                        });
                break;
            case R.id.button_4:
                Router.build("module1://activity/main").open();
                break;
            case R.id.button_5:
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("http://m.baidu.com"));
//                startActivity(intent);
                Router.build("http://m.baidu.com").open();
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
