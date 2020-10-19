package com.jove.mpsp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MPContentProvider extends ContentProvider implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    static {
        uriMatcher.addURI("com.jove.mpsp.provider", "remove/*/*", 0x666);
        uriMatcher.addURI("com.jove.mpsp.provider", "clear/*", 0x999);
        uriMatcher.addURI("com.jove.mpsp.provider", "*/*/#", 0x888);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        try {
            lock.readLock().lock();
            switch (uriMatcher.match(uri)) {
                case 0x888:
                    final List<String> paths = uri.getPathSegments();
                    final SharedPreferences sp = getContext()
                            .getSharedPreferences(paths.get(0), Context.MODE_PRIVATE);
                    final int action = Integer.parseInt(paths.get(2));
                    if (action == 0) {
                        final Map<String, ?> map = sp.getAll();
                        if (null != map && 0 != map.size()) {
                            final Object[] columnValues = new Object[map.size()];
                            final String[] columnNames = new String[map.size()];
                            int index = 0;
                            for (final String key : map.keySet()) {
                                columnNames[index] = key;
                                columnValues[index] = map.get(key);
                                index++;
                            }
                            final MatrixCursor cursor = new MatrixCursor(columnNames
                                    , 1);
                            cursor.addRow(columnValues);
                            return cursor;
                        }
                        return null;
                    } else if (action == 1) {
                        final String key = paths.get(1);
                        if (sp.contains(key)) {
                            final MatrixCursor cursor = new MatrixCursor(new String[]{"v"}
                                    , 1);
                            cursor.addRow(new Object[]{sp.getString(paths.get(1), "")});
                            return cursor;
                        }
                        return null;
                    } else if (action == 2) {
                        final String key = paths.get(1);
                        if (sp.contains(key)) {
                            final MatrixCursor cursor = new MatrixCursor(new String[]{"v"}
                                    , 1);
                            cursor.addRow(new Object[]{sp.getBoolean(paths.get(1), false) ? 1 : 0});
                            return cursor;
                        }
                        return null;
                    } else if (action == 3) {
                        final String key = paths.get(1);
                        if (sp.contains(key)) {
                            final MatrixCursor cursor = new MatrixCursor(new String[]{"v"}
                                    , 1);
                            cursor.addRow(new Object[]{sp.getFloat(paths.get(1), 0)});
                            return cursor;
                        }
                        return null;
                    } else if (action == 4) {
                        final String key = paths.get(1);
                        if (sp.contains(key)) {
                            final MatrixCursor cursor = new MatrixCursor(new String[]{"v"}
                                    , 1);
                            cursor.addRow(new Object[]{sp.getInt(paths.get(1), 0)});
                            return cursor;
                        }
                        return null;
                    } else if (action == 5) {
                        final String key = paths.get(1);
                        if (sp.contains(key)) {
                            final MatrixCursor cursor = new MatrixCursor(new String[]{"v"}
                                    , 1);
                            cursor.addRow(new Object[]{sp.getLong(paths.get(1), 0)});
                            return cursor;
                        }
                        return null;
                    } else if (action == 6) {
                        final String key = paths.get(1);
                        if (sp.contains(key)) {
                            final Set<String> sets = sp.getStringSet(key, null);
                            if (null != sets && 0 != sets.size()) {
                                final String[] columnNames = new String[sets.size()];
                                final Object[] columnValues = new Object[sets.size()];
                                int index = 0;
                                for (final String value : sets) {
                                    columnNames[index] = "v" + index;
                                    columnValues[index] = value;
                                    index++;
                                }
                                final MatrixCursor cursor = new MatrixCursor(columnNames
                                        , 1);
                                cursor.addRow(columnValues);
                                return cursor;
                            }
                        }
                        return null;
                    } else if (action == 7) {
                        final String key = paths.get(1);
                        if (sp.contains(key)) {
                            final MatrixCursor cursor = new MatrixCursor(new String[]{"v"}
                                    , 1);
                            cursor.addRow(new Object[]{1});
                            return cursor;
                        }
                        return null;
                    }
                    break;
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        try {
            lock.writeLock().lock();
            switch (uriMatcher.match(uri)) {
                case 0x888:
                    final List<String> paths = uri.getPathSegments();
                    final SharedPreferences sp = getContext()
                            .getSharedPreferences(paths.get(0), Context.MODE_PRIVATE);
                    sp.unregisterOnSharedPreferenceChangeListener(this);
                    sp.registerOnSharedPreferenceChangeListener(this);
                    final int action = Integer.parseInt(paths.get(2));
                    if (action == 1) {
                        final String key = paths.get(1);
                        if (sp.edit().putString(key, values.getAsString(key)).commit()) {
                            return uri;
                        }
                        return null;
                    } else if (action == 2) {
                        final String key = paths.get(1);
                        if (sp.edit().putBoolean(key, values.getAsBoolean(key)).commit()) {
                            return uri;
                        }
                        return null;
                    } else if (action == 3) {
                        final String key = paths.get(1);
                        if (sp.edit().putFloat(key, values.getAsFloat(key)).commit()) {
                            return uri;
                        }
                        return null;
                    } else if (action == 4) {
                        final String key = paths.get(1);
                        if (sp.edit().putInt(key, values.getAsInteger(key)).commit()) {
                            return uri;
                        }
                        return null;
                    } else if (action == 5) {
                        final String key = paths.get(1);
                        if (sp.edit().putLong(key, values.getAsLong(key)).commit()) {
                            return uri;
                        }
                        return null;
                    } else if (action == 6) {
                        final Set<String> sets = new HashSet<>();
                        for (final String key : values.keySet()) {
                            sets.add(values.getAsString(key));
                        }
                        final String key = paths.get(1);
                        if (sp.edit().putStringSet(key, sets).commit()) {
                            return uri;
                        }
                        return null;
                    }
                    break;
            }
            return null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        try {
            lock.writeLock().lock();
            final List<String> paths = uri.getPathSegments();
            final SharedPreferences sp = getContext()
                    .getSharedPreferences(paths.get(1), Context.MODE_PRIVATE);
            sp.unregisterOnSharedPreferenceChangeListener(this);
            sp.registerOnSharedPreferenceChangeListener(this);
            switch (uriMatcher.match(uri)) {
                case 0x666:
                    return sp.edit().remove(paths.get(2)).commit() ? 1 : 0;
                case 0x999:
                    return sp.edit().clear().commit() ? 1 : 0;
            }
        } finally {
            lock.writeLock().unlock();
        }
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values
            , @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences,
                                          final String key) {
        try {
            final Field field = sharedPreferences.getClass().getDeclaredField("mFile");
            field.setAccessible(true);
            final File file = (File) field.get(sharedPreferences);
            getContext().getContentResolver()
                    .notifyChange(Uri.parse(String.format("content://com.jove.mpsp.provider/%s/%s"
                            , file.getName().replaceAll(".xml", ""), key)), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
