package com.example.barcode_scanner;

import android.app.Activity;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.camera2.internal.annotation.CameraExecutor;
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
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public class Camera {
  private static ImageCapture imageCapture = null;

  private
  Camera ()
  {}

  public static void
  startCamera ()
  {
    Singleton singleton = Singleton.getInstance();
    ExecutorService cameraExecutor = singleton.getCameraExecutor();
    Activity activity = singleton.getActivity();

    ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
        ProcessCameraProvider.getInstance (activity);

    // This may not work
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
        PreviewView viewFinder = activity.findViewById(R.id.viewFinder);
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(viewFinder.createSurfaceProvider());

        imageCapture = new ImageCapture.Builder ().build ();

        ImageAnalysis.Builder imageAnalyzerBuild =
            new ImageAnalysis.Builder ();
        imageAnalyzerBuild
            .setBackpressureStrategy (ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST);
        ImageAnalysis imageAnalyzer = imageAnalyzerBuild.build ();
        imageAnalyzer.setAnalyzer (cameraExecutor,
                                   new BarcodeAnalyzer());

        // Select back camera as a default
        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        try {
          // Unbind use cases before rebinding
          cameraProvider.unbindAll();

          LifecycleOwner lifecycleOwner =
              (LifecycleOwner) singleton.getActivity();

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
    }, ContextCompat.getMainExecutor (activity));
  }

  public static void
  takePhoto ()
  {
    Singleton singleton = Singleton.getInstance();
    // Get a stable reference of the modifiable image capture use case
    if (imageCapture == null)
    {
      return;
    }

    // Create a time-stamped output file to hold the image
    SimpleDateFormat dateFormat =
        new SimpleDateFormat(singleton.getFilenameFormat(),
            Locale.US);
    String dateFormatString = dateFormat.format(System.currentTimeMillis ())
        + ".jpg";
    File outputDirectory = singleton.getOutputDirectory ();
    File photoFile = new File (outputDirectory, dateFormatString);

    // Create output options object which contains file + metadata
    ImageCapture.OutputFileOptions outputOptions =
        new ImageCapture.OutputFileOptions.Builder (photoFile).build ();

    // Set up image capture listener, which is triggered after photo has
    // been taken
    Activity activity = singleton.getActivity();
    imageCapture.takePicture (
        outputOptions,
        ContextCompat.getMainExecutor (activity),
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
            Toast.makeText (activity.getBaseContext(),
                            msg,
                            Toast.LENGTH_SHORT).show ();
            Log.d (singleton.getTag(), msg);
          }
        });
  }
}
