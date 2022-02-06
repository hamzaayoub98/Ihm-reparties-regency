package com.example.ihm_reparties;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ekn.gruzer.gaugelibrary.HalfGauge;
import com.ekn.gruzer.gaugelibrary.Range;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int LAYOUT_ONE = 0;
    private final int LAYOUT_TWO = 1;
    private int gaugeValue;
    private List<OrdersApiResponse> orders;

    // Pass in the contact array into the constructor
    public OrdersAdapter(List<OrdersApiResponse> orders, int value) {
        this.orders = orders;
        this.gaugeValue = value;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType){
            case 0:
                View orderView = inflater.inflate(R.layout.activity_orders_item, parent, false);
                ViewHolder viewHolder = new ViewHolder(orderView);
                return viewHolder;
            case 1:
                View orderViewWithGauge = inflater.inflate(R.layout.activity_orders_item_with_gauge, parent, false);
                ViewHolderWithGauge viewHolderWithGauge = new ViewHolderWithGauge(orderViewWithGauge);
                return viewHolderWithGauge;
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
            case 0:
                ViewHolder viewHolder = (ViewHolder) holder;
                // Set item views based on your views and data model
                textView = viewHolder.orderTextView;
                textView.setText(order.getTitle());
                break;
            case 1:
                ViewHolderWithGauge viewHolderWithGauge = (ViewHolderWithGauge) holder;
                // Set item views based on your views and data model
                textView = viewHolderWithGauge.orderTextView;
                textView.setText(order.getTitle());

                HalfGauge speedGauge = viewHolderWithGauge.speedGauge;
                speedGauge.enableAnimation(false);
                Range range = new Range();
                range.setColor(Color.parseColor("#ce0000"));
                range.setFrom(0.0);
                range.setTo(75.0);

                Range range2 = new Range();
                range2.setColor(Color.parseColor("#00b20b"));
                range2.setFrom(75.0);
                range2.setTo(100.0);

                Range range3 = new Range();
                range3.setColor(Color.parseColor("#ce0000"));
                range3.setFrom(100.0);
                range3.setTo(175.0);

                //add color ranges to gauge
                speedGauge.addRange(range);
                speedGauge.addRange(range2);
                speedGauge.addRange(range3);

                //set min max and current value
                speedGauge.setMinValue(0.0);
                speedGauge.setMaxValue(175.0);
                speedGauge.setValue(gaugeValue);
                Log.d("GAUGE", "value: " + speedGauge.getValue());
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        OrdersApiResponse order = orders.get(position);
        if(order.getId().equals("slider")) {
            Log.d("", "SLIDER SLIDER");
            return 1;
        }
        else {
            Log.d("", "PAS SLIDER PAS SLIDER");
            return 0;
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
}