package com.example.barcode_scanner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

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
        Product product = createProduct ();
        BarcodeDatabase.addProduct (product);
        singleton.setProduct (product);
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
  createProduct ()
  {
    EditText barcodeEditText = findViewById (R.id.barcodeEditText);
    String barcode = barcodeEditText.getText ().toString ();

    EditText nameEditText = findViewById (R.id.nameEditText);
    String name = nameEditText.getText ().toString ();

    EditText priceEditText = findViewById (R.id.priceEditText);
    Float price = Float.valueOf (priceEditText.getText ().toString ());

    CheckBox taxedCheckBox = findViewById (R.id.taxedCheckBox);
    Boolean taxed = taxedCheckBox.isChecked ();

    CheckBox crvCheckBox = findViewById (R.id.crvCheckBox);
    Boolean crv = crvCheckBox.isChecked ();

    EditText notesEditText = findViewById (R.id.notesEditText);
    String notes = notesEditText.getText ().toString ();

    Product product = new Product (barcode, name, price, taxed, crv, notes);
    return product;
  }
}