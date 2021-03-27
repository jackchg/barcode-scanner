package com.example.barcode_scanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageCapture;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
  private ImageCapture imageCapture = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }
}