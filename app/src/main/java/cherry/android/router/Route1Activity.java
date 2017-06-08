package cherry.android.router;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import cherry.android.router.annotations.Route;
import cherry.android.router.annotations.RouteField;

@Route(value = "activity://cherry/route1", interceptor = "route1")
public class Route1Activity extends AppCompatActivity {

    @RouteField(name = "id")
    int mId = -1;
    @RouteField
    String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route1);
        TextView textView = (TextView) findViewById(R.id.textView);
        if (getIntent().getExtras() != null) {
            StringBuilder sb = new StringBuilder();
            for (String key : getIntent().getExtras().keySet()) {
                sb.append(key)
                        .append(" : ")
                        .append(getIntent().getExtras().get(key))
                        .append('\n');
            }
            textView.setText("Extra=\n" + sb.toString());
        }
    }
}
