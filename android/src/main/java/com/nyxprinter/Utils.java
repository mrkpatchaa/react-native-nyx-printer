package com.nyxprinter;

import android.annotation.SuppressLint;
import android.app.Application;

import com.facebook.react.bridge.ReadableArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yyzz on 2019/1/4.
 */
public class Utils {

    private static final String TAG = "Utils";

    private static Application sApplication;
    private static Utils sUtils;

    public static Utils getInstance() {
        if (sUtils == null)
            sUtils = new Utils();
        return sUtils;
    }

    public static Utils init(final Application app) {
        getInstance();
        if (sApplication == null) {
            if (app == null) {
                sApplication = getApplicationByReflect();
            } else {
                sApplication = app;
            }
        } else {
            if (app != null && app.getClass() != sApplication.getClass()) {
                sApplication = app;
            }
        }
        return sUtils;
    }

    public static Application getApp() {
        if (sApplication != null)
            return sApplication;
        Application app = getApplicationByReflect();
        init(app);
        return app;
    }

  /**
   * https://github.com/reneweb/react-native-tensorflow/blob/master/android/src/main/java/com/rntensorflow/converter/ArrayConverter.java
   * @param readableArray
   * @return
   */
    public static byte[] readableArrayToByteStringArray(ReadableArray readableArray) {
    List<Byte> bytes = new ArrayList<>(readableArray.size() * 5);
    for (int i = 0; i < readableArray.size(); i++) {
      for (byte b :readableArray.getString(i).getBytes()) {
        bytes.add(b);
      }
    }

    byte[] bytesArr = new byte[bytes.size()];
    for (int i = 0; i < bytes.size(); i++) {
      bytesArr[i] = bytes.get(i);
    }

    return bytesArr;
  }

    private static Application getApplicationByReflect() {
        try {
            @SuppressLint("PrivateApi")
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object thread = activityThread.getMethod("currentActivityThread").invoke(null);
            Object app = activityThread.getMethod("getApplication").invoke(thread);
            if (app == null) {
                throw new NullPointerException("u should init first");
            }
            return (Application) app;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new NullPointerException("u should init first");
    }
}
