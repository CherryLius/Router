package cherry.android.router.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import cherry.android.router.R;
import cherry.android.router.annotations.Route;

@Route("test/activity")
public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }
}
