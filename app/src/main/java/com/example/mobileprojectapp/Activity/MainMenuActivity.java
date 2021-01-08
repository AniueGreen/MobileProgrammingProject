package com.example.mobileprojectapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;

import com.example.mobileprojectapp.Database.DatabaseConnection;
import com.example.mobileprojectapp.R;
import com.google.android.material.button.MaterialButton;

import java.util.Arrays;


public class MainMenuActivity extends AppCompatActivity {

    MaterialButton btnStart;
    MaterialButton btnBag;
    MaterialButton btnStore;
    MaterialButton btnLogout;

    DatabaseConnection db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        db = new DatabaseConnection(this);
        btnStart = (MaterialButton) findViewById(R.id.main_btn_battle);
        btnBag = (MaterialButton) findViewById(R.id.main_btn_bag);
        btnStore = (MaterialButton) findViewById(R.id.main_btn_store);
        btnLogout = (MaterialButton) findViewById(R.id.main_btn_logout);

        Intent intent = getIntent();
        final int PlayerID = intent.getIntExtra("playerID",0);
        System.err.println("Main Menu PlayerID : " + PlayerID);

        Cursor cursor = db.getPlayerPokemon(PlayerID);
        System.err.println(Arrays.toString(cursor.getColumnNames()));
        while (cursor.moveToNext()){
            System.err.println("Move : " + cursor.getString(8));
            System.err.println("Damage : " + cursor.getLong(9));
        }

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, BattleActivity.class);
                intent.putExtra("playerID",PlayerID);
                startActivity(intent);
            }
        });

        btnBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, PlayerBagActivity.class);
                intent.putExtra("playerID",PlayerID);
                startActivity(intent);
            }
        });

        btnStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, StoreActivity.class);
                intent.putExtra("playerID",PlayerID);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

}
