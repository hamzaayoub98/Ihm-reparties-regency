package com.example.ihm_reparties;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
    private int valueAntimatiere;

    // Pass in the contact array into the constructor
    public OrdersAdapter(List<OrdersApiResponse> orders, String restAddress, int valueAntimatiere) {
        this.orders = orders;
        this.restAddress = restAddress;
        this.valueAntimatiere = valueAntimatiere;
    }

    public int getValueAntimatiere(){
        return this.valueAntimatiere;
    }

    public void setValueAntimatiere(int value){
        this.valueAntimatiere = value;
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
            case LAYOUT_GAUGE:
                View orderViewWithGauge = inflater.inflate(R.layout.activity_orders_item_with_gauge, parent, false);
                ViewHolderWithGauge viewHolderWithGauge = new ViewHolderWithGauge(orderViewWithGauge);
                return viewHolderWithGauge;
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

        switch(holder.getItemViewType()){
            case LAYOUT_DEFAULT:
                ViewHolder viewHolder = (ViewHolder) holder;
                // Set item views based on your views and data model
                textView = viewHolder.orderTextView;
                textView.setText(order.getTitle());
                break;
            case LAYOUT_GAUGE:
                ViewHolderWithGauge viewHolderWithGauge = (ViewHolderWithGauge) holder;
                // Set item views based on your views and data model
                textView = viewHolderWithGauge.orderTextView;
                textView.setText(order.getTitle());

                HalfGauge speedGauge = viewHolderWithGauge.speedGauge;
                speedGauge.enableAnimation(false);
                Range range = new Range();
                range.setColor(Color.parseColor("#ce0000"));
                range.setFrom(0.0);
                range.setTo(order.getValue() - 10);

                Range range2 = new Range();
                range2.setColor(Color.parseColor("#00b20b"));
                range2.setFrom(order.getValue() - 10);
                range2.setTo(order.getValue() + 10);

                Range range3 = new Range();
                range3.setColor(Color.parseColor("#ce0000"));
                range3.setFrom(order.getValue() + 10);
                range3.setTo(175.0);

                //add color ranges to gauge
                speedGauge.addRange(range);
                speedGauge.addRange(range2);
                speedGauge.addRange(range3);

                //set min max and current value
                speedGauge.setMinValue(0.0);
                speedGauge.setMaxValue(175.0);
                speedGauge.setValue(0.0);
                Log.d("GAUGE", "value: " + speedGauge.getValue());
                break;
            case LAYOUT_PLUS:
                ApiInterface api = ServiceGenerator.createService(ApiInterface.class, restAddress);

                ViewHolderWithButtonPlus viewHolderWithButtonPlus = (ViewHolderWithButtonPlus) holder;
                // Set item views based on your views and data model
                textView = viewHolderWithButtonPlus.orderTextView;
                textView.setText(order.getTitle());

                Button buttonPlus = viewHolderWithButtonPlus.buttonPlus;
                buttonPlus.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Call<AddAntimatiere> callSync = api.addAntimatiere(new AddAntimatiere(valueAntimatiere));
                        callSync.enqueue(new Callback<AddAntimatiere>() {
                            @Override
                            public void onResponse(Call<AddAntimatiere> call, Response<AddAntimatiere> response) {
                                valueAntimatiere += 1;
                                Log.d("Antimatiere", "Adding one antimatiere");
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
        if(order.getId().equals("slider")) {
            return LAYOUT_GAUGE;
        }
        else if (order.getId().equals("antimatiere")){
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

    public class ViewHolderWithGauge extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView orderTextView;
        public HalfGauge speedGauge;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolderWithGauge(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            orderTextView = (TextView) itemView.findViewById(R.id.order_title);
            speedGauge = (HalfGauge) itemView.findViewById(R.id.speed_gauge);
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
        }
    }
}