package com.example.mobileprojectapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Random;

public class BattleActivity extends AppCompatActivity {

    //region Declare Components
    ImageView imgPlayerPokemon;
    ImageView imgWildPokemon;
    ImageView imgHealthPotion;

    TextView txtPlayerPokemonName;
    TextView txtPlayerPokemonLevel;
    TextView txtWildPokemonName;
    TextView txtWildPokemonLevel;
    TextView txtBattleHistory;
    TextView txtHealthPotionQuantity;
    TextView txtRevivePotionQuantity;
    TextView txtPokeBallQuantity;
    TextView txtMasterBallQuantity;

    ProgressBar prgPlayerPokemonHealth;
    ProgressBar prgWildPokemonHealth;

    MaterialButton btnFirstMove;
    MaterialButton btnSecondMove;
    MaterialButton btnThirdMove;
    MaterialButton btnFourthMove;

    //endregion

    static Pokemon wildPokemon = new Pokemon();
    static Pokemon playerPokemon = new Pokemon();

    ArrayList<Pokemon> pokemonList = new ArrayList<>();

    DatabaseConnection db;

    int playerID = 0;
    int pokemonMaxHealth;
    int playerBalance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);
        //region findViewByID

        imgPlayerPokemon = (ImageView) findViewById(R.id.battle_img_playerPokemon);
        imgWildPokemon = (ImageView) findViewById(R.id.battle_img_wildPokemon);
        imgHealthPotion = (ImageView) findViewById(R.id.battle_img_healthPotion);

        txtPlayerPokemonName = (TextView) findViewById(R.id.battle_txt_playerPokemonName);
        txtPlayerPokemonLevel = (TextView) findViewById(R.id.battle_txt_playerPokemonLevel);
        txtWildPokemonName = (TextView) findViewById(R.id.battle_txt_wildPokemonName);
        txtWildPokemonLevel = (TextView) findViewById(R.id.battle_txt_wildPokemonLevel);
        txtBattleHistory = (TextView) findViewById(R.id.battle_txt_battleHistory);

        prgPlayerPokemonHealth = (ProgressBar) findViewById(R.id.battle_prg_playerPokemonHealth);
        prgWildPokemonHealth = (ProgressBar) findViewById(R.id.battle_prg_wildPokemonHealth);

        btnFirstMove = (MaterialButton) findViewById(R.id.battle_btn_firstMove);
        btnSecondMove = (MaterialButton) findViewById(R.id.battle_btn_secondMove);
        btnThirdMove = (MaterialButton) findViewById(R.id.battle_btn_thirdMove);
        btnFourthMove = (MaterialButton) findViewById(R.id.battle_btn_fourthMove);

        txtHealthPotionQuantity = (TextView) findViewById(R.id.battle_txt_healthPotionQuantity);
        txtRevivePotionQuantity = (TextView) findViewById(R.id.battle_txt_revivePotionQuantity);
        txtPokeBallQuantity = (TextView) findViewById(R.id.battle_txt_pokeBallQuantity);
        txtMasterBallQuantity = (TextView) findViewById(R.id.battle_txt_masterBallQuantity);

        //endregion

        db = new DatabaseConnection(this);
        Intent intent = getIntent();
        playerID = intent.getIntExtra("playerID",0);

        //region Initialize Wild Pokemon
        getPokemon();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                setWildPokemon();
            }
        }, 1500);

        //endregion

        //region Initialize Player Pokemon

        setPlayerPokemon();
        setPlayerUI();
        pokemonMaxHealth = playerPokemon.getPokemonHP();
        playerTurn(btnFirstMove,playerPokemon.getFirstMoveDamage());
        playerTurn(btnSecondMove,playerPokemon.getSecondMoveDamage());
        playerTurn(btnThirdMove,playerPokemon.getThirdMoveDamage());
        playerTurn(btnFourthMove,playerPokemon.getFourthMoveDamage());
        //endregion

    }


    private void initiateBattle(){
        txtBattleHistory.setText(getString(R.string.InitiateBattle,wildPokemon.getPokemonName()));
    }

    private void playerTurn(final MaterialButton attackButton , final Integer damage){
        attackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wildPokemon.setPokemonHP(Math.round(wildPokemon.getPokemonHP() - playerPokemon.getPokemonStrength() * damage));
                if(wildPokemon.getPokemonHP() <= 0){
                    disableUI();
                    prgWildPokemonHealth.setProgress(0,true);
                    battleEnded();
                }else{
                    txtBattleHistory.setText(getString(R.string.PlayerBattleActions,playerPokemon.getPokemonName(),attackButton.getText()));
                    prgWildPokemonHealth.setProgress(wildPokemon.getPokemonHP(),true);
                    setTurn();
                }
            }
        });
    }

    private void wildPokemonTurn(){
       Random r = new Random();
       int move = r.nextInt(4 - 1) + 1;
       switch (move){
           case 1:
               playerPokemon.setPokemonHP(Math.round(playerPokemon.getPokemonHP() - (wildPokemon.getFirstMoveDamage() * wildPokemon.getPokemonStrength())));
               prgPlayerPokemonHealth.setProgress(playerPokemon.getPokemonHP());
               txtBattleHistory.setText(getString(R.string.WildPokemonBattleActions,wildPokemon.getPokemonName(),wildPokemon.getFirstMoveName()));
               break;
           case 2:
               playerPokemon.setPokemonHP(Math.round(playerPokemon.getPokemonHP() - (wildPokemon.getSecondMoveDamage() * wildPokemon.getPokemonStrength())));
               prgPlayerPokemonHealth.setProgress(playerPokemon.getPokemonHP());
               txtBattleHistory.setText(getString(R.string.WildPokemonBattleActions,wildPokemon.getPokemonName(),wildPokemon.getSecondMoveName()));
               break;
           case 3:
               playerPokemon.setPokemonHP(Math.round(playerPokemon.getPokemonHP() - (wildPokemon.getThirdMoveDamage() * wildPokemon.getPokemonStrength())));
               prgPlayerPokemonHealth.setProgress(playerPokemon.getPokemonHP());
               txtBattleHistory.setText(getString(R.string.WildPokemonBattleActions,wildPokemon.getPokemonName(),wildPokemon.getThirdMoveName()));
               break;
           case 4:
               playerPokemon.setPokemonHP(Math.round(playerPokemon.getPokemonHP() - (wildPokemon.getFourthMoveDamage() * wildPokemon.getPokemonStrength())));
               prgPlayerPokemonHealth.setProgress(playerPokemon.getPokemonHP());
               txtBattleHistory.setText(getString(R.string.WildPokemonBattleActions,wildPokemon.getPokemonName(),wildPokemon.getFourthMoveName()));
               break;
       }
       if(playerPokemon.getPokemonHP() <= 0){
           txtBattleHistory.setText(getString(R.string.PlayerPokemonFainted,playerPokemon.getPokemonName()));
           disableUI();
       }else{
           enableUI();
       }
    }

    private void disableUI(){
        btnFirstMove.setEnabled(false);
        btnSecondMove.setEnabled(false);
        btnThirdMove.setEnabled(false);
        btnFourthMove.setEnabled(false);
    }

    private void enableUI(){
        btnFirstMove.setEnabled(true);
        btnSecondMove.setEnabled(true);
        btnThirdMove.setEnabled(true);
        btnFourthMove.setEnabled(true);
    }

    View.OnClickListener useHealthPotionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int quantity = Integer.parseInt(txtHealthPotionQuantity.getText().toString());
            if(quantity != 0){
                if(playerPokemon.getPokemonHP() + 100 > pokemonMaxHealth){
                    playerPokemon.setPokemonHP(pokemonMaxHealth);
                    prgPlayerPokemonHealth.setProgress(playerPokemon.getPokemonHP());
                    quantity--;
                    db.updateHealthPotionQuantity(playerID,quantity);
                    txtHealthPotionQuantity.setText(String.valueOf(quantity));
                    txtBattleHistory.setText(getString(R.string.PlayerItemUse,"Health Potion",playerPokemon.getPokemonName()));
                    setTurn();
                }else{
                    playerPokemon.setPokemonHP(playerPokemon.getPokemonHP() + 100);
                    prgPlayerPokemonHealth.setProgress(playerPokemon.getPokemonHP());
                    quantity--;
                    db.updateHealthPotionQuantity(playerID,quantity);
                    txtHealthPotionQuantity.setText(String.valueOf(quantity));
                    txtBattleHistory.setText(getString(R.string.PlayerItemUse,"Health Potion",playerPokemon.getPokemonName()));
                    setTurn();
                }
            }
        }
    };

    private void setTurn(){
        disableUI();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                wildPokemonTurn();
            }
        }, 1000);
    }

    private void battleEnded(){
        Handler fainted = new Handler();
        fainted.postDelayed(new Runnable() {
            public void run() {
                txtBattleHistory.setText(getString(R.string.WildPokemonFainted,wildPokemon.getPokemonName()));
            }
        }, 1500);

        Handler moneyEarned = new Handler();
        moneyEarned.postDelayed(new Runnable() {
            public void run() {
                int earned = new Random().nextInt(50) + 50;
                txtBattleHistory.setText(getString(R.string.MoneyEarned,earned));
                db.updatePlayerBalance(playerID,playerBalance + earned);
            }
        }, 3000);

        final Handler xpGained = new Handler();
        xpGained.postDelayed(new Runnable() {
            public void run() {
                txtBattleHistory.setText(getString(R.string.XPEarned,playerPokemon.getPokemonName(),20));
//                if(playerPokemon.getPokemonNextLevel() <= (playerPokemon.getPokemonXP() + 20)){
//
//                }
            }
        }, 4500);
    }

    //region PlayerMethods

    private void setPlayerPokemon(){
        Cursor resultSet = db.getPlayerPokemon(playerID);
        while(resultSet.moveToNext()){
            playerPokemon.setPokemonName(resultSet.getString(2));
            playerPokemon.setPokemonID(resultSet.getInt(3));
            playerPokemon.setPokemonHP(resultSet.getInt(4));
            playerPokemon.setPokemonLevel(resultSet.getInt(5));
            playerPokemon.setPokemonStrength(resultSet.getFloat(7));
            playerPokemon.setFirstMoveName(resultSet.getString(8));
            playerPokemon.setFirstMoveDamage(resultSet.getInt(9));
            playerPokemon.setSecondMoveName(resultSet.getString(10));
            playerPokemon.setSecondMoveDamage(resultSet.getInt(11));
            playerPokemon.setThirdMoveName(resultSet.getString(12));
            playerPokemon.setThirdMoveDamage(resultSet.getInt(13));
            playerPokemon.setFourthMoveName(resultSet.getString(14));
            playerPokemon.setFirstMoveDamage(resultSet.getInt(15));
            playerPokemon.setPokemonXP(resultSet.getInt(16));
        }
    }

    private void setPlayerItems(){
        Cursor resultSet = db.getPlayerBag(playerID);
        while(resultSet.moveToNext()){
            txtHealthPotionQuantity.setText(String.valueOf(resultSet.getInt(3)));
            txtRevivePotionQuantity.setText(String.valueOf(resultSet.getInt(4)));
            txtPokeBallQuantity.setText(String.valueOf(resultSet.getInt(5)));
            txtMasterBallQuantity.setText(String.valueOf(resultSet.getInt(6)));
            playerBalance = resultSet.getInt(2);
        }
    }

    private void setPlayerUI(){
        //Set player pokemon card
        txtPlayerPokemonName.append(playerPokemon.getPokemonName());
        getPokemonPictures(playerPokemon.getPokemonID(),imgPlayerPokemon);
        txtPlayerPokemonLevel.append(String.valueOf(playerPokemon.getPokemonLevel()));

        //Set button texts
        btnFirstMove.setText(playerPokemon.getFirstMoveName());
        btnSecondMove.setText(playerPokemon.getSecondMoveName());
        btnThirdMove.setText(playerPokemon.getThirdMoveName());
        btnFourthMove.setText(playerPokemon.getFourthMoveName());

        prgPlayerPokemonHealth.setMax(playerPokemon.getPokemonHP());
        prgPlayerPokemonHealth.setProgress(playerPokemon.getPokemonHP(),true);

        setPlayerItems();
        imgHealthPotion.setOnClickListener(useHealthPotionListener);
    }

    //endregion

    //region WildPokemonMethods

    private void setWildPokemon(){
        wildPokemon = randomPokemon();
        txtWildPokemonName.append(wildPokemon.getPokemonName());
        txtWildPokemonLevel.append(String.valueOf(wildPokemon.getPokemonLevel()));
        getPokemonPictures(wildPokemon.getPokemonID(),imgWildPokemon);
        prgWildPokemonHealth.setMax(wildPokemon.getPokemonHP());
        prgWildPokemonHealth.setProgress(wildPokemon.getPokemonHP());
        initiateBattle();
        getWildPokemonMoves();
    }

    private Pokemon randomPokemon(){
        Pokemon pokemon;
        final int random = new Random().nextInt(30);
        pokemon = pokemonList.get(random);
        pokemon.setPokemonHP(new Random().nextInt(150) + 150);
        pokemon.setPokemonLevel(new Random().nextInt(2) + 20);
        pokemon.setPokemonStrength((0.1F + new Random().nextFloat() * (1F - 0.1F)));
        System.out.println("Strength : " + pokemon.getPokemonStrength());
        return pokemon;
    }

    private void getWildPokemonMoves(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://pokeapi.co/api/v2/pokemon/" + wildPokemon.getPokemonID();
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
                                        wildPokemon.setFirstMoveName(res.getJSONObject("move").getString("name"));
                                        wildPokemon.setFirstMoveDamage(new Random().nextInt(50) + 50);
                                        break;
                                    case 1:
                                        wildPokemon.setSecondMoveName(res.getJSONObject("move").getString("name"));
                                        wildPokemon.setSecondMoveDamage(new Random().nextInt(50) + 50);
                                        break;
                                    case 2:
                                        wildPokemon.setThirdMoveName(res.getJSONObject("move").getString("name"));
                                        wildPokemon.setThirdMoveDamage(new Random().nextInt(50) + 50);
                                        break;
                                    case 3:
                                        wildPokemon.setFourthMoveName(res.getJSONObject("move").getString("name"));
                                        wildPokemon.setFourthMoveDamage(new Random().nextInt(50) + 50);
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
                Toast.makeText(BattleActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(request);
    }

    //endregion

    //region Volley Methods

    private void getPokemon(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://pokeapi.co/api/v2/pokemon?limit=30";
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
                                pokemonList.add(pokemon);
                            }
                        }catch (JSONException e){
                            e.getStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BattleActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(request);
    }

    private void getPokemonPictures(int pokemonID,final ImageView imgView){
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
                        imgView.setImageResource(R.drawable.add);
                    }
                }
        );
        queue.add(request);
    }

    //endregion


}
