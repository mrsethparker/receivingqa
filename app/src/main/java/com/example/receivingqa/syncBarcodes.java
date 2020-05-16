package com.example.receivingqa;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Future;

public class syncBarcodes extends AppCompatActivity {

    String sheet;
    ProgressDialog loading;
    TextView syncTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_barcodes);
        syncTextView = (TextView)findViewById(R.id.tv_sync);

        //Figure out the name of the button that brought us here, it'll be the sheet name.
        Intent intent = getIntent();
        sheet = intent.getStringExtra("SheetName");

        //Set the action bar title to the sheet name from above
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(sheet);

        getItems();
    }


    private void getItems() {

        loading = ProgressDialog.show(this, "Loading", "This will take a minute...", false, true);

        String SheetUrl = "https://script.google.com/macros/s/AKfycbyyCISwwePQcFhsWPiYXRh96BpI81pAUh8b1c-NJRNLDhHaUX5_/exec?action=getBarcodes";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, SheetUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseItems(response);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }


    private void parseItems(String jsonResposnce) {

        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        try {
            JSONObject jobj = new JSONObject(jsonResposnce);
            JSONArray jarray = jobj.getJSONArray("items");


            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jo = jarray.getJSONObject(i);

                String palletNumber = jo.getString("barcode").toUpperCase();
                String itemNumber = jo.getString("itemNumber").toUpperCase();

                HashMap<String, String> item = new HashMap<>();
                item.put("barcode", palletNumber);
                item.put("itemNumber", itemNumber);
                list.add(0, item);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ItemRoomDatabase.databaseWriteExecutor.execute(() -> {
            ItemDao itemDao = (ItemDao) ItemRoomDatabase.getDatabase(getApplicationContext()).itemDao();
            itemDao.deleteAll();

            for (int i = 0; i < list.size(); i++) {
                Item item = new Item(list.get(i).get("barcode"), list.get(i).get("itemNumber"));
                itemDao.insert(item);
            }

        });

        syncTextView.setText("Complete.");
        loading.dismiss();
    }
}
