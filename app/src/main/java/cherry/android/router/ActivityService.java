package cherry.android.router;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;

import cherry.android.router.annotations.Action;
import cherry.android.router.annotations.ClassName;
import cherry.android.router.annotations.Options;
import cherry.android.router.annotations.OptionsCompat;
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

    @URL("/activity/route1")
    void startActivity(@Query("name") String name,
                       @Query("id") int id,
                       @Query("object") Object object);

//    @URL("/activity/movie/detail")
//    void startActivity(@Query("name") String name,
//                       @Query("id") String id,
//                       @Query("imageUrl") String url);

    @ClassName(Route1Activity.class)
    void route1(@OptionsCompat ActivityOptionsCompat compat);

    @URL("cherry://activity/route2")
    @Options(requestCode = 100,
            ignoreInterceptor = true,
            enterAnim = R.anim.slide_in_bottom,
            exitAnim = R.anim.slide_out_bottom)
    void route2(Context context);

    @URL("cherry://activity/module1/main")
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

    @URL("cherry://activity/web")
    void startWebView(@Query("url") String url);
}
