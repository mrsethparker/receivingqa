package com.example.receivingqa;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

public class AddItem extends AppCompatActivity implements View.OnClickListener {

    //Set up all of our variables
    EditText editTextPalletNumber, editTextPO, editTextBarcode, editTextItemNumber, editTextQuantity, editTextQA;
    Button buttonAddItem, buttonLookupItem;
    String sheet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Figure out the name of the button that brought us here, it'll be the sheet name.
        Intent intent = getIntent();
        sheet = intent.getStringExtra("SheetName");
        setContentView(R.layout.add_item);

        //Set the action bar title to the sheet name from above
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(sheet);

        //Collect all of our editText fields
        editTextPalletNumber = (EditText) findViewById(R.id.etPalletNumber);
        editTextPO = (EditText) findViewById(R.id.etPO);
        editTextBarcode = (EditText) findViewById(R.id.etBarcode);
        editTextItemNumber = (EditText) findViewById(R.id.etItemNumber);
        editTextQuantity = (EditText) findViewById(R.id.etQuantity);
        editTextQA = (EditText) findViewById(R.id.etQA);

        //Grab our buttons and set click listeners
        buttonAddItem = (Button) findViewById(R.id.btn_add_item);
        buttonAddItem.setOnClickListener(this);
        buttonLookupItem = (Button) findViewById(R.id.btn_lookup_item);
        buttonLookupItem.setOnClickListener(this);
    }

    //Use HTTP Rest API calls to send the form data to our Google sheet
    private void addItemToSheet() {

        //Show a loading dialog while we work
        final ProgressDialog loading = ProgressDialog.show(this, "Adding Item", "Please wait");

        //Collect our field values
        final String palletNumber = editTextPalletNumber.getText().toString().trim();
        final String PO = editTextPO.getText().toString().trim();
        final String barcode = editTextBarcode.getText().toString().trim();
        final String itemNumber = editTextItemNumber.getText().toString().trim();
        final String QA = editTextQA.getText().toString().trim();
        final String quantity = editTextQuantity.getText().toString().trim();

        //Send our field values to the API
        StringRequest stringRequest = new StringRequest(Request.Method.POST, BuildConfig.SHEET_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //We're finished so show success and clear form fields. A few fields are left intact so we can reuse the values
                        loading.dismiss();
                        Toast.makeText(AddItem.this, response, Toast.LENGTH_LONG).show();
                        editTextQuantity.setText("");
                        editTextItemNumber.setText("");
                        editTextBarcode.setText("");

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                //Set up our parameters to pass to the API
                params.put("action", "addItem");
                params.put("palletNumber", palletNumber);
                params.put("PO", PO);
                params.put("barcode", barcode);
                params.put("QA", QA);
                params.put("itemNumber", itemNumber);
                params.put("quantity", quantity);
                params.put("sheetName", sheet);

                return params;
            }
        };

        int socketTimeOut = 10000; //Our socket time out value, here it is 10 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);

    }

    //A button was clicked so figure out which one and take action
    @Override
    public void onClick(View v) {

        //Add an item to our Google Sheet
        if (v == buttonAddItem) {
            addItemToSheet();
        }
        //Lookup an item number from the barcode field value
        else if (v == buttonLookupItem) {

            String barcodeText = editTextBarcode.getText().toString().trim().toUpperCase();

            //The barcode field was blank so we should say so
            if (barcodeText.equals("")) {
                editTextBarcode.setText("Enter a barcode");
            } else {
                try {
                    Future future = ItemRoomDatabase.databaseWriteExecutor.submit(() -> {
                        ItemDao itemDao = (ItemDao) ItemRoomDatabase.getDatabase(getApplicationContext()).itemDao();
                        return itemDao.getItemByBarcode(barcodeText).getItem();
                    });

                    editTextItemNumber.setText(future.get().toString());

                } catch (Exception e) {
                    //We didn't find the item number so we should say so
                    editTextItemNumber.setText("Not Found");
                }
            }

        }
    }
}
