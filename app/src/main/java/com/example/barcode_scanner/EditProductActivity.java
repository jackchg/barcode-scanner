package com.example.barcode_scanner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class EditProductActivity extends AppCompatActivity
{
  private Singleton singleton = Singleton.getInstance ();

  @Override
  protected void
  onCreate (Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);
    setContentView (R.layout.activity_edit_product);
    singleton.setActivity (this);
  }

  @Override
  protected void
  onResume ()
  {
    super.onResume ();
    singleton.setActivity (this);
  }
}