package cherry.android.router.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ROOT on 2017/8/21.
 */

public class ParcelableFoo implements Parcelable {
    private String name;
    private int value;

    public ParcelableFoo(String name, int value) {
        this.name = name;
        this.value = value;
    }

    protected ParcelableFoo(Parcel in) {
        name = in.readString();
        value = in.readInt();
    }

    public static final Creator<ParcelableFoo> CREATOR = new Creator<ParcelableFoo>() {
        @Override
        public ParcelableFoo createFromParcel(Parcel in) {
            return new ParcelableFoo(in);
        }

        @Override
        public ParcelableFoo[] newArray(int size) {
            return new ParcelableFoo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(value);
    }

    @Override
    public String toString() {
        return "ParcelableFoo{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
