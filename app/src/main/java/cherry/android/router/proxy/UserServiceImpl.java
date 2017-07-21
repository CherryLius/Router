package cherry.android.router.proxy;

import cherry.android.router.api.utils.Logger;

/**
 * Created by ROOT on 2017/7/21.
 */

public class UserServiceImpl implements UserService {
    @Override
    public String getName() {
        Logger.i("Test", "--getName---");
        return "Tom";
    }

    @Override
    public int getAge() {
        Logger.i("Test", "--getAge--");
        return 8;
    }
}
