package com.example.barcode_scanner;

import android.Manifest;
import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Singleton
{
  private static Singleton instance = new Singleton();

  Activity activity;
  File outputDirectory;
  ExecutorService cameraExecutor;

  static String TAG;
  String FILENAME_FORMAT;
  int REQUEST_CODE_PERMISSIONS;
  String[] REQUIRED_PERMISSIONS;

  private SQLiteDatabase barcodeDatabase;
  String barcode;
  Queue<String> barcodeQueue;
  Product product;

  private
  Singleton ()
  {
    TAG = "CameraXBasic";
    FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    REQUEST_CODE_PERMISSIONS = 10;
    REQUIRED_PERMISSIONS = new String[] {Manifest.permission.CAMERA};
    cameraExecutor = null;
    barcode = "";
    barcodeQueue = new LinkedList<>();
  }

  public static Singleton getInstance () { return instance; }
  public String[] getReqPerms () { return REQUIRED_PERMISSIONS; }
  public int getReqCodePerms () { return REQUEST_CODE_PERMISSIONS; }
  public String getTag () { return TAG; }
  public String getFilenameFormat () { return FILENAME_FORMAT; }
  public Activity getActivity () { return activity; }
  public File getOutputDirectory () { return outputDirectory; }
  public Queue<String> getBarcodeQueue () { return barcodeQueue; }

  /**
   * Returns the Product object currently referenced by the barcode scanner, or
   * null if there is no Product object for that barcode or no barcode being
   * scanned.
   * @return The Product object for a barcode, or null.
   */
  public Product getProduct () { return product; }

  public String getBarcode () { return barcode; }
  public void setActivity (Activity activity) { this.activity = activity; }

  public ExecutorService
  getCameraExecutor ()
  {
    if (cameraExecutor != null)
      {
        cameraExecutor.shutdown();
      }
    /* Need to create a new camera executor because the activity is recreated
     * when switching orientation.  */
    cameraExecutor = Executors.newSingleThreadExecutor();
    return cameraExecutor;
  }

  public SQLiteDatabase
  getBarcodeDatabase ()
  {
    if (barcodeDatabase == null)
      {
        barcodeDatabase = BarcodeDatabase.retrieveDatabase ();
      }
    return barcodeDatabase;
  }

  public void
  setOutputDirectory (File outputDirectory)
  {
    this.outputDirectory = outputDirectory;
  }

  public void setProduct (Product product) { this.product = product; }
  public void setBarcode (String barcode) { this.barcode = barcode; }
}
