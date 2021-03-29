package com.example.barcode_scanner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EditProductActivity extends AppCompatActivity
{
  private Singleton singleton = Singleton.getInstance ();

  @Override
  protected void
  onCreate (Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);
    setContentView (R.layout.activity_edit_product);
    singleton.setActivity (this);

    Button saveButton = findViewById (R.id.saveButton);
    saveButton.setOnClickListener (new View.OnClickListener ()
    {
      @Override
      public void
      onClick (View view)
      {
        Product product = saveProduct();
        // TODO: implement saving product to database, make sure to get timestamp!
        // TODO: After saving, set singleton to product
      }
    });

    Button resetButton = findViewById (R.id.resetButton);
    resetButton.setOnClickListener (new View.OnClickListener ()
    {
      @Override
      public void
      onClick (View view)
      {
        // TODO: Reset product values to original, should be stored in singleton
      }
    });
  }

  @Override
  protected void
  onResume ()
  {
    super.onResume ();
    singleton.setActivity (this);
  }

  private Product
  saveProduct ()
  {
    return null;
  }
}