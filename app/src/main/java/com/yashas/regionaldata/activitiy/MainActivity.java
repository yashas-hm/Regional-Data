package com.yashas.regionaldata.activitiy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RegionalAdapter adapter;

    private String selectedRegion = "Asia";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUp();
    }

    private void setUp(){
        initUi();
        listeners();
        loadData();
    }

    private void initUi(){
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
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
        adapter = new RegionalAdapter(this, regionEntity);
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