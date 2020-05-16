package com.example.receivingqa;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Item item);

    @Query("DELETE FROM item_table")
    void deleteAll();

    @Query("SELECT * FROM item_table WHERE item_barcode = :barcode")
    public Item getItemByBarcode(String barcode);

    @Query("SELECT COUNT(*) FROM item_table")
    public int getItemCount();
}
