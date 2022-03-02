package com.example.ihm_reparties;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ekn.gruzer.gaugelibrary.HalfGauge;
import com.ekn.gruzer.gaugelibrary.Range;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int LAYOUT_DEFAULT = 0;
    private final int LAYOUT_GAUGE = 1;
    private final int LAYOUT_PLUS = 2;
    private List<OrdersApiResponse> orders;
    private String restAddress;
    AddAntimatiere addAntimatiere = new AddAntimatiere(1);
    AntimatiereUnlocked antimatiereUnlocked;
    private int speedGaugeValue = 0;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1000;
    ApiInterface api;
    Button buttonPlus;
    boolean courantRestarted = false;
    Context context;
    Vibrator vib;
    boolean isUnlocked = false;

    // Pass in the contact array into the constructor
    public OrdersAdapter(List<OrdersApiResponse> orders, String restAddress, Context context, Vibrator vib) {
        this.orders = orders;
        this.restAddress = restAddress;
        this.context = context;
        this.vib = vib;
    }

    public void setOrders(List<OrdersApiResponse> orders){
        this.orders = orders;
    }

    public void callAPIAntimatiereUnlocked() {
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                Call<AntimatiereUnlocked> callSyncForAntimatiereUnlocked = api.getAntimatiereUnlockedCall();
                callSyncForAntimatiereUnlocked.enqueue(new Callback<AntimatiereUnlocked>() {
                    @Override
                    public void onResponse(Call<AntimatiereUnlocked> call, Response<AntimatiereUnlocked> response) {
                        antimatiereUnlocked = response.body();
                        if (antimatiereUnlocked != null && antimatiereUnlocked.getUnlocked() != null && !isUnlocked) {
                            if (antimatiereUnlocked.getUnlocked()) {
                                buttonPlus.setEnabled(true);
                                vib.vibrate(200);
                                Toast.makeText(context, "Oh.. un bouton a été débloqué..", Toast.LENGTH_SHORT).show();
                                isUnlocked = true;
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<AntimatiereUnlocked> call, Throwable t) {
                        Log.d("CallBack AntimatiereUnlocked", "Callback failure");
                        t.printStackTrace();
                    }
                });
            }
        }, delay);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType){
            case LAYOUT_DEFAULT:
                View orderView = inflater.inflate(R.layout.activity_orders_item, parent, false);
                ViewHolder viewHolder = new ViewHolder(orderView);
                return viewHolder;
            case LAYOUT_PLUS:
                View orderViewWithButtonPlus = inflater.inflate(R.layout.activity_orders_item_with_plus, parent, false);
                ViewHolderWithButtonPlus viewWithButtonPlus = new ViewHolderWithButtonPlus(orderViewWithButtonPlus);
                return viewWithButtonPlus;
            default:
                View orderViewDefault = inflater.inflate(R.layout.activity_orders_item, parent, false);
                ViewHolder viewHolder2 = new ViewHolder(orderViewDefault);
                return viewHolder2;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // Get the data model based on position
        OrdersApiResponse order = orders.get(position);
        TextView textView;
        api = ServiceGenerator.createService(ApiInterface.class, restAddress);

        switch(holder.getItemViewType()){
            case LAYOUT_DEFAULT:
                ViewHolder viewHolder = (ViewHolder) holder;
                // Set item views based on your views and data model
                textView = viewHolder.orderTextView;
                textView.setText(order.getTitle());
                break;
            case LAYOUT_PLUS:
                ViewHolderWithButtonPlus viewHolderWithButtonPlus = (ViewHolderWithButtonPlus) holder;
                // Set item views based on your views and data model
                textView = viewHolderWithButtonPlus.orderTextView;
                textView.setText(order.getTitle());
                buttonPlus = viewHolderWithButtonPlus.buttonPlus;

                buttonPlus.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Call<AddAntimatiere> callSync = api.addAntimatiere(addAntimatiere);
                        callSync.enqueue(new Callback<AddAntimatiere>() {
                            @Override
                            public void onResponse(Call<AddAntimatiere> call, Response<AddAntimatiere> response) {
                                if(addAntimatiere.getValue() == 5) {
                                    buttonPlus.setText("Stock vide");
                                    buttonPlus.setEnabled(false);
                                    return;
                                }
                                addAntimatiere.setValue(addAntimatiere.getValue() + 1);
                                Toast.makeText(context, "De l'antimatière a été envoyée", Toast.LENGTH_SHORT).show();
                                Log.d("Antimatiere", "Adding one antimatiere : " + addAntimatiere.getValue());
                            }

                            @Override
                            public void onFailure(Call<AddAntimatiere> call, Throwable t) {
                                Log.d("CallBackAddAntimatiereFailed", "AddAntimatiere callback failure");
                                t.printStackTrace();
                            }
                        });
                    }
                });

                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        OrdersApiResponse order = orders.get(position);
        if (order.getId().equals("antimatiere")){
            return LAYOUT_PLUS;
        } else {
            return LAYOUT_DEFAULT;
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView orderTextView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            orderTextView = (TextView) itemView.findViewById(R.id.order_title);
        }
    }

    public class ViewHolderWithButtonPlus extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView orderTextView;
        public Button buttonPlus;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolderWithButtonPlus(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            orderTextView = (TextView) itemView.findViewById(R.id.order_title);
            buttonPlus = (Button) itemView.findViewById(R.id.button_plus);

            if (antimatiereUnlocked != null && antimatiereUnlocked.getUnlocked() != null && antimatiereUnlocked.getUnlocked()) {
                buttonPlus.setEnabled(true);
            }
        }
    }
}