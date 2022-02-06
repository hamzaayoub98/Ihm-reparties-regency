package com.example.ihm_reparties;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ekn.gruzer.gaugelibrary.HalfGauge;
import com.ekn.gruzer.gaugelibrary.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;
    List<OrdersApiResponse> orders = new ArrayList<>();
    private Context context = this;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1000;
    // Save state
    private Parcelable recyclerViewState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        sharedPref = this.getSharedPreferences("app", this.MODE_PRIVATE);
        // Lookup the recyclerview in activity layout
        RecyclerView rvOrders = (RecyclerView) findViewById(R.id.rvOrders);
        rvOrders.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        if(getIpv4PortAddress().length() < 10){
            Toast toast = Toast.makeText(getApplicationContext(), "No ipv4 and port address defined in the settings.", Toast.LENGTH_SHORT);
            toast.show();
//            return;
        }
        ApiInterface api = ServiceGenerator.createService(ApiInterface.class, getIpv4PortAddress());

        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);
                // Calling '/'
                Call<List<OrdersApiResponse>> callSync = api.getOrdersApiResponseCall();
                callSync.enqueue(new Callback<List<OrdersApiResponse>>() {
                    @Override
                    public void onResponse(Call<List<OrdersApiResponse>> call, Response<List<OrdersApiResponse>> response) {
                        orders = response.body();
                        Log.d("CallBack Orders", "Asking for orders !");
                        if(orders != null) {
                            Log.d("Orders", orders.toString());
                            // Create adapter passing in the sample user data
                            int random = new Random().nextInt(176);
                            OrdersAdapter adapter = new OrdersAdapter(orders, random);
                            // Attach the adapter to the recyclerview to populate items
                            rvOrders.setAdapter(adapter);
                            // Set layout manager to position the items
                            rvOrders.setLayoutManager(new LinearLayoutManager(context));


                        }

                    }

                    @Override
                    public void onFailure(Call<List<OrdersApiResponse>> call, Throwable t) {
                        Toast.makeText(OrdersActivity.this, "Orders callback failure",
                                Toast.LENGTH_SHORT).show();
                        Log.d("CallBackOrdersFailed", "Orders callback failure");
                    }
                });
            }
        }, delay);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, delay);
    }

    public String getIpv4PortAddress(){
        String ipv4 = sharedPref.getString(getString(R.string.ipv4), "");
        String port = sharedPref.getString(getString(R.string.port), "");
        String address = "http://" + ipv4 + ":" + port + "/";
        return address;
    }

}
