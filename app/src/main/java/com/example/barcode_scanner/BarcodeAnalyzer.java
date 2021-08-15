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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class
BarcodeAnalyzer implements ImageAnalysis.Analyzer
{

  public
  BarcodeAnalyzer ()
  {
  }

  @Override
  @ExperimentalGetImage
  public void
  analyze (ImageProxy imageProxy)
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
              public void onSuccess (List<Barcode> barcodes)
              {
                for (Barcode barcode: barcodes)
                {
                  // Rect bounds = barcode.getBoundingBox ();
                  // Point[] corners = barcode.getCornerPoints ();
                  String barcodeString = barcode.getRawValue ();
                  obtainBarcodeInformation(barcodeString);
                }
              }
            })
            .addOnFailureListener (new OnFailureListener ()
            {
              @Override
              public void onFailure (@NonNull Exception e)
              {
                Activity activity = singleton.getActivity();
                Toast toast = Toast.makeText (activity,
                                         "Failed to recognize",
                                              Toast.LENGTH_SHORT);
                toast.show ();
              }
            })
            .addOnCompleteListener (new OnCompleteListener<List<Barcode>> ()
            {
              @Override
              public void onComplete (@NonNull Task<List<Barcode>> task)
              {
                imageProxy.close ();
              }
            });
      }
  }

  /**
   * Given a barcode value as a String, will add it to the queue of barcodes
   * scanned in previous frames. When the queue is full (decided by
   * BARCODE_QUEUE_LIMIT), the information for the most frequently occurring
   * barcode will populating the ScanningActivity. Prevents stuttering from
   * the MLKit Barcode Scanner library.
   * @param barcodeString Barcode value as a String
   */
  private void
  obtainBarcodeInformation (String barcodeString)
  {
    final int BARCODE_QUEUE_LIMIT = 30;
    Singleton singleton = Singleton.getInstance ();
    Queue<String> barcodeQueue = singleton.getBarcodeQueue();
    barcodeQueue.add (barcodeString);
    if (barcodeQueue.size() >= BARCODE_QUEUE_LIMIT)
      {
        String mostCommonBarcode = mostCommonBarcode ();
        singleton.setBarcode (mostCommonBarcode);
        Product product = BarcodeDatabase.getProduct (barcodeString);
        singleton.setProduct (product);

        if (ScanningActivity.active)
          {
            /* Populate the scanning activity.  */
            ScanningActivity activity =
                (ScanningActivity) singleton.getActivity ();
            activity.fillProductInformation ();
        }
      }

  }

  /**
   * Sets the product shown on ScanningActivity to the most often seen barcode,
   * preventing the barcode from changing erratically with camera errors. Used
   * in obtainBarcodeInformation().
   */
  private String
  mostCommonBarcode ()
  {
    Singleton singleton = Singleton.getInstance ();
    Queue<String> barcodeQueue = singleton.getBarcodeQueue ();
    Map<String, Integer> barcodeFrequency = new HashMap<>();

    while (!barcodeQueue.isEmpty ())
      {
        /* Sum up all occurrences of each barcode.  */
        String barcode = barcodeQueue.remove ();

        /* Set frequency to 0 if not yet in the map.  */
        Integer frequency = barcodeFrequency.getOrDefault (barcode,
                                                0);
        barcodeFrequency.put (barcode, frequency + 1);
      }

    /* Identify most frequent barcode.  */
    String mostFrequentBarcode = "";
    Integer mostFrequentBarcodeValue = 0;
    for (Map.Entry<String, Integer> entry: barcodeFrequency.entrySet ())
      {
        if (mostFrequentBarcode.isEmpty ())
          {
            mostFrequentBarcode = entry.getKey ();
            mostFrequentBarcodeValue = entry.getValue ();
          }
        else
          {
            if (entry.getValue () > mostFrequentBarcodeValue)
              {
                mostFrequentBarcode = entry.getKey ();
                mostFrequentBarcodeValue = entry.getValue ();
              }
          }
      }

    return mostFrequentBarcode;
  }
}
