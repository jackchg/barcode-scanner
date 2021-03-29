package com.example.barcode_scanner;

import android.app.Activity;
import android.media.Image;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageInfo;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;

public class
BarcodeAnalyzer implements ImageAnalysis.Analyzer
{
  public
  BarcodeAnalyzer ()
  {
  }

  @Override
  @ExperimentalGetImage
  public void analyze (ImageProxy imageProxy)
  {
    Singleton singleton = Singleton.getInstance ();
    Image mediaImage = imageProxy.getImage ();
    if (mediaImage != null)
    {
      ImageInfo imageInfo = imageProxy.getImageInfo();
      InputImage image =
          InputImage.fromMediaImage(mediaImage,
              imageInfo.getRotationDegrees());
      BarcodeScanner scanner = BarcodeScanning.getClient ();
      Task<List<Barcode>> result = scanner.process (image)
          .addOnSuccessListener(new OnSuccessListener<List<Barcode>>()
          {
            @Override
            public void onSuccess(List<Barcode> barcodes)
            {
              for (Barcode barcode: barcodes)
              {
                // Rect bounds = barcode.getBoundingBox ();
                // Point[] corners = barcode.getCornerPoints ();

                String barcodeString = barcode.getRawValue ();
                Product product = BarcodeDatabase.getProduct (barcodeString);
                singleton.setProduct (product);
                if (ScanningActivity.active)
                {
                  /* Populate the scanning activity.  */
                  Activity activity = singleton.getActivity();
                  TextView priceText = activity.findViewById(R.id.priceText);
                  priceText.setText(barcodeString);
                }
              }
            }
          })
          .addOnFailureListener(new OnFailureListener ()
          {
            @Override
            public void onFailure(@NonNull Exception e)
            {
              Activity activity = singleton.getActivity();
              Toast toast = Toast.makeText (activity,
                                       "Failed to recognize",
                                            Toast.LENGTH_SHORT);
              toast.show ();
            }
          })
          .addOnCompleteListener(new OnCompleteListener<List<Barcode>>()
          {
            @Override
            public void onComplete(@NonNull Task<List<Barcode>> task)
            {
              imageProxy.close();
            }
          });
    }
  }
}
