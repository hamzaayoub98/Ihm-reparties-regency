package com.example.ihm_reparties;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Orders {
        @SerializedName("orders")
        private List<OrdersApiResponse> orders;

        public List<OrdersApiResponse> getOrders() {
            return orders;
        }
}
