package com.example.barcode_scanner;

import android.Manifest;

import java.util.Arrays;
import java.util.List;

public class Singleton {
  private static Singleton instance = new Singleton();
  static String TAG;
  String FILENAME_FORMAT;
  int REQUEST_CODE_PERMISSIONS;
  String[] REQUIRED_PERMISSIONS;

  String barcode;

  private
  Singleton ()
  {
    TAG = "CameraXBasic";
    FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    REQUEST_CODE_PERMISSIONS = 10;
    REQUIRED_PERMISSIONS = new String[] {Manifest.permission.CAMERA};
  }

  public static Singleton getInstance () { return instance; }
  public String[] getReqPerms () { return REQUIRED_PERMISSIONS; }
  public int getReqCodePerms () { return REQUEST_CODE_PERMISSIONS; }
  public String getTag () { return TAG; }
  public String getFilenameFormat () { return FILENAME_FORMAT; }
  public void setBarcode (String barcode) { this.barcode = barcode; System.out.println(barcode); }


}
