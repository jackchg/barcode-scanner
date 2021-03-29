package com.example.barcode_scanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.util.concurrent.ExecutorService;

public class
ScanningActivity extends AppCompatActivity
{
  /* CameraX code mostly taken from
   * https://codelabs.developers.google.com/codelabs/camerax-getting-started#1
   * but translated into Java to the best possible extent.
   *
   * Barcode analyzer code taken from
   * https://developers.google.com/ml-kit/vision/barcode-scanning
   * to obtain barcode values.
   */
  private Singleton singleton = Singleton.getInstance();
  static boolean active = false;

  @Override
  protected void
  onCreate (Bundle savedInstanceState)
  {
    super.onCreate (savedInstanceState);
    setContentView (R.layout.activity_scanning);
    singleton.setActivity (this);
    singleton.setOutputDirectory (getOutputDirectory());

    // Request all camera permissions
    if (Permissions.allPermissionsGranted ())
      {
        Camera.startCamera ();
      }
    else
      {
        Permissions.requestPermissions (this);
      }

    // Set up the listener for the take photo button
    Button cameraCaptureButton = findViewById (R.id.cameraCaptureButton);
    cameraCaptureButton.setOnClickListener(new View.OnClickListener ()
    {
      @Override
      public void
      onClick (View view)
      {
        Camera.takePhoto ();
      }
    });

    Button editProductButton = findViewById (R.id.editProductButton);
    editProductButton.setOnClickListener (new View.OnClickListener ()
    {
      @Override
      public void
      onClick (View view)
      {
        Intent intent = new Intent (view.getContext(),
                                    EditProductActivity.class);
        startActivity (intent);
      }
    });
  }

  @Override
  protected void
  onStart ()
  {
    super.onStart ();
    active = true;
  }

  @Override
  protected void
  onResume ()
  {
    super.onResume ();
    singleton.setActivity (this);
  }

  @Override
  protected void
  onStop ()
  {
    super.onStop ();
    active = false;
  }

  private File
  getOutputDirectory ()
  {
    String appName = getResources().getString(R.string.app_name);
    File[] mediaDirs = getExternalMediaDirs();
    if (mediaDirs == null) return null;
    for (File mediaDir: mediaDirs)
      {
        // Not sure if this actually works
        File newFile = new File (mediaDir, appName);
        newFile.mkdirs ();
      }
    File mediaDir = null;
    if (mediaDirs.length > 0)
      {
        mediaDir = mediaDirs[0];
      }
    return mediaDir;
  }

  @Override
  protected void
  onDestroy ()
  {
    super.onDestroy();
    ExecutorService cameraExecutor = singleton.getCameraExecutor();
    cameraExecutor.shutdown ();
  }

  @Override
  public void
  onRequestPermissionsResult (int requestCode,
                              String[] permissions,
                              int[] grantResults)
  {
    Permissions.onRequestPermissionsResult (requestCode,
                                            permissions,
                                            grantResults);
  }
}