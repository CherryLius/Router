package cherry.android.router.model;

import java.io.Serializable;

/**
 * Created by ROOT on 2017/8/21.
 */

public class SeriFoo implements Serializable {
    private String name;
    private int value;

    public SeriFoo(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SeriFoo{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
