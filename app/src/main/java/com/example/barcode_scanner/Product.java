package com.example.barcode_scanner;

import android.database.Cursor;

public class Product {
  String barcode;
  String name;
  Float price;
  Boolean taxed;
  Boolean crv;
  String notes;
  Integer _id;

  public
  Product (String barcode,
           String name,
           Float price,
           Boolean taxed,
           Boolean crv)
  {
    this.barcode = barcode;
    this.name = name;
    this.price = price;
    this.taxed = taxed;
    this.crv = crv;
    this.notes = "";
  }

  /**
   * Creates a product from the cursor that should point at a line in the
   * barcodes table. Undefined behavior if the cursor is not pointer at
   * a line in the barcodes table.
   * @param cursor Cursor object that should point at a line in the
   *               barcodes table.
   */
  public
  Product (Cursor cursor)
  {
    barcode = cursor.getString (0);
    name = cursor.getString (1);
    price = cursor.getFloat (2);
    taxed = cursor.getInt (3) == 1;
    crv = cursor.getInt (4) == 1;
    notes = cursor.getString (5);
    _id = cursor.getInt (6);
  }

  public String getBarcode () { return barcode; }
  public String getName () { return name; }
  public Float getPrice () { return price; };
  public Boolean isTaxed () { return taxed; }
  public Boolean isCrv () { return crv; }

  public void setBarcode (String barcode) { this.barcode = barcode; }
  public void setName (String name) { this.name = name; }
  public void setPrice (Float price) { this.price = price;}
  public void setTaxed (Boolean taxed) { this.taxed = taxed; }
  public void setCrv (Boolean crv) { this.crv = crv; }
}
