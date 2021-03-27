package com.example.barcode_scanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageCapture;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class
MainActivity extends AppCompatActivity
{
  /* Most code taken from
   * https://codelabs.developers.google.com/codelabs/camerax-getting-started#1
   * but translated somewhat into Java
   */
  private Singleton singleton = Singleton.getInstance();

  private ImageCapture imageCapture = null;

  // Neither should be null when accessing
  private File outputDirectory = null;
  private ExecutorService cameraExecutor = null;

  @Override
  protected void
  onCreate (Bundle savedInstanceState)
  {
    super.onCreate (savedInstanceState);
    setContentView (R.layout.activity_main);

    // Request all camera permissions
    if (allPermissionsGranted ())
      {
        startCamera ();
      }
    else
      {
        ActivityCompat.requestPermissions (this,
                                          singleton.getReqPerms(),
                                          singleton.getReqCodePerms());
      }

    // Set up the listener for the take photo button
    Button cameraCaptureButton = findViewById (R.id.camera_capture_button);
    cameraCaptureButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        takePhoto ();
      }
    });

    outputDirectory = getOutputDirectory ();

    cameraExecutor = Executors.newSingleThreadExecutor();
  }

  private boolean
  allPermissionsGranted ()
  {
    boolean allGranted = true;
    for (String permission: singleton.getReqPerms())
      {
        int perm = ContextCompat.checkSelfPermission (getBaseContext(),
                                                      permission);
        boolean permGranted = perm == PackageManager.PERMISSION_GRANTED;
        allGranted = permGranted && allGranted;
      }
    return allGranted;
  }

  private void
  startCamera ()
  {
    Toast toast = Toast.makeText(this,
                                 "Starting Camera",
                                 Toast.LENGTH_SHORT);
    toast.show();
  }

  private void
  takePhoto ()
  {
    Toast toast = Toast.makeText(this,
                            "Taking Photo",
                                 Toast.LENGTH_SHORT);
    toast.show();
  }

  private File
  getOutputDirectory ()
  {
    String appName = getResources().getString(R.string.app_name);
    File[] mediaDirs = getExternalMediaDirs();
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
    cameraExecutor.shutdown();
  }
}