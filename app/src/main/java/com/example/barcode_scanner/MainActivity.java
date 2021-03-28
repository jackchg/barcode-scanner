package com.example.barcode_scanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
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

    ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
        ProcessCameraProvider.getInstance (this);

    // This may not work
    LifecycleOwner lifecycleOwner = this;
    cameraProviderFuture.addListener (new Runnable ()
    {
      @Override
      public void run () {
        // Used to bind the lifecycle of cameras to the lifecycle owner
        ProcessCameraProvider cameraProvider = null;
        try
          {
            cameraProvider = cameraProviderFuture.get ();
          }
        catch (ExecutionException | InterruptedException exception)
          {
            exception.printStackTrace ();
          }

        // Preview
        PreviewView viewFinder = findViewById(R.id.viewFinder);
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(viewFinder.createSurfaceProvider());

        imageCapture = new ImageCapture.Builder ().build ();

        ImageAnalysis imageAnalyzer = new ImageAnalysis.Builder ().build ();
        imageAnalyzer.setAnalyzer (cameraExecutor,
                                   new LuminosityAnalyzer ());

        // Select back camera as a default
        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        try {
          // Unbind use cases before rebinding
          cameraProvider.unbindAll();

          // Bind use cases to camera
          cameraProvider.bindToLifecycle(lifecycleOwner,
                                         cameraSelector,
                                         preview,
                                         imageCapture,
                                         imageAnalyzer);
        } catch (Exception exception) {
          Log.e(singleton.getTag(),
              "Use case binding failed",
              exception);
        }
      }

    }, ContextCompat.getMainExecutor (this));

  }

  private void
  takePhoto ()
  {
    // Get a stable reference of the modifiable image capture use case
    if (imageCapture == null)
      {
        return;
      }

    // Create a time-stamepd output file to hold the image
    SimpleDateFormat dateFormat =
        new SimpleDateFormat(singleton.getFilenameFormat(),
                             Locale.US);
    String dateFormatString = dateFormat.format(System.currentTimeMillis ())
                              + ".jpg";
    File photoFile = new File (outputDirectory, dateFormatString);

    // Create output options object which contains file + metadata
    ImageCapture.OutputFileOptions outputOptions =
        new ImageCapture.OutputFileOptions.Builder (photoFile).build ();

    // Set up image capture listener, which is triggered after photo has
    // been taken
    imageCapture.takePicture (
        outputOptions,
        ContextCompat.getMainExecutor (this),
        new ImageCapture.OnImageSavedCallback ()
        {
          @Override
          public void
          onError (ImageCaptureException exception)
          {
            Log.e (singleton.getTag (),
                   "Photo capture failed: ${exception.message}",
                   exception);
          }

          @Override
          public void
          onImageSaved (ImageCapture.OutputFileResults output)
          {
            Uri savedUri = Uri.fromFile (photoFile);
            String msg = "Photo capture succeeded: " + savedUri.toString();
            Toast.makeText (getBaseContext(), msg, Toast.LENGTH_SHORT).show ();
            Log.d (singleton.getTag(), msg);
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
    cameraExecutor.shutdown();
  }

  @Override
  public void
  onRequestPermissionsResult (int requestCode,
                              String[] permissions,
                              int[] grantResults)
  {
    if (requestCode == singleton.getReqCodePerms ())
      {
        if (allPermissionsGranted ())
          {
            startCamera();
          }
        else
          {
            Toast.makeText (this,
                            "Permissions not granted by the user.",
                            Toast.LENGTH_SHORT).show();
            finish();
          }
      }
  }

  private class
  LuminosityAnalyzer implements ImageAnalysis.Analyzer {
    private LuminosityAnalyzer ()
    {

    }

    private byte[]
    toByteArray (ByteBuffer buffer)
    {
      buffer.rewind();
      byte[] data = new byte[buffer.remaining ()];
      buffer.get (data);
      return data;
    }

    @Override
    public void
    analyze (ImageProxy image)
    {
      ByteBuffer buffer = image.getPlanes()[0].getBuffer();
      byte[] data = toByteArray (buffer);
      byte[] pixels = new byte[data.length];
      double totalVal = 0;
      for (int i = 0; i < data.length; i++)
        {
          byte b = data[i];
          pixels[i] = (byte)(b & 0xFF);
          totalVal += pixels[i];
        }
      double luma = totalVal / pixels.length;
      Log.d (singleton.getTag(), "Average luminosity: " + luma);
      image.close();
    }
  }
}