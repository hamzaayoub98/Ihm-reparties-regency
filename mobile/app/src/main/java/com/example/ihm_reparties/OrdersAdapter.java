package com.example.ihm_reparties;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    private List<OrdersApiResponse> orders;

    // Pass in the contact array into the constructor
    public OrdersAdapter(List<OrdersApiResponse> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View orderView = inflater.inflate(R.layout.activity_orders_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(orderView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on position
        OrdersApiResponse order = orders.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.orderTextView;
        textView.setText(order.getTitle());
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
}