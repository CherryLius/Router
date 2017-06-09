package cherry.android.router;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import cherry.android.router.annotations.Route;
import cherry.android.router.annotations.RouteField;
import cherry.android.router.api.Router;

@Route(value = "activity://cherry/route1", interceptor = "route1")
public class Route1Activity extends AppCompatActivity {

    @RouteField(name = "id", nonNull = true)
    int mId = -1;
    @RouteField(nonNull = true)
    String name;
    @RouteField
    Object object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route1);
        Router.bind(this);
        TextView textView = (TextView) findViewById(R.id.textView);
        TextView textView1 = (TextView) findViewById(R.id.textView_1);
        textView.setText("id=" + mId + ",name=" + name
                + ", object=" + object);
        if (getIntent().getExtras() != null) {
            StringBuilder sb = new StringBuilder();
            for (String key : getIntent().getExtras().keySet()) {
                sb.append(key)
                        .append(" : ")
                        .append(getIntent().getExtras().get(key))
                        .append('\n');
            }
            textView1.setText("Extra=\n" + sb.toString());
        }
    }
}
