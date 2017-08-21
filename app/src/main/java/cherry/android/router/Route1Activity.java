package cherry.android.router;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.List;

import cherry.android.router.annotations.Args;
import cherry.android.router.annotations.Route;
import cherry.android.router.api.Router;
import cherry.android.router.model.ParcelableFoo;
import cherry.android.router.model.SeriFoo;
import cherry.android.router.model.User;

@Route(value = "cherry://activity/route1", interceptor = "route1")
public class Route1Activity extends AppCompatActivity {

    @Args(name = "id", nonNull = true)
    int mId = -2;
    @Args
    String name;
    @Args
    User user;
    @Args
    SeriFoo seri;
    @Args
    ParcelableFoo parcel;
    @Args
    List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route1);
        Router.inject(this);
        TextView textView = (TextView) findViewById(R.id.textView);
        TextView textView1 = (TextView) findViewById(R.id.textView_1);
        textView.setText("id=" + mId + ",name=" + name
                + "\n: user=" + user
                + "\n seri=" + seri
                + "\n parcel=" + parcel
        +"\n list[0]=" + users.get(0));
        if (getIntent().getExtras() != null) {
            StringBuilder sb = new StringBuilder();
            for (String key : getIntent().getExtras().keySet()) {
                sb.append(key)
                        .append(" : ")
                        .append(getIntent().getExtras().get(key))
                        .append('\n');
            }
            textView1.setText("Args=\n" + sb.toString());
        }
    }
}
