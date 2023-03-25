package com.example.ipaddresstask;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    TextView Ip;
    Button b;
    FirebaseDatabase database;
    DatabaseReference myRef;
    IpAddress ipadd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Ip = findViewById(R.id.textView2);
        b=findViewById(R.id.button);


        database = FirebaseDatabase.getInstance() ;
        myRef = database.getReference("IpAddress");

        ipadd = new IpAddress();


        String url = getString(R.string.server_url_Myip);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Gson gson = new Gson();

                        String dataArray = null;

                        try {
                            dataArray = response.getString("ip");
                        } catch (JSONException e) {
                            Log.e(this.getClass().toString(), e.getMessage());
                        }


                        Ip.setText(dataArray);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Ip.setText("Error! " + error.toString());
                    }
                });

        queue.add(jsonObjectRequest);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = Ip.getText().toString();
                addDatatoFirebase(ip);
            }
        });
    }
    private void addDatatoFirebase(String ip) {

        ipadd.setIP(ip);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                myRef.setValue(ipadd);

                Toast.makeText(MainActivity.this, "data added", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(MainActivity.this, "Fail to add data " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}



