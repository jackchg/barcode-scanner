package barcode.scanner;

import android.database.Cursor;

public class Product {
  private enum Column
  {
    /* NOTE: Keep enum in order of database columns because we use ordinal()
     * in the "Product (Cursor)" constructor.  */
    BARCODE,
    NAME,
    PRICE,
    TAXED,
    CRV,
    NOTES,
    TIMESTAMP,
    _ID
  }

  String barcode;
  String name;
  Float price;
  Boolean taxed;
  Boolean crv;
  String notes;
  Long timestamp;
  Integer _id;

  public
  Product (String barcode,
           String name,
           Float price,
           Boolean taxed,
           Boolean crv,
           String notes)
  {
    this.barcode = barcode;
    this.name = name;
    this.price = price;
    this.taxed = taxed;
    this.crv = crv;
    this.notes = notes;
  }

  /**
   * Creates a product from the cursor that should point at a line in the
   * barcodes table. Undefined behavior if the cursor is not pointing at
   * a line in the barcodes table.
   * @param cursor Cursor object that should point at a line in the
   *               barcodes table.
   */
  public
  Product (Cursor cursor)
  {
    barcode = cursor.getString (0);
    name = cursor.getString (Column.NAME.ordinal());
    price = cursor.getFloat (Column.PRICE.ordinal());
    taxed = cursor.getInt (Column.TAXED.ordinal()) == 1;
    crv = cursor.getInt (Column.CRV.ordinal()) == 1;
    notes = cursor.getString (Column.NOTES.ordinal());
    timestamp = cursor.getLong (Column.TIMESTAMP.ordinal());
    _id = cursor.getInt (Column._ID.ordinal());
  }

  public String getBarcode () { return barcode; }
  public String getName () { return name; }
  public Float getPrice () { return price; }
  public Boolean isTaxed () { return taxed; }
  public Boolean isCrv () { return crv; }
  public String getNotes () { return notes; }
  public Long getTimestamp () { return timestamp; }
  public Integer getId () { return _id; }

  public void setBarcode (String barcode) { this.barcode = barcode; }
  public void setName (String name) { this.name = name; }
  public void setPrice (Float price) { this.price = price;}
  public void setTaxed (Boolean taxed) { this.taxed = taxed; }
  public void setCrv (Boolean crv) { this.crv = crv; }
}
