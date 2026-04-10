package com.example.lostfound;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lostfound.activities.AddObjectActivity;
import com.example.lostfound.activities.LoginActivity;
import com.example.lostfound.activities.RegisterActivity;
import com.example.lostfound.adapter.PostAdapter;
import com.example.lostfound.api.ApiService;
import com.example.lostfound.api.RetrofitClient;
import com.example.lostfound.models.LostObject;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button btnLogout;
    private Button btnGoLogin;
    private Button btnGoRegister;

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private List<LostObject> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            setContentView(R.layout.activity_auth);
            btnGoLogin = findViewById(R.id.btnGoLogin);
            btnGoRegister = findViewById(R.id.btnGoRegister);

            btnGoLogin.setOnClickListener(v ->
                    startActivity(new Intent(this, LoginActivity.class))
            );

            btnGoRegister.setOnClickListener(v ->
                    startActivity(new Intent(this, RegisterActivity.class))
            );
        } else {
            setContentView(R.layout.activity_main);

            btnLogout = findViewById(R.id.btnLogout);
            btnLogout.setOnClickListener(v -> {
                mAuth.signOut();
                recreate();
            });

            recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new PostAdapter(list);
            recyclerView.setAdapter(adapter);

            FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
            fabAdd.setOnClickListener(v ->
                    startActivity(new Intent(this, AddObjectActivity.class))
            );

            loadObjects();
        }
    }

    private void loadObjects() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getObjects().enqueue(new Callback<List<LostObject>>() {
            @Override
            public void onResponse(Call<List<LostObject>> call, Response<List<LostObject>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    list.clear();
                    list.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    if (list.isEmpty()) {
                        findViewById(R.id.mapContainer).setVisibility(View.GONE);
                        findViewById(R.id.recyclerView).setVisibility(View.GONE);
                        findViewById(R.id.tvEmpty).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.mapContainer).setVisibility(View.VISIBLE);
                        findViewById(R.id.recyclerView).setVisibility(View.VISIBLE);
                        findViewById(R.id.tvEmpty).setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<LostObject>> call, Throwable t) {
            }
        });
    }
}