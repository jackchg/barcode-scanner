package barcode.scanner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

// import com.example.barcode_scanner.R;
import com.google.mlkit.vision.barcode.Barcode;

import barcode.scanner.R;

import java.io.IOException;
import java.text.NumberFormat;

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
        TextView barcode = findViewById (R.id.barcodeText);
        if (barcode.getText ().toString ().isEmpty ())
          {
            Toast.makeText (getApplicationContext(),
                      "Scan a valid barcode",
                           Toast.LENGTH_SHORT).show ();
          }
        else
          {
            Product product = createProduct ();
            BarcodeDatabase.addProduct (product);
            singleton.setProduct (product);
          }
      }
    });

    Button resetButton = findViewById (R.id.resetButton);
    resetButton.setOnClickListener (new View.OnClickListener ()
    {
      @Override
      public void
      onClick (View view)
      {
        TextView barcode = findViewById (R.id.barcodeText);
        if (barcode.getText ().toString ().isEmpty ())
        {
          Toast.makeText (getApplicationContext (),
              "Scan a valid barcode",
              Toast.LENGTH_SHORT).show ();
        }
        else
        {
          fillEditProductActivity ();
        }
      }
    });

    Button exportDBButton = findViewById (R.id.exportDBButton);
    exportDBButton.setOnClickListener (new View.OnClickListener ()
    {
      @Override
      public void
      onClick (View view)
      {
        try {
          BarcodeDatabase.exportDatabase(EditProductActivity.this);
          Toast.makeText(EditProductActivity.this,
                  "Successfully exported barcodes to: "
                          + Environment.DIRECTORY_DOWNLOADS + "/ directory.",
                  Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
          e.printStackTrace();
          Toast.makeText(EditProductActivity.this,
                  "Failed to export barcodes.",
                  Toast.LENGTH_SHORT).show();
        }
      }
    });
  }

  @Override
  protected void
  onResume ()
  {
    super.onResume ();
    singleton.setActivity (this);
    fillEditProductActivity();
  }

  private void
  fillEditProductActivity ()
  {
    Product product = singleton.getProduct ();
    EditText priceEditText = findViewById (R.id.priceEditText);

    /* Prevent the user from adding numbers other than the end of price.  */
    priceEditText.setCursorVisible (false);
    priceEditText.setOnClickListener(new View.OnClickListener ()
    {
      @Override
      public void onClick (View v)
      {
        priceEditText.setSelection (priceEditText.getText ().length ());
      }
    });

    TextView barcodeText = findViewById (R.id.barcodeText);
    String barcode;
    if (product == null)
      {
        /* Product was not found in database, so we only fill out barcode.  */
        barcode = singleton.getBarcode ();
        barcodeText.setText (barcode);
        if (priceEditText.getText ().toString ().equals (""))
          {
            /* Price is empty, set to 0.00.  */
            priceEditText.setText("0.00");
          }
        return;
      }

    barcode = product.getBarcode ();
    barcodeText.setText (barcode);

    EditText nameEditText = findViewById (R.id.nameEditText);
    String name = product.getName ();
    nameEditText.setText (name);

    Float price = product.getPrice ();
    NumberFormat formatter = NumberFormat.getCurrencyInstance ();
    String priceString = formatter.format (price).substring (1);
    priceEditText.setText (priceString);

    /* Add listener to price text to format it on user input.  */
    priceEditText.addTextChangedListener (new TextWatcher ()
    {
      @Override
      public void beforeTextChanged (CharSequence s,
                                     int start,
                                     int count,
                                     int after) {}
      @Override
      public void onTextChanged (CharSequence s,
                                 int start,
                                 int before,
                                 int count) {}

      @Override
      public void afterTextChanged (Editable s)
      {
        priceEditText.removeTextChangedListener (this);

        String price = formatToCurrency (s.toString ());
        priceEditText.setText (price);

        /* Set the cursor the end of the line.  */
        priceEditText.setSelection (priceEditText.getText ().length ());

        priceEditText.addTextChangedListener (this);
      }
    });

    CheckBox taxedCheckBox = findViewById (R.id.taxedCheckBox);
    Boolean taxed = product.isTaxed ();
    taxedCheckBox.setChecked (taxed);

    CheckBox crvCheckBox = findViewById (R.id.crvCheckBox);
    Boolean crv = product.isCrv ();
    crvCheckBox.setChecked (crv);

    EditText notesEditText = findViewById (R.id.notesEditText);
    String notes = product.getNotes ();
    notesEditText.setText (notes);
  }

  private Product
  createProduct ()
  {
    TextView barcodeText = findViewById (R.id.barcodeText);
    String barcode = barcodeText.getText ().toString ();

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

  private String
  formatToCurrency (String price)
  {
    if (price.length () == 0)
      {
        /* Happens when user long-presses backspace.  */
        price = "0.00";
      }
    if (price.length () - 1 == 2)
      {
        /* Price is only in cents.  */
        price = "0." + price.replace(".", "");
      }
    else if (price.length () - 1 >= 4 && price.startsWith ("0"))
      {
        /* Trim off leading zero.  */
        price = price.replace (".", "");
        price = price.charAt (1) + "." + price.substring (2);
      }
    else
      {
        price = price.replace (".", "");
        String beginning = price.substring (0, price.length () - 2);
        String end = price.substring (price.length () - 2);
        price = beginning + "." + end;
      }
    return price;
  }
}