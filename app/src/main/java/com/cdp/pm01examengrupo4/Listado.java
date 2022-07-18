package com.cdp.pm01examengrupo4;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Listado extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private List<Modelo> mLista = new ArrayList<>();
    private ListView mlistView;
    ListAdapter mAdapter;
    EditText txtBuscador;
    public static int resultado = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado);

        getSupportActionBar().hide();

        mlistView = (ListView) findViewById(R.id.mlistView);
        txtBuscador = (EditText) findViewById(R.id.txtBuscador);
        mlistView.setOnItemClickListener(this);

        BusquedadPersonalizada("");

        findViewById(R.id.RegresarMain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });


        txtBuscador.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {


                if (s.toString().trim().length() == 0) {
                    BusquedadPersonalizada("");
                } else {
                    BusquedadPersonalizada(s.toString());
                }


            }
        });
    }

    // Clase Busqueda de contacto conectada con ResApi
    private void BusquedadPersonalizada(String dato) {
        String url = "http://droidnotes.herokuapp.com/api/examen_grupo4/contacts/tableContacts/" + dato + "";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;

                 mLista = new ArrayList<>();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        Modelo modelo = new Modelo();
                        modelo.setId(jsonObject.getString("id"));
                        modelo.setNombre(jsonObject.getString("nombre"));
                        modelo.setTelefono(jsonObject.getString("telefono"));
                        modelo.setLatitud(jsonObject.getString("latitud"));
                        modelo.setLongitud(jsonObject.getString("longitud"));
                        modelo.setUrl(jsonObject.getString("video"));

                        mLista.add(modelo);


                        mAdapter = new ListAdapter(getApplicationContext(), R.layout.item_row, mLista);

                        mlistView.setAdapter(mAdapter);

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error de conexion al buscar", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }


     // Al seleccionar el cuerpo de cada Registro de mostrara opciones correspondiente
    // al contacto.
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        try {
            Intent intent = new Intent(getApplicationContext(), SegundoActividad.class);
            intent.putExtra("id", mLista.get(position).getId());
            intent.putExtra("nombre", mLista.get(position).getNombre());
            intent.putExtra("telefono", mLista.get(position).getTelefono());
            //intent.putExtra("url", mLista.get(position).getUrl());
            intent.putExtra("latitud", mLista.get(position).getLatitud());
            intent.putExtra("longitud", mLista.get(position).getLongitud());
            startActivityForResult(intent, resultado);
        }catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "Error : " + e, Toast.LENGTH_LONG).show();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == resultado && requestCode == RESULT_OK) {
            BusquedadPersonalizada("");
            txtBuscador.setText("");
        }
    }

}