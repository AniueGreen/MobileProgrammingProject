package com.example.mobileprojectapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.mobileprojectapp.Database.DatabaseConnection;
import com.example.mobileprojectapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    FloatingActionButton btnAddNew;
    ListView listPlayers;
    ArrayList<String> playerList;
    ArrayAdapter adapter;
    DatabaseConnection db;
    HashMap<String,Integer> playerMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = new DatabaseConnection(this);

        btnAddNew = (FloatingActionButton) findViewById(R.id.login_fltbtn_addNew);
        listPlayers = (ListView) findViewById(R.id.login_listView_players);

        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        listPlayers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String playerName = listPlayers.getItemAtPosition(position).toString();
                Integer playerID = playerMap.get(playerName);

                Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                intent.putExtra("playerName" , playerName);
                intent.putExtra("playerID",playerID);
                startActivity(intent);
            }
        });

        playerList = new ArrayList<>();
        getPlayerList();
    }

    private void getPlayerList(){
        Cursor resultSet = db.getPlayers();

        if(resultSet.getCount() == 0){
            playerList.add("There is no player");
        }else{
            while(resultSet.moveToNext()){
                playerMap = new HashMap<>();
                playerMap.put(resultSet.getString(1),resultSet.getInt(0));
                playerList.add(resultSet.getString(1));
            }
        }

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,playerList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the current item from ListView
                View view = super.getView(position,convertView,parent);
                view.setBackgroundColor(Color.parseColor("#fff200"));
                return view;
            }
        };

        listPlayers.setAdapter(adapter);
    }

}
