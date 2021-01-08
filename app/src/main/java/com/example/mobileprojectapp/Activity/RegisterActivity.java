package com.example.mobileprojectapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobileprojectapp.Database.DatabaseConnection;
import com.example.mobileprojectapp.Pokemon.Pokemon;
import com.example.mobileprojectapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    MaterialButton btnSave;
    TextInputEditText txtPlayerName;
    DatabaseConnection db;

    TextView txtFirstPokemon;
    TextView txtSecondPokemon;
    TextView txtThirdPokemon;

    ImageView imgFirstPokemon;
    ImageView imgSecondPokemon;
    ImageView imgThirdPokemon;

    final ArrayList<Pokemon> pokemonList = new ArrayList<>();

    boolean isSelected = false;
    int selectedPokemonIndex = -1;
    String selectedPokemonName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = new DatabaseConnection(this);

        btnSave = (MaterialButton) findViewById(R.id.register_btn_save);
        txtPlayerName = (TextInputEditText) findViewById(R.id.register_txt_playerName);

        imgFirstPokemon = (ImageView) findViewById(R.id.register_img_firstPokemon);
        imgSecondPokemon = (ImageView) findViewById(R.id.register_img_secondPokemon);
        imgThirdPokemon = (ImageView) findViewById(R.id.register_img_thirdPokemon);

        txtFirstPokemon = (TextView) findViewById(R.id.register_txt_firstPokemon);
        txtSecondPokemon = (TextView) findViewById(R.id.register_txt_secondPokemon);
        txtThirdPokemon = (TextView) findViewById(R.id.register_txt_thirdPokemon);

        getPokemon();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                JsonGetPokemonPictures(pokemonList.get(0).getPokemonID(),imgFirstPokemon);
                txtFirstPokemon.setText(pokemonList.get(0).getPokemonName());

                JsonGetPokemonPictures(pokemonList.get(3).getPokemonID(),imgSecondPokemon);
                txtSecondPokemon.setText(pokemonList.get(3).getPokemonName());

                JsonGetPokemonPictures(pokemonList.get(6).getPokemonID(),imgThirdPokemon);
                txtThirdPokemon.setText(pokemonList.get(6).getPokemonName());

                selectStarterPokemon(imgFirstPokemon,1,pokemonList.get(0).getPokemonName());
                selectStarterPokemon(imgSecondPokemon,4,pokemonList.get(3).getPokemonName());
                selectStarterPokemon(imgThirdPokemon,7,pokemonList.get(6).getPokemonName());
            }
        }, 2500);



        //region onClickListener

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtPlayerName.getText() != null && !txtPlayerName.getText().toString().trim().isEmpty() && selectedPokemonIndex != -1){
                    Long playerID = db.addNewPlayer(txtPlayerName.getText().toString());
                    if(playerID == 0){
                        Toast.makeText(RegisterActivity.this, "Register is not successful", Toast.LENGTH_SHORT).show();
                    }else{
                        int pokemonID = selectedPokemonIndex-1;
                        Pokemon selectedPokemon = pokemonList.get(pokemonID);
                        db.createUserBag(playerID);
                        db.addPlayerPokemon(playerID,selectedPokemon);
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }else{
                    Toast.makeText(RegisterActivity.this, "Please enter a user name and choose a starter pokemon", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //endregion
    }

    public void getPokemon(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://pokeapi.co/api/v2/pokemon?limit=10";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i = 0 ; i < jsonArray.length();i++){
                                JSONObject res = jsonArray.getJSONObject(i);
                                Pokemon pokemon = new Pokemon();
                                pokemon.setPokemonID(i+1);
                                String pokemonName = res.getString("name");
                                pokemon.setPokemonName(pokemonName.substring(0,1).toUpperCase() + pokemonName.substring(1));
                                pokemon.setPokemonHP(new Random().nextInt(150) + 150);
                                pokemon.setPokemonLevel(1);
                                pokemon.setPokemonNextLevel(100);
                                pokemon.setPokemonStrength((0.1F + new Random().nextFloat() * (1F - 0.1F)));
                                pokemon.setPokemonXP(1);
                                getPokemonMove(pokemon);

                                pokemonList.add(pokemon);
                            }
                        }catch (JSONException e){
                            e.getStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                          Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
        queue.add(request);
}

    public void JsonGetPokemonPictures(int pokemonID,final ImageView imgView){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://pokeres.bastionbot.org/images/pokemon/" + pokemonID + ".png";
        ImageRequest request = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        imgView.setImageBitmap(bitmap);
                    }
                }, 0, 0,ImageView.ScaleType.CENTER_CROP, // Image scale type
                Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        imgView.setImageResource(R.drawable.ic_launcher_foreground);
                    }
                }
                );
        queue.add(request);
    }

    public void getPokemonMove(final Pokemon pokemon){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://pokeapi.co/api/v2/pokemon/" + pokemon.getPokemonID();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("moves");
                            for (int i = 0 ; i < 4;i++){
                                JSONObject res = jsonArray.getJSONObject(i);
                                switch (i) {
                                    case 0:
                                        pokemon.setFirstMoveName(res.getJSONObject("move").getString("name"));
                                        pokemon.setFirstMoveDamage(new Random().nextInt(50) + 50);
                                        break;
                                    case 1:
                                        pokemon.setSecondMoveName(res.getJSONObject("move").getString("name"));
                                        pokemon.setSecondMoveDamage(new Random().nextInt(50) + 50);
                                        break;
                                    case 2:
                                        pokemon.setThirdMoveName(res.getJSONObject("move").getString("name"));
                                        pokemon.setThirdMoveDamage(new Random().nextInt(50) + 50);
                                        break;
                                    case 3:
                                        pokemon.setFourthMoveName(res.getJSONObject("move").getString("name"));
                                        pokemon.setFourthMoveDamage(new Random().nextInt(50) + 50);
                                        break;
                                }
                            }
                        }catch (JSONException e){
                            e.getStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(request);
    }

    public void selectStarterPokemon(final ImageView imgView,final int index,final String pokemonName){
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isSelected){
                    imgView.setBackgroundColor(Color.rgb(100, 100, 50));
                    selectedPokemonIndex = index;
                    selectedPokemonName = pokemonName;
                    isSelected = true;
                }else{
                    imgView.setBackgroundColor(Color.rgb(255, 255, 255));
                    selectedPokemonIndex = -1;
                    selectedPokemonName = "";
                    isSelected = false;
                }
            }
        });
    }
}
