package com.example.ihm_reparties;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ekn.gruzer.gaugelibrary.HalfGauge;
import com.ekn.gruzer.gaugelibrary.Range;

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
    private final int DEFAULT_REACTOR_GAUGE_VALUE = 80;
    private SharedPreferences sharedPref;
    List<OrdersApiResponse> orders = new ArrayList<>();
    GameFinished isGameFinished;
    NoMoreAntimatiere noMoreAntimatiere;
    boolean areOrdersVisible = true;
    private Context context = this;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1000;
    private OkHttpClient client;
    private WebSocket ws;

    //Graphic components
    OrdersAdapter adapter;
    Button buttonCaptors, buttonOrders, buttonHypervitesse;
    Group captorsLayoutGroup, ordersLayoutGroup;
    HalfGauge gaugeReact1, gaugeReact2;
    ImageView arrowEnergyIndicator, warningEnergy, warningAntimatiere, warningHypervitesse, checkEnergy, checkAntimatiere, checkHypervitesse;

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

        //Manage API
        if(getRestAddressPortString().length() < 10){
            Toast toast = Toast.makeText(getApplicationContext(), "No ipv4 and port address defined in the settings.", Toast.LENGTH_SHORT);
            toast.show();
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

                Call<NoMoreAntimatiere> callSyncForNoMoreAntimatiere = api.getNoMoreAntimatiereApiResponseCall();
                callSyncForNoMoreAntimatiere.enqueue(new Callback<NoMoreAntimatiere>() {
                    @Override
                    public void onResponse(Call<NoMoreAntimatiere> call, Response<NoMoreAntimatiere> response) {
                        noMoreAntimatiere = response.body();
                        if(noMoreAntimatiere != null && noMoreAntimatiere.getNoMoreAntimatiere() != null) {
                            if (noMoreAntimatiere.getNoMoreAntimatiere()) {
                                Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                // Vibrate for 400 milliseconds
                                vib.vibrate(600);
                            }
                            Log.d("CallBack FinishGame", "isFinished : " + isGameFinished.getIsFinished());
                        }
                    }

                    @Override
                    public void onFailure(Call<NoMoreAntimatiere> call, Throwable t) {
                        Toast.makeText(OrdersActivity.this, "FinishGame callback failure",
                                Toast.LENGTH_SHORT).show();
                        Log.d("CallBackFinishGameFailed", "FinishGame callback failure");
                        t.printStackTrace();
                    }
                });
            }
        }, delay);

        gaugeReact1 = (HalfGauge) findViewById(R.id.gauge_react1);
        gaugeReact2 = (HalfGauge) findViewById(R.id.gauge_react2);
        arrowEnergyIndicator = (ImageView) findViewById(R.id.logo_arrow_indicator);
        warningEnergy = (ImageView) findViewById(R.id.logo_warning_energy);;
        warningAntimatiere = (ImageView) findViewById(R.id.logo_warning_antimatiere);;
        warningHypervitesse = (ImageView) findViewById(R.id.logo_warning_hypervitesse);;
        checkEnergy = (ImageView) findViewById(R.id.logo_check_energy);;
        checkAntimatiere = (ImageView) findViewById(R.id.logo_check_antimatiere);;
        checkHypervitesse = (ImageView) findViewById(R.id.logo_check_hypervitesse);;
        buttonHypervitesse = (Button) findViewById(R.id.button_hypervitesse);
        buttonOrders = (Button) findViewById(R.id.button_orders);
        captorsLayoutGroup = (Group) findViewById(R.id.captorsLayoutGroup);
        ordersLayoutGroup = (Group) findViewById(R.id.ordersLayoutGroup);

        initGauge(R.id.gauge_react1);
        initGauge(R.id.gauge_react2);

        buttonCaptors = (Button) findViewById(R.id.button_captors);
        buttonCaptors.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                swapGroupVisibility();
            }
        });

        buttonOrders = (Button) findViewById(R.id.button_orders);
        buttonOrders.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                swapGroupVisibility();
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

    private void swapGroupVisibility(){
        if(areOrdersVisible){
            for(int id : captorsLayoutGroup.getReferencedIds()){
                findViewById(id).setVisibility(View.VISIBLE);
            }
            for(int id : ordersLayoutGroup.getReferencedIds()){
                findViewById(id).setVisibility(View.INVISIBLE);
            }
            areOrdersVisible = false;
        }
        else {
            for(int id : captorsLayoutGroup.getReferencedIds()){
                findViewById(id).setVisibility(View.INVISIBLE);
            }
            for(int id : ordersLayoutGroup.getReferencedIds()){
                findViewById(id).setVisibility(View.VISIBLE);
            }
            areOrdersVisible = true;
        }
    }

    public int getReactorValue(){
        for(OrdersApiResponse order : orders) {
            if(order.getId().equals("slider")){
                return order.getValue();
            }
        }
        return -1;
    }

    public void transformWarningEnergyToCheck(){
        warningEnergy.setVisibility(View.GONE);
        checkEnergy.setVisibility(View.VISIBLE);
    }

    public void transformWarningAntimatiereToCheck(){
        warningAntimatiere.setVisibility(View.GONE);
        checkAntimatiere.setVisibility(View.VISIBLE);
    }

    public void transformWarningHypervitesseToCheck(){
        warningHypervitesse.setVisibility(View.GONE);
        checkHypervitesse.setVisibility(View.VISIBLE);
    }

    public void moveArrowIndicator(int energyLevelToShow){
        ConstraintLayout constraintLayout = findViewById(R.id.captorsLayout);
        ConstraintSet constraintSet = new ConstraintSet();
        switch(energyLevelToShow){
            case 1:
                constraintSet.clone(constraintLayout);
                constraintSet.connect(R.id.logo_arrow_indicator,ConstraintSet.END,R.id.guideline_end_lightning_half,ConstraintSet.START,0);
                constraintSet.connect(R.id.logo_arrow_indicator,ConstraintSet.START,R.id.guideline_start_lightning_half,ConstraintSet.START,0);
                constraintSet.connect(R.id.logo_arrow_indicator,ConstraintSet.TOP,R.id.logo_lightning_yellow_half_stroke,ConstraintSet.BOTTOM,0);
                constraintSet.applyTo(constraintLayout);
                break;
            case 2:
                constraintSet.clone(constraintLayout);
                constraintSet.connect(R.id.logo_arrow_indicator,ConstraintSet.END,R.id.guideline_start_lightning_full,ConstraintSet.START,0);
                constraintSet.connect(R.id.logo_arrow_indicator,ConstraintSet.START,R.id.guideline_start_lightning_full,ConstraintSet.START,0);
                constraintSet.connect(R.id.logo_arrow_indicator,ConstraintSet.TOP,R.id.logo_lightning_yellow_full_stroke,ConstraintSet.BOTTOM,0);
                constraintSet.applyTo(constraintLayout);
                break;
            case 3:
                constraintSet.clone(constraintLayout);
                constraintSet.connect(R.id.logo_arrow_indicator,ConstraintSet.END,R.id.guideline_start_lightning_filled,ConstraintSet.START,0);
                constraintSet.connect(R.id.logo_arrow_indicator,ConstraintSet.START,R.id.guideline_start_lightning_filled,ConstraintSet.START,0);
                constraintSet.connect(R.id.logo_arrow_indicator,ConstraintSet.TOP,R.id.logo_lightning_yellow_filled,ConstraintSet.BOTTOM,0);
                constraintSet.applyTo(constraintLayout);
                break;
            default:
                constraintSet.clone(constraintLayout);
                constraintSet.connect(R.id.logo_arrow_indicator,ConstraintSet.END,R.id.guideline_end_lightning_grey,ConstraintSet.START,0);
                constraintSet.connect(R.id.logo_arrow_indicator,ConstraintSet.START,R.id.guideline_start_lightning_grey,ConstraintSet.START,0);
                constraintSet.connect(R.id.logo_arrow_indicator,ConstraintSet.TOP,R.id.logo_lightning_grey,ConstraintSet.BOTTOM,0);
                constraintSet.applyTo(constraintLayout);
        }
    }

    public void initGauge(int gaugeId){
        int gaugeValue = getReactorValue();
        if(gaugeValue == -1){
            gaugeValue = DEFAULT_REACTOR_GAUGE_VALUE;
        }

        HalfGauge gaugeReact = (HalfGauge) findViewById(gaugeId);
        gaugeReact.enableAnimation(false);

        Range range = new Range();
        range.setColor(Color.parseColor("#ce0000"));
        range.setFrom(0.0);
        range.setTo(gaugeValue - 10);

        Range range2 = new Range();
        range2.setColor(Color.parseColor("#00b20b"));
        range2.setFrom(gaugeValue - 10);
        range2.setTo(gaugeValue + 10);

        Range range3 = new Range();
        range3.setColor(Color.parseColor("#ce0000"));
        range3.setFrom(gaugeValue + 10);
        range3.setTo(100.0);

        //add color ranges to gauge
        gaugeReact.addRange(range);
        gaugeReact.addRange(range2);
        gaugeReact.addRange(range3);

        //set min max and current value
        gaugeReact.setMinValue(0.0);
        gaugeReact.setMaxValue(100.0);
        if(gaugeId == R.id.gauge_react1){
            gaugeReact.setValue(85.0);
        } else {
            gaugeReact.setValue(9.0);

        }
    }

    //Connections
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
                //adapter.setSpeedGaugeValue(Integer.parseInt(txt));
                if (error) Toast.makeText(OrdersActivity.this, "Message :" + txt,
                        Toast.LENGTH_SHORT).show();
            }
        });
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
}
