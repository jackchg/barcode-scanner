package com.example.barcode_scanner;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Permissions {
  private
  Permissions ()
  {}

  public static void
  requestPermissions (Activity activity)
  {
    Singleton singleton = Singleton.getInstance();
    ActivityCompat.requestPermissions (activity,
                                       singleton.getReqPerms(),
                                       singleton.getReqCodePerms());
  }

  public static boolean
  allPermissionsGranted ()
  {
    Singleton singleton = Singleton.getInstance();
    boolean allGranted = true;
    for (String permission: singleton.getReqPerms ())
      {
        Activity activity = singleton.getActivity();
        Context context = activity.getBaseContext();
        int perm = ContextCompat.checkSelfPermission (context,
                                                      permission);
        boolean permGranted = perm == PackageManager.PERMISSION_GRANTED;
        allGranted = permGranted && allGranted;
      }
    return allGranted;
  }

  public static void
  onRequestPermissionsResult (int requestCode,
                              String[] permissions,
                              int[] grantResults)
  {
    Singleton singleton = Singleton.getInstance();
    if (requestCode == singleton.getReqCodePerms ())
    {
      if (allPermissionsGranted ())
      {
        Camera.startCamera ();
      }
      else
      {
        Activity activity = singleton.getActivity ();
        Toast.makeText (activity,
            "Permissions not granted by the user.",
            Toast.LENGTH_SHORT).show();
        activity.finish();
      }
    }
  }
}
