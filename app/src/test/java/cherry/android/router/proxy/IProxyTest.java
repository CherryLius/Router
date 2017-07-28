package cherry.android.router.proxy;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by ROOT on 2017/7/21.
 */
public class IProxyTest {
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void create() throws Exception {
        UserService impl = IProxy.create(UserService.class, new UserServiceImpl());
        assertEquals("Tom", impl.getName());
        assertEquals(8, impl.getAge(), 0);
    }

}