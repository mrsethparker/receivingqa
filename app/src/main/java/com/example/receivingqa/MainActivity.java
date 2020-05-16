package com.example.receivingqa;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Our main screen buttons
    Button buttonQaFreightItem,
            buttonQaGroundItem,
            buttonQaContainerItem,
            buttonQaAaWallItem,
            buttonListFreightItem,
            buttonListGroundItem,
            buttonListContainerItem,
            buttonListAaWallItem,
            buttonSync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find our buttons and set click listeners on them
        buttonQaFreightItem = (Button) findViewById(R.id.btn_qa_freight_item);
        buttonQaGroundItem = (Button) findViewById(R.id.btn_qa_ground_item);
        buttonQaContainerItem = (Button) findViewById(R.id.btn_qa_container_item);
        buttonQaAaWallItem = (Button) findViewById(R.id.btn_qa_aaWall_item);

        buttonListFreightItem = (Button) findViewById(R.id.btn_list_freight_items);
        buttonListGroundItem = (Button) findViewById(R.id.btn_list_ground_items);
        buttonListContainerItem = (Button) findViewById(R.id.btn_list_container_items);
        buttonListAaWallItem = (Button) findViewById(R.id.btn_list_aawall_items);

        buttonSync = (Button) findViewById(R.id.btn_sync);

        buttonQaFreightItem.setOnClickListener(this);
        buttonQaGroundItem.setOnClickListener(this);
        buttonQaContainerItem.setOnClickListener(this);
        buttonQaAaWallItem.setOnClickListener(this);

        buttonListFreightItem.setOnClickListener(this);
        buttonListGroundItem.setOnClickListener(this);
        buttonListContainerItem.setOnClickListener(this);
        buttonListAaWallItem.setOnClickListener(this);
        buttonSync.setOnClickListener(this);

    }

    //A button was clicked so figure out which one. Start an activity and pass in the proper button name
    @Override
    public void onClick(View v) {

        if (v == buttonQaFreightItem) {

            Intent intent = new Intent(getApplicationContext(), AddItem.class);
            intent.putExtra("SheetName", "Freight");
            startActivity(intent);
        }

        if (v == buttonQaGroundItem) {

            Intent intent = new Intent(getApplicationContext(), AddItem.class);
            intent.putExtra("SheetName", "Ground");
            startActivity(intent);
        }

        if (v == buttonQaContainerItem) {

            Intent intent = new Intent(getApplicationContext(), AddItem.class);
            intent.putExtra("SheetName", "Container");
            startActivity(intent);
        }

        if (v == buttonQaAaWallItem) {

            Intent intent = new Intent(getApplicationContext(), AddItem.class);
            intent.putExtra("SheetName", "AA Wall");
            startActivity(intent);
        }

        if (v == buttonListFreightItem) {

            Intent intent = new Intent(getApplicationContext(), ListItem.class);
            intent.putExtra("SheetName", "Freight List");
            startActivity(intent);
        }

        if (v == buttonListGroundItem) {

            Intent intent = new Intent(getApplicationContext(), ListItem.class);
            intent.putExtra("SheetName", "Ground List");
            startActivity(intent);
        }

        if (v == buttonListContainerItem) {

            Intent intent = new Intent(getApplicationContext(), ListItem.class);
            intent.putExtra("SheetName", "Container List");
            startActivity(intent);
        }

        if (v == buttonListAaWallItem) {

            Intent intent = new Intent(getApplicationContext(), ListItem.class);
            intent.putExtra("SheetName", "AA Wall List");
            startActivity(intent);
        }

        if (v == buttonSync) {

            OneTimeWorkRequest syncWorkRequest = new OneTimeWorkRequest.Builder(syncBarcodesWorker.class)
                    .build();
            WorkManager.getInstance(getApplicationContext()).enqueue(syncWorkRequest);
            //Intent intent = new Intent(getApplicationContext(),syncBarcodes.class);
            //intent.putExtra("SheetName", "Update Barcodes");
            //startActivity(intent);
        }

    }

}

