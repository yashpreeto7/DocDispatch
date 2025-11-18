package online.ppriyanshu26.docdispatch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class QueriesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    QueriesAdapter adapter;
    ArrayList<QueryModel> queryList = new ArrayList<>();

    String BASE_URL = BuildConfig.QUERIES_URL;
    String PREFS_NAME = "MyAppPrefs";
    String KEY_PHONE = "phone_number";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queries);

        recyclerView = findViewById(R.id.recyclerQueries);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new QueriesAdapter(queryList);
        recyclerView.setAdapter(adapter);

        fetchQueries();
    }

    private void fetchQueries() {
        new Thread(() -> {
            try {
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                String savedPhone = prefs.getString(KEY_PHONE, null);

                URL url = new URL(BASE_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");

                JSONObject json = new JSONObject();
                json.put("phone", savedPhone);

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(json.toString());
                writer.flush();
                writer.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }

                    reader.close();
                    parseResponse(sb.toString());
                } else {
                    Log.e("QUERIES_API", "Error code: " + responseCode);
                }

                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void parseResponse(String response) {
        try {
            JSONArray arr = new JSONArray(response);
            queryList.clear();

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

                int qid = obj.getInt("qid");
                boolean attended = obj.getBoolean("attended");
                String name = obj.getString("name");

                String doctor = obj.isNull("doctor") ? "-" : obj.getString("doctor");
                String treatment = obj.isNull("treatment") ? "-" : obj.getString("treatment");
                String remarks = obj.isNull("remarks") ? "-" : obj.getString("remarks");

                queryList.add(new QueryModel(qid, attended, name, doctor, treatment, remarks));
            }

            runOnUiThread(() -> adapter.notifyDataSetChanged());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
