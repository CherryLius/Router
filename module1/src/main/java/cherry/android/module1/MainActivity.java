package cherry.android.module1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import cherry.android.router.annotations.Route;

@Route(value = "cherry://activity/module1/main", interceptor = "route1")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module1_activity_main);
    }
}
