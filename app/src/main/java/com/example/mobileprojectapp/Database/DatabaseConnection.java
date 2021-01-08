package com.example.mobileprojectapp.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

import com.example.mobileprojectapp.Pokemon.Pokemon;


public class DatabaseConnection extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Pokemon.db";
    private static final String PLAYER_TABLE = "Player";
    private static final String POKEMON_TABLE = "Pokemon";
    private static final String BAG_TABLE = "Bag";
    private static final String createPlayerTable = ("CREATE TABLE " + PLAYER_TABLE + "(PlayerID INTEGER PRIMARY KEY AUTOINCREMENT," +
                                                                                      " PlayerName TEXT)");

    private static final String createPokemonTable = ("CREATE TABLE " + POKEMON_TABLE + "(PokemonID INTEGER PRIMARY KEY AUTOINCREMENT," +
                                                                                        " PlayerID INTEGER ," +
                                                                                        " PokemonName TEXT ," +
                                                                                        " PokemonAPIID INTEGER," +
                                                                                        " PokemonHP INTEGER," +
                                                                                        " PokemonLevel INTEGER," +
                                                                                        " PokemonNextLevel INTEGER," +
                                                                                        " PokemonStrength REAL," +
                                                                                        " PokemonFirstMove TEXT," +
                                                                                        " PokemonFirstMoveDamage INTEGER," +
                                                                                        " PokemonSecondMove TEXT," +
                                                                                        " PokemonSecondMoveDamage INTEGER," +
                                                                                        " PokemonThirdMove TEXT," +
                                                                                        " PokemonThirdMoveDamage INTEGER," +
                                                                                        " PokemonFourthMove TEXT," +
                                                                                        " PokemonFourthMoveDamage INTEGER," +
                                                                                        " PokemonXP INTEGER)");

    private static final String createBagTable = ("CREATE TABLE " + BAG_TABLE + "(BagID INTEGER PRIMARY KEY AUTOINCREMENT," +
                                                                                " PlayerID INTEGER," +
                                                                                " PlayerBalance INTEGER," +
                                                                                " HealthPotionQuantity INTEGER," +
                                                                                " RevivePotionQuantity INTEGER," +
                                                                                " PokeBallQuantity INTEGER," +
                                                                                " MasterBallQuantity INTEGER)");

    public DatabaseConnection(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createPlayerTable);
        db.execSQL(createPokemonTable);
        db.execSQL(createBagTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PLAYER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + POKEMON_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + BAG_TABLE);
    }

    public Long addNewPlayer(String PlayerName){
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            String SQL = "INSERT INTO Player(PlayerName) VALUES(?)";
            SQLiteStatement statement = db.compileStatement(SQL);
            statement.bindString(1,PlayerName);
            Long id = statement.executeInsert();
            return id;
        }catch (SQLException e){
            System.err.println(e.getMessage());
        }
        return 0L;
    }

    public void addPlayerPokemon(long PlayerID, Pokemon pokemon){
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            String SQL = "INSERT INTO " + POKEMON_TABLE + "(PlayerID,PokemonName,PokemonAPIID,PokemonFirstMove,PokemonFirstMoveDamage,PokemonSecondMove" +
                                                          ",PokemonSecondMoveDamage,PokemonThirdMove,PokemonThirdMoveDamage,PokemonFourthMove,PokemonFourthMoveDamage," +
                                                          "PokemonHP,PokemonLevel,PokemonNextLevel,PokemonStrength,PokemonXP) " +
                         "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            SQLiteStatement statement = db.compileStatement(SQL);
            statement.bindLong(1,PlayerID);
            statement.bindString(2,pokemon.getPokemonName());
            statement.bindLong(3,pokemon.getPokemonID());
            statement.bindString(4,pokemon.getFirstMoveName());
            statement.bindLong(5,pokemon.getFirstMoveDamage());
            statement.bindString(6,pokemon.getSecondMoveName());
            statement.bindLong(7,pokemon.getSecondMoveDamage());
            statement.bindString(8,pokemon.getThirdMoveName());
            statement.bindLong(9,pokemon.getThirdMoveDamage());
            statement.bindString(10,pokemon.getFourthMoveName());
            statement.bindLong(11,pokemon.getFourthMoveDamage());
            statement.bindLong(12,pokemon.getPokemonHP());
            statement.bindLong(13,pokemon.getPokemonLevel());
            statement.bindLong(14,pokemon.getPokemonNextLevel());
            statement.bindDouble(15,pokemon.getPokemonStrength());
            statement.bindLong(16,pokemon.getPokemonXP());
            Long id = statement.executeInsert();
        }catch (SQLException e){
            System.err.println(e.getMessage());
        }
    }

    public void createUserBag(Long PlayerID){
        System.err.println("PlayerID " + PlayerID);
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String SQL = "INSERT INTO " + BAG_TABLE + "(PlayerID,PlayerBalance,HealthPotionQuantity,RevivePotionQuantity,PokeBallQuantity,MasterBallQuantity)" +
                                                     "VALUES (?,?,?,?,?,?)";
            SQLiteStatement statement = db.compileStatement(SQL);
            statement.bindLong(1,PlayerID);
            statement.bindLong(2,5000);
            statement.bindLong(3,5);
            statement.bindLong(4,1);
            statement.bindLong(5,5);
            statement.bindLong(6,1);
            statement.executeInsert();
        }catch (SQLiteException e){
            System.err.println(e.getMessage());
        }
    }

    public Cursor getPlayerPokemon(int PlayerID){
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            return db.rawQuery("SELECT * FROM Pokemon WHERE PlayerID = ? ",new String[]{String.valueOf(PlayerID)});
        }catch (SQLiteException e){
            System.err.println(e.getMessage());
        }
        return null;
    }

    public Cursor getPlayers(){
        SQLiteDatabase db = this.getReadableDatabase();
        String SQL = "SELECT * FROM " + PLAYER_TABLE;
        return db.rawQuery(SQL,null);
    }


    //region Store
        public Cursor getPlayerBag(int PlayerID){
            try {
                SQLiteDatabase db = this.getReadableDatabase();
                return db.rawQuery("SELECT * FROM Bag WHERE PlayerID = ?", new String[]{String.valueOf(PlayerID)});
            }
            catch (SQLiteException e) {
                System.err.println(e.getMessage());
            }
            return null;
        }

        public boolean updatePlayerBag(int PlayerID,int PlayerBalance,int HealthPotionQuantity,int RevivePotionQuantity,int PokeBallQuantity,int MasterBallQuantity){
            String SQL = "UPDATE " + BAG_TABLE + " SET PlayerBalance = ? , HealthPotionQuantity = ? , RevivePotionQuantity = ? , PokeBallQuantity = ? , MasterBallQuantity = ? WHERE PlayerID = ?";
            try{
                SQLiteDatabase db = this.getReadableDatabase();
                SQLiteStatement statement = db.compileStatement(SQL);
                statement.bindLong(1,PlayerBalance);
                statement.bindLong(2,HealthPotionQuantity);
                statement.bindLong(3,RevivePotionQuantity);
                statement.bindLong(4,PokeBallQuantity);
                statement.bindLong(5,MasterBallQuantity);
                statement.bindLong(6,PlayerID);
                statement.executeInsert();
                return true;
            }catch (SQLiteException e){
                System.err.println(e.getMessage());
            }
            return false;
        }

    //endregion

    public void updateHealthPotionQuantity(int PlayerID,int HealthPotionQuantity){
        String SQL = "UPDATE " + BAG_TABLE + " SET HealthPotionQuantity = ? WHERE PlayerID = ?";
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            SQLiteStatement statement = db.compileStatement(SQL);
            statement.bindLong(1,HealthPotionQuantity);
            statement.bindLong(2,PlayerID);
            statement.executeInsert();
        }catch (SQLiteException e){
            System.err.println(e.getMessage());
        }
    }

    public void updatePlayerBalance(int PlayerID,int balance){
        String SQL = "UPDATE " + BAG_TABLE + " SET PlayerBalance = ? WHERE PlayerID = ?";
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            SQLiteStatement statement = db.compileStatement(SQL);
            statement.bindLong(1,balance);
            statement.bindLong(2,PlayerID);
            statement.executeInsert();
        }catch (SQLiteException e){
            System.err.println(e.getMessage());
        }
    }
}
