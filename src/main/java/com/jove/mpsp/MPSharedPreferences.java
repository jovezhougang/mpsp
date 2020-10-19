package com.jove.mpsp;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MPSharedPreferences {

    private HashSet<OnSharedPreferenceChangeListener> listeners = new HashSet<>();

    public interface OnSharedPreferenceChangeListener {
        void onSharedPreferenceChanged(final MPSharedPreferences sharedPreferences,
                                       final String key);
    }

    private String name;
    private Context context;

    private ContentObserver mContentObserver =
            new ContentObserver(new Handler(Looper.getMainLooper())) {
                @Override
                public void onChange(boolean selfChange, Uri uri) {
                    synchronized (MPSharedPreferences.this) {
                        for (final OnSharedPreferenceChangeListener listener : listeners) {
                            listener.onSharedPreferenceChanged(MPSharedPreferences.this
                                    , uri.getPathSegments().get(1));
                        }
                    }
                }
            };

    public MPSharedPreferences(@NonNull Context context, @NonNull final String name) {
        this.name = name;
        this.context = context;
        this.context.getContentResolver()
                .registerContentObserver(Uri.parse(String.format("content://com.jove.mpsp" +
                                ".provider/%s"
                        , name)), true, mContentObserver);
    }

    public Map<String, ?> getAll() {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver()
                    .query(Uri.parse(String.format("content://com.jove.mpsp.provider/%s/all/0"
                            , name)), null, null, null, null);
            if (null != cursor && cursor.moveToNext()) {
                final Map<String, Object> map = new HashMap<>(cursor.getColumnCount());
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    switch (cursor.getType(i)) {
                        case Cursor.FIELD_TYPE_BLOB:
                            map.put(cursor.getColumnName(i), cursor.getBlob(i));
                            break;
                        case Cursor.FIELD_TYPE_STRING:
                        case Cursor.FIELD_TYPE_NULL:
                            map.put(cursor.getColumnName(i), cursor.getString(i));
                            break;
                        case Cursor.FIELD_TYPE_INTEGER:
                            map.put(cursor.getColumnName(i), cursor.getInt(i));
                            if (Long.parseLong(cursor.getString(i)) != cursor.getInt(i)) {
                                map.put(cursor.getColumnName(i),
                                        Long.parseLong(cursor.getString(i)));
                            }
                            break;
                        case Cursor.FIELD_TYPE_FLOAT:
                            map.put(cursor.getColumnName(i), cursor.getFloat(i));
                            break;
                    }
                }
                return map;
            }
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return null;
    }

    @Nullable
    public String getString(String key, @Nullable String defValue) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver()
                    .query(Uri.parse(String.format("content://com.jove.mpsp.provider/%s/%s/1"
                            , name, key)), null, null, null, null);
            if (null != cursor && cursor.moveToNext()) {
                return cursor.getString(0);
            }
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return defValue;
    }

    @Nullable
    public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver()
                    .query(Uri.parse(String.format("content://com.jove.mpsp.provider/%s/%s/6"
                            , name, key)), null, null, null, null);
            if (null != cursor && cursor.moveToNext()) {
                final Set<String> sets = new HashSet<>();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    sets.add(cursor.getString(i));
                }
                return sets;
            }
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return defValues;
    }

    public int getInt(String key, int defValue) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver()
                    .query(Uri.parse(String.format("content://com.jove.mpsp.provider/%s/%s/4"
                            , name, key)), null, null, null, null);
            if (null != cursor && cursor.moveToNext()) {
                return cursor.getInt(0);
            }
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return defValue;
    }

    public long getLong(String key, long defValue) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver()
                    .query(Uri.parse(String.format("content://com.jove.mpsp.provider/%s/%s/5"
                            , name, key)), null, null, null, null);
            if (null != cursor && cursor.moveToNext()) {
                return Long.parseLong(cursor.getString(0));
            }
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return defValue;
    }

    public float getFloat(final String key, final float defValue) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver()
                    .query(Uri.parse(String.format("content://com.jove.mpsp.provider/%s/%s/3"
                            , name, key)), null, null, null, null);
            if (null != cursor && cursor.moveToNext()) {
                return cursor.getFloat(0);
            }
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return defValue;
    }

    public boolean getBoolean(final String key, final boolean defValue) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver()
                    .query(Uri.parse(String.format("content://com.jove.mpsp.provider/%s/%s/2"
                            , name, key)), null, null, null, null);
            if (null != cursor && cursor.moveToNext()) {
                return 0 == cursor.getInt(0) ? false : true;
            }
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return defValue;
    }

    public boolean contains(final String key) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver()
                    .query(Uri.parse(String.format("content://com.jove.mpsp.provider/%s/%s/7"
                            , name, key)), null, null, null, null);
            if (null != cursor && cursor.moveToNext()) {
                return cursor.getInt(0) > 0;
            }
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return false;
    }

    public synchronized void registerOnSharedPreferenceChangeListener(final @NonNull OnSharedPreferenceChangeListener listener) {
        listeners.add(listener);
    }

    public synchronized void unregisterOnSharedPreferenceChangeListener(final @NonNull OnSharedPreferenceChangeListener listener) {
        listeners.remove(listener);
    }

    public boolean putString(String key, @Nullable String value) {
        final ContentValues values = new ContentValues();
        values.put(key, value);
        final Uri uri = context.getContentResolver()
                .insert(Uri.parse(String.format("content://com.jove.mpsp.provider/%s/%s/1"
                        , name, key)), values);
        return null != uri;
    }

    public boolean putStringSet(final String key, final @Nullable Set<String> values) {
        final ContentValues contentValues = new ContentValues();
        int index = 0;
        for (final String value : values) {
            contentValues.put("k" + index, value);
            index++;
        }
        final Uri uri = context.getContentResolver()
                .insert(Uri.parse(String.format("content://com.jove.mpsp.provider/%s/%s/6"
                        , name, key)), contentValues);
        return null != uri;
    }

    public boolean putInt(String key, int value) {
        final ContentValues values = new ContentValues();
        values.put(key, value);
        final Uri uri = context.getContentResolver()
                .insert(Uri.parse(String.format("content://com.jove.mpsp.provider/%s/%s/4"
                        , name, key)), values);
        return null != uri;
    }

    public boolean putLong(String key, long value) {
        final ContentValues values = new ContentValues();
        values.put(key, value);
        final Uri uri = context.getContentResolver()
                .insert(Uri.parse(String.format("content://com.jove.mpsp.provider/%s/%s/5"
                        , name, key)), values);
        return null != uri;
    }

    public boolean putFloat(String key, float value) {
        final ContentValues values = new ContentValues();
        values.put(key, value);
        final Uri uri = context.getContentResolver()
                .insert(Uri.parse(String.format("content://com.jove.mpsp.provider/%s/%s/3"
                        , name, key)), values);
        return null != uri;
    }

    public boolean putBoolean(String key, boolean value) {
        final ContentValues values = new ContentValues();
        values.put(key, value);
        final Uri uri = context.getContentResolver()
                .insert(Uri.parse(String.format("content://com.jove.mpsp.provider/%s/%s/2"
                        , name, key)), values);
        return null != uri;
    }

    public boolean remove(final String key) {
        final int nums = context.getContentResolver()
                .delete(Uri.parse(String.format("content://com.jove.mpsp.provider/remove/%s/%s"
                        , name, key)), null, null);
        return nums > 0;
    }

    public boolean clear() {
        final int nums = context.getContentResolver()
                .delete(Uri.parse(String.format("content://com.jove.mpsp.provider/clear/%s"
                        , name)), null, null);
        return nums > 0;
    }
}
