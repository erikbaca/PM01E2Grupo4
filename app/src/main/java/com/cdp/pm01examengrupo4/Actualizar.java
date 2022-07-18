package com.cdp.pm01examengrupo4;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

public class Actualizar extends AppCompatActivity {

    VideoView videoview;
    EditText nombre, numero;
    TextView tvlatitud, tvlongitud,mensaje;

//    static final int RESULT_GALLERY_IMG = 100;

    public static final int recordVideo = 4;
    public static String videoData = "";

    public static String latitud = "";
    public static String longitud = "";

    public static String id;
    public static String nombre1;
    public static String url;
    public static String telefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar);

        getSupportActionBar().hide();

        videoview =findViewById(R.id.Videoview);
        nombre = findViewById(R.id.txtnombre);
        numero = findViewById(R.id.txtnumero);
        tvlatitud = findViewById(R.id.txtlatitud);
        tvlongitud = findViewById(R.id.txtlongitud);
        mensaje = findViewById(R.id.mensaje);


        nombre.setText(nombre1+"");
        numero.setText(telefono);
        tvlatitud.setText(latitud);
        tvlongitud.setText(longitud);
        mensaje.setText("");

        findViewById(R.id.btnVideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GrabarVideo();

            }
        });

        findViewById(R.id.btnSalvar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoview != null) {


                    if (nombre.getText().toString().trim().length() == 0) {
                        nombre.setError("Este campo es obligatorio");

                    } else {
                        nombre.setError(null);
                        if (numero.getText().toString().trim().length() == 0) {
                            numero.setError("Este campo es obligatorio");
                        } else {
                            numero.setError(null);
                            if (numero.getText().toString().length() >= 11) {
                                numero.setError("No se puede poner mayor de 11 cracteres");
                            } else {
                                numero.setError(null);
                                Actualizar();
                            }
                        }

                    }


                } else {
                    if (nombre.getText().toString().trim().length() == 0) {
                        nombre.setError("Este campo es obligatorio");
                    } else {
                        nombre.setError(null);
                    }

                    if (numero.getText().toString().trim().length() == 0) {
                        numero.setError("Este campo es obligatorio");
                    } else {
                        numero.setError(null);
                    }

                    if (numero.getText().toString().length()  >=  11 ) {
                        numero.setError("No se puede poner mayor de 8 cracteres");
                    } else {
                        numero.setError(null);
                    }


                    AlertaDialogo("Seleccione la imagen por favor", "Imagen no seleccionada");
                }
            }
        });


    }

    private void Actualizar()
    {

        String url = "http://droidnotes.herokuapp.com/api/examen_grupo4/contacts/tableContacts";

        HashMap<String,String> params = new HashMap<>();
        params.put("nombre", nombre.getText().toString().toLowerCase());
        params.put("telefono", numero.getText().toString());
        params.put("latitud", latitud.toString());
        params.put("longitud", longitud.toString());
        params.put("video", videoData.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, new JSONObject(params),
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



    }


    // ---------- Grabar Video

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
            videoview.setVideoURI(videoUri);
            videoview.start();

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

    // ---------------------




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

    public static String getLatitud() {
        return latitud;
    }

    public static void setLatitud(String latitud) {
        Actualizar.latitud = latitud;
    }

    public static String getLongitud() {
        return longitud;
    }

    public static void setLongitud(String longitud) {
        Actualizar.longitud = longitud;
    }

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        Actualizar.id = id;
    }

    public static String getNombre1() {
        return nombre1;
    }

    public static void setNombre1(String nombre1) {
        Actualizar.nombre1 = nombre1;
    }

    public static String getUrl() {
        return url;
    }

    public static void setUrl(String url) {
        Actualizar.url = url;
    }

    public static String getTelefono() {
        return telefono;
    }

    public static void setTelefono(String telefono) {
        Actualizar.telefono = telefono;
    }



}