package com.example.barcode_scanner;

import android.Manifest;

import java.util.Arrays;
import java.util.List;

public class Singleton {
  private static Singleton instance = new Singleton();
  static String TAG;
  static String FILENAME_FORMAT;
  static int REQUEST_CODE_PERMISSIONS;
  static String[] REQUIRED_PERMISSIONS;

  private
  Singleton ()
  {
    TAG = "CameraXBasic";
    FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    REQUEST_CODE_PERMISSIONS = 10;
    REQUIRED_PERMISSIONS =
            Arrays.asList(Manifest.permission.CAMERA).toArray(new String[0]);
  }

  public static Singleton getInstance () { return instance; }
  public static String[] getReqPerms () { return REQUIRED_PERMISSIONS; }
  public static int getReqCodePerms () { return REQUEST_CODE_PERMISSIONS; }
  public static String getFilenameFormat () { return FILENAME_FORMAT; }


}
