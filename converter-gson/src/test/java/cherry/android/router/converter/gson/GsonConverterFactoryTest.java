package cherry.android.router.converter.gson;

import org.junit.Before;
import org.junit.Test;

import cherry.android.router.api.convert.Converter;

import static org.junit.Assert.assertEquals;

/**
 * Created by ROOT on 2017/8/21.
 */
public class GsonConverterFactoryTest {
    Converter.Factory factory;

    @Before
    public void setUp() throws Exception {
        factory = GsonConverterFactory.create();
    }

    @Test
    public void stringConverter() throws Exception {
        Converter converter = factory.stringConverter(int.class);
        String value = (String) converter.convert(120);
        System.out.println("value=" + value);
        assertEquals(value, "120");

        factory.stringConverter(Float.class);
        factory.stringConverter(float.class);
        System.out.println("\n");
    }

    @Test
    public void classConverter() throws Exception {
        Converter converter = factory.classConverter(int.class);
        int value = (int) converter.convert("123456");
        System.out.println("value=" + value);
        assertEquals(value, 123456);
    }

}