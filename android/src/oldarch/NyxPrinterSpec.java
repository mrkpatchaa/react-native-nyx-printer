package com.nyxprinter;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

abstract class NyxPrinterSpec extends ReactContextBaseJavaModule {
  NyxPrinterSpec(ReactApplicationContext context) {
    super(context);
  }

  public abstract void multiply(double a, double b, Promise promise);
  public abstract void printText(String content, ReadableMap textFormat, Promise promise);
  public abstract void printBarcode(String content, double width, double height, Promise promise);
  public abstract void printQrCode(String content, double width, double height, Promise promise);
  public abstract void printBitmap(ReadableArray inputBytes, Promise promise);
}
