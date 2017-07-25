package cherry.android.router;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cherry.android.router.annotations.Route;
import cherry.android.router.api.Router;
import cherry.android.router.api.utils.Logger;

@Route(value = "activity://cherry/route2", interceptor = "route2")
public class Route2Activity extends AppCompatActivity implements View.OnClickListener {

    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route2);
        findViewById(R.id.button_0).setOnClickListener(this);
        fragment = Router.build("fragment://blank").invoke();
    }

    @Override
    public void onClick(View v) {
        Logger.i("Test", "fragment=" + fragment + "," + fragment.isAdded());
        if (!fragment.isAdded())
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
    }
}
