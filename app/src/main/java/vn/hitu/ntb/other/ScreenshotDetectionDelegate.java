package vn.hitu.ntb.other;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import vn.hitu.ntb.model.entity.ImageUpload;

public class ScreenshotDetectionDelegate {
    private final WeakReference<Activity> activityWeakReference;
    private final ScreenshotDetectionListener listener;

    public ScreenshotDetectionDelegate(Activity activityWeakReference, ScreenshotDetectionListener listener) {
        this.activityWeakReference = new WeakReference<>(activityWeakReference);
        this.listener = listener;
    }

    public void startScreenshotDetection() {
        activityWeakReference.get()
                .getContentResolver()
                .registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, contentObserver);
    }

    public void stopScreenshotDetection() {
        activityWeakReference.get().getContentResolver().unregisterContentObserver(contentObserver);
    }

    private final ContentObserver contentObserver = new ContentObserver(new Handler()) {
        @Override
        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
        }

        @RequiresApi(api = Build.VERSION_CODES.R)
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            if (isReadExternalStoragePermissionGranted()) {
                String path = getFilePathFromContentResolver(activityWeakReference.get(), uri);
                if (isScreenshotPath(path)) {
                    if (!path.contains("pending"))
                        onScreenCaptured(path);
                }
            } else {
                onScreenCapturedWithDeniedPermission();
            }
        }
    };

    private void onScreenCaptured(String path) {
        if (listener != null) {
            listener.onScreenCaptured(path);
        }
    }

    private void onScreenCapturedWithDeniedPermission() {
        if (listener != null) {
            listener.onScreenCapturedWithDeniedPermission();
        }
    }

    private boolean isScreenshotPath(String path) {
        return path != null;
    }

    private String getFilePathFromContentResolver(Context context, Uri uri) {
        try {
            Cursor cursor;
            cursor = context.getContentResolver().query(uri, new String[]{
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATA
            }, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                cursor.close();
                return path;
            }
        } catch (IllegalStateException ignored) {
        }
        return null;
    }

    private boolean isReadExternalStoragePermissionGranted() {
        return ContextCompat.checkSelfPermission(activityWeakReference.get(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public interface ScreenshotDetectionListener {
        void onScreenCaptured(String path);

        void onScreenCapturedWithDeniedPermission();
    }


    @SuppressLint("Range")
    public static ArrayList<String> getFilePathClipFromContentResolver(Context context) {
        try {
            Cursor cursor;
            cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DATE_TAKEN
            }, null, null, null);
            if (cursor != null) {
                ArrayList<String> path = new ArrayList<>();
                while (cursor.moveToNext()) {
                    Long time;
                    time = parseToNumber(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)));
                    if (time != null) {
                        long timeNow = (new Date().getTime() - time) / 1000 / 60;
                        if (timeNow <= 1) {
                            int status = 0;
                            for (String s : path) {
                                if (s.contains(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)))) {
                                    status = 1;
                                }
                            }
                            if (status != 1) {
                                path.add(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                            }
                        }
                    }
                }
                cursor.close();
                return path;
            }
        } catch (IllegalStateException ignored) {
        }
        return null;
    }

    @SuppressLint("Range")
    public static List<ImageUpload> getListImageFromLocal(Context context) {
        try {
            Cursor cursor;
            cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{
                    MediaStore.Images.Media.DATA
            }, null, null, null);
            if (cursor != null) {
                List<ImageUpload> path = new ArrayList<>();
                while (cursor.moveToNext()) {
                    int status = 0;
                    for (ImageUpload s : path) {
                        if (s.getImage().contains(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)))) {
                            status = 1;
                        }
                    }
                    if (status != 1) {
                        path.add(new ImageUpload(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))));
                    }
                }
                cursor.close();
                return path;
            }
        } catch (IllegalStateException ignored) {
        }
        return null;
    }
    @SuppressLint("Range")
    public static List<ImageUpload> getListVideoFromLocal(Context context) {
        try {
            Cursor cursor;
            cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{
                    MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.DURATION
            }, null, null, null);
            if (cursor != null) {
                List<ImageUpload> path = new ArrayList<>();
                while (cursor.moveToNext()) {
                    int status = 0;
                    for (ImageUpload s : path) {
                        if (s.getImage().contains(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)))) {
                            status = 1;
                        }
                    }
                    if (status != 1) {
                        path.add(new ImageUpload(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)), parseToNumber(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)))));
                    }
                }
                cursor.close();
                return path;
            }
        } catch (IllegalStateException ignored) {
        }
        return null;
    }

    private static Long parseToNumber(String receivedParam) {
        System.out.println("received param:" + receivedParam);
        if (receivedParam != null && !receivedParam.trim().isEmpty()) {
            try {
                return Long.parseLong(receivedParam.trim());
            } catch (NumberFormatException nfe) {
                System.out.println("received param is not a number");
                return null;
            }
        } else {
            System.out.println("received param is null or empty");
            return null;
        }
    }
}