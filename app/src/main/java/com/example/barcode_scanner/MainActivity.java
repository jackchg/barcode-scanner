package com.example.barcode_scanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageInfo;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.io.File;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class
MainActivity extends AppCompatActivity
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

  @Override
  protected void
  onCreate (Bundle savedInstanceState)
  {
    super.onCreate (savedInstanceState);
    setContentView (R.layout.activity_main);
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
    Button cameraCaptureButton = findViewById (R.id.camera_capture_button);
    cameraCaptureButton.setOnClickListener(new View.OnClickListener ()
    {
      @Override
      public void
      onClick (View view)
      {
        Camera.takePhoto ();
      }
    });
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