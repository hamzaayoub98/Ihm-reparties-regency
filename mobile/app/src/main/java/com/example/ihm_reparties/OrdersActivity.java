package com.example.ihm_reparties;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
    ApiInterface api;
    private final int DEFAULT_REACTOR_GAUGE_VALUE = 80;
    private SharedPreferences sharedPref;
    List<OrdersApiResponse> orders = new ArrayList<>();
    GameFinished isGameFinished;
    NoMoreAntimatiere noMoreAntimatiere;
    ActivateEnergy activateEnergy = new ActivateEnergy();
    ActivateHypervitesse activateHypervitesse = new ActivateHypervitesse();
    AntimatiereVRValue antimatiereValue = new AntimatiereVRValue();
    MissileReady missileReady;
    MissilePlaced missilePlaced;
    HypervitesseReady hypervitesseReady;
    CourantStatus courantStatus;
    boolean hasCourantStatusBeenCalled = false;
    boolean enigmAlreadyStartedOnce = false;
    int currentAntimatiereValue = 0;
    int indicatorLevel = 0;
    boolean isLevierActivated = false;
    boolean isSlider1To100 = false;
    boolean isSlider2To100 = false;
    boolean isAlreadyFull = false;
    CourantSequence courantSequence;
    int sizeCourantSequence = 0;
    int hypervitesseButtonCountClick = 3;
    boolean areOrdersVisible = true;
    boolean isMissileLaunched = false;
    boolean isHelpVisible = false;
    private Context context = this;
    Vibrator vib;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1000;
    private OkHttpClient client;
    private WebSocket ws;

    //Graphic components
    OrdersAdapter adapter;
    Button buttonCaptors, buttonOrders, buttonHypervitesse, buttonCourant, buttonMissile, buttonMissile2;
    Button buttonIndiceCourant, buttonIndiceAntimatiere, buttonIndiceMissile, buttonIndiceHypervitesse, buttonSolutionCourant, buttonSolutionAntimatiere, buttonSolutionMissile, buttonSolutionHypervitesse;
    Group captorsLayoutGroup, ordersLayoutGroup, helpLayout;
    HalfGauge gaugeReact1, gaugeReact2;
    ImageView arrowEnergyIndicator, warningEnergy, warningAntimatiere, warningHypervitesse, checkEnergy, checkAntimatiere, checkHypervitesse;
    TextView counterHypervitesse;
    TextView indiceCourant, indiceAntimatiere, indiceMissile, indiceHypervitesse, solutionCourant, solutionAntimatiere, solutionMissile, solutionHypervitesse;
    CountDownTimer timer_help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        client = new OkHttpClient();

        startTimerHelpCourant();

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
//        startWsConnection();
        api = ServiceGenerator.
                createService(ApiInterface.class, getRestAddressPortString());

        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //Initialize graphic components
        adapter = new OrdersAdapter(orders, getRestAddressPortString(), getApplicationContext(), vib);
        // Attach the adapter to the recyclerview to populate items
        rvOrders.setAdapter(adapter);
        // Set layout manager to position the items
        rvOrders.setLayoutManager(new LinearLayoutManager(context));
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
        buttonCourant = (Button) findViewById(R.id.button_activate_energy);
        captorsLayoutGroup = (Group) findViewById(R.id.captorsLayoutGroup);
        ordersLayoutGroup = (Group) findViewById(R.id.ordersLayoutGroup);
        helpLayout = (Group) findViewById(R.id.helpLayoutGroup);
        counterHypervitesse = (TextView) findViewById(R.id.counter_hypervitesse);
        buttonMissile = (Button) findViewById(R.id.button_missile);
        buttonMissile2 = (Button) findViewById(R.id.button_missile2);
        buttonIndiceCourant = (Button) findViewById(R.id.button_indice_courant);
        buttonIndiceAntimatiere = (Button) findViewById(R.id.button_indice_antimatiere);
        buttonIndiceMissile = (Button) findViewById(R.id.button_indice_missile);
        buttonIndiceHypervitesse = (Button) findViewById(R.id.button_indice_hypervitesse);
        buttonSolutionCourant = (Button) findViewById(R.id.button_solution_courant);
        buttonSolutionAntimatiere = (Button) findViewById(R.id.button_solution_antimatiere);
        buttonSolutionMissile = (Button) findViewById(R.id.button_solution_missile);
        buttonSolutionHypervitesse = (Button) findViewById(R.id.button_solution_hypervitesse);
        indiceCourant = (TextView) findViewById(R.id.text_indice_courant);
        indiceAntimatiere = (TextView) findViewById(R.id.text_indice_antimatiere);
        indiceMissile = (TextView) findViewById(R.id.text_indice_missile);
        indiceHypervitesse = (TextView) findViewById(R.id.text_indice_hypervitesse);
        solutionCourant = (TextView) findViewById(R.id.text_solution_courant);
        solutionAntimatiere = (TextView) findViewById(R.id.text_solution_antimatiere);
        solutionMissile = (TextView) findViewById(R.id.text_solution_missile);
        solutionHypervitesse = (TextView) findViewById(R.id.text_solution_hypervitesse);

        implementButtonsHelp();

        courantStatus = new CourantStatus(false);
        initGauge(gaugeReact1);
        initGauge(gaugeReact2);

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

        buttonCourant.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hasCourantStatusBeenCalled = false;
                Call<ActivateEnergy> callSync = api.activateEnergy(new ActivateEnergy());
                callSync.enqueue(new Callback<ActivateEnergy>() {
                    @Override
                    public void onResponse(Call<ActivateEnergy> call, Response<ActivateEnergy> response) {
                        Log.d("CallBack Energy", "Sending activate energy action successful");
                    }

                    @Override
                    public void onFailure(Call<ActivateEnergy> call, Throwable t) {
                        Log.d("CallBack Energy", "Sending activate energy action failed");
                        t.printStackTrace();
                    }
                });
            }
        });

        buttonMissile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<String> callSync = api.getLaunchMissileCall();
                callSync.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.d("CallBack LaunchMissile", "Sending launch missile action successful");
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("CallBack LaunchMissile", "Sending launch missile action failed");
                        t.printStackTrace();
                    }
                });
                buttonMissile.setVisibility(View.INVISIBLE);
                buttonMissile2.setVisibility(View.INVISIBLE);
                isMissileLaunched = true;
            }
        });

        buttonMissile2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<String> callSync = api.getLaunchMissileCall();
                callSync.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.d("CallBack LaunchMissile", "Sending launch missile action successful");
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("CallBack LaunchMissile", "Sending launch missile action failed");
                        t.printStackTrace();
                    }
                });
                buttonMissile2.setVisibility(View.INVISIBLE);
                buttonMissile.setVisibility(View.INVISIBLE);
                isMissileLaunched = true;
            }
        });

        //API Call
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
                                // Vibrate for 600 milliseconds
                                vib.vibrate(600);
                                Toast.makeText(OrdersActivity.this, "Il n'y a plus d'antimati??re.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            Log.d("CallBack NoMoreAntimatiere", "No more antimatiere");
                        }
                    }

                    @Override
                    public void onFailure(Call<NoMoreAntimatiere> call, Throwable t) {
                        Toast.makeText(OrdersActivity.this, "NoMoreAntimatiere callback failure",
                                Toast.LENGTH_SHORT).show();
                        Log.d("CallBack NoMoreAntimatiere", "No more antimatiere failed");
                        t.printStackTrace();
                    }
                });

                Call<CourantSequence> callSyncForCourantSequence = api.getCourantSequenceCall();
                callSyncForCourantSequence.enqueue(new Callback<CourantSequence>() {
                    @Override
                    public void onResponse(Call<CourantSequence> call, Response<CourantSequence> response) {
                        courantSequence = response.body();
                        if(courantSequence != null && courantSequence.getCourantSequence() != null && !courantStatus.getCourantStatus()) {
                            Log.d("Sequence : ", courantSequence.getCourantSequence().toString());
                            if(courantSequence.getCourantSequence().contains(new Integer(1)) && !isSlider1To100){
                                indicatorLevel += 1;
                                moveArrowIndicator(indicatorLevel);
                                Toast.makeText(getApplicationContext(), "Il y a un changement avec le courant... !", Toast.LENGTH_SHORT).show();
                                isSlider1To100 = true;
                                vib.vibrate(200);
                            }
                            if(courantSequence.getCourantSequence().contains(new Integer(2)) && !isSlider2To100){
                                indicatorLevel += 1;
                                moveArrowIndicator(indicatorLevel);
                                Toast.makeText(getApplicationContext(), "On devrait ??tre sur la bonne voie pour activer le courant...", Toast.LENGTH_SHORT).show();
                                isSlider2To100 = true;
                                vib.vibrate(200);
                            }
                            if(courantSequence.getCourantSequence().contains(new Integer(3)) && !isLevierActivated){
                                indicatorLevel += 1;
                                moveArrowIndicator(indicatorLevel);
                                Toast.makeText(getApplicationContext(), "Tiens tiens tiens, le voyant du courant r??agit...", Toast.LENGTH_SHORT).show();
                                isLevierActivated = true;
                                vib.vibrate(200);
                            }
                            if(isSlider1To100){
                                if(!courantSequence.getCourantSequence().contains(new Integer(1))){
                                    isSlider1To100 = false;
                                    indicatorLevel -= 1;
                                    moveArrowIndicator(indicatorLevel);
                                    Toast.makeText(getApplicationContext(), "Oh non ! Le voyant du courant est descendu !!", Toast.LENGTH_SHORT).show();
                                    vib.vibrate(600);
                                }
                            }
                            if(isSlider2To100){
                                if(!courantSequence.getCourantSequence().contains(new Integer(2))){
                                    isSlider2To100 = false;
                                    indicatorLevel -= 1;
                                    moveArrowIndicator(indicatorLevel);
                                    Toast.makeText(getApplicationContext(), "Oh non ! Le voyant du courant est descendu !!", Toast.LENGTH_SHORT).show();
                                    vib.vibrate(600);
                                }
                            }
                            if(isLevierActivated){
                                if(!courantSequence.getCourantSequence().contains(new Integer(3))){
                                    isLevierActivated = false;
                                    indicatorLevel -= 1;
                                    moveArrowIndicator(indicatorLevel);
                                    Toast.makeText(getApplicationContext(), "Oh non ! Le voyant du courant est descendu !!", Toast.LENGTH_SHORT).show();
                                    vib.vibrate(600);
                                }
                            }
                            if(isLevierActivated && isSlider1To100 && isSlider2To100){
                                buttonCourant.setVisibility(View.VISIBLE);
                                vib.vibrate(200);
                            }
                            else {
                                buttonCourant.setVisibility(View.INVISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CourantSequence> call, Throwable t) {
                        Toast.makeText(OrdersActivity.this, "Courant Sequence failure",
                                Toast.LENGTH_SHORT).show();
                        Log.d("CallBack Courant Sequence", "Callback failure");
                        t.printStackTrace();
                    }
                });

                Call<CourantStatus> callSyncForCourantStatus = api.getCourantStatusCall();
                callSyncForCourantStatus.enqueue(new Callback<CourantStatus>() {
                    @Override
                    public void onResponse(Call<CourantStatus> call, Response<CourantStatus> response) {
                        courantStatus = response.body();
                        if(courantStatus != null && courantStatus.getCourantStatus() != null && !hasCourantStatusBeenCalled){ //&& !hasCourantStatusBeenCalled
                            if (courantStatus.getCourantStatus()) {
                                Log.d("Status", "Courant status success");
                                transformWarningEnergyToCheck();
                                moveArrowIndicator(3);
                                buttonCourant.setEnabled(false);
                                buttonCourant.setVisibility(View.VISIBLE);
                                buttonCourant.setText("Le courant est activ??");
                                Toast.makeText(getApplicationContext(), "Le courant a ??t?? r??tabli !", Toast.LENGTH_LONG).show();
                                vib.vibrate(200);
                                hasCourantStatusBeenCalled = true;
                            }
                            Log.d("Status", "Courant status : " + courantStatus.getCourantStatus());
                        }
                    }

                    @Override
                    public void onFailure(Call<CourantStatus> call, Throwable t) {
                        Toast.makeText(OrdersActivity.this, "Courant Status failure",
                                Toast.LENGTH_SHORT).show();
                        Log.d("CallBack Courant Status", "Callback failure");
                        t.printStackTrace();
                    }
                });

                if (courantStatus != null && courantStatus.getCourantStatus() && courantStatus.getCourantStatus()) {
                    adapter.callAPIAntimatiereUnlocked();
                }

                Call<AntimatiereVRValue> callSyncForAntimatiereValue = api.getAntimatiereVrValueCall();
                callSyncForAntimatiereValue.enqueue(new Callback<AntimatiereVRValue>() {
                    @Override
                    public void onResponse(Call<AntimatiereVRValue> call, Response<AntimatiereVRValue> response) {
                        antimatiereValue = response.body();
                        if(antimatiereValue != null && currentAntimatiereValue != antimatiereValue.getAntimatiereVRValue()*20 && currentAntimatiereValue != 80) {
                            currentAntimatiereValue = antimatiereValue.getAntimatiereVRValue()*20;
                            gaugeReact2.setValue(antimatiereValue.getAntimatiereVRValue()*20);
                            vib.vibrate(200);
                            Toast.makeText(OrdersActivity.this, "Le r??acteur 2 se remplit...",
                                    Toast.LENGTH_SHORT).show();
                            Log.d("antimatiereValue","valeur back = " + antimatiereValue.getAntimatiereVRValue() + "\n currentAntimatiereValue = " + currentAntimatiereValue);
                            if(antimatiereValue.getAntimatiereVRValue()*20 == 80){
                                transformWarningAntimatiereToCheck();
                                Toast.makeText(OrdersActivity.this, "Le r??acteur 2 est remplit !!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<AntimatiereVRValue> call, Throwable t) {
                        Toast.makeText(OrdersActivity.this, "AntimatiereValue failed",
                                Toast.LENGTH_SHORT).show();
                        Log.d("CallBack AntimatiereValue", "Callback failure");
                        t.printStackTrace();
                    }
                });

                Call<MissilePlaced> callSyncForMissilePlaced = api.getMissilePlacedCall();
                callSyncForMissilePlaced.enqueue(new Callback<MissilePlaced>() {
                    @Override
                    public void onResponse(Call<MissilePlaced> call, Response<MissilePlaced> response) {
                        missilePlaced = response.body();
                        if(missilePlaced != null && missilePlaced.getMissilePlaced() != null) {
                            if(missilePlaced.getMissilePlaced() && !isMissileLaunched){
                                vib.vibrate(1000);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MissilePlaced> call, Throwable t) {
                        Toast.makeText(OrdersActivity.this, "MissilePlaced failed",
                                Toast.LENGTH_SHORT).show();
                        Log.d("CallBack MissilePlaced", "Callback failure");
                        t.printStackTrace();
                    }
                });

                Call<MissileReady> callSyncForMissileReady = api.getMissileReadyCall();
                callSyncForMissileReady.enqueue(new Callback<MissileReady>() {
                    @Override
                    public void onResponse(Call<MissileReady> call, Response<MissileReady> response) {
                        missileReady = response.body();
                        Log.d("Ready", "call API");
                        if(missileReady != null && missileReady.getMissileReady() != null && !isMissileLaunched) {
                            Log.d("Ready", "1er if");
                            if(missileReady.getMissileReady()) {
                                Log.d("Ready", "2e if");
                                buttonMissile.setVisibility(View.VISIBLE);
                                buttonMissile2.setVisibility(View.VISIBLE);
                                Toast.makeText(OrdersActivity.this, "Les pilotes ont donn?? leur accord, faites feu !",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MissileReady> call, Throwable t) {
                        Toast.makeText(OrdersActivity.this, "MissileReady failed",
                                Toast.LENGTH_SHORT).show();
                        Log.d("CallBack MissileReady", "Callback failure");
                        t.printStackTrace();
                    }
                });

                Call<HypervitesseReady> callSyncForHypervitesseReady = api.getHypervitesseReadyCall();
                callSyncForHypervitesseReady.enqueue(new Callback<HypervitesseReady>() {
                    @Override
                    public void onResponse(Call<HypervitesseReady> call, Response<HypervitesseReady> response) {
                        hypervitesseReady = response.body();
                        if(hypervitesseReady != null && hypervitesseReady.getHypervitesseReady() != null) {
                            Log.d("hypervitesse", "1er if");
                            if(hypervitesseReady.getHypervitesseReady() && !enigmAlreadyStartedOnce){
                                Log.d("hypervitesse", "2e if");
                                Toast.makeText(OrdersActivity.this, "Le bouton semble d??faillant...",
                                        Toast.LENGTH_LONG).show();
                                enigmWithVibrations();
                                enigmAlreadyStartedOnce = true;

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<HypervitesseReady> call, Throwable t) {
                        Toast.makeText(OrdersActivity.this, "HypervitesseReady failed",
                                Toast.LENGTH_SHORT).show();
                        Log.d("CallBack HypervitesseReady", "Callback failure");
                        t.printStackTrace();
                    }
                });
            }
        }, 500);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.help:
                if(!isHelpVisible){
                    swapGroupVisibilityForHelp();
                    item.setIcon(R.drawable.ic_baseline_videogame_asset_24);
                } else {
                    swapGroupVisibilityForHelp();
                    item.setIcon(R.drawable.ic_baseline_help_24);
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void enigmWithVibrations(){
        Toast.makeText(getApplicationContext(), "Une histoire de longueur ?", Toast.LENGTH_SHORT).show();
        List<Integer> checkClicks = new ArrayList<>();
        List<Integer> correctsClicks = new ArrayList<>();
        correctsClicks.add(0);
        correctsClicks.add(0);
        correctsClicks.add(1);
        hypervitesseButtonCountClick = 3;
        counterHypervitesse.setText("" + hypervitesseButtonCountClick);
        buttonHypervitesse.setEnabled(true);
        buttonHypervitesse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkClicks.add(0);
                hypervitesseButtonCountClick -= 1;
                counterHypervitesse.setText("" + hypervitesseButtonCountClick);
                if(hypervitesseButtonCountClick == 0){
                    buttonHypervitesse.setEnabled(false);
                    if(checkClicks.equals(correctsClicks)){
                        Call<ActivateHypervitesse> callSync = api.activateHypervitesse(activateHypervitesse);
                        callSync.enqueue(new Callback<ActivateHypervitesse>() {
                            @Override
                            public void onResponse(Call<ActivateHypervitesse> call, Response<ActivateHypervitesse> response) {
                                Log.d("CallBack HypervitesseActivated", "Sending activate hypervitesse action successful");
                            }

                            @Override
                            public void onFailure(Call<ActivateHypervitesse> call, Throwable t) {
                                Log.d("CallBack HypervitesseActivated", "Sending activate hypervitesse action failed");
                                t.printStackTrace();
                            }
                        });
                        Toast.makeText(getApplicationContext(), "Bravo ! Vous ??tes sortis du champ d'ast??ro??de en vie !", Toast.LENGTH_LONG);
                    } else {
                        enigmWithVibrations();
                    }
                }
            }
        });

        buttonHypervitesse.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                checkClicks.add(1);
                hypervitesseButtonCountClick -= 1;
                counterHypervitesse.setText("" + hypervitesseButtonCountClick);
                if(hypervitesseButtonCountClick == 0){
                    buttonHypervitesse.setEnabled(false);
                    if(checkClicks.equals(correctsClicks)){
                        Call<ActivateHypervitesse> callSync = api.activateHypervitesse(activateHypervitesse);
                        callSync.enqueue(new Callback<ActivateHypervitesse>() {
                            @Override
                            public void onResponse(Call<ActivateHypervitesse> call, Response<ActivateHypervitesse> response) {
                                Log.d("CallBack HypervitesseActivated", "Sending activate hypervitesse action successful");
                            }

                            @Override
                            public void onFailure(Call<ActivateHypervitesse> call, Throwable t) {
                                Log.d("CallBack HypervitesseActivated", "Sending activate hypervitesse action failed");
                                t.printStackTrace();
                            }
                        });
                        Toast.makeText(getApplicationContext(), "Bravo ! Vous ??tes sortis du champ d'ast??ro??de en vie !", Toast.LENGTH_LONG);
                    } else {
                        enigmWithVibrations();
                    }
                }
                return false;
            }
        });
        long[] pattern = {0,200, 1500, 200, 1500, 800};
        vib.vibrate(pattern, -1);
//        new CountDownTimer(6000, 2000) {
//            long[] pattern = {0,200, 1300, 200, 600};
//            public void onTick(long millisUntilFinished) {
//                if(millisUntilFinished / 2000 > 2 ){
//                    vib.vibrate(200);
//                } else if (millisUntilFinished / 2000 > 1 && millisUntilFinished / 2000 < 2) {
//                    vib.vibrate(200);
//                } else {
//                    vib.vibrate(2000);
//                }
//            }
//
//            public void onFinish() {
//                Toast.makeText(getApplicationContext(), "3 vibrations...", Toast.LENGTH_LONG).show();
//            }
//        }.start();
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

    private void swapGroupVisibilityForHelp(){
        if(!isHelpVisible){
            for(int id : captorsLayoutGroup.getReferencedIds()){
                findViewById(id).setVisibility(View.INVISIBLE);
            }
            for(int id : ordersLayoutGroup.getReferencedIds()){
                findViewById(id).setVisibility(View.INVISIBLE);
            }
            for(int id : helpLayout.getReferencedIds()){
                findViewById(id).setVisibility(View.VISIBLE);
            }
            isHelpVisible = true;
        } else {
            for(int id : helpLayout.getReferencedIds()){
                findViewById(id).setVisibility(View.INVISIBLE);
            }
            if(areOrdersVisible){
                for(int id : ordersLayoutGroup.getReferencedIds()){
                    findViewById(id).setVisibility(View.VISIBLE);
                }
            }
            else {
                for(int id : captorsLayoutGroup.getReferencedIds()){
                    findViewById(id).setVisibility(View.VISIBLE);
                }
            }
            isHelpVisible = false;
        }
    }



//    public int getReactorValue(){
//        for(OrdersApiResponse order : orders) {
//            if(order.getId().equals("slider")){
//                return order.getValue();
//            }
//        }
//        return -1;
//    }

    public void transformWarningEnergyToCheck(){
        warningEnergy.setVisibility(View.INVISIBLE);
        checkEnergy.setVisibility(View.VISIBLE);
    }

    public void transformWarningAntimatiereToCheck(){
        warningAntimatiere.setVisibility(View.INVISIBLE);
        checkAntimatiere.setVisibility(View.VISIBLE);
    }

    public void transformWarningHypervitesseToCheck(){
        warningHypervitesse.setVisibility(View.INVISIBLE);
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
                constraintSet.connect(R.id.logo_arrow_indicator,ConstraintSet.END,R.id.guideline_end_lightning_full,ConstraintSet.START,0);
                constraintSet.connect(R.id.logo_arrow_indicator,ConstraintSet.START,R.id.guideline_start_lightning_full,ConstraintSet.START,0);
                constraintSet.connect(R.id.logo_arrow_indicator,ConstraintSet.TOP,R.id.logo_lightning_yellow_full_stroke,ConstraintSet.BOTTOM,0);
                constraintSet.applyTo(constraintLayout);
                break;
            case 3:
                constraintSet.clone(constraintLayout);
                constraintSet.connect(R.id.logo_arrow_indicator,ConstraintSet.END,R.id.guideline_end_lightning_filled,ConstraintSet.START,0);
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

    public void initGauge(HalfGauge gauge){
        int gaugeGreenSectionValue = DEFAULT_REACTOR_GAUGE_VALUE;
        gauge.enableAnimation(false);

        Range range = new Range();
        range.setColor(Color.parseColor("#ce0000"));
        range.setFrom(0.0);
        range.setTo(gaugeGreenSectionValue - 10);

        Range range2 = new Range();
        range2.setColor(Color.parseColor("#00b20b"));
        range2.setFrom(gaugeGreenSectionValue - 10);
        range2.setTo(gaugeGreenSectionValue + 10);

        Range range3 = new Range();
        range3.setColor(Color.parseColor("#ce0000"));
        range3.setFrom(gaugeGreenSectionValue + 10);
        range3.setTo(100.0);

        //add color ranges to gauge
        gauge.addRange(range);
        gauge.addRange(range2);
        gauge.addRange(range3);

        //set min max and current value
        gauge.setMinValue(0.0);
        gauge.setMaxValue(100.0);
        if(gauge.equals(gaugeReact1)){
            gauge.setValue(85.0);
        } else {
            gauge.setValue(9.0);

        }
    }

    public void startTimerHelpCourant(){
        timer_help = new CountDownTimer(180000, 1000) {

            public void onTick(long millisUntilFinished) {
                buttonSolutionCourant.setText("Voir Solution : " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                buttonSolutionCourant.setEnabled(true);
                startTimerHelpAntimatiere();
            }
        }.start();
    }

    public void startTimerHelpAntimatiere(){
        timer_help = new CountDownTimer(180000, 1000) {

            public void onTick(long millisUntilFinished) {
                buttonSolutionAntimatiere.setText("Voir Solution : " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                buttonSolutionAntimatiere.setEnabled(true);
                startTimerHelpMissile();
            }
        }.start();
    }

    public void startTimerHelpMissile(){
        timer_help = new CountDownTimer(180000, 1000) {

            public void onTick(long millisUntilFinished) {
                buttonSolutionMissile.setText("Voir Solution : " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                buttonSolutionMissile.setEnabled(true);
                startTimerHelpHypervitesse();
            }
        }.start();
    }

    public void startTimerHelpHypervitesse(){
        timer_help = new CountDownTimer(180000, 1000) {

            public void onTick(long millisUntilFinished) {
                buttonSolutionHypervitesse.setText("Voir Solution : " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                buttonSolutionHypervitesse.setEnabled(true);
            }
        }.start();
    }

    public void implementButtonsHelp(){
        buttonIndiceCourant.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonIndiceCourant.setVisibility(View.INVISIBLE);
                indiceCourant.setVisibility(View.VISIBLE);
            }
        });

        buttonIndiceAntimatiere.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonIndiceAntimatiere.setVisibility(View.INVISIBLE);
                indiceAntimatiere.setVisibility(View.VISIBLE);
            }
        });

        buttonIndiceMissile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonIndiceMissile.setVisibility(View.INVISIBLE);
                indiceMissile.setVisibility(View.VISIBLE);
            }
        });

        buttonIndiceHypervitesse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonIndiceHypervitesse.setVisibility(View.INVISIBLE);
                indiceHypervitesse.setVisibility(View.VISIBLE);
            }
        });

        buttonSolutionCourant.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonSolutionCourant.setVisibility(View.INVISIBLE);
                solutionCourant.setVisibility(View.VISIBLE);
            }
        });

        buttonSolutionAntimatiere.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonSolutionAntimatiere.setVisibility(View.INVISIBLE);
                solutionAntimatiere.setVisibility(View.VISIBLE);
            }
        });

        buttonSolutionMissile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonSolutionMissile.setVisibility(View.INVISIBLE);
                solutionMissile.setVisibility(View.VISIBLE);
            }
        });

        buttonSolutionHypervitesse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonSolutionHypervitesse.setVisibility(View.INVISIBLE);
                solutionHypervitesse.setVisibility(View.VISIBLE);
            }
        });
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
