package com.example.ihm_reparties;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OrdersActivity extends AppCompatActivity {
    final class IHMWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;
        @Override
        public void onOpen(WebSocket webSocket, okhttp3.Response response) {
            webSocket.send("Hello, mobile here !");
        }
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            String[] tab = text.split(",");
            output(tab[1], false);
            Log.d("onMessageWS", text);
        }
        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            output(bytes.hex(), false);
            Log.d("onMessageWS", bytes.hex());

        }
        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            output(code + " / " + reason, true);
        }
        @Override
        public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
            output(t.getMessage(), true);
            Log.d("onFailureWS", t.getMessage());
        }
    }

    private SharedPreferences sharedPref;
    List<OrdersApiResponse> orders = new ArrayList<>();
    GameFinished isGameFinished;
    private Context context = this;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1000;
    // Save state
    private Parcelable recyclerViewState;
    private OkHttpClient client;
    private WebSocket ws;
    OrdersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        client = new OkHttpClient();

        sharedPref = this.getSharedPreferences("app", this.MODE_PRIVATE);
        // Lookup the recyclerview in activity layout
        RecyclerView rvOrders = (RecyclerView) findViewById(R.id.rvOrders);
        rvOrders.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        if(getRestAddressPortString().length() < 10){
            Toast toast = Toast.makeText(getApplicationContext(), "No ipv4 and port address defined in the settings.", Toast.LENGTH_SHORT);
            toast.show();
//            return;
        }
        startWsConnection();
        ApiInterface api = ServiceGenerator.createService(ApiInterface.class, getRestAddressPortString());
        adapter = new OrdersAdapter(orders, getRestAddressPortString());
        // Attach the adapter to the recyclerview to populate items
        rvOrders.setAdapter(adapter);
        // Set layout manager to position the items
        rvOrders.setLayoutManager(new LinearLayoutManager(context));

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
                            adapter.setOrders(orders);
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

                Call<GameFinished> callSyncForGameFinished = api.getGameFinishedApiResponseCall();
                callSyncForGameFinished.enqueue(new Callback<GameFinished>() {
                    @Override
                    public void onResponse(Call<GameFinished> call, Response<GameFinished> response) {
                        isGameFinished = response.body();
                        if(isGameFinished != null) {
                            if (isGameFinished.getIsFinished()) {
                                Intent intent = new Intent(getApplicationContext(), GameFinishedActivity.class);
                                startActivity(intent);
                            }
                            Log.d("CallBack FinishGame", "isFinished : " + isGameFinished.getIsFinished());
                        }
                    }

                    @Override
                    public void onFailure(Call<GameFinished> call, Throwable t) {
                        Toast.makeText(OrdersActivity.this, "FinishGame callback failure",
                                Toast.LENGTH_SHORT).show();
                        Log.d("CallBackFinishGameFailed", "FinishGame callback failure");
                        t.printStackTrace();
                    }
                });
            }
        }, delay);
    }

    private void startWsConnection() {
        sharedPref = this.getSharedPreferences("app", this.MODE_PRIVATE);
        Request request = new Request.Builder().url(getWsAddressPortString()).build();
        IHMWebSocketListener listener = new IHMWebSocketListener();
        ws = client.newWebSocket(request, listener);
    }

    public void output(final String txt, boolean error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.setSpeedGaugeValue(Integer.parseInt(txt));
                if (error) Toast.makeText(OrdersActivity.this, "Message :" + txt,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        client.dispatcher().executorService().shutdown(); // close WS
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, delay);
    }

    public final String getRestAddressPortString(){
        String restAddress = sharedPref.getString(getString(R.string.ipv4), "");
        String restPort = sharedPref.getString(getString(R.string.port), "");
        String address = "";
        if(restPort.equals("")) {
            address = "http://" + restAddress;
        } else {
            address = "http://" + restAddress + ":" + restPort;
        }
        return address;
    }

    public final String getWsAddressPortString(){
        String wsAddress = sharedPref.getString(getString(R.string.wsAddress), "");
        String wsPort = sharedPref.getString(getString(R.string.wsPort), "");
        String address;
        if(wsPort.equals("")){
            address = "ws://" + wsAddress;
        } else {
            address = "ws://" + wsAddress + ":" + wsPort + "/";
        }
        return address;
    }

    /**
     * Vibrates the device. Used for providing feedback when the user performs an action.
     */
    public void vibrate(View view) {
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
    }
}
