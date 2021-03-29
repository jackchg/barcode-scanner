package com.example.barcode_scanner;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BarcodeDatabase
{
  private static final String DB_NAME = "barcode_scanner_database";
  private static final String SELECT_BARCODES = "SELECT * FROM sqlite_master "
                                                + "WHERE type='table' "
                                                + "AND name='barcodes';";
  private static final String CREATE_BARCODES_TABLE =
      "CREATE TABLE barcodes ("
      + " barcode TEXT, "
      + " name TEXT, "
      + " price REAL, "
      + " taxed INTEGER, "
      + " crv INTEGER, "
      + " notes TEXT, "
      + " _id INTEGER PRIMARY KEY AUTOINCREMENT"
      + " );";

  /*
   * Database Columns:
   * barcode TEXT | name TEXT | price REAL | taxed INTEGER |
   * crv INTEGER | notes TEXT | _id INTEGER PRIMARY KEY AUTOINCREMENT
   *
   * Note: Changing the barcode table requires changing the Product.java class.
   */
  private
  BarcodeDatabase ()
  {}

  public static SQLiteDatabase
  retrieveDatabase ()
  {
    Singleton singleton = Singleton.getInstance();
    Activity activity = singleton.getActivity();
    SQLiteDatabase database;
    database = activity.openOrCreateDatabase(DB_NAME,
                                             activity.MODE_PRIVATE,
                                     null);

    /* See if the barcodes table exists in the database.  */
    Cursor cursor;
    cursor = database.rawQuery (SELECT_BARCODES, null);
    if (cursor.getCount () == 0)
      {
        /* Barcodes table did not exist, so create the table.  */
        database.execSQL (CREATE_BARCODES_TABLE);
      }
    cursor.close();
    return database;
  }

  /**
   * Returns for a given barcode string in a Product object, or null
   * if the barcode does not have an associated product.
   * @param barcode Barcode text obtained the raw value of a barcode object.
   * @return Newly created Product object with the provided barcode, or null.
   */
  public static Product
  getProduct (String barcode)
  {
    Singleton singleton = Singleton.getInstance();
    SQLiteDatabase database = singleton.getBarcodeDatabase();
    Product product = null;

    String queryString = "SELECT FROM barcodes WHERE barcode=\""
                         + barcode
                         + "\";";
    Cursor cursor;
    cursor = database.rawQuery (queryString, null);
    if (cursor.getCount () != 0)
      {
        /* Product exists so create a new Product object.  */
        product = new Product (cursor);
      }
    return product;
  }

  public static void
  addProduct (Product product)
  {

  }
}
