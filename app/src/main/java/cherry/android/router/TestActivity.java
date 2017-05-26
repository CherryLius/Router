package cherry.android.router;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cherry.android.router.annotations.Route;
import cherry.android.router.api.utils.Logger;

@Route(value = {"test/activity", "activity://test/test1"}, interceptor = {"m", "nnn"})
public class TestActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Logger.i("Test", "key=" + key + ",value=" + bundle.get(key));
            }
        }
        findViewById(R.id.button_0).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        setResult(RESULT_OK);
        finish();
    }
}
