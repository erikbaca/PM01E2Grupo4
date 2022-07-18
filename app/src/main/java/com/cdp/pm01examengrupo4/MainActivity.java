package com.cdp.pm01examengrupo4;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    VideoView VideoView;
    EditText nombre, numero;
    TextView tvlatitud, tvlongitud;
    public static String latitud = "";
    public static String longitud = "";
    public static final int recordVideo = 4;
    public static String videoData = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        VideoView =findViewById(R.id.VideoPantalla);
        nombre = findViewById(R.id.txtnombre);
        numero = findViewById(R.id.txtnumero);
        tvlatitud = findViewById(R.id.txtlatitud);
        tvlongitud = findViewById(R.id.txtlongitud);



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        }
        else
        {
            locationStart();
        }

        findViewById(R.id.btnListContact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Listado.class);
                startActivity(i);
            }
        });


        findViewById(R.id.btnVideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(getApplicationContext(), "Boton Video",Toast.LENGTH_LONG).show();

                GrabarVideo();

            }
        });


        findViewById(R.id.btnSalvar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VideoView != null) {


                    if (nombre.getText().toString().trim().length() == 0) {
                        nombre.setError("CAMPO OBLIGATORIO");

                    } else {
                        nombre.setError(null);
                        if (numero.getText().toString().trim().length() == 0) {
                            numero.setError("CAMPO OBLIGATORIO");
                        } else {
                            numero.setError(null);
                            if (numero.getText().toString().length() >= 11) {
                                numero.setError("MAXIMO 11 CARACTERES");
                            } else {
                                numero.setError(null);
                                crear();
                            }
                        }

                    }


                } else {
                    if (nombre.getText().toString().trim().length() == 0) {
                        nombre.setError("CAMPO OBLIGATORIO");
                    } else {
                        nombre.setError(null);
                    }

                    if (numero.getText().toString().trim().length() == 0) {
                        numero.setError("CAMPO OBLIGATORIO");
                    } else {
                        numero.setError(null);
                    }

                    if (numero.getText().toString().length()  >=  11 ) {
                        numero.setError("MAXIMO 8 CARACTERES");
                    } else {
                        numero.setError(null);
                    }


                    AlertaDialogo("Graba un video", "Upps!!");
                }
            }


    /* *Guardar la informacion del contacto en la RestApi* */
            private void crear() {


                String url = "http://droidnotes.herokuapp.com/api/examen_grupo4/contacts/tableContacts";

                HashMap<String,String> params = new HashMap<>();
                params.put("nombre", nombre.getText().toString().toLowerCase());
                params.put("telefono", numero.getText().toString());
                params.put("latitud", latitud.toString());
                params.put("longitud", longitud.toString());
                params.put("video", videoData.toString());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                AlertaDialogo("INGRESO EXITOSO", "REGISTRO EXITOSO");
                                //Toast.makeText(getApplicationContext(),"Se ha Ingresado Exitosamente",Toast.LENGTH_SHORT).show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("Error", "Error: " + error.getMessage());
                        AlertaDialogo("Error al ingresar " + error.getMessage(), "Error");
                    }
                });


                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(jsonObjectRequest);

                Limpiar();
            }
        });



    }


    //Funcion limpiar campos
    private void Limpiar()
    {
        nombre.setText("");
        numero.setText("");
    }

    // Grabar el Video
    private void GrabarVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, recordVideo);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == recordVideo && resultCode == RESULT_OK){
            Uri videoUri = data.getData();
            VideoView.setVideoURI(videoUri);
            VideoView.start();

            try {
                AssetFileDescriptor videoAsset = getContentResolver().openAssetFileDescriptor(data.getData(), "r");
                FileInputStream in = videoAsset.createInputStream();
                byte[] buf = new byte[1024*60];
                ByteArrayOutputStream objByteArrayOS = new ByteArrayOutputStream();
                try
                {
                    for (int readNum; (readNum = in.read(buf)) != -1;)
                    {
                        objByteArrayOS.write(buf, 0, readNum);
                        System.out.println("read " + readNum + " bytes,");
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                String videodata = Base64.encodeToString(buf, Base64.DEFAULT);
                Log.d("VideoData**>  " ,videodata);
                videoData=videodata;
            }
            catch (IOException e)
            {
                Toast.makeText(this, "Error: ", Toast.LENGTH_LONG).show();
            }
        }

    }


    private String GetStringImage(Bitmap imagen)
    {
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        imagen.compress(Bitmap.CompressFormat.JPEG,100, ba);
        byte[] imagebyte = ba.toByteArray();
        String encode = Base64.encodeToString(imagebyte, Base64.DEFAULT);
        return "data:image/png;base64,"+encode;
    }

    private void AlertaDialogo(String mensaje, String title){

        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setMessage(mensaje)
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog titulo = alerta.create();
        titulo.setTitle(title);
        titulo.show();

    }

    public void permisosUbicacion(){
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    public void DatosUbicacion(){
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {

            return;
        }else
        {
            LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            latitud = loc.getLatitude()+"";
            longitud = loc.getLongitude()+"";

            tvlatitud.setText(latitud);
            tvlongitud.setText(longitud);

        }
    }

    public void otro(){


        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            permisosUbicacion();

        }else
        {
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    latitud = location.getLatitude()+"";
                    longitud = location.getLongitude()+"";

                    tvlatitud.setText(latitud);
                    tvlongitud.setText(longitud);
                }
            };

            int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);



        }

    }




   // LOCATION
    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);


    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }

    //Obtener la direccion partir de la logitud latitud.
    public void setLocation(Location loc) {
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /// Aqui empieza la Clase Location
    public class Localizacion implements LocationListener {
        MainActivity mainActivity;

        public MainActivity getMainActivity() {
            return mainActivity;
        }

        public void setMainActivity(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }


        // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
        @Override
        public void onLocationChanged(Location loc) {
            loc.getLatitude();
            loc.getLongitude();

            String Text = "Mi ubicacion actual es: " + "\n Lat = "
                    + loc.getLatitude() + "\n Long = " + loc.getLongitude();


            MainActivity.setLatitud(loc.getLatitude()+"");
            MainActivity.setLongitud(loc.getLongitude()+"");
            tvlatitud.setText(loc.getLatitude()+"");
            tvlongitud.setText(loc.getLongitude()+"");
            this.mainActivity.setLocation(loc);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado

        }

        @Override
        public void onProviderEnabled(String provider) {
            // Al estar activo el GPS activado

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }
    ///


    public static String getLatitud() {
        return latitud;
    }

    public static void setLatitud(String latitud) {
        MainActivity.latitud = latitud;
    }

    public static String getLongitud() {
        return longitud;
    }

    public static void setLongitud(String longitud) {
        MainActivity.longitud = longitud;
    }
}