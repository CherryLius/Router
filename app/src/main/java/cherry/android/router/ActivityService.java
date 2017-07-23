package cherry.android.router;

import cherry.android.router.annotations.ClassName;
import cherry.android.router.annotations.Query;
import cherry.android.router.annotations.URL;

/**
 * Created by LHEE on 2017/7/22.
 */

public interface ActivityService {
    @URL("module1://activity/main")
    @ClassName(Route1Activity.class)
    void startActivity(@Query("name") String name,
                       @Query("id") int id);
}
