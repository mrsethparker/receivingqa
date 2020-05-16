package com.example.receivingqa;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

//TODO: update this code to use RecylerView
//TODO: should this code use the Room/ViewModel/Livedata architecture?
//TODO: complete code documentation
//Display a list of items from a Google Spreadsheet
public class ListItem extends AppCompatActivity {

    String sheet;
    ListView listView;
    ListAdapter adapter;
    ProgressDialog loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item);

        //Figure out the name of the button that brought us here, it'll be the sheet name.
        Intent intent = getIntent();
        sheet = intent.getStringExtra("SheetName");

        //Set the action bar title to the sheet name from above
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(sheet);

        listView = (ListView) findViewById(R.id.lv_items);

        getItems();

    }


    private void getItems() {

        loading =  ProgressDialog.show(this,"Loading","please wait",false,true);

        String action;
        String SheetUrl = BuildConfig.SHEET_URL + "?action=";

        switch (sheet){
            case "Freight List": action = "getFreightItems";
            break;

            case "Ground List": action = "getGroundItems";
            break;

            case "Container List": action = "getContainerItems";
            break;

            case "AA Wall List": action = "getAaWallItems";
            break;

            default: action = "unknown";
            break;
        }

        SheetUrl += action;

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


    private void parseItems(String jsonResponse) {

        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        try {
            JSONObject jobj = new JSONObject(jsonResponse);
            JSONArray jarray = jobj.getJSONArray("items");


            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jo = jarray.getJSONObject(i);

                String palletNumber = jo.getString("palletNumber").toUpperCase();
                String po = jo.getString("PO");
                String itemNumber = jo.getString("itemNumber").toUpperCase();
                String quantity = jo.getString("quantity");
                String qa = jo.getString("qa").toUpperCase();


                HashMap<String, String> item = new HashMap<>();
                item.put("palletNumber", palletNumber);
                item.put("PO", po);
                item.put("itemNumber", itemNumber);
                item.put("quantity", quantity);
                item.put("qa",qa);

                list.add(0,item);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        adapter = new SimpleAdapter(this,list,R.layout.list_item_row,
                new String[]{"palletNumber","PO","itemNumber","quantity","qa"},new int[]{R.id.tvPalletNumber,R.id.tvPO,R.id.tvItemNumber,R.id.tvQuantity,R.id.tvQA});


        listView.setAdapter(adapter);
        loading.dismiss();
    }


}
