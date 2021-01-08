package com.example.mobileprojectapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import com.example.mobileprojectapp.Database.DatabaseConnection;
import com.example.mobileprojectapp.R;

public class PlayerBagActivity extends AppCompatActivity {

    TextView txtHealthQuantity;
    TextView txtReviveQuantity;
    TextView txtPokeballQuantity;
    TextView txtMasterBallQuantity;

    DatabaseConnection db;

    int playerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_bag);

        db = new DatabaseConnection(this);

        Intent intent = getIntent();
        playerID = intent.getIntExtra("playerID",0);
        txtHealthQuantity = (TextView) findViewById(R.id.bag_txt_healthPotion);
        txtReviveQuantity = (TextView) findViewById(R.id.bag_txt_revivePotion);
        txtPokeballQuantity = (TextView) findViewById(R.id.bag_txt_pokeBall);
        txtMasterBallQuantity = (TextView) findViewById(R.id.bag_txt_masterBall);

        getPlayerBag(playerID);

    }

    private void getPlayerBag(int PlayerID){
        Cursor resultSet = db.getPlayerBag(PlayerID);
        while(resultSet.moveToNext()){
            txtHealthQuantity.setText(getString(R.string.Owned,resultSet.getInt(3)));
            txtReviveQuantity.setText(getString(R.string.Owned,resultSet.getInt(4)));
            txtPokeballQuantity.setText(getString(R.string.Owned,resultSet.getInt(5)));
            txtMasterBallQuantity.setText(getString(R.string.Owned,resultSet.getInt(6)));
        }
    }
}
