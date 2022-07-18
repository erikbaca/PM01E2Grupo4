package com.cdp.pm01examengrupo4;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;

public class SegundoActividad extends AppCompatActivity {

    String id ;
    String nombre;
//    String url ;
    String latitud ;
    String longitud;
    String telefono;
    VideoView videopantalla2;

    public static int resultado = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segundo_actividad);

        getSupportActionBar().hide();
        VideoView videopantalla2 = (VideoView) findViewById(R.id.videoView);

        TextView caja = (TextView) findViewById(R.id.caja);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        nombre = intent.getStringExtra("nombre");
        telefono = intent.getStringExtra("telefono");
        latitud = intent.getStringExtra("latitud");
        longitud = intent.getStringExtra("longitud");

        //Llamado de la funcion
        getVideo();

        String texto = "Informacion personal\n\n"+
                "ID: "+id + "\n"+
                "Nombre: "+nombre + "\n"+
                "Telefono: "+telefono + "\n"+
                "Latitud: "+latitud + "\n"+
                "Longitud: "+longitud + "\n";

        caja.setText(texto);

        findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Eliminar();

            }
        });

        // Funcion actualizar el contacto.
        findViewById(R.id.btnUpdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Actualizar.class);


                Actualizar.setId(id);
                Actualizar.setLatitud(latitud);
                Actualizar.setLongitud(longitud);
                Actualizar.setNombre1(nombre);
                Actualizar.setTelefono(telefono);
//                Actualizar.setUrl(url);



                startActivityForResult(intent,resultado);
            }
        });

        //Funcion Localizacion del contacto.
        findViewById(R.id.btnLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String geoUri = "http://maps.google.com/maps?q=loc:" + latitud + "," + longitud + " (" + "Destino" + ")";
                Uri location = Uri.parse(geoUri);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
                startActivity(mapIntent);
            }
        });

    }

   // ----------------
    private void getVideo(){
        String url = "http://droidnotes.herokuapp.com/api/examen_grupo4/contacts/tableContacts/21";
        StringRequest postResquest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONArray resultArray = new JSONArray(response);
                    JSONObject jsonObject = resultArray.getJSONObject(0);

                    System.out.println(jsonObject.getJSONObject("video").getString("data"));
                    String videoString = jsonObject.getJSONObject("video").getString("data");
                    byte[] videoArrayByte = Base64.decode(videoString, Base64.DEFAULT);
                    InputStream videoStream = new ByteArrayInputStream(videoArrayByte);


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError err){

            }
        });
        Volley.newRequestQueue(this).add(postResquest);
    }
    // -------

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

    // Funcion Eliminar el contacto.
    private void Eliminar()
    {

        try {
            String url = "http://droidnotes.herokuapp.com/api/examen_grupo4/contacts/tableContacts/" + id;


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, new JSONObject(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(getApplicationContext(),"Se ha Eliminado exitosamente :)",Toast.LENGTH_SHORT).show();
                            Intent list = new Intent(getApplicationContext(),Listado.class);
                            startActivity(list);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Error", "Error: " + error.getMessage());
                }
            });


            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(jsonObjectRequest);

        }catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "Error : " + e, Toast.LENGTH_LONG).show();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == resultado && requestCode == RESULT_OK) {


        }
    }



}