package com.nyxprinter;

import static com.nyxprinter.Result.msg;
import static com.nyxprinter.Utils.readableArrayToByteStringArray;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

import net.nyx.printerservice.print.IPrinterService;
import net.nyx.printerservice.print.PrintTextFormat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NyxPrinterModule extends com.nyxprinter.NyxPrinterSpec {
  public static final String NAME = "NyxPrinter";

  private final Context mContext;
  String[] version = new String[1];

  NyxPrinterModule(ReactApplicationContext context) {
    super(context);
    mContext = context.getApplicationContext();
    startService();
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  private void printStackE(Exception e) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
  }

  private final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
  private IPrinterService printerService;
  private final ServiceConnection connService = new ServiceConnection() {
    @Override
    public void onServiceDisconnected(ComponentName name) {
      showLog("printer service disconnected, try reconnect");
      printerService = null;
      new Handler(mContext.getMainLooper()).postDelayed(() -> startService(), 5000);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      showLog("onServiceConnected: " + name);
      printerService = IPrinterService.Stub.asInterface(service);
      getVersion();
    }
  };

  private void startService() {
    Intent intent = new Intent();
    intent.setPackage("net.nyx.printerservice");
    intent.setAction("net.nyx.printerservice.IPrinterService");
    mContext.bindService(intent, connService, Context.BIND_AUTO_CREATE);
    showLog("startService");
  }

  private void stopService() {
    mContext.unbindService(connService);
  }

  private void executeTask(Runnable task) {
    executeTask(task, new Promise() {
      @Override
      public void resolve(@Nullable Object o) {

      }

      @Override
      public void reject(String s, String s1) {

      }

      @Override
      public void reject(String s, Throwable throwable) {

      }

      @Override
      public void reject(String s, String s1, Throwable throwable) {

      }

      @Override
      public void reject(Throwable throwable) {

      }

      @Override
      public void reject(Throwable throwable, WritableMap writableMap) {

      }

      @Override
      public void reject(String s, @NonNull WritableMap writableMap) {

      }

      @Override
      public void reject(String s, Throwable throwable, WritableMap writableMap) {

      }

      @Override
      public void reject(String s, String s1, @NonNull WritableMap writableMap) {

      }

      @Override
      public void reject(String s, String s1, Throwable throwable, WritableMap writableMap) {

      }

      @Override
      public void reject(String s) {

      }
    });
  }
  private void executeTask(Runnable task, Promise promise) {
    Future<?> future = singleThreadExecutor.submit(task);
    try {
      future.get();
    } catch (ExecutionException | InterruptedException e) {
      //Exception rootException = e.getCause();
      promise.reject(e);
    }
  }

  private void getVersion() {
    executeTask(new Runnable() {
      @Override
      public void run() {
        try {
          int ret = printerService.getPrinterVersion(version);
          showLog("Version: " + msg(ret) + "  " + version[0]);
        } catch (RemoteException e) {
          printStackE(e);
        }
      }
    });
  }

  private void showLog(String s) {
    Log.d("NyxPrinterModule", s);
  }

  private void paperOut() {
    executeTask(new Runnable() {
      @Override
      public void run() {
        try {
          printerService.paperOut(80);
        } catch (RemoteException e) {
          printStackE(e);
        }
      }
    });
  }

  private void paperOutText(int size) {
    executeTask(new Runnable() {
      @Override
      public void run() {
        try {
          printerService.paperOut(size);
        } catch (RemoteException e) {
          printStackE(e);
        }
      }
    });
  }

  @ReactMethod
  public void printText(String content, ReadableMap textFormat, Promise promise) {
    executeTask(new Runnable() {
      @Override
      public void run() {
        try {
          PrintTextFormat printTextFormat = new PrintTextFormat();
          printTextFormat.setAli(textFormat.getInt("align"));
          printTextFormat.setTextSize(textFormat.getInt("textSize"));
          printTextFormat.setUnderline(textFormat.getBoolean("underline"));
          printTextFormat.setTextScaleX((float)textFormat.getDouble("textScaleX"));
          printTextFormat.setTextScaleY((float)textFormat.getDouble("textScaleY"));
          printTextFormat.setLetterSpacing((float)textFormat.getDouble("letterSpacing"));
          printTextFormat.setLineSpacing((float)textFormat.getDouble("lineSpacing"));
          printTextFormat.setTopPadding(textFormat.getInt("topPadding"));
          printTextFormat.setLeftPadding(textFormat.getInt("leftPadding"));
          printTextFormat.setStyle(textFormat.getInt("style"));
          printTextFormat.setFont(textFormat.getInt("font"));
          int ret = printerService.printText(content, printTextFormat);
          showLog("Print text: " + msg(ret));
          if (ret == 0) {
            paperOut();
              //paperOutText(textFormat.getInt("textSize"));
          }
          promise.resolve(ret);
        } catch (RemoteException e) {
          printStackE(e);
          promise.reject(e);
        }
      }
    }, promise);
  }

  @ReactMethod
  public void printBarcode(String content, double width, double height, Promise promise) {
    executeTask(new Runnable() {
      @Override
      public void run() {
        try {
          int ret = printerService.printBarcode(content, (int) width, (int) height, 1, 1);
          showLog("Print barcode: " + msg(ret));
          if (ret == 0) {
            paperOut();
          }
          promise.resolve(ret);
        } catch (RemoteException e) {
          printStackE(e);
          promise.reject(e);
        }
      }
    }, promise);
  }

  @ReactMethod
  public void printQrCode(String content, double width, double height, Promise promise) {
    executeTask(new Runnable() {
      @Override
      public void run() {
        try {
          int ret = printerService.printQrCode(content, (int) width, (int) height, 1);
          showLog("Print qrCode: " + msg(ret));
          if (ret == 0) {
            paperOut();
          }
          promise.resolve(ret);
        } catch (RemoteException e) {
          printStackE(e);
          promise.reject(e);
        }
      }
    }, promise);
  }

  @ReactMethod
  public void printBitmap(ReadableArray inputBytes, Promise promise) {
    executeTask(new Runnable() {
      @Override
      public void run() {
        try {
          ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(readableArrayToByteStringArray(inputBytes));
          Bitmap decoded = BitmapFactory.decodeStream(arrayInputStream);
          ByteArrayOutputStream stream = new ByteArrayOutputStream();
          decoded.compress(Bitmap.CompressFormat.PNG, 100, stream);
          int ret = printerService.printBitmap(decoded, 1, 1);
          showLog("Print bitmap: " + msg(ret));
          if (ret == 0) {
            paperOut();
          }
          promise.resolve(ret);
        } catch (Exception e) {
          printStackE(e);
          promise.reject(e);
        }
      }
    }, promise);
  }

  @ReactMethod
  public void printLabel() {
    singleThreadExecutor.submit(new Runnable() {
      @Override
      public void run() {
        try {
          int ret = printerService.labelLocate(240, 16);
          if (ret == 0) {
            PrintTextFormat format = new PrintTextFormat();
            printerService.printText("\nModel:\t\tNB55", format);
            printerService.printBarcode("1234567890987654321", 320, 90, 2, 0);
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            printerService.printText("Time:\t\t" + date, format);
            ret = printerService.labelPrintEnd();
          }
          showLog("Print label: " + msg(ret));
        } catch (Exception e) {
          printStackE(e);
        }
      }
    });
  }

  private void printLabelLearning(Promise promise) {
    if (version[0] != null && Float.parseFloat(version[0]) < 1.10) {
      // showLog(getString(R.string.res_not_support));
      promise.reject("res_not_support");
      return;
    }
    singleThreadExecutor.submit(new Runnable() {
      @Override
      public void run() {
        int ret = 0;
        try {
          if (!printerService.hasLabelLearning()) {
            // label learning
            ret = printerService.labelDetectAuto();
          }
          if (ret == 0) {
            ret = printerService.labelLocateAuto();
            if (ret == 0) {
              PrintTextFormat format = new PrintTextFormat();
              printerService.printText("\nModel:\t\tNB55", format);
              printerService.printBarcode("1234567890987654321", 320, 90, 2, 0);
              String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
              printerService.printText("Time:\t\t" + date, format);
              printerService.labelPrintEnd();
            }
          }
        } catch (RemoteException e) {
          printStackE(e);
        }
        showLog("Label learning print: " + msg(ret));
      }
    });
  }

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  public void multiply(double a, double b, Promise promise) {
    promise.resolve(a * b);
  }
}
