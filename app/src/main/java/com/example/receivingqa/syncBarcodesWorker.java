package com.example.receivingqa;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

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
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.Future;

//TODO: complete code documentation

public class syncBarcodesWorker extends Worker {

    String logMessage;

    public syncBarcodesWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        // Do the work here
        getItems();
        // Indicate whether the task finished successfully with the Result
        return Result.retry();
    }

    //TODO: the remainder of this code is almost identical to the code in ListItem.java. Can we do something about this?
    private void getItems() {

        String SheetUrl = BuildConfig.SHEET_URL + "?action=getBarcodes";

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

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(stringRequest);

    }

    private void parseItems(String jsonResponse) {


        //TODO: improve insert operation. should be able to perform a single insert with an array of Items. That way we won't have to loop over everything twice.
        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        try {
            JSONObject jobj = new JSONObject(jsonResponse);
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

        try {
            Future future = ItemRoomDatabase.databaseWriteExecutor.submit(() -> {
                ItemDao itemDao = (ItemDao) ItemRoomDatabase.getDatabase(getApplicationContext()).itemDao();
                itemDao.getItemCount();
            });

            logMessage = Calendar.getInstance().getTime().toString() + ": There are " + future.get().toString() + " entries in the database.";
        } catch (Exception e) {

        }

        //Log.i("Message", logMessage);

        ItemRoomDatabase.databaseWriteExecutor.execute(() -> {
            ItemDao itemDao = (ItemDao) ItemRoomDatabase.getDatabase(getApplicationContext()).itemDao();
            itemDao.deleteAll();

            for (int i = 0; i < list.size(); i++) {
                Item item = new Item(list.get(i).get("barcode"), list.get(i).get("itemNumber"));
                itemDao.insert(item);
            }

        });

    }


}
