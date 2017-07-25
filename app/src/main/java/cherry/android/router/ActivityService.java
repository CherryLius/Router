package cherry.android.router;

import android.content.Intent;

import cherry.android.router.annotations.Action;
import cherry.android.router.annotations.ClassName;
import cherry.android.router.annotations.Options;
import cherry.android.router.annotations.Query;
import cherry.android.router.annotations.URL;
import cherry.android.router.annotations.Uri;
import cherry.android.router.api.request.Request;

/**
 * Created by LHEE on 2017/7/22.
 */

public interface ActivityService {
    //    @URL("activity://cherry/route1")
    @ClassName(SecondActivity.class)
    Request startActivity(@Query("name") String name,
                          @Query("id") int id,
                          boolean flag,
                          @Query("state") int state);

    @URL("activity://cherry/route1")
    void startActivity(@Query("name") String name,
                       @Query("id") int id,
                       @Query("object") Object object);

    @ClassName(Route1Activity.class)
    void route1();

    @URL("activity://cherry/route2")
    @Options(requestCode = 100,
            ignoreIntercepter = true,
            enterAnim = R.anim.slide_in_bottom,
            exitAnim = R.anim.slide_out_bottom)
    void route2();

    @URL("module1://activity/main")
    Request module1();

    @URL("fragment://blank")
    android.support.v4.app.Fragment getFragment();

    @ClassName(BlankFragment.class)
    android.support.v4.app.Fragment getFragmentByClass();

    @Action(Intent.ACTION_VIEW)
    void goUrl(@Uri String url);

    @Action(Intent.ACTION_VIEW)
    void sendSms(@Uri String uri,
                 @Query("sms_body") String body);
}
