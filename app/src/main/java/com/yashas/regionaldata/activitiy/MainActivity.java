package com.yashas.regionaldata.activitiy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.yashas.regionaldata.R;
import com.yashas.regionaldata.adapter.RegionalAdapter;
import com.yashas.regionaldata.database.RegionEntity;
import com.yashas.regionaldata.database.RegionsDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SharedPreferences sharedPreferences;

    private String selectedRegion = "Asia";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chooser, menu);
        int reg = sharedPreferences.getInt("region", 1);
        MenuItem item = menu.findItem(reg-1);
        item.setChecked(true);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.asia:
                selectedRegion = "asia";
                item.setChecked(true);
                sharedPreferences.edit().putInt("region", 1).apply();
                break;
            case R.id.africa:
                selectedRegion = "africa";
                item.setChecked(true);
                sharedPreferences.edit().putInt("region", 2).apply();
                break;
            case R.id.america:
                selectedRegion = "america";
                item.setChecked(true);
                sharedPreferences.edit().putInt("region", 3).apply();
                break;
            case R.id.europe:
                selectedRegion = "europe";
                item.setChecked(true);
                sharedPreferences.edit().putInt("region", 4).apply();
                break;
            case R.id.oceania:
                selectedRegion = "oceania";
                item.setChecked(true);
                sharedPreferences.edit().putInt("region", 5).apply();
                break;
            case R.id.clear:
                new DBFunctions(2).execute();
        }
        loadData();
        return super.onOptionsItemSelected(item);
    }

    private void setUp(){
        initUi();
        listeners();
        regionSelection();
        loadData();
    }

    private void initUi(){
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        sharedPreferences = getSharedPreferences("region", Context.MODE_PRIVATE);
    }

    private void regionSelection(){
        int id = sharedPreferences.getInt("region", 1);
        switch(id){
            case 1:
                selectedRegion = "asia";
                break;
            case 2:
                selectedRegion = "africa";
                break;
            case 3:
                selectedRegion = "america";
                break;
            case 4:
                selectedRegion = "europe";
                break;
            case 5:
                selectedRegion = "oceania";
                break;
        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void loadData(){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://restcountries.eu/rest/v2/region/"+selectedRegion;

        if(isNetworkAvailable()){
            JsonArrayRequest objectRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    url,
                    null,
                    response -> {
                        ArrayList<RegionEntity> regionEntityList = new ArrayList<>();
                        for(int i=0;i<response.length();i++){
                            try {
                                JSONObject data = response.getJSONObject(i);
                                ArrayList<String> lang = new ArrayList<>(), border = new ArrayList<>();
                                JSONArray borderList = data.getJSONArray("borders");
                                for(int j=0;j<borderList.length();j++){
                                    border.add(borderList.getString(j));
                                }
                                JSONArray langList = data.getJSONArray("languages");
                                for(int j=0;j<langList.length();j++){
                                    JSONObject langObj = langList.getJSONObject(j);
                                    lang.add(langObj.getString("name"));
                                }
                                regionEntityList.add(new RegionEntity(
                                   data.getString("name"),
                                   data.getString("capital"),
                                   data.getString("flag"),
                                   data.getString("region"),
                                   data.getString("subregion"),
                                   data.getLong("population"),
                                   border.toString(),
                                   lang.toString()
                                ));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        setUpRecycler(regionEntityList);
                        reNewDB(regionEntityList);
                    }, error -> {
                        Toast.makeText(
                                MainActivity.this,
                                "some error occurred!",
                                Toast.LENGTH_SHORT
                        ).show();
                    }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> map = Collections.emptyMap();
                    map.put("Content-type", "application/json");
                    return map;
                }
            };
            queue.add(objectRequest);
        }else{
            try {
                Toast.makeText(
                        MainActivity.this,
                        "You seem to be offline! Loaded from previously stored data",
                        Toast.LENGTH_SHORT
                ).show();
                ArrayList<RegionEntity> regionEntityList = (ArrayList<RegionEntity>) new DBFunctions(3).execute().get();
                setUpRecycler(regionEntityList);
            } catch (Exception e) {
                Toast.makeText(
                        MainActivity.this,
                        "some error occurred!",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }

        Toast.makeText(MainActivity.this, "Fetching data for "+selectedRegion, Toast.LENGTH_SHORT).show();
    }

    private void reNewDB(List<RegionEntity> regionEntity){
        new DBFunctions(2).execute();
        for(RegionEntity regions: regionEntity){
            new DBFunctions(1, regions).execute();
        }
    }

    private void setUpRecycler(ArrayList<RegionEntity> regionEntity){
        RegionalAdapter adapter = new RegionalAdapter(this, regionEntity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void listeners(){

    }

    public class DBFunctions extends AsyncTask<Void, Void, Object>{
        private final int mode;
        private RegionEntity regionEntity = null;
        DBFunctions(int mode){
            this.mode = mode;
        }

        DBFunctions(int mode, RegionEntity regionEntity){
            this.mode = mode;
            this.regionEntity = regionEntity;
        }
        @Override
        protected Object doInBackground(Void... voids) {
            RegionsDatabase db = Room.databaseBuilder(getApplicationContext(), RegionsDatabase.class, "regionsData").build();
            switch (mode){
                case 1:
                    db.regionDao().insertIntoDb(regionEntity);
                    break;
                case 2:
                    db.regionDao().deleteAll();
                    break;
                case 3:
                    return db.regionDao().getAll();
            }
            return true;
        }
    }
}