package com.example.ihm_reparties;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private Button buttonCallApi;
    List<OrdersApiResponse> orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        sharedPref = this.getSharedPreferences("app", this.MODE_PRIVATE);

        // Lookup the recyclerview in activity layout
        RecyclerView rvOrders = (RecyclerView) findViewById(R.id.rvOrders);

        if(getIpv4PortAddress().length() < 10){
            Toast toast = Toast.makeText(getApplicationContext(), "No ipv4 and port address defined in the settings.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        ApiInterface api = ServiceGenerator.createService(ApiInterface.class, getIpv4PortAddress());

        // Calling '/'
        Call<List<OrdersApiResponse>> callSync = api.getOrdersApiResponseCall();
        callSync.enqueue(new Callback<List<OrdersApiResponse>>() {
            @Override
            public void onResponse(Call<List<OrdersApiResponse>> call, Response<List<OrdersApiResponse>> response) {
                orders = response.body();
            }

            @Override
            public void onFailure(Call<List<OrdersApiResponse>> call, Throwable t) {

            }
        });


        // Create adapter passing in the sample user data
        OrdersAdapter adapter = new OrdersAdapter(orders);
        // Attach the adapter to the recyclerview to populate items
        rvOrders.setAdapter(adapter);
        // Set layout manager to position the items
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        // That's all!
    }

    public String getIpv4PortAddress(){
        String ipv4 = sharedPref.getString(getString(R.string.ipv4), "");
        String port = sharedPref.getString(getString(R.string.port), "");
        String address = "http://" + ipv4 + ":" + port + "/";
        return address;
    }

}
