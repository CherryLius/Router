package cherry.android.router;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import cherry.android.router.api.request.Request;
import cherry.android.router.api.Router;
import cherry.android.router.api.utils.Logger;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
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
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
        service = Router.create(ActivityService.class);
    }

    private ActivityService service;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                service.startActivity("jack", 20, "MainApp");
                break;
            case R.id.button2:
                service.route1();
                break;
            case R.id.button3:
                service.route2();
                break;
            case R.id.button4:
                Request request = service.module1();
                Logger.i("Test", request.getDestination() + "");
                service.getFragment();
                service.getFragmentByClass();
                service.goUrl("http://m.baidu.com");
//                service.sendSms("sms:", "SMSSS");
                break;
        }
    }
    /**
     * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) //At least KitKat
     {
     String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(this); //Need to change the build to API 19

     Intent sendIntent = new Intent(Intent.ACTION_SEND);
     sendIntent.setType("text/plain");
     sendIntent.putExtra(Intent.EXTRA_TEXT, "信息内容....");

     if (defaultSmsPackageName != null)//Can be null in case that there is no default, then the user would be able to choose any app that support this intent.
     {
     sendIntent.setPackage(defaultSmsPackageName);
     }
     startActivity(sendIntent);

     } else //For early versions, do what worked for you before.
     {
     Intent sendIntent = new Intent(Intent.ACTION_VIEW);
     sendIntent.setData(Uri.parse("sms:"));
     sendIntent.putExtra("sms_body", "信息内容....");
     startActivity(sendIntent);
     }
     */
}
