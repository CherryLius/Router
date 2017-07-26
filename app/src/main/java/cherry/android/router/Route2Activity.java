package cherry.android.router;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import cherry.android.router.annotations.Route;
import cherry.android.router.api.Router;
import cherry.android.router.api.utils.Logger;

@Route(value = "cherry://activity/route2", interceptor = "route2")
public class Route2Activity extends AppCompatActivity implements View.OnClickListener {

    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route2);
        findViewById(R.id.button_0).setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        if (fragment == null)
            fragment = Router.build("fragment://blank").invoke();
        Logger.i("Test", "fragment=" + fragment + "," + fragment.isAdded());
        if (!fragment.isAdded())
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
    }
}
