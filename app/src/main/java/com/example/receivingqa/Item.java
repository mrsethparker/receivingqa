package com.example.receivingqa;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity (tableName = "item_table")
public class Item {

    @PrimaryKey(autoGenerate = true)
    private int mId;

    @ColumnInfo (name = "item_barcode")
    private String mBarcode;
    @ColumnInfo (name = "item_number")
    private String mItem;

    public Item(int id, @NonNull String barcode, @NonNull String item) {this.mId = id; this.mBarcode = barcode; this.mItem = item;}

    @Ignore
    public Item(@NonNull String barcode, @NonNull String item) {this.mBarcode = barcode; this.mItem = item;}

    public String getItem(){return this.mItem;}
    public String getBarcode(){return this.mBarcode;}
    public int getId(){return this.mId;}

}
