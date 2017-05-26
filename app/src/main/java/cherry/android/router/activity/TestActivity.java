package cherry.android.router.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import cherry.android.router.R;
import cherry.android.router.annotations.Route;

@Route(value = {"test/activity", "activity://test/test1"}, interceptor = {"m", "nnn"})
public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Log.i("Test", "key=" + key + ",value=" + bundle.get(key));
            }
        }
    }
}
