package vip.mokardder.smsclient.ui;


import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.List;

import vip.mokardder.smsclient.R;
import vip.mokardder.smsclient.database.SmsSentListDB;
import vip.mokardder.smsclient.models.sms_list_payload;
import vip.mokardder.smsclient.ui.Adapter.SmsSentListAdapter;


public class SmsListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SmsSentListAdapter adapter;

    SmsSentListDB db;
    private List<sms_list_payload> updateItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sms_list);
        db = new SmsSentListDB(this);

        FirebaseApp.initializeApp(this);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize data
        updateItemList = db.getAllSmsLists();




        // Set up the adapter
        adapter = new SmsSentListAdapter(this, updateItemList);
        recyclerView.setAdapter(adapter);
    }

}