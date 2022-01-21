package com.example.ihm_reparties;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    public static final String EXTRA_MESSAGE = "Data";

    private Display display;
    private SensorManager sensorManager;
    private Sensor accelerometer, light;
    private CameraManager cameraManager;
    private List listX, listY, listZ;
    private List<List> allDataList;
    private boolean isSaving = false;
    private SharedPreferences sharedPref;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private Button buttonSave, buttonShow, buttonHelloWorld, buttonGoOrders;
    private TextView textView1, textViewAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //PreferenceManager.setDefaultValues(this, R.xml.fragment_settings, false);
        sharedPref = this.getSharedPreferences("app", this.MODE_PRIVATE);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);

        listX = new ArrayList();
        listY = new ArrayList();
        listZ = new ArrayList();
        allDataList = new ArrayList();

        buttonSave = (Button) findViewById(R.id.button_save);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!isSaving) {
                    isSaving = true;
                    Toast toast = Toast.makeText(v.getContext(), "Saving the data", Toast.LENGTH_SHORT);
                    toast.show();
                    buttonSave.setText("Stop");
                }
                else{
                    isSaving = false;
                    Toast toast = Toast.makeText(v.getContext(), "Stop the saving", Toast.LENGTH_SHORT);
                    toast.show();
                    buttonSave.setText("Save accelero");
                }
            }
        });

        buttonShow = (Button) findViewById(R.id.button_show);
        buttonShow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String data = "";
                for(int i=0; i<allDataList.size(); i++){
                    data += "[ ";
                    for(int j=0; j<3; j++){
                        data += allDataList.get(i).get(j).toString() + " ";
                    }
                    data += "]\n";
                }
                Intent intent = new Intent(v.getContext(), DataActivity.class);
                intent.putExtra(EXTRA_MESSAGE, data);
                startActivity(intent);
            }
        });

        buttonHelloWorld = (Button) findViewById(R.id.button_helloworld);
        textViewAnswer = findViewById(R.id.textViewAnswer);
        buttonHelloWorld.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 400 milliseconds
                vib.vibrate(400);

                if(getIpv4PortAddress().length() < 10){
                    Toast toast = Toast.makeText(v.getContext(), "No ipv4 and port address defined in the settings.", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                ApiInterface api = ServiceGenerator.createService(ApiInterface.class, getIpv4PortAddress());

                // Calling '/'
                Call<HelloWorldApiResponse> callSync = api.getHelloWorldCall();
                callSync.enqueue(new Callback<HelloWorldApiResponse>() {
                    @Override
                    public void onResponse(Call<HelloWorldApiResponse> call, Response<HelloWorldApiResponse> response) {
                        HelloWorldApiResponse apiResponse = response.body();
                        System.out.println(apiResponse.getHelloworld());
                        textViewAnswer.setText(apiResponse.getHelloworld());
                    }

                    @Override
                    public void onFailure(Call<HelloWorldApiResponse> call, Throwable t) {
                        t.printStackTrace();
                        Toast toast = Toast.makeText(v.getContext(), "Communication failed. Verify ipv4 and port in settings.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
        });

        buttonGoOrders = (Button) findViewById(R.id.button_view_orders);
        buttonGoOrders.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OrdersActivity.class);
                startActivity(intent);
            }
        });

        textView1 = findViewById(R.id.textView1);
    }

    protected void onResume() {
        sensorManager.registerListener(this, accelerometer, sensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, light, sensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    protected void onPause() {
        sensorManager.unregisterListener(this);
        super.onPause();
    }


    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            textView1.setText("x: " + x + "\ny: " + y + "\nz: " + z);

            switch (display.getRotation()) {
                case Surface.ROTATION_0:
                    x = event.values[0];
                    y = event.values[1];
                    break;
                case Surface.ROTATION_90:
                    x = -event.values[1];
                    y = event.values[0];
                    break;
                case Surface.ROTATION_180:
                    x = -event.values[0];
                    y = -event.values[1];
                    break;
                case Surface.ROTATION_270:
                    x = event.values[1];
                    y = -event.values[0];
                    break;
            }

            if(isSaving){
                listX.add(x);
                listY.add(y);
                listZ.add(z);
                List tempList = new ArrayList();
                tempList.add(x);
                tempList.add(y);
                tempList.add(z);
                allDataList.add(tempList);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String getIpv4PortAddress(){
        String ipv4 = sharedPref.getString(getString(R.string.ipv4), "");
        String port = sharedPref.getString(getString(R.string.port), "");
        String address = "http://" + ipv4 + ":" + port + "/";
        return address;
    }
}