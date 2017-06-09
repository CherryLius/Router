package cherry.android.router.api.bundle;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/8.
 */

public class FieldBundle {
    private Bundle mBundle;
    private Uri mUri;

    private FieldBundle(Bundle bundle) {
        this.mBundle = bundle;
    }

    private FieldBundle(Bundle bundle, Uri data) {
        this.mBundle = bundle;
        this.mUri = data;
    }

    public boolean containKey(String key) {
        return (mBundle != null && mBundle.containsKey(key))
                || (mUri != null && mUri.getQueryParameter(key) != null);
    }

    private Object get(String key) {
        Object object = null;
        if (mBundle != null) {
            object = mBundle.get(key);
        }
        if (object == null && mUri != null) {
            object = mUri.getQueryParameter(key);
        }
        return object;
    }

    public Object get(String key, Object value) {
        return get(key);
    }

    public int getInt(String key, int defaultValue) {
        return castValue(get(key), defaultValue, new IParser<Integer>() {
            @Override
            public Integer getValue(String val) {
                return Integer.valueOf(val);
            }
        });
    }

    public short getShort(String key, short defaultValue) {
        return castValue(get(key), defaultValue, new IParser<Short>() {
            @Override
            public Short getValue(String val) {
                return Short.valueOf(val);
            }
        });
    }

    public long getLong(String key, long defaultValue) {
        return castValue(get(key), defaultValue, new IParser<Long>() {
            @Override
            public Long getValue(String val) {
                return Long.valueOf(val);
            }
        });
    }

    public String getString(String key, String defaultValue) {
        return castValue(get(key), defaultValue, new IParser<String>() {
            @Override
            public String getValue(String val) {
                return val;
            }
        });
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return castValue(get(key), defaultValue, new IParser<Boolean>() {
            @Override
            public Boolean getValue(String val) {
                return Boolean.valueOf(val);
            }
        });
    }

    public float getFloat(String key, float defaultValue) {
        return castValue(get(key), defaultValue, new IParser<Float>() {
            @Override
            public Float getValue(String val) {
                return Float.valueOf(val);
            }
        });
    }

    public double getDouble(String key, double defaultValue) {
        return castValue(get(key), defaultValue, new IParser<Double>() {
            @Override
            public Double getValue(String val) {
                return Double.valueOf(val);
            }
        });
    }

    public Character getCharacter(String key, Character defaultValue) {
        return castValue(get(key), defaultValue, new IParser<Character>() {
            @Override
            public Character getValue(String val) {
                return val.charAt(0);
            }
        });
    }

    public CharSequence getCharSequence(String key, CharSequence defaultValue) {
        return castValue(get(key), defaultValue, new IParser<CharSequence>() {
            @Override
            public CharSequence getValue(String val) {
                return val.subSequence(0, val.length());
            }
        });
    }

    public Byte getByte(String key, Byte defaultValue) {
        return castValue(get(key), defaultValue, new IParser<Byte>() {
            @Override
            public Byte getValue(String val) {
                return val.getBytes()[0];
            }
        });
    }

    public Bundle getBundle(String key, Bundle value) {
        if (mBundle == null)
            return null;
        return mBundle.getBundle(key);
    }

    public <T extends Parcelable> T getParcelable(String key, T value) {
        if (mBundle == null)
            return null;
        return mBundle.getParcelable(key);
    }

    public <T extends Serializable> T getSerializable(String key, T value) {
        if (mBundle == null)
            return null;
        return (T) mBundle.getSerializable(key);
    }

    public static FieldBundle newBundle(Activity activity) {
        Intent intent = activity.getIntent();
        return new FieldBundle(intent.getExtras(), intent.getData());
    }

    public static FieldBundle newBundle(Fragment fragment) {
        return new FieldBundle(fragment.getArguments());
    }

    public static FieldBundle newBundle(android.app.Fragment fragment) {
        return new FieldBundle(fragment.getArguments());
    }

    private static <T> T castValue(Object object, T defaultValue, IParser<T> parser) {
        if (object == null)
            return defaultValue;
        if (object instanceof String) {
            if (parser != null)
                return parser.getValue(object.toString());
            else
                return (T) object.toString();

        }
        return (T) object;
    }

    interface IParser<T> {
        T getValue(String val);
    }
}
