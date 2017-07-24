package cherry.android.router;

import cherry.android.router.annotations.Query;
import cherry.android.router.annotations.URL;
import cherry.android.router.api.Request;

/**
 * Created by LHEE on 2017/7/22.
 */

public interface ActivityService {
    @URL("activity://cherry/route1")
//    @ClassName(Route1Activity.class)
    Request startActivity(@Query("name") String name,
                          @Query("id") int id,
                          boolean flag,
                          @Query("state") int state);
}
