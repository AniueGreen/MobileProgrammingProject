package com.example.mobileprojectapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mobileprojectapp.Database.DatabaseConnection;
import com.example.mobileprojectapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.Arrays;

public class StoreActivity extends AppCompatActivity {
    DatabaseConnection db;

    MaterialTextView healthPotionQuantity;
    MaterialTextView healthPotionQuantityDecrease;
    MaterialTextView healthPotionQuantityIncrease;

    MaterialTextView reviveQuantity;
    MaterialTextView reviveQuantityDecrease;
    MaterialTextView reviveQuantityIncrease;

    MaterialTextView pokeballQuantity;
    MaterialTextView pokeballQuantityDecrease;
    MaterialTextView pokeballQuantityIncrease;

    MaterialTextView masterballQuantity;
    MaterialTextView masterballQuantityDecrease;
    MaterialTextView masterballQuantityIncrease;

    MaterialTextView txtTotalCost;
    MaterialTextView txtPlayerBalance;

    MaterialButton btnBuy;

    int totalCost = 0;
    int playerBalance = 0;
    int playerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        db = new DatabaseConnection(this);

        Intent intent = getIntent();
        playerID = intent.getIntExtra("playerID",0);

        healthPotionQuantity = (MaterialTextView) findViewById(R.id.store_txt_healthPotionQuantity);
        healthPotionQuantityDecrease = (MaterialTextView) findViewById(R.id.store_txt_healthPotionQuantityDecrease);
        healthPotionQuantityIncrease = (MaterialTextView) findViewById(R.id.store_txt_healthPotionQuantityIncrease);

        reviveQuantity = (MaterialTextView) findViewById(R.id.store_txt_reviveQuantity);
        reviveQuantityDecrease = (MaterialTextView) findViewById(R.id.store_txt_reviveQuantityDecrease);
        reviveQuantityIncrease = (MaterialTextView) findViewById(R.id.store_txt_reviveQuantityIncrease);

        pokeballQuantity = (MaterialTextView) findViewById(R.id.store_txt_pokeballQuantity);
        pokeballQuantityDecrease = (MaterialTextView) findViewById(R.id.store_txt_pokeballQuantityDecrease);
        pokeballQuantityIncrease = (MaterialTextView) findViewById(R.id.store_txt_pokeballQuantityIncrease);

        masterballQuantity = (MaterialTextView) findViewById(R.id.store_txt_masterballQuantity);
        masterballQuantityDecrease = (MaterialTextView) findViewById(R.id.store_txt_masterballQuantityDecrease);
        masterballQuantityIncrease = (MaterialTextView) findViewById(R.id.store_txt_masterballQuantityIncrease);

        btnBuy = (MaterialButton) findViewById(R.id.store_btn_buy);

        txtTotalCost = (MaterialTextView) findViewById(R.id.store_txt_totalCost);
        txtPlayerBalance = (MaterialTextView) findViewById(R.id.store_txt_playerBalance);

        txtTotalCost.setText(getString(R.string.Total,totalCost));
        txtPlayerBalance.setText(getString(R.string.Balance,playerBalance));

        //region Set OnClickEvents

        decreaseQuantity(healthPotionQuantity,healthPotionQuantityDecrease,250);
        increaseQuantity(healthPotionQuantity,healthPotionQuantityIncrease,250);

        decreaseQuantity(reviveQuantity,reviveQuantityDecrease,750);
        increaseQuantity(reviveQuantity,reviveQuantityIncrease,750);

        decreaseQuantity(pokeballQuantity,pokeballQuantityDecrease,500);
        increaseQuantity(pokeballQuantity,pokeballQuantityIncrease,500);

        decreaseQuantity(masterballQuantity,masterballQuantityDecrease,1000);
        increaseQuantity(masterballQuantity,masterballQuantityIncrease,1000);

        btnBuy.setOnClickListener(buyListener);

        //endregion

        //region setPlayerBalance
        Cursor resultSet = db.getPlayerBag(playerID);
        while (resultSet.moveToNext()){
            playerBalance = resultSet.getInt(2);
            txtPlayerBalance.setText(getString(R.string.Balance,playerBalance));
        }
        //endregion

    }

    //region OnClick Methods

    public void increaseQuantity(final MaterialTextView itemQuantity, MaterialTextView itemQuantityIncrease,final int itemCost){
        itemQuantityIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(itemQuantity.getText().toString());
                if(quantity != 10){
                    quantity++;
                    itemQuantity.setText(String.valueOf(quantity));
                    totalCost = totalCost + itemCost ;
                    txtTotalCost.setText(getString(R.string.Total,totalCost));
                }
            }
        });
    }

    public void decreaseQuantity(final MaterialTextView itemQuantity,MaterialTextView itemQuantityDecrease,final int itemCost){
        itemQuantityDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(itemQuantity.getText().toString());
                if (quantity > 0){
                    quantity--;
                    itemQuantity.setText(String.valueOf(quantity));
                    totalCost = totalCost - itemCost ;
                    txtTotalCost.setText(getString(R.string.Total,totalCost));
                }
            }
        });
    }

    View.OnClickListener buyListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(totalCost > playerBalance){
                Toast.makeText(StoreActivity.this, "You don't have enough money", Toast.LENGTH_SHORT).show();
            }
            if(totalCost == 0){
                Toast.makeText(StoreActivity.this, "You don't have anything in your cart", Toast.LENGTH_SHORT).show();
            }
            if(playerBalance != 0 && totalCost != 0 && playerBalance >= totalCost){
                playerBalance = playerBalance - totalCost;
                boolean result = db.updatePlayerBag(playerID,playerBalance,Integer.parseInt(healthPotionQuantity.getText().toString()),
                                                                           Integer.parseInt(reviveQuantity.getText().toString()),
                                                                           Integer.parseInt(pokeballQuantity.getText().toString()),
                                                                           Integer.parseInt(masterballQuantity.getText().toString()));

                healthPotionQuantity.setText(getString(R.string.Zero));
                reviveQuantity.setText(getString(R.string.Zero));
                pokeballQuantity.setText(getString(R.string.Zero));
                masterballQuantity.setText(getString(R.string.Zero));
                txtPlayerBalance.setText(getString(R.string.Balance,playerBalance));

                totalCost = 0;
                txtTotalCost.setText(getString(R.string.Total,totalCost));
                System.err.println("Result : " + result);
                if(result){
                    Toast.makeText(StoreActivity.this, "Purchase Successful", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    //endregion
}
